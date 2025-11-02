package com.example.oreohack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class ReportRequestDTO {
    private String from;
    private String to;
    private String branch;

    @Email
    @NotBlank
    private String emailTo;
}
