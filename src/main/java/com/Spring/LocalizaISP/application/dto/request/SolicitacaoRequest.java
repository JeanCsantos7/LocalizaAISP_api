package com.Spring.LocalizaISP.application.dto.request;

import com.Spring.LocalizaISP.domain.enums.TipoSolicitacao;

import java.util.UUID;

public record SolicitacaoRequest(
		UUID casoId,
		TipoSolicitacao tipo,
		String motivo,
		String descricao
) {
}
