package com.Spring.LocalizaISP.infrastructure.config;

import com.Spring.LocalizaISP.application.service.UsuarioService;
import com.Spring.LocalizaISP.domain.model.Usuario;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetail implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByEmail(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não localizado");
        }

        return new UsuarioPrincipal(usuario);


    }

    @Getter
    public static class UsuarioPrincipal extends User {

        private final Long id;

        public UsuarioPrincipal(Usuario usuario) {
            super(
                    usuario.getEmail(), usuario.getSenha(), List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole()))

            );

            this.id = usuario.getId();


        }


    }


}
