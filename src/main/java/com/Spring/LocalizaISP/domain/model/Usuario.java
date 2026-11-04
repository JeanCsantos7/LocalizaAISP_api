package com.Spring.LocalizaISP.domain.model;

import com.Spring.LocalizaISP.domain.enums.TipoUsuario;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	private String nome;

	private String email;

	private String senha;

	@Enumerated(EnumType.STRING)

	private TipoUsuario role = TipoUsuario.USUARIO;

	private LocalDateTime criadoEm;

	private Boolean ativo;

	@OneToMany(mappedBy = "usuario")
	private List<Caso> casos = new ArrayList<>();

	@OneToMany(mappedBy = "usuario")
	private List<Solicitacao> solicitacoes = new ArrayList<>();

	@OneToMany(mappedBy = "usuario")
	private List<InformacaoContato> informacoesContato = new ArrayList<>();


}
