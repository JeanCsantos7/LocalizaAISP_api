package com.Spring.LocalizaISP.domain.model;

import com.Spring.LocalizaISP.domain.enums.OrigemCaso;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "casos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Caso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String titulo;

	@Column(length = 2000)
	private String descricao;

	private String regiao;

	private String faixaEtaria;

	private LocalDate dataDesaparecimento;

	@Enumerated(EnumType.STRING)
	private StatusCaso status;

	@Enumerated(EnumType.STRING)
	private OrigemCaso origem;

	@Column(length = 2000)
	private String observacoes;

	private LocalDateTime criadoEm;

	private LocalDateTime atualizadoEm;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@OneToMany(mappedBy = "caso")
	private List<Foto> fotos = new ArrayList<>();

	@OneToMany(mappedBy = "caso")
	private List<HistoricoStatus> historicoStatus = new ArrayList<>();

	@OneToMany(mappedBy = "caso")
	private List<Solicitacao> solicitacoes = new ArrayList<>();

	@OneToMany(mappedBy = "caso")
	private List<InformacaoContato> informacoesContato = new ArrayList<>();

}
