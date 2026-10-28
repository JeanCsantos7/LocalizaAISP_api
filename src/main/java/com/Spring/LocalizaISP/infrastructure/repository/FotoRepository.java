package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FotoRepository extends JpaRepository<Foto, Long> {
}
