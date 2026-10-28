package com.Spring.LocalizaISP.application.dto.request;

import com.Spring.LocalizaISP.domain.enums.StatusCaso;

public record AtualizarStatusCasoRequest(
		StatusCaso status,
		String observacao
) {
}
