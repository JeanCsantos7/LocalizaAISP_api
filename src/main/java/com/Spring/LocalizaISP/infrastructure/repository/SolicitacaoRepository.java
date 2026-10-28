package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
}
