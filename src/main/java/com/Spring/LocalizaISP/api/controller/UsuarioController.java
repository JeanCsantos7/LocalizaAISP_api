package com.Spring.LocalizaISP.api.controller;

import com.Spring.LocalizaISP.application.dto.request.LoginRequest;
import com.Spring.LocalizaISP.application.dto.request.UsuarioAtualizacaoRequest;
import com.Spring.LocalizaISP.application.dto.request.UsuarioCadastroRequest;
import com.Spring.LocalizaISP.application.dto.response.UsuarioResponse;
import com.Spring.LocalizaISP.application.service.UsuarioService;

import com.Spring.LocalizaISP.infrastructure.config.JWTService;

import com.Spring.LocalizaISP.infrastructure.config.UserDetail;
import com.Spring.LocalizaISP.infrastructure.exception.UsuarioNaoEncontrado;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JWTService JWTService;
    private final AuthenticationManager authenticationManager;
    private final UserDetail userDetail;


    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest dados) throws UsuarioNaoEncontrado {


        UserDetail.UsuarioPrincipal principal = (UserDetail.UsuarioPrincipal) userDetail.loadUserByUsername(dados.email());


        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dados.email(), dados.senha()));

        usuarioService.findByEmail(dados.email());


        return JWTService.generateJWT(dados.email(), principal.getAuthorities().toString());
    }


    @PostMapping("/criarConta")
    public ResponseEntity<UsuarioResponse> save(@RequestBody @Valid UsuarioCadastroRequest dados) {

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(dados));

    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> findAll(Pageable page) {


        return ResponseEntity.ok(usuarioService.findAll(page));

    }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/buscarUsuario/{id}")
    public ResponseEntity<UsuarioResponse> findByEmail(@PathVariable Long id) {


        return ResponseEntity.ok(usuarioService.findById(id));

    }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @PutMapping("/editarConta/{id}")
    public ResponseEntity<UsuarioResponse> update(@PathVariable Long id, @RequestBody @Valid UsuarioAtualizacaoRequest dados) {

        return ResponseEntity.ok(usuarioService.update(id, dados));

    }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @DeleteMapping("/excluirConta/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        usuarioService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }


}
