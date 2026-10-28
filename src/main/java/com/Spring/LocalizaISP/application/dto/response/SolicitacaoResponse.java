package com.Spring.LocalizaISP.application.dto.response;

import com.Spring.LocalizaISP.domain.enums.StatusSolicitacao;
import com.Spring.LocalizaISP.domain.enums.TipoSolicitacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record SolicitacaoResponse(
		UUID id,
		TipoSolicitacao tipo,
		String motivo,
		String descricao,
		StatusSolicitacao status,
		LocalDateTime criadoEm,
		LocalDateTime respondidoEm,
		UUID usuarioId,
		UUID casoId
) {
}
