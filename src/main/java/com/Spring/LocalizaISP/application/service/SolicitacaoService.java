package com.Spring.LocalizaISP.application.service;

import com.Spring.LocalizaISP.application.dto.request.AtualizarStatusCasoRequest;
import com.Spring.LocalizaISP.application.dto.request.SolicitacaoRequest;
import com.Spring.LocalizaISP.application.dto.response.SolicitacaoResponse;
import com.Spring.LocalizaISP.application.mapper.SolicitacaoMapper;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;
import com.Spring.LocalizaISP.domain.enums.StatusSolicitacao;
import com.Spring.LocalizaISP.domain.enums.TipoSolicitacao;
import com.Spring.LocalizaISP.domain.enums.TipoUsuario;
import com.Spring.LocalizaISP.domain.model.Caso;
import com.Spring.LocalizaISP.domain.model.Solicitacao;
import com.Spring.LocalizaISP.domain.model.Usuario;
import com.Spring.LocalizaISP.infrastructure.exception.CasoNaoEncontrado;
import com.Spring.LocalizaISP.infrastructure.exception.RegraNegocioException;
import com.Spring.LocalizaISP.infrastructure.exception.SolicitacaoNaoEncontrada;
import com.Spring.LocalizaISP.infrastructure.exception.UsuarioNaoEncontrado;
import com.Spring.LocalizaISP.infrastructure.repository.CasoRepository;
import com.Spring.LocalizaISP.infrastructure.repository.SolicitacaoRepository;
import com.Spring.LocalizaISP.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private static final int TAMANHO_MAXIMO_PAGINA = 50;
    private static final int LIMITE_CAMPO_LONGO = 2000;


    private static final int LIMITE_RESPOSTA = 1800;


    private static final int MAXIMO_ABERTAS_POR_USUARIO = 5;


    private static final Set<StatusCaso> CASOS_SOLICITAVEIS = Set.of(StatusCaso.PUBLICADO, StatusCaso.LOCALIZADO);

    private static final List<StatusSolicitacao> ABERTAS = List.of(StatusSolicitacao.PENDENTE, StatusSolicitacao.EM_ANALISE);

    private final SolicitacaoRepository solicitacaoRepository;
    private final CasoRepository casoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CasoService casoService;
    private final SolicitacaoMapper solicitacaoMapper;




    @Transactional
    public SolicitacaoResponse criar(SolicitacaoRequest dados, Long usuarioId) {
        validarDados(dados);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não localizado ou inativo!"));

        Caso caso = casoRepository.findById(dados.casoId())
                .orElseThrow(() -> new CasoNaoEncontrado("Caso não localizado!"));

        boolean titular = caso.getUsuario() != null && Objects.equals(caso.getUsuario().getId(), usuarioId);
        validarElegibilidade(caso, dados.tipo(), titular);

        if (solicitacaoRepository.existsByUsuarioIdAndCasoIdAndTipoAndStatusIn(
                usuarioId, caso.getId(), dados.tipo(), ABERTAS)) {
            throw new RegraNegocioException("Você já possui uma solicitação desse tipo em aberto para este caso.");
        }
        if (solicitacaoRepository.countByUsuarioIdAndStatusIn(usuarioId, ABERTAS) >= MAXIMO_ABERTAS_POR_USUARIO) {
            throw new RegraNegocioException("Limite de solicitações em aberto atingido. Aguarde a análise das anteriores.");
        }

        Solicitacao solicitacao = solicitacaoMapper.toEntity(dados);
        solicitacao.setMotivo(limpar(dados.motivo()));
        solicitacao.setDescricao(limpar(dados.descricao()));


        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setCriadoEm(LocalDateTime.now());
        solicitacao.setRespondidoEm(null);
        solicitacao.setUsuario(usuario);
        solicitacao.setCaso(caso);

        Solicitacao salva = solicitacaoRepository.save(solicitacao);



        if (ocultaCaso(salva)) {
            casoService.registrarEvento(caso.getId(),
                    "Solicitação de retirada #" + salva.getId()
                            + " aberta pelo titular ou por administrador: caso oculto da consulta pública até a decisão.",
                    usuarioId);
        }

        return solicitacaoMapper.toDTO(salva);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponse> listarMinhas(Long usuarioId, StatusSolicitacao status, Pageable pageable) {
        Pageable pagina = paginacao(pageable, Sort.by(Sort.Direction.DESC, "criadoEm"));

        Page<Solicitacao> solicitacoes = (status == null)
                ? solicitacaoRepository.findByUsuarioId(usuarioId, pagina)
                : solicitacaoRepository.findByUsuarioIdAndStatus(usuarioId, status, pagina);

        return solicitacoes.map(solicitacaoMapper::toDTO);
    }


    @Transactional(readOnly = true)
    public SolicitacaoResponse buscarMinha(Long id, Long usuarioId) {
        return solicitacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .map(solicitacaoMapper::toDTO)
                .orElseThrow(() -> new SolicitacaoNaoEncontrada("Solicitação não localizada!"));
    }



    @Transactional(readOnly = true)
    public Page<SolicitacaoResponse> listarFila(StatusSolicitacao status, Long adminId, Pageable pageable) {
        carregarAdministrador(adminId);

        List<StatusSolicitacao> filtro = (status == null) ? ABERTAS : List.of(status);
        Pageable pagina = paginacao(pageable, Sort.unsorted());

        return solicitacaoRepository.buscarFila(filtro, pagina).map(solicitacaoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponse> listarPorCaso(Long casoId, Long adminId, Pageable pageable) {
        carregarAdministrador(adminId);

        Pageable pagina = paginacao(pageable, Sort.by(Sort.Direction.DESC, "criadoEm"));
        return solicitacaoRepository.findByCasoId(casoId, pagina).map(solicitacaoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponse buscarPorIdAdmin(Long id, Long adminId) {
        carregarAdministrador(adminId);
        return solicitacaoMapper.toDTO(buscarEntidade(id));
    }


    @Transactional
    public SolicitacaoResponse iniciarAnalise(Long id, Long adminId) {
        Usuario admin = carregarAdministrador(adminId);
        Solicitacao solicitacao = buscarEntidade(id);

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RegraNegocioException("Só é possível iniciar a análise de solicitações PENDENTES.");
        }

        solicitacao.setStatus(StatusSolicitacao.EM_ANALISE);
        solicitacao.setResponsavel(admin);

        return solicitacaoMapper.toDTO(solicitacao);
    }

    @Transactional
    public SolicitacaoResponse aprovar(Long id, String resposta, Long adminId) {
        return decidir(id, StatusSolicitacao.APROVADA, resposta, adminId);
    }

    @Transactional
    public SolicitacaoResponse rejeitar(Long id, String resposta, Long adminId) {
        return decidir(id, StatusSolicitacao.REJEITADA, resposta, adminId);
    }




    private SolicitacaoResponse decidir(Long id, StatusSolicitacao decisao, String resposta, Long adminId) {
        Usuario admin = carregarAdministrador(adminId);
        Solicitacao solicitacao = buscarEntidade(id);

        if (!ABERTAS.contains(solicitacao.getStatus())) {
            throw new RegraNegocioException("Esta solicitação já foi respondida (" + solicitacao.getStatus() + ").");
        }

        String texto = limpar(resposta);
        if (decisao == StatusSolicitacao.REJEITADA && texto == null) {
            throw new RegraNegocioException("É obrigatório informar o motivo da rejeição.");
        }
        validarTamanho(texto, LIMITE_RESPOSTA, "resposta");


        if (decisao == StatusSolicitacao.APROVADA && solicitacao.getTipo() == TipoSolicitacao.RETIRADA) {
            retirarCaso(solicitacao, texto, adminId);
        }

        solicitacao.setStatus(decisao);
        solicitacao.setResposta(texto);
        solicitacao.setResponsavel(admin);
        solicitacao.setRespondidoEm(LocalDateTime.now());

        if (decisao == StatusSolicitacao.REJEITADA && ocultaCaso(solicitacao)) {
            registrarFimDaOcultacao(solicitacao, adminId);
        }

        return solicitacaoMapper.toDTO(solicitacao);
    }



    private boolean ocultaCaso(Solicitacao solicitacao) {
        if (solicitacao.getTipo() != TipoSolicitacao.RETIRADA) {
            return false;
        }
        Usuario solicitante = solicitacao.getUsuario();
        Usuario titular = solicitacao.getCaso().getUsuario();

        boolean ehTitular = titular != null && Objects.equals(titular.getId(), solicitante.getId());
        return ehTitular || solicitante.getRole() == TipoUsuario.ADMIN;
    }

    private void registrarFimDaOcultacao(Solicitacao solicitacao, Long adminId) {
        Long casoId = solicitacao.getCaso().getId();
        boolean aindaOculto = solicitacaoRepository.existeRetiradaQueOculta(casoId, ABERTAS);

        String observacao = "Solicitação de retirada #" + solicitacao.getId() + " rejeitada: "
                + (aindaOculto
                ? "o caso segue oculto por outra solicitação de retirada em aberto."
                : "o caso volta a ser exibido na consulta pública.");

        casoService.registrarEvento(casoId, observacao, adminId);
    }

    private void retirarCaso(Solicitacao solicitacao, String resposta, Long adminId) {
        Caso caso = solicitacao.getCaso();


        if (!CASOS_SOLICITAVEIS.contains(caso.getStatus())) {
            return;
        }


        String observacao = "Retirada aprovada a partir da solicitação #" + solicitacao.getId()
                + (resposta != null ? ": " + resposta : ".");

        casoService.atualizarStatus(caso.getId(),
                new AtualizarStatusCasoRequest(StatusCaso.RETIRADO, observacao), adminId);
    }

    private Solicitacao buscarEntidade(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new SolicitacaoNaoEncontrada("Solicitação não localizada!"));
    }


    private Usuario carregarAdministrador(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não localizado!"));

        if (usuario.getRole() != TipoUsuario.ADMIN || !Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new AccessDeniedException("Apenas administradores podem executar esta operação.");
        }
        return usuario;
    }


    private void validarElegibilidade(Caso caso, TipoSolicitacao tipo, boolean titular) {
        StatusCaso status = caso.getStatus();

        if (CASOS_SOLICITAVEIS.contains(status)) {
            return;
        }
        if (!titular) {
            throw new CasoNaoEncontrado("Caso não localizado!");
        }
        if (status == StatusCaso.PENDENTE) {
            if (tipo != TipoSolicitacao.CORRECAO) {
                throw new RegraNegocioException("Enquanto está em análise, o caso só aceita solicitação de correção.");
            }
            return;
        }
        throw new RegraNegocioException("Casos " + status + " estão encerrados e não aceitam solicitações.");
    }

    private void validarDados(SolicitacaoRequest dados) {
        if (dados == null) {
            throw new RegraNegocioException("Dados da solicitação não informados.");
        }
        if (dados.casoId() == null) {
            throw new RegraNegocioException("O campo casoId é obrigatório.");
        }
        if (dados.tipo() == null) {
            throw new RegraNegocioException("O campo tipo é obrigatório.");
        }
        exigirTexto(dados.motivo(), "motivo");
        exigirTexto(dados.descricao(), "descricao");
    }

    private void exigirTexto(String valor, String campo) {
        String texto = limpar(valor);
        if (texto == null) {
            throw new RegraNegocioException("O campo " + campo + " é obrigatório.");
        }
        validarTamanho(texto, LIMITE_CAMPO_LONGO, campo);
    }

    private void validarTamanho(String valor, int limite, String campo) {
        if (valor != null && valor.length() > limite) {
            throw new RegraNegocioException("O campo " + campo + " excede " + limite + " caracteres.");
        }
    }

    private String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        String aparado = valor.trim();
        return aparado.isEmpty() ? null : aparado;
    }


    private Pageable paginacao(Pageable pageable, Sort ordenacao) {
        int tamanho = Math.min(pageable.getPageSize(), TAMANHO_MAXIMO_PAGINA);
        return PageRequest.of(pageable.getPageNumber(), tamanho, ordenacao);
    }
}
