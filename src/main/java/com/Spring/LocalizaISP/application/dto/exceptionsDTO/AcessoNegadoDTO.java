package com.Spring.LocalizaISP.application.dto.exceptionsDTO;

import java.time.LocalDateTime;

public record AcessoNegadoDTO(
        String mensagem,
        Integer statusCode,
        LocalDateTime data
) {
}
