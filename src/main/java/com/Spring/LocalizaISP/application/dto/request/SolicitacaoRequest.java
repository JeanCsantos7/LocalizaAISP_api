package com.Spring.LocalizaISP.application.dto.request;

import com.Spring.LocalizaISP.domain.enums.TipoSolicitacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SolicitacaoRequest(
		@NotNull(message = "Informe o caso")
		Long casoId,

		@NotNull(message = "Informe o tipo da solicitação")
		TipoSolicitacao tipo,

		@NotBlank(message = "Informe o motivo")
		@Size(max = 2000, message = "O motivo deve ter no máximo 2000 caracteres")
		String motivo,

		@NotBlank(message = "Informe a descrição")
		@Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
		String descricao
) {
}
