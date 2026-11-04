package com.Spring.LocalizaISP.application.dto.exceptionsDTO;

import java.time.LocalDateTime;

public record EmailExistenteDTO(
        String mensagem,
        Integer statusCode,
        LocalDateTime data
){}
