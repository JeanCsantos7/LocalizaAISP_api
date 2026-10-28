package com.Spring.LocalizaISP.application.dto.request;

import java.time.LocalDate;

public record CasoRequest(
		String titulo,
		String descricao,
	    String regiao,
		String faixaEtaria,
		LocalDate dataDesaparecimento,
		String observacoes
) {
}
