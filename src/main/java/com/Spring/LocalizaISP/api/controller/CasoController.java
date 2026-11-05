package com.Spring.LocalizaISP.api.controller;

import com.Spring.LocalizaISP.application.dto.request.AtualizarStatusCasoRequest;
import com.Spring.LocalizaISP.application.dto.request.CasoRequest;
import com.Spring.LocalizaISP.application.dto.response.CasoPublicoResponse;
import com.Spring.LocalizaISP.application.dto.response.CasoResponse;
import com.Spring.LocalizaISP.application.service.CasoService;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;
import com.Spring.LocalizaISP.infrastructure.config.UserDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@RestController
@RequestMapping("/casos")
@RequiredArgsConstructor
public class CasoController {

    private final CasoService casoService;



    @GetMapping
    public ResponseEntity<Page<CasoPublicoResponse>> buscarPublicados(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String regiao,
            @RequestParam(required = false) String faixaEtaria,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Pageable pageable) {

        return ResponseEntity.ok(
                casoService.buscarPublicados(nome, cidade, regiao, faixaEtaria, dataInicio, dataFim, pageable));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<CasoPublicoResponse> buscarPublicadoPorId(@PathVariable Long id) {

        return ResponseEntity.ok(casoService.buscarPublicadoPorId(id));
    }



    @PostMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<CasoResponse> criar(@RequestBody @Valid CasoRequest dados,
                                              @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED).body(casoService.criar(dados, principal.getId()));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<Page<CasoResponse>> listarMeusCasos(
            @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal,
            Pageable pageable) {

        return ResponseEntity.ok(casoService.listarMeusCasos(principal.getId(), pageable));
    }

    @GetMapping("/meus/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<CasoResponse> buscarMeuCaso(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {


        return ResponseEntity.ok(casoService.buscarCasoPrivado(id, principal.getId(), false));
    }



    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CasoResponse>> listarPorStatus(
            @RequestParam(required = false) StatusCaso status,
            Pageable pageable) {

        return ResponseEntity.ok(casoService.listarPorStatus(status, pageable));
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CasoResponse> buscarPorIdAdmin(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(casoService.buscarCasoPrivado(id, principal.getId(), true));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CasoResponse> editar(@PathVariable Long id,
                                               @RequestBody @Valid CasoRequest dados,
                                               @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(casoService.editar(id, dados, principal.getId()));
    }


    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CasoResponse> atualizarStatus(@PathVariable Long id,
                                                        @RequestBody @Valid AtualizarStatusCasoRequest dados,
                                                        @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(casoService.atualizarStatus(id, dados, principal.getId()));
    }

}
