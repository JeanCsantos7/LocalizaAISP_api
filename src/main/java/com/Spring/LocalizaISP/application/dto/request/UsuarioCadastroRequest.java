package com.Spring.LocalizaISP.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroRequest(

        @NotBlank(message = "Informe seu nome!")
        String nome,

        @Email
        @NotBlank(message = "Informe um endereço de e-mail!")
        String email,
        @NotBlank(message = "Informe uma senha que tenha no mínimo 6 caracteres e no máximo 72.")
        @Size(min = 8, max = 72)
        String senha
) {
}
