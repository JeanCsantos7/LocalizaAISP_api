package com.Spring.LocalizaISP.application.dto.response;

import com.Spring.LocalizaISP.domain.enums.TipoUsuario;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoUsuario permissao,
        LocalDateTime criadoEm,
        Boolean ativo
) {
}
