package com.Spring.LocalizaISP.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record FotoResponse(
		UUID id,
		String url,
		String legenda,
		Boolean principal,
		LocalDateTime criadoEm,
		UUID casoId
) {
}
