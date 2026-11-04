package com.Spring.LocalizaISP.application.dto.request;

import java.util.UUID;

public record FotoRequest(
		Long casoId,
		String url,
		String legenda,
		Boolean principal
) {
}
