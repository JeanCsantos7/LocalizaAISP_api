package com.Spring.LocalizaISP.application.service;

import com.Spring.LocalizaISP.application.dto.response.FotoResponse;
import com.Spring.LocalizaISP.domain.enums.StatusCaso;
import com.Spring.LocalizaISP.domain.model.Foto;
import com.Spring.LocalizaISP.infrastructure.exception.CasoNaoEncontrado;
import com.Spring.LocalizaISP.infrastructure.exception.RegraNegocioException;
import com.Spring.LocalizaISP.infrastructure.repository.CasoRepository;
import com.Spring.LocalizaISP.infrastructure.repository.FotoRepository;
import com.Spring.LocalizaISP.infrastructure.storage.SupabaseStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FotoService {

    private static final int MAX_FOTOS_POR_CASO = 5;
    private static final Map<String, String> EXTENSOES = Map.of("image/jpeg", "jpg", "image/png", "png", "image/webp", "webp");

    private final FotoRepository fotoRepository;
    private final CasoRepository casoRepository;
    private final CasoService casoService;
    private final SupabaseStorage storage;

    @Transactional
    public FotoResponse enviar(Long casoId, MultipartFile arquivo, String legenda, Long usuarioId, boolean administrador) {
        StatusCaso status = casoService.buscarCasoPrivado(casoId, usuarioId, administrador).status();
        if (status == StatusCaso.REJEITADO || status == StatusCaso.RETIRADO) {
            throw new RegraNegocioException("Não é possível adicionar fotos a um caso " + status + ".");
        }
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("Envie uma imagem.");
        }
        if (legenda != null && legenda.length() > 255) {
            throw new RegraNegocioException("A legenda deve ter no máximo 255 caracteres.");
        }

        long total = fotoRepository.countByCasoId(casoId);
        if (total >= MAX_FOTOS_POR_CASO) {
            throw new RegraNegocioException("Cada caso aceita no máximo " + MAX_FOTOS_POR_CASO + " fotos.");
        }

        byte[] conteudo = lerBytes(arquivo);
        String tipo = detectarTipo(conteudo);
        if (tipo == null) {
            throw new RegraNegocioException("Formato inválido. Envie uma imagem JPEG, PNG ou WebP.");
        }

        String caminho = "casos/" + casoId + "/" + UUID.randomUUID() + "." + EXTENSOES.get(tipo);
        storage.enviar(caminho, conteudo, tipo);

        Foto foto = new Foto();
        foto.setCaso(casoRepository.getReferenceById(casoId));
        foto.setUrl(caminho);
        foto.setLegenda(legenda);
        foto.setPrincipal(total == 0);
        foto.setCriadoEm(LocalDateTime.now());

        try {
            fotoRepository.saveAndFlush(foto);
        } catch (RuntimeException e) {
            storage.remover(caminho);
            throw e;
        }

        return resposta(foto);
    }

    @Transactional(readOnly = true)
    public List<FotoResponse> listarPublicas(Long casoId) {
        casoService.buscarPublicadoPorId(casoId);
        return listar(casoId);
    }

    @Transactional(readOnly = true)
    public List<FotoResponse> listarPrivadas(Long casoId, Long usuarioId, boolean administrador) {
        casoService.buscarCasoPrivado(casoId, usuarioId, administrador);
        return listar(casoId);
    }

    @Transactional
    public void apagar(Long id, Long usuarioId, boolean administrador) {
        Foto foto = fotoRepository.findById(id)
                .filter(f -> administrador || ehTitular(f, usuarioId))
                .orElseThrow(() -> new CasoNaoEncontrado("Foto não localizada!"));

        fotoRepository.delete(foto);

        if (Boolean.TRUE.equals(foto.getPrincipal())) {
            fotoRepository.findFirstByCasoIdAndIdNotOrderByIdAsc(foto.getCaso().getId(), id)
                    .ifPresent(proxima -> proxima.setPrincipal(true));
        }

        storage.remover(foto.getUrl());
    }

    private List<FotoResponse> listar(Long casoId) {
        return fotoRepository.findByCasoIdOrderByPrincipalDescIdAsc(casoId).stream().map(this::resposta).toList();
    }

    private FotoResponse resposta(Foto foto) {
        return new FotoResponse(foto.getId(), storage.urlAssinada(foto.getUrl()), foto.getLegenda(),
                foto.getPrincipal(), foto.getCriadoEm(), foto.getCaso().getId());
    }

    private boolean ehTitular(Foto foto, Long usuarioId) {
        return foto.getCaso().getUsuario() != null && foto.getCaso().getUsuario().getId().equals(usuarioId);
    }

    private byte[] lerBytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new RegraNegocioException("Não foi possível ler o arquivo enviado.");
        }
    }

    private static String detectarTipo(byte[] conteudo) {
        String inicio = new String(conteudo, 0, Math.min(conteudo.length, 12), StandardCharsets.ISO_8859_1);

        if (inicio.startsWith("\u00FF\u00D8\u00FF")) {
            return "image/jpeg";
        }
        if (inicio.startsWith("\u0089PNG\r\n\u001A\n")) {
            return "image/png";
        }
        if (inicio.length() == 12 && inicio.startsWith("RIFF") && inicio.startsWith("WEBP", 8)) {
            return "image/webp";
        }
        return null;
    }
}
