package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.model.Caso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CasoRepository extends JpaRepository<Caso, Long> {
}
