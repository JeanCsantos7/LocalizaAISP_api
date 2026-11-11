package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FotoRepository extends JpaRepository<Foto, Long> {

    long countByCasoId(Long casoId);

    List<Foto> findByCasoIdOrderByPrincipalDescIdAsc(Long casoId);

    Optional<Foto> findFirstByCasoIdAndIdNotOrderByIdAsc(Long casoId, Long id);
}
