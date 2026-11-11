package com.Spring.LocalizaISP.application.dto.response;

import com.Spring.LocalizaISP.domain.enums.StatusCaso;

import java.time.LocalDateTime;

public record HistoricoStatusResponse(
		Long id,
		StatusCaso statusAnterior,
		StatusCaso statusAtual,
		String observacao,
		LocalDateTime dataAlteracao,
		Long casoId,
		Long responsavelId,
		String responsavelNome
) {
}
