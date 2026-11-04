package com.Spring.LocalizaISP.application.dto.exceptionsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude
public record ErrosDTO(
        String mensagem,
        Integer statusCode,
        LocalDateTime data,
        Map<String, String> erros

) {
    public ErrosDTO(String mensagem, Integer statusCode) {

        this(mensagem, statusCode, LocalDateTime.now(), null);


    }
}
