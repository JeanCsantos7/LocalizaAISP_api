package com.Spring.LocalizaISP.application.dto.response;

public record InformacaoContatoResponse(
		Long id,
		String nome,
		String email,
		String telefone,
		Long casoId
) {
}
