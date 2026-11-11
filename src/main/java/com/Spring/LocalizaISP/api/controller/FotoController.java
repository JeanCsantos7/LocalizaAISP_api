package com.Spring.LocalizaISP.api.controller;

import com.Spring.LocalizaISP.application.dto.response.FotoResponse;
import com.Spring.LocalizaISP.application.service.FotoService;
import com.Spring.LocalizaISP.infrastructure.config.UserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fotos")
public class FotoController {

    private final FotoService fotoService;

    @PostMapping(value = "/caso/{casoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<FotoResponse> enviar(@PathVariable Long casoId,
                                               @RequestParam("file") MultipartFile arquivo,
                                               @RequestParam(required = false) String legenda,
                                               @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fotoService.enviar(casoId, arquivo, legenda, principal.getId(), admin(principal)));
    }

    @GetMapping("/caso/{casoId}")
    public ResponseEntity<List<FotoResponse>> listarPublicas(@PathVariable Long casoId) {

        return ResponseEntity.ok(fotoService.listarPublicas(casoId));
    }

    @GetMapping("/privado/caso/{casoId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<List<FotoResponse>> listarPrivadas(@PathVariable Long casoId,
                                                             @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {

        return ResponseEntity.ok(fotoService.listarPrivadas(casoId, principal.getId(), admin(principal)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    public ResponseEntity<Void> apagar(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetail.UsuarioPrincipal principal) {
        fotoService.apagar(id, principal.getId(), admin(principal));
        return ResponseEntity.noContent().build();
    }

    private boolean admin(UserDetail.UsuarioPrincipal principal) {
        return principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

}
