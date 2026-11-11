package com.Spring.LocalizaISP.application.dto.response;

import com.Spring.LocalizaISP.domain.enums.OrigemCaso;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CasoResponse(
		Long id,
		String titulo,
		String nomePessoa,
		String descricao,
		String cidade,
		String regiao,
		String faixaEtaria,
		LocalDate dataDesaparecimento,
		StatusCaso status,
		OrigemCaso origem,
		String fonteAutorizacao,
		String observacoes,
		LocalDateTime criadoEm,
		LocalDateTime atualizadoEm,
		List<InformacaoContatoResponse> informacaoContatos,
		Long usuarioId
) {
}
