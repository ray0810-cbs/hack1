# 🍪 Oreo Insight Factory - Backend Hackathon UTEC

> Proyecto desarrollado como parte del **Hackathon #1**: “Oreo Insight Factory”, un sistema de backend seguro, asincrónico y con generación automática de reportes inteligentes usando **GitHub Models** y **Spring Boot 3**.

---

## 👨‍💻 Información del Equipo

| Nombre completo             | Código UTEC  |
|-----------------------------|--------------|
| **Ray Bolaños**             | **202210051** |

---

## 🚀 Descripción General

Este proyecto implementa el backend de una **plataforma de análisis de ventas** para la fábrica **Oreo**, que permite:

1. Registrar usuarios y sucursales (roles `CENTRAL` y `BRANCH`).
2. Crear, listar, actualizar y eliminar ventas según permisos de rol.
3. Generar reportes semanales **asíncronos** con un **LLM (GitHub Models)**.
4. Enviar automáticamente los resúmenes por **correo electrónico (SMTP Gmail)**.
5. Garantizar seguridad con **JWT** y autenticación basada en roles.

---

## ⚙️ Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.5**
- **Spring Security (JWT)**
- **Spring Data JPA / PostgreSQL**
- **Spring Mail**
- **Spring Async / ApplicationEventPublisher**
- **GitHub Models API (LLM Integration)**
- **ModelMapper**
- **Lombok**
- **Mockito / JUnit 5**
- **Postman API Testing**

---

## 🧩 Estructura de Paquetes

```
com.example.oreohack
 ├── controladores/           # REST Controllers (Auth, Sales, Users)
 ├── dto/
 │    ├── request/            # DTOs de entrada (Register, Login, Sales, Report)
 │    └── response/           # DTOs de salida (AuthResponse, SaleResponse, etc.)
 ├── entidades/               # Entidades JPA (UserClass, Branch, Sale)
 │    └── roles/              # Enum de roles: CENTRAL, BRANCH
 ├── excepciones/             # Excepciones personalizadas + GlobalExceptionHandler
 ├── repositorios/            # Repositorios JPA
 ├── seguridad/               # JWT, filtros, configuración de seguridad
 ├── servicios/               # Lógica de negocio (Auth, Sales, Report, etc.)
 ├── eventos/                 # Eventos asincrónicos: ReportRequestedEvent, Listener
 ├── config/                  # Configuración general (Mail, Async, ModelMapper)
 └── OreoHackApplication.java # Clase principal con @EnableAsync
```


---

## 🧠 Instrucciones para Ejecutar el Proyecto

### 1️⃣ Requisitos Previos
- Tener **Java 21** y **Maven 3.9+** instalados.
- PostgreSQL corriendo localmente (o en Docker):
  ```bash
  docker run --name postgres-oreo -e POSTGRES_PASSWORD=mypassword -e POSTGRES_DB=mydb -p 5435:5432 -d postgres
  ```
- Archivo `.env` configurado correctamente en el directorio raíz.

---

### 2️⃣ Compilación y Ejecución

En el directorio raíz del proyecto:

```bash
mvn clean install
mvn spring-boot:run
```

El servidor se levantará en:
```
http://localhost:8081
```

---

### 3️⃣ Endpoints Principales

| Método | Endpoint | Descripción | Roles |
|--------|-----------|--------------|--------|
| **POST** | `/auth/register` | Registro de usuario | Público |
| **POST** | `/auth/login` | Autenticación JWT | Público |
| **POST** | `/sales` | Crear venta | CENTRAL / BRANCH |
| **GET** | `/sales` | Listar ventas | CENTRAL / BRANCH |
| **GET** | `/sales/{id}` | Ver detalle de venta | CENTRAL / BRANCH |
| **PUT** | `/sales/{id}` | Actualizar venta | CENTRAL / BRANCH |
| **DELETE** | `/sales/{id}` | Eliminar venta | CENTRAL |
| **POST** | `/sales/summary/weekly` | Generar resumen semanal asíncrono | CENTRAL / BRANCH |
| **GET** | `/users` | Listar usuarios | CENTRAL |

---

## 🧪 Instrucciones para Correr el Postman Workflow

1. Abre **Postman** e importa la colección `OreoInsightFactory.postman_collection.json` del repositorio.
2. Define la variable `{{baseUrl}}` como:
   ```
   http://localhost:8081
   ```
3. Ejecuta el flujo completo en orden:
   - **Register CENTRAL**
   - **Login CENTRAL** → guarda el token en `{{centralToken}}`
   - **Register BRANCH (Miraflores)**
   - **Login BRANCH** → guarda el token en `{{branchToken}}`
   - **Crear 5 ventas (con CENTRAL)**  
   - **Listar todas las ventas (CENTRAL)**
   - **Listar ventas (BRANCH)**
   - **Generar resumen semanal (asíncrono)**
   - **Intentar crear venta en otra sucursal (BRANCH)** → debe retornar 403
   - **Eliminar venta (CENTRAL)** → debe retornar 204

Cada paso tiene **asserts automáticos** y **tests de validación de código HTTP** (200, 201, 202, 204, 403, 503, etc.).

---

## ⚙️ Explicación de la Implementación Asíncrona

La parte más importante del proyecto es el **procesamiento asíncrono de reportes**, implementado mediante **eventos Spring**.

### 🔄 Flujo del Reporte Semanal (`/sales/summary/weekly`)

1. El **controller** recibe la solicitud y responde inmediatamente con **HTTP 202 Accepted**.
2. Se publica un **evento asíncrono**.
3. El **listener** procesa el evento en segundo plano (`@Async @EventListener`).
4. Si el servicio de correo o el LLM falla, se lanza `ServiceUnavailableException` → responde con **503**.

---

### 💡 Ventajas del Enfoque Asíncrono

| Característica | Beneficio |
|----------------|------------|
| `@Async` | Libera el hilo principal, mejorando tiempos de respuesta |
| `ApplicationEventPublisher` | Permite un flujo desacoplado y escalable |
| `JavaMailSender` | Envía correos en background sin bloquear API |
| `WebClient` | Permite integración reactiva con GitHub Models |
| `@EnableAsync` | Habilita ejecución en hilos paralelos seguros |

---

## ✅ Testing Unitario

El archivo `SalesAggregationServiceTest.java` contiene **5 casos de prueba unitarios** implementados con **Mockito**.

Ejecutar los tests:

```bash
mvn test
```

Todos los tests deben pasar con estado ✅

---

## ✨ Créditos

Desarrollado por **Ray Bolaños (202210051)**  
Facultad de Ingeniería Mecatrónica – UTEC  

---
