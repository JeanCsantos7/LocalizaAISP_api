package com.Spring.LocalizaISP.application.service;

import com.Spring.LocalizaISP.application.dto.request.UsuarioAtualizacaoRequest;
import com.Spring.LocalizaISP.application.dto.request.UsuarioCadastroRequest;
import com.Spring.LocalizaISP.application.dto.response.UsuarioResponse;
import com.Spring.LocalizaISP.application.mapper.UsuarioMapper;
import com.Spring.LocalizaISP.domain.model.Usuario;
import com.Spring.LocalizaISP.infrastructure.exception.EmailExistente;
import com.Spring.LocalizaISP.infrastructure.exception.UsuarioNaoEncontrado;
import com.Spring.LocalizaISP.infrastructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UsuarioResponse save(UsuarioCadastroRequest dados) {

        Usuario usuario = usuarioMapper.toEntity(dados);
        String senhaHash = bCryptPasswordEncoder.encode(dados.senha());

        toExistEmail(dados.email());
        usuario.setSenha(senhaHash);
        usuario.setAtivo(true);

        usuario.setCriadoEm(LocalDateTime.now());

        Usuario salvarUsuario = usuarioRepository.save(usuario);


        return usuarioMapper.toDTO(salvarUsuario);


    }


    public Page<UsuarioResponse> findAll(Pageable pages) {

        Page<Usuario> usuarios = usuarioRepository.findAll(pages);
        return usuarios.map(usuarioMapper::toDTO);


    }

    public UsuarioResponse findById(Long id) {

        Usuario usuarioProcurado = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado(" Usuário não Localizado!"));


        return usuarioMapper.toDTO(usuarioProcurado);

    }

    public Usuario findByEmail(String email){



        Usuario usuarioBuscado = usuarioRepository.findByEmail(email);

        if(usuarioBuscado == null || usuarioBuscado.getAtivo() == false ){
            throw new UsuarioNaoEncontrado("Verifique suas credenciais ou se possui uma conta ativa!");
        }
        return usuarioRepository.findByEmail(email);



    }


    @Transactional
    public UsuarioResponse update(Long id, UsuarioAtualizacaoRequest dados) {





        Usuario usuarioProcurado = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não Localizado"));

        toExistEmail(dados.email());

        usuarioMapper.update(dados, usuarioProcurado);




        return usuarioMapper.toDTO(usuarioProcurado);


    }

    public Boolean toExistEmail(String email) {


        Boolean test = usuarioRepository.existsByEmail(email);

        if (test) {

            throw new EmailExistente("Email já possui uma conta!");

        }


        return false;


    }

    public void delete(Long id) {




        usuarioRepository.deleteById(id);


    }


}
