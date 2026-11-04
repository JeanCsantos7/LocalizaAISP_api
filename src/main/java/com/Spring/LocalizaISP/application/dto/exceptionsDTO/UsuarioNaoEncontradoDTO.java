package com.Spring.LocalizaISP.application.dto.exceptionsDTO;

import java.time.LocalDateTime;

public record UsuarioNaoEncontradoDTO(

        String mensagem,
        Integer statusCode,
        LocalDateTime data

) {
}
