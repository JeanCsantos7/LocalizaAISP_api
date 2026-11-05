package com.Spring.LocalizaISP.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CasoRequest(
		@NotBlank(message = "O título é obrigatório")
		@Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
		String titulo,

		@NotBlank(message = "O nome da pessoa é obrigatório")
		@Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
		String nomePessoa,

		@NotBlank(message = "A descrição é obrigatória")
		@Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
		String descricao,

		@NotBlank(message = "A cidade é obrigatória")
		@Size(max = 255, message = "A cidade deve ter no máximo 255 caracteres")
		String cidade,

		@NotBlank(message = "A região é obrigatória")
		@Size(max = 255, message = "A região deve ter no máximo 255 caracteres")
		String regiao,

		@NotBlank(message = "A faixa etária é obrigatória")
		@Size(max = 255, message = "A faixa etária deve ter no máximo 255 caracteres")
		String faixaEtaria,

		@NotNull(message = "A data do desaparecimento é obrigatória")
		@PastOrPresent(message = "A data do desaparecimento não pode estar no futuro")
		LocalDate dataDesaparecimento,

		@NotBlank(message = "Informe a fonte da informação ou a autorização de divulgação")
		@Size(max = 255, message = "A fonte/autorização deve ter no máximo 255 caracteres")
		String fonteAutorizacao,

		@Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres")
		String observacoes
) {
}
