package com.Spring.LocalizaISP.application.service;

import com.Spring.LocalizaISP.application.dto.response.HistoricoStatusResponse;
import com.Spring.LocalizaISP.application.mapper.HistoricoStatusMapper;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;
import com.Spring.LocalizaISP.domain.enums.TipoUsuario;
import com.Spring.LocalizaISP.domain.model.HistoricoStatus;
import com.Spring.LocalizaISP.domain.model.Usuario;
import com.Spring.LocalizaISP.infrastructure.exception.CasoNaoEncontrado;
import com.Spring.LocalizaISP.infrastructure.exception.RegraNegocioException;
import com.Spring.LocalizaISP.infrastructure.exception.UsuarioNaoEncontrado;
import com.Spring.LocalizaISP.infrastructure.repository.CasoRepository;
import com.Spring.LocalizaISP.infrastructure.repository.HistoricoStatusRepository;
import com.Spring.LocalizaISP.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class HistoricoStatusService {

    private static final int TAMANHO_MAXIMO_PAGINA = 50;

    private final HistoricoStatusRepository historicoStatusRepository;
    private final CasoRepository casoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoStatusMapper historicoStatusMapper;



    @Transactional(readOnly = true)
    public Page<HistoricoStatusResponse> listarPorCaso(Long casoId, Long adminId, Pageable pageable) {
        carregarAdministrador(adminId);

        if (!casoRepository.existsById(casoId)) {
            throw new CasoNaoEncontrado("Caso não localizado!");
        }

        return consultar(casoId, null, null, null, null, false, pageable);
    }

    @Transactional(readOnly = true)
    public Page<HistoricoStatusResponse> listar(Long adminId,
                                                Long casoId,
                                                StatusCaso statusAtual,
                                                Long responsavelId,
                                                LocalDate dataInicio,
                                                LocalDate dataFim,
                                                boolean apenasMudancasDeStatus,
                                                Pageable pageable) {
        carregarAdministrador(adminId);

        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final.");
        }

        return consultar(casoId, statusAtual, responsavelId, dataInicio, dataFim, apenasMudancasDeStatus, pageable);
    }


    private Page<HistoricoStatusResponse> consultar(Long casoId,
                                                    StatusCaso statusAtual,
                                                    Long responsavelId,
                                                    LocalDate dataInicio,
                                                    LocalDate dataFim,
                                                    boolean apenasMudancasDeStatus,
                                                    Pageable pageable) {

        Specification<HistoricoStatus> filtro = (root, query, cb) -> cb.conjunction();

        if (casoId != null) {
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("caso").get("id"), casoId));
        }
        if (statusAtual != null) {
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("statusAtual"), statusAtual));
        }
        if (responsavelId != null) {
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("responsavel").get("id"), responsavelId));
        }
        if (dataInicio != null) {
            LocalDateTime inicio = dataInicio.atStartOfDay();
            filtro = filtro.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.<LocalDateTime>get("dataAlteracao"), inicio));
        }
        if (dataFim != null) {

            LocalDateTime fim = dataFim.plusDays(1).atStartOfDay();
            filtro = filtro.and((root, query, cb) -> cb.lessThan(root.<LocalDateTime>get("dataAlteracao"), fim));
        }
        if (apenasMudancasDeStatus) {
            filtro = filtro.and((root, query, cb) -> cb.notEqual(root.get("statusAnterior"), root.get("statusAtual")));
        }

        return historicoStatusRepository.findAll(filtro, paginacao(pageable)).map(historicoStatusMapper::toDTO);
    }


    private Usuario carregarAdministrador(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não localizado!"));

        if (usuario.getRole() != TipoUsuario.ADMIN || !Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new AccessDeniedException("Apenas administradores podem executar esta operação.");
        }
        return usuario;
    }


    private Pageable paginacao(Pageable pageable) {
        int tamanho = Math.min(pageable.getPageSize(), TAMANHO_MAXIMO_PAGINA);
        Sort ordem = Sort.by(Sort.Direction.DESC, "dataAlteracao").and(Sort.by(Sort.Direction.DESC, "id"));
        return PageRequest.of(pageable.getPageNumber(), tamanho, ordem);
    }
}
