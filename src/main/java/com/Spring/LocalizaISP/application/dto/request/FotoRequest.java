package com.Spring.LocalizaISP.application.dto.request;

import java.util.UUID;

public record FotoRequest(
		UUID casoId,
		String url,
		String legenda,
		Boolean principal
) {
}
