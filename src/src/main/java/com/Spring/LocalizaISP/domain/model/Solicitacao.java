package com.Spring.LocalizaISP.domain.model;

import com.Spring.LocalizaISP.domain.enums.StatusSolicitacao;
import com.Spring.LocalizaISP.domain.enums.TipoSolicitacao;
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
@Table(name = "solicitacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Solicitacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private TipoSolicitacao tipo;

	@Column(length = 2000)
	private String motivo;

	@Column(length = 2000)
	private String descricao;

	@Enumerated(EnumType.STRING)
	private StatusSolicitacao status;

	private LocalDateTime criadoEm;

	private LocalDateTime respondidoEm;

	@Column(length = 2000)
	private String resposta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsavel_id")
	private Usuario responsavel;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "caso_id", nullable = false)
	private Caso caso;

}
