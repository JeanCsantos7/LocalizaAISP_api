package com.Spring.LocalizaISP.api.controller;

import com.Spring.LocalizaISP.application.dto.request.RespostaSolicitacaoRequest;
import com.Spring.LocalizaISP.application.dto.request.SolicitacaoRequest;
import com.Spring.LocalizaISP.application.dto.response.SolicitacaoResponse;
import com.Spring.LocalizaISP.application.service.SolicitacaoService;
import com.Spring.LocalizaISP.domain.enums.StatusSolicitacao;
import com.Spring.LocalizaISP.infrastructure.config.UserDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * /solicitacoes/minhas e POST /solicitacoes: usuário autenticado (só as próprias).
 * /solicitacoes/admin/**: somente ADMIN.
 * O id do usuário vem sempre do token, nunca do corpo da requisição.
 */
@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    // ------------------------------------------------------- usuário autenticado

    @PostMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<SolicitacaoResponse> criar(@RequestBody @Valid SolicitacaoRequest dados,
                                                     @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoService.criar(dados, principal.getId()));
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<Page<SolicitacaoResponse>> listarMinhas(
            @RequestParam(required = false) StatusSolicitacao status,
            @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal,
            Pageable pageable) {

        return ResponseEntity.ok(solicitacaoService.listarMinhas(principal.getId(), status, pageable));
    }

    @GetMapping("/minhas/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<SolicitacaoResponse> buscarMinha(@PathVariable Long id,
                                                           @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(solicitacaoService.buscarMinha(id, principal.getId()));
    }

    // ------------------------------------------------------------ administrador

    /** Fila priorizada (retirada > correção > informação). Sem status, lista só as abertas. */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SolicitacaoResponse>> listarFila(
            @RequestParam(required = false) StatusSolicitacao status,
            @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal,
            Pageable pageable) {

        return ResponseEntity.ok(solicitacaoService.listarFila(status, principal.getId(), pageable));
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponse> buscarPorIdAdmin(@PathVariable Long id,
                                                                @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(solicitacaoService.buscarPorIdAdmin(id, principal.getId()));
    }

    @GetMapping("/admin/caso/{casoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SolicitacaoResponse>> listarPorCaso(@PathVariable Long casoId,
                                                                   @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal,
                                                                   Pageable pageable) {

        return ResponseEntity.ok(solicitacaoService.listarPorCaso(casoId, principal.getId(), pageable));
    }

    @PatchMapping("/admin/{id}/analise")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponse> iniciarAnalise(@PathVariable Long id,
                                                              @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(solicitacaoService.iniciarAnalise(id, principal.getId()));
    }

    /** Corpo opcional. Aprovar RETIRADA retira o caso; CORRECAO e INFORMACAO_ADICIONAL só registram o aceite. */
    @PatchMapping("/admin/{id}/aprovar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponse> aprovar(@PathVariable Long id,
                                                       @RequestBody(required = false) @Valid RespostaSolicitacaoRequest dados,
                                                       @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        String resposta = (dados == null) ? null : dados.resposta();
        return ResponseEntity.ok(solicitacaoService.aprovar(id, resposta, principal.getId()));
    }

    /** O motivo (resposta) é obrigatório na rejeição. */
    @PatchMapping("/admin/{id}/rejeitar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolicitacaoResponse> rejeitar(@PathVariable Long id,
                                                        @RequestBody(required = false) @Valid RespostaSolicitacaoRequest dados,
                                                        @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        String resposta = (dados == null) ? null : dados.resposta();
        return ResponseEntity.ok(solicitacaoService.rejeitar(id, resposta, principal.getId()));
    }

}
