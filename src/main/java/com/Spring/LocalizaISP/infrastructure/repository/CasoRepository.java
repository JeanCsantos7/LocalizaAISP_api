package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.enums.StatusCaso;
import com.Spring.LocalizaISP.domain.model.Caso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CasoRepository extends JpaRepository<Caso, Long>, JpaSpecificationExecutor<Caso> {

    Page<Caso> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Caso> findByStatus(StatusCaso status, Pageable pageable);

}
