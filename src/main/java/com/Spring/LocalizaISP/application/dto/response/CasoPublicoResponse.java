package com.Spring.LocalizaISP.application.dto.response;

import com.Spring.LocalizaISP.domain.enums.StatusCaso;

import java.time.LocalDate;
import java.time.LocalDateTime;


public record CasoPublicoResponse(
		Long id,
		String titulo,
		String nomePessoa,
		String descricao,
		String cidade,
		String regiao,
		String faixaEtaria,
		LocalDate dataDesaparecimento,
		StatusCaso status,
		LocalDateTime atualizadoEm
) {
}
