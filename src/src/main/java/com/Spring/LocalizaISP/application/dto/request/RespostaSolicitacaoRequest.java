package com.Spring.LocalizaISP.application.dto.request;

import jakarta.validation.constraints.Size;

public record RespostaSolicitacaoRequest(
		@Size(max = 1800, message = "A resposta deve ter no máximo 1800 caracteres")
		String resposta
) {
}
