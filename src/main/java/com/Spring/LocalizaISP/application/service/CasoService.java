package com.Spring.LocalizaISP.application.service;

import com.Spring.LocalizaISP.application.dto.request.AtualizarStatusCasoRequest;
import com.Spring.LocalizaISP.application.dto.request.CasoRequest;
import com.Spring.LocalizaISP.application.dto.response.CasoPublicoResponse;
import com.Spring.LocalizaISP.application.dto.response.CasoResponse;
import com.Spring.LocalizaISP.application.mapper.CasoMapper;
import com.Spring.LocalizaISP.domain.enums.OrigemCaso;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;
import com.Spring.LocalizaISP.domain.enums.TipoUsuario;
import com.Spring.LocalizaISP.domain.model.Caso;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class CasoService {

    private static final int TAMANHO_MAXIMO_PAGINA_PUBLICA = 50;
    private static final int LIMITE_CAMPO_CURTO = 255;
    private static final int LIMITE_CAMPO_LONGO = 2000;


    private static final Map<StatusCaso, Set<StatusCaso>> TRANSICOES_PERMITIDAS = Map.of(
            StatusCaso.PENDENTE, EnumSet.of(StatusCaso.PUBLICADO, StatusCaso.REJEITADO),
            StatusCaso.PUBLICADO, EnumSet.of(StatusCaso.LOCALIZADO, StatusCaso.RETIRADO),
            StatusCaso.LOCALIZADO, EnumSet.of(StatusCaso.PUBLICADO, StatusCaso.RETIRADO),
            StatusCaso.REJEITADO, EnumSet.noneOf(StatusCaso.class),
            StatusCaso.RETIRADO, EnumSet.noneOf(StatusCaso.class)
    );


    private static final List<StatusCaso> STATUS_VISIVEIS = List.of(StatusCaso.PUBLICADO, StatusCaso.LOCALIZADO);


    private static final Set<StatusCaso> EXIGEM_JUSTIFICATIVA =
            EnumSet.of(StatusCaso.REJEITADO, StatusCaso.RETIRADO);

    private final CasoRepository casoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoStatusRepository historicoStatusRepository;
    private final CasoMapper casoMapper;


    @Transactional(readOnly = true)
    public Page<CasoPublicoResponse> buscarPublicados(String nome,
                                                      String cidade,
                                                      String regiao,
                                                      String faixaEtaria,
                                                      LocalDate dataInicio,
                                                      LocalDate dataFim,
                                                      Pageable pageable) {

        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final.");
        }

        Specification<Caso> filtro = (root, query, cb) -> root.get("status").in(STATUS_VISIVEIS);

        if (temTexto(nome)) {
            String padrao = padraoLike(nome);
            filtro = filtro.and((root, query, cb) -> cb.like(cb.lower(root.get("nomePessoa")), padrao, '\\'));
        }
        if (temTexto(cidade)) {
            String valor = cidade.trim().toLowerCase();
            filtro = filtro.and((root, query, cb) -> cb.equal(cb.lower(root.get("cidade")), valor));
        }
        if (temTexto(regiao)) {
            String valor = regiao.trim().toLowerCase();
            filtro = filtro.and((root, query, cb) -> cb.equal(cb.lower(root.get("regiao")), valor));
        }
        if (temTexto(faixaEtaria)) {
            String valor = faixaEtaria.trim().toLowerCase();
            filtro = filtro.and((root, query, cb) -> cb.equal(cb.lower(root.get("faixaEtaria")), valor));
        }
        if (dataInicio != null) {
            filtro = filtro.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.<LocalDate>get("dataDesaparecimento"), dataInicio));
        }
        if (dataFim != null) {
            filtro = filtro.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.<LocalDate>get("dataDesaparecimento"), dataFim));
        }

        return casoRepository.findAll(filtro, paginacaoPublica(pageable)).map(casoMapper::toPublicoDTO);
    }


    @Transactional(readOnly = true)
    public CasoPublicoResponse buscarPublicadoPorId(Long id) {
        Caso caso = casoRepository.findById(id)
                .filter(c -> STATUS_VISIVEIS.contains(c.getStatus()))
                .orElseThrow(() -> new CasoNaoEncontrado("Caso não localizado!"));

        return casoMapper.toPublicoDTO(caso);
    }


    @Transactional
    public CasoResponse criar(CasoRequest dados, Long usuarioId) {
        validarDados(dados);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()))
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não localizado ou inativo!"));

        Caso caso = casoMapper.toEntity(dados);
        normalizar(caso);

        LocalDateTime agora = LocalDateTime.now();
        caso.setStatus(StatusCaso.PENDENTE);
        caso.setOrigem(OrigemCaso.COMUNIDADE);
        caso.setUsuario(usuario);
        caso.setCriadoEm(agora);
        caso.setAtualizadoEm(agora);

        return casoMapper.toDTO(casoRepository.save(caso));
    }


    @Transactional(readOnly = true)
    public Page<CasoResponse> listarMeusCasos(Long usuarioId, Pageable pageable) {
        return casoRepository.findByUsuarioId(usuarioId, pageable).map(casoMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CasoResponse buscarCasoPrivado(Long casoId, Long usuarioId, boolean administrador) {
        Caso caso = buscarEntidade(casoId);

        boolean titular = caso.getUsuario() != null && Objects.equals(caso.getUsuario().getId(), usuarioId);
        if (!titular && !administrador) {
            throw new CasoNaoEncontrado("Caso não localizado!");
        }

        return casoMapper.toDTO(caso);
    }


    @Transactional(readOnly = true)
    public Page<CasoResponse> listarPorStatus(StatusCaso status, Pageable pageable) {
        Page<Caso> casos = (status == null)
                ? casoRepository.findAll(pageable)
                : casoRepository.findByStatus(status, pageable);

        return casos.map(casoMapper::toDTO);
    }


    @Transactional
    public CasoResponse atualizarStatus(Long casoId, AtualizarStatusCasoRequest dados, Long responsavelId) {
        Usuario responsavel = carregarAdministrador(responsavelId);

        if (dados == null || dados.status() == null) {
            throw new RegraNegocioException("Informe o novo status do caso.");
        }

        Caso caso = buscarEntidade(casoId);
        StatusCaso atual = caso.getStatus();

        StatusCaso novo = (dados.status() == StatusCaso.APROVADO) ? StatusCaso.PUBLICADO : dados.status();

        if (atual == novo) {
            throw new RegraNegocioException("O caso já está com o status " + novo + ".");
        }
        if (!TRANSICOES_PERMITIDAS.getOrDefault(atual, Set.of()).contains(novo)) {
            throw new RegraNegocioException(
                    "Transição de status não permitida: " + atual + " -> " + novo + ".");
        }

        String observacao = limpar(dados.observacao());
        if (EXIGEM_JUSTIFICATIVA.contains(novo) && observacao == null) {
            throw new RegraNegocioException("É obrigatório informar o motivo ao mudar o status para " + novo + ".");
        }
        validarTamanho(observacao, LIMITE_CAMPO_LONGO, "observacao");

        caso.setStatus(novo);
        caso.setAtualizadoEm(LocalDateTime.now());
        registrarHistorico(caso, atual, novo, observacao, responsavel);

        return casoMapper.toDTO(caso);
    }


    @Transactional
    public CasoResponse retirar(Long casoId, String motivo, Long responsavelId) {
        return atualizarStatus(casoId, new AtualizarStatusCasoRequest(StatusCaso.RETIRADO, motivo), responsavelId);
    }


    @Transactional
    public CasoResponse editar(Long casoId, CasoRequest dados, Long responsavelId) {
        Usuario responsavel = carregarAdministrador(responsavelId);
        validarDados(dados);

        Caso caso = buscarEntidade(casoId);
        if (caso.getStatus() == StatusCaso.REJEITADO || caso.getStatus() == StatusCaso.RETIRADO) {
            throw new RegraNegocioException("Casos " + caso.getStatus() + " não podem ser editados.");
        }

        List<String> alterados = camposAlterados(caso, dados);
        if (alterados.isEmpty()) {
            return casoMapper.toDTO(caso);
        }

        casoMapper.update(dados, caso);
        normalizar(caso);
        caso.setAtualizadoEm(LocalDateTime.now());

        registrarHistorico(caso, caso.getStatus(), caso.getStatus(),
                "Dados do caso editados. Campos alterados: " + String.join(", ", alterados), responsavel);

        return casoMapper.toDTO(caso);
    }



    private Caso buscarEntidade(Long id) {
        return casoRepository.findById(id)
                .orElseThrow(() -> new CasoNaoEncontrado("Caso não localizado!"));
    }


    private Usuario carregarAdministrador(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontrado("Usuário não localizado!"));

        if (usuario.getRole() != TipoUsuario.ADMIN || !Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new AccessDeniedException("Apenas administradores podem executar esta operação.");
        }
        return usuario;
    }

    private void registrarHistorico(Caso caso, StatusCaso anterior, StatusCaso atual,
                                    String observacao, Usuario responsavel) {
        HistoricoStatus historico = new HistoricoStatus();
        historico.setCaso(caso);
        historico.setStatusAnterior(anterior);
        historico.setStatusAtual(atual);
        historico.setObservacao(observacao);
        historico.setResponsavel(responsavel);
        historico.setDataAlteracao(LocalDateTime.now());
        historicoStatusRepository.save(historico);
    }


    private void validarDados(CasoRequest dados) {
        if (dados == null) {
            throw new RegraNegocioException("Dados do caso não informados.");
        }
        exigirTexto(dados.titulo(), "titulo", LIMITE_CAMPO_CURTO);
        exigirTexto(dados.nomePessoa(), "nomePessoa", LIMITE_CAMPO_CURTO);
        exigirTexto(dados.descricao(), "descricao", LIMITE_CAMPO_LONGO);
        exigirTexto(dados.cidade(), "cidade", LIMITE_CAMPO_CURTO);
        exigirTexto(dados.regiao(), "regiao", LIMITE_CAMPO_CURTO);
        exigirTexto(dados.faixaEtaria(), "faixaEtaria", LIMITE_CAMPO_CURTO);
        exigirTexto(dados.fonteAutorizacao(), "fonteAutorizacao", LIMITE_CAMPO_CURTO);
        validarTamanho(limpar(dados.observacoes()), LIMITE_CAMPO_LONGO, "observacoes");

        if (dados.dataDesaparecimento() == null) {
            throw new RegraNegocioException("O campo dataDesaparecimento é obrigatório.");
        }
        if (dados.dataDesaparecimento().isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data do desaparecimento não pode estar no futuro.");
        }
    }

    private void exigirTexto(String valor, String campo, int limite) {
        if (!temTexto(valor)) {
            throw new RegraNegocioException("O campo " + campo + " é obrigatório.");
        }
        validarTamanho(valor.trim(), limite, campo);
    }

    private void validarTamanho(String valor, int limite, String campo) {
        if (valor != null && valor.length() > limite) {
            throw new RegraNegocioException("O campo " + campo + " excede " + limite + " caracteres.");
        }
    }

    private void normalizar(Caso caso) {
        caso.setTitulo(limpar(caso.getTitulo()));
        caso.setNomePessoa(limpar(caso.getNomePessoa()));
        caso.setCidade(limpar(caso.getCidade()));
        caso.setFonteAutorizacao(limpar(caso.getFonteAutorizacao()));
        caso.setDescricao(limpar(caso.getDescricao()));
        caso.setRegiao(limpar(caso.getRegiao()));
        caso.setFaixaEtaria(limpar(caso.getFaixaEtaria()));
        caso.setObservacoes(limpar(caso.getObservacoes()));
    }

    private List<String> camposAlterados(Caso caso, CasoRequest dados) {
        List<String> campos = new ArrayList<>();
        if (!Objects.equals(caso.getTitulo(), limpar(dados.titulo()))) campos.add("titulo");
        if (!Objects.equals(caso.getNomePessoa(), limpar(dados.nomePessoa()))) campos.add("nomePessoa");
        if (!Objects.equals(caso.getCidade(), limpar(dados.cidade()))) campos.add("cidade");
        if (!Objects.equals(caso.getFonteAutorizacao(), limpar(dados.fonteAutorizacao()))) campos.add("fonteAutorizacao");
        if (!Objects.equals(caso.getDescricao(), limpar(dados.descricao()))) campos.add("descricao");
        if (!Objects.equals(caso.getRegiao(), limpar(dados.regiao()))) campos.add("regiao");
        if (!Objects.equals(caso.getFaixaEtaria(), limpar(dados.faixaEtaria()))) campos.add("faixaEtaria");
        if (!Objects.equals(caso.getDataDesaparecimento(), dados.dataDesaparecimento())) campos.add("dataDesaparecimento");
        if (!Objects.equals(caso.getObservacoes(), limpar(dados.observacoes()))) campos.add("observacoes");
        return campos;
    }


    private String limpar(String valor) {
        if (valor == null) {
            return null;
        }
        String aparado = valor.trim();
        return aparado.isEmpty() ? null : aparado;
    }

    private boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }


    private String padraoLike(String texto) {
        String seguro = texto.trim().toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + seguro + "%";
    }

    private Pageable paginacaoPublica(Pageable pageable) {
        int tamanho = Math.min(pageable.getPageSize(), TAMANHO_MAXIMO_PAGINA_PUBLICA);
        return PageRequest.of(pageable.getPageNumber(), tamanho, Sort.by(Sort.Direction.DESC, "dataDesaparecimento"));
    }
}