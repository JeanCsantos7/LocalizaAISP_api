package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.model.InformacaoContato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InformacaoContatoRepository extends JpaRepository<InformacaoContato, Long> {
}
