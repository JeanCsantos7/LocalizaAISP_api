package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.model.HistoricoStatus;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long>, JpaSpecificationExecutor<HistoricoStatus> {


    @Override
    @EntityGraph(attributePaths = "responsavel")
    Page<HistoricoStatus> findAll(Specification<HistoricoStatus> spec, Pageable pageable);
}
