package com.Spring.LocalizaISP.application.dto.response;

import java.util.UUID;

public record InformacaoContatoResponse(
		UUID id,
		String nome,
		String email,
		String telefone,
		UUID casoId

) {
}
