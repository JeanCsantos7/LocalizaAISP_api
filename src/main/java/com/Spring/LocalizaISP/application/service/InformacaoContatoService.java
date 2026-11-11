package com.Spring.LocalizaISP.application.service;

import com.Spring.LocalizaISP.application.dto.request.InformacaoContatoRequest;
import com.Spring.LocalizaISP.application.dto.response.InformacaoContatoResponse;
import com.Spring.LocalizaISP.application.mapper.InformacaoContatoMapper;
import com.Spring.LocalizaISP.domain.model.Caso;
import com.Spring.LocalizaISP.domain.model.InformacaoContato;
import com.Spring.LocalizaISP.infrastructure.exception.CasoNaoEncontrado;
import com.Spring.LocalizaISP.infrastructure.exception.RegraNegocioException;
import com.Spring.LocalizaISP.infrastructure.repository.CasoRepository;
import com.Spring.LocalizaISP.infrastructure.repository.InformacaoContatoRepository;
import com.Spring.LocalizaISP.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InformacaoContatoService {

    private final InformacaoContatoRepository informacaoContatoRepository;
    private final CasoRepository casoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InformacaoContatoMapper informacaoContatoMapper;

    @Transactional
    public InformacaoContatoResponse salvarInformacoes(InformacaoContatoRequest dto, Long usuarioId, boolean administrador) {
        if (dto.casoId() == null) {
            throw new RegraNegocioException("Informe o caso ao qual o contato pertence.");
        }

        Caso caso = casoRepository.findById(dto.casoId())
                .filter(c -> administrador || c.getUsuario().getId().equals(usuarioId))
                .orElseThrow(() -> new CasoNaoEncontrado("Caso não localizado!"));

        InformacaoContato contato = informacaoContatoMapper.toEntity(dto);
        contato.setCaso(caso);
        contato.setUsuario(usuarioRepository.getReferenceById(usuarioId));
        contato.setCriadoEm(LocalDateTime.now());

        return informacaoContatoMapper.toDTO(informacaoContatoRepository.save(contato));
    }

    @Transactional(readOnly = true)
    public Page<InformacaoContatoResponse> listarTodosContatos(Pageable pageable) {
        return informacaoContatoRepository.findAll(pageable).map(informacaoContatoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public InformacaoContatoResponse listarPorID(Long id, Long usuarioId, boolean administrador) {
        return informacaoContatoMapper.toDTO(buscarDoUsuario(id, usuarioId, administrador));
    }

    @Transactional
    public InformacaoContatoResponse editarInformacoes(InformacaoContatoRequest dto, Long id, Long usuarioId, boolean administrador) {
        InformacaoContato contato = buscarDoUsuario(id, usuarioId, administrador);
        informacaoContatoMapper.update(dto, contato);

        return informacaoContatoMapper.toDTO(contato);
    }

    @Transactional
    public void delete(Long id, Long usuarioId, boolean administrador) {
        informacaoContatoRepository.delete(buscarDoUsuario(id, usuarioId, administrador));
    }

    private InformacaoContato buscarDoUsuario(Long id, Long usuarioId, boolean administrador) {
        return informacaoContatoRepository.findById(id)
                .filter(c -> administrador || c.getUsuario().getId().equals(usuarioId))
                .orElseThrow(() -> new CasoNaoEncontrado("Contato não localizado!"));
    }

}
