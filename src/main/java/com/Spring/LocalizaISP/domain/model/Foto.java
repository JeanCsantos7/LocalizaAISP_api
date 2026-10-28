package com.Spring.LocalizaISP.domain.model;

import jakarta.persistence.Entity;
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
@Table(name = "fotos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Foto {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	private String url;

	private String legenda;

	private Boolean principal;

	private LocalDateTime criadoEm;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "caso_id", nullable = false)
	private Caso caso;

}
