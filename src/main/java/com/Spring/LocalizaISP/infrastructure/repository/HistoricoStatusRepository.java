package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.model.HistoricoStatus;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {
}
