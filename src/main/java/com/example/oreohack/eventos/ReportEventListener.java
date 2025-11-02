package com.example.oreohack.eventos;

import com.example.oreohack.dto.request.ReportRequestDTO;
import com.example.oreohack.entidades.Branch;
import com.example.oreohack.entidades.Sale;
import com.example.oreohack.entidades.UserClass;
import com.example.oreohack.entidades.roles.Role;
import com.example.oreohack.excepciones.ServiceUnavailableException;
import com.example.oreohack.repositorios.BranchRepository;
import com.example.oreohack.repositorios.SalesRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static java.util.Map.Entry.comparingByValue;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportEventListener {

    private final SalesRepository salesRepository;
    private final BranchRepository branchRepository;
    private final JavaMailSender mailSender;

    @Value("${GITHUB_TOKEN}")
    private String githubToken;

    @Value("${GITHUB_MODELS_URL}")
    private String githubModelsUrl;

    @Value("${MODEL_ID}")
    private String modelId;

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE;

    @Async
    @org.springframework.context.event.EventListener
    public void handleReportRequest(ReportRequestedEvent event) {
        ReportRequestDTO req = event.getRequest();
        UserClass user = event.getUser();

        try {
            // 1) Rango de fechas (DTO trae String → LocalDate)
            LocalDate from = (req.getFrom() != null && !req.getFrom().isBlank())
                    ? LocalDate.parse(req.getFrom(), ISO)
                    : LocalDate.now().minusDays(7);

            LocalDate to = (req.getTo() != null && !req.getTo().isBlank())
                    ? LocalDate.parse(req.getTo(), ISO)
                    : LocalDate.now();

            // Instants para la consulta (inclusivo hasta el final del día "to")
            Instant fromInstant = from.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant toInstant = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).minusNanos(1).toInstant();

            // 2) Sucursal
            if (req.getBranch() == null || req.getBranch().isBlank()) {
                throw new IllegalArgumentException("El campo 'branch' es obligatorio para el reporte.");
            }
            Branch branch = branchRepository.findByName(req.getBranch())
                    .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada: " + req.getBranch()));

            // Si es BRANCH, restringir a su propia sucursal
            final List<Sale> sales;
            if (user.getRole() == Role.CENTRAL) {
                sales = salesRepository.findByDateRange(fromInstant, toInstant);
            } else { // BRANCH
                if (!branch.equals(user.getBranch())) {
                    // Seguridad extra (de todos modos ya se valida antes en el servicio)
                    throw new SecurityException("No puede generar reportes de otra sucursal");
                }
                sales = salesRepository.findByDateRangeAndBranch(fromInstant, toInstant, branch);
            }

            if (sales.isEmpty()) {
                sendEmail(req.getEmailTo(),
                        "Reporte Semanal Oreo - " + from + " a " + to,
                        "No se encontraron ventas en el rango especificado (" + from + " a " + to + ").");
                log.info("Reporte sin ventas enviado a {}", req.getEmailTo());
                return;
            }

            // 3) Agregados
            int totalUnits = sales.stream().mapToInt(Sale::getUnits).sum();
            double totalRevenue = sales.stream().mapToDouble(s -> s.getUnits() * s.getPrice()).sum();

            Map<String, Integer> skuCount = new HashMap<>();
            Map<String, Integer> branchCount = new HashMap<>();
            for (Sale s : sales) {
                skuCount.merge(s.getSku(), s.getUnits(), Integer::sum);
                branchCount.merge(s.getBranch().getName(), s.getUnits(), Integer::sum);
            }

            String topSku = skuCount.entrySet().stream()
                    .max(comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            String topBranch = branchCount.entrySet().stream()
                    .max(comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            // 4) Llamada a GitHub Models (Map JSON + generics seguros)
            WebClient client = WebClient.builder().build();

            Map<String, Object> body = Map.of(
                    "model", modelId,
                    "messages", List.of(
                            Map.of("role", "system",
                                    "content", "Eres un analista que escribe resúmenes breves y claros para emails corporativos."),
                            Map.of("role", "user",
                                    "content", String.format(
                                            "Con estos datos: totalUnits=%d, totalRevenue=%.2f, topSku=%s, topBranch=%s. " +
                                                    "Devuelve un resumen ≤120 palabras, en español, claro y conciso.",
                                            totalUnits, totalRevenue, topSku, topBranch))
                    ),
                    "max_tokens", 200
            );

            Map<String, Object> response = client.post()
                    .uri(githubModelsUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + githubToken)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            String summaryText = extractSummary(response);

            // 5) Email
            String subject = String.format("Reporte Semanal Oreo - %s a %s", from, to);
            String bodyText = """
                    %s

                    Datos principales:
                    - Total Unidades: %d
                    - Total Recaudado: %.2f
                    - SKU Más Vendido: %s
                    - Sucursal Top: %s
                    """.formatted(summaryText, totalUnits, totalRevenue, topSku, topBranch);

            sendEmail(req.getEmailTo(), subject, bodyText);
            log.info("Reporte enviado correctamente a {}", req.getEmailTo());

        } catch (WebClientResponseException ex) {
            log.error("Error en GitHub Models API: {}", ex.getMessage());
            throw new ServiceUnavailableException("El servicio de LLM no está disponible actualmente", ex);
        } catch (Exception ex) {
            log.error("Error procesando el reporte: {}", ex.getMessage());
            throw new ServiceUnavailableException("Error procesando el reporte: " + ex.getMessage(), ex);
        }
    }

    private void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, false);
        mailSender.send(message);
    }

    @SuppressWarnings("unchecked")
    private String extractSummary(Map<String, Object> resp) {
        if (resp == null) return "No se obtuvo respuesta del modelo.";
        try {
            Object choicesObj = resp.get("choices");
            if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
                return "Respuesta del modelo sin 'choices'.";
            }
            Object first = choices.get(0);
            if (!(first instanceof Map<?,?> firstMap)) {
                return "Respuesta 'choices[0]' inválida.";
            }
            Object messageObj = firstMap.get("message");
            if (!(messageObj instanceof Map<?,?> msg)) {
                return "Respuesta sin 'message'.";
            }
            Object content = msg.get("content");
            return content != null ? content.toString() : "Respuesta sin 'content'.";
        } catch (Exception e) {
            return "No se pudo interpretar la respuesta del modelo.";
        }
    }
}
