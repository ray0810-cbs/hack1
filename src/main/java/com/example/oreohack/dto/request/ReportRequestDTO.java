package com.example.oreohack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {
    private String from;
    private String to;
    private String branch;

    @Email
    @NotBlank
    private String emailTo;
}
