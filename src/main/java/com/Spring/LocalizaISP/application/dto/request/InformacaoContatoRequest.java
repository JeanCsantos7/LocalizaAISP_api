package com.Spring.LocalizaISP.application.dto.request;

import java.util.UUID;

public record InformacaoContatoRequest(
		UUID casoId,
		String nome,
		String email,
		String telefone

) {
}
