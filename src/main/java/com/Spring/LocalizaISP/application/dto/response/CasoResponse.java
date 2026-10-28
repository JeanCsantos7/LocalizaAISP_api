package com.Spring.LocalizaISP.application.dto.response;

import com.Spring.LocalizaISP.domain.enums.OrigemCaso;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CasoResponse(
		UUID id,
		String titulo,
		String descricao,
		String regiao,
		String faixaEtaria,
		LocalDate dataDesaparecimento,
		StatusCaso status,
		OrigemCaso origem,
		String observacoes,
		LocalDateTime criadoEm,
		LocalDateTime atualizadoEm,
		UUID usuarioId
) {
}
