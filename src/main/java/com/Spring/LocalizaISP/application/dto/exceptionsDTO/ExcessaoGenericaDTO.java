package com.Spring.LocalizaISP.application.dto.exceptionsDTO;

import java.time.LocalDateTime;

public record ExcessaoGenericaDTO(
        String mensagem,
        Integer statusCode,
        LocalDateTime data
) {
}
