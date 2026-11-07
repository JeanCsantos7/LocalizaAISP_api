package com.Spring.LocalizaISP.application.dto.response;

import java.util.UUID;

public record InformacaoContatoResponse(
		Long id,
		String nome,
		String email,
		String telefone,
		Long casoId

) {
}
