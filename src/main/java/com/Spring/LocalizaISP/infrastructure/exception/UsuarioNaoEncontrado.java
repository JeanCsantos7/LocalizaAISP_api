package com.Spring.LocalizaISP.infrastructure.exception;

public class UsuarioNaoEncontrado extends RuntimeException {
    public UsuarioNaoEncontrado(String message) {
        super(message);
    }
}
