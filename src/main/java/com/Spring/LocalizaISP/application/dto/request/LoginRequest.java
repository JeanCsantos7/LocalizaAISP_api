package com.Spring.LocalizaISP.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email
        @NotBlank(message = "Preencha o endereço e-mail!")
        String email,
        @NotBlank(message = "Informe sua senha!")
        String senha
){}
