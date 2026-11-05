package com.Spring.LocalizaISP.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(


        @Email
        @NotBlank(message = "Preencha o endereço e-mail!")
        String email,
        @NotBlank(message = "Informe sua senha!")
        String senha
){}
