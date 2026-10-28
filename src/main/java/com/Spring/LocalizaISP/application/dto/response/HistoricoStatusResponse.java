package com.Spring.LocalizaISP.application.dto.response;

import com.Spring.LocalizaISP.domain.enums.StatusCaso;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoricoStatusResponse(
		UUID id,
		StatusCaso statusAnterior,
		StatusCaso statusAtual,
		String observacao,
		LocalDateTime dataAlteracao,
		UUID casoId
) {
}
