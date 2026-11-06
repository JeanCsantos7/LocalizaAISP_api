package com.Spring.LocalizaISP.application.dto.response;

import com.Spring.LocalizaISP.domain.enums.StatusSolicitacao;
import com.Spring.LocalizaISP.domain.enums.TipoSolicitacao;

import java.time.LocalDateTime;

public record SolicitacaoResponse(
		Long id,
		TipoSolicitacao tipo,
		String motivo,
		String descricao,
		StatusSolicitacao status,
		String resposta,
		LocalDateTime criadoEm,
		LocalDateTime respondidoEm,
		Long usuarioId,
		Long casoId
) {
}
