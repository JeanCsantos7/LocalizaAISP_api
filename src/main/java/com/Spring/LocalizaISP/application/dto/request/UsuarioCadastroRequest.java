package com.Spring.LocalizaISP.application.dto.request;

public record UsuarioCadastroRequest(
		String nome,
		String email,
		String senha
) {
}
