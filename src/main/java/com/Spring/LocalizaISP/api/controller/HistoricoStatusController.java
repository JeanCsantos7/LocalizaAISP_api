package com.Spring.LocalizaISP.api.controller;

import com.Spring.LocalizaISP.application.dto.response.HistoricoStatusResponse;
import com.Spring.LocalizaISP.application.service.HistoricoStatusService;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;
import com.Spring.LocalizaISP.infrastructure.config.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@RestController
@RequestMapping("/historico")
@RequiredArgsConstructor
public class HistoricoStatusController {

    private final HistoricoStatusService historicoStatusService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<HistoricoStatusResponse>> listar(
            @RequestParam(required = false) Long casoId,
            @RequestParam(required = false) StatusCaso status,
            @RequestParam(required = false) Long responsavelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "false") boolean apenasMudancasDeStatus,
            @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal,
            Pageable pageable) {

        return ResponseEntity.ok(historicoStatusService.listar(principal.getId(), casoId, status, responsavelId,
                dataInicio, dataFim, apenasMudancasDeStatus, pageable));
    }


    @GetMapping("/caso/{casoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<HistoricoStatusResponse>> listarPorCaso(
            @PathVariable Long casoId,
            @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal,
            Pageable pageable) {

        return ResponseEntity.ok(historicoStatusService.listarPorCaso(casoId, principal.getId(), pageable));
    }

}
