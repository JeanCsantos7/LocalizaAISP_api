package com.Spring.LocalizaISP.api.controller;

import com.Spring.LocalizaISP.application.dto.request.InformacaoContatoRequest;
import com.Spring.LocalizaISP.application.dto.response.InformacaoContatoResponse;
import com.Spring.LocalizaISP.application.service.InformacaoContatoService;
import com.Spring.LocalizaISP.infrastructure.config.UserDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contatos")
@PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
public class InformacaoContatoController {

    private final InformacaoContatoService informacaoContatoService;

    @PostMapping("/criarContato")
    public ResponseEntity<InformacaoContatoResponse> salvar(@RequestBody @Valid InformacaoContatoRequest dto,
                                                            @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(informacaoContatoService.salvarInformacoes(dto, principal.getId(), admin(principal)));
    }

    @GetMapping("/listarContatos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InformacaoContatoResponse>> listarTodos(Pageable pageable) {

        return ResponseEntity.ok(informacaoContatoService.listarTodosContatos(pageable));
    }

    @GetMapping("/listarContato/{id}")
    public ResponseEntity<InformacaoContatoResponse> listarPorID(@PathVariable Long id,
                                                                 @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(informacaoContatoService.listarPorID(id, principal.getId(), admin(principal)));
    }

    @PutMapping("/editarContato/{id}")
    public ResponseEntity<InformacaoContatoResponse> editarContato(@RequestBody @Valid InformacaoContatoRequest dto,
                                                                   @PathVariable Long id,
                                                                   @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(informacaoContatoService.editarInformacoes(dto, id, principal.getId(), admin(principal)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarContato(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {
        informacaoContatoService.delete(id, principal.getId(), admin(principal));
        return ResponseEntity.noContent().build();
    }

    private boolean admin(UserDetail.UsuarioPrincipal principal) {
        return principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

}
