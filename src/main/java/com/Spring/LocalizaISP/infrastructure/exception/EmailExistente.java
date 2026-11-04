package com.Spring.LocalizaISP.infrastructure.exception;

public class EmailExistente extends RuntimeException {
    public EmailExistente(String message) {
        super(message);
    }
}
