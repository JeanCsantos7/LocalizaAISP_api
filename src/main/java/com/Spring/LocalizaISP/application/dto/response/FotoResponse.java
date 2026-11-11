package com.Spring.LocalizaISP.application.dto.response;

import java.time.LocalDateTime;

public record FotoResponse(
		Long id,
		String url,
		String legenda,
		Boolean principal,
		LocalDateTime criadoEm,
		Long casoId
) {
}
