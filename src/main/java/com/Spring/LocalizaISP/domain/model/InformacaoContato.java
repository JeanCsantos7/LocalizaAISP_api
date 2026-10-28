package com.Spring.LocalizaISP.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "informacoes_contato")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InformacaoContato {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String nome;

	private String email;

	private String telefone;

	@Column(length = 2000)
	private String mensagem;

	private LocalDateTime criadoEm;



	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "caso_id", nullable = false)
	private Caso caso;

	// Opcional: visitantes tambem podem enviar informacoes
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

}
