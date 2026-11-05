package com.Spring.LocalizaISP.application.dto.request;

import java.util.UUID;

public record InformacaoContatoRequest(
		Long casoId,
		String nome,
		String email,
		String telefone

) {
}
