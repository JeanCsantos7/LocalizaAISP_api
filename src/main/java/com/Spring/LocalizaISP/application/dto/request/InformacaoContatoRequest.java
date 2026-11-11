package com.Spring.LocalizaISP.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InformacaoContatoRequest(
		Long casoId,

		@NotBlank(message = "O nome do contato é obrigatório")
		@Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
		String nome,

		@Email(message = "E-mail inválido")
		@Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres")
		String email,

		@NotBlank(message = "O telefone é obrigatório")
		@Size(max = 30, message = "O telefone deve ter no máximo 30 caracteres")
		String telefone
) {
}
