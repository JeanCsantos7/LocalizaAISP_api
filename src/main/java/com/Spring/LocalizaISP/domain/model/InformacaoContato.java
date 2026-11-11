package com.Spring.LocalizaISP.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "informacoes_contato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InformacaoContato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String email;

	private String telefone;

	@Column(length = 2000)
	private String mensagem;

	private LocalDateTime criadoEm;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "caso_id", nullable = false)
	private Caso caso;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

}
