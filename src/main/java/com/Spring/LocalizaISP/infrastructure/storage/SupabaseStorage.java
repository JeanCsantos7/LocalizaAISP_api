package com.Spring.LocalizaISP.infrastructure.storage;

import com.Spring.LocalizaISP.infrastructure.exception.ArmazenamentoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Comunicação com o Supabase Storage (bucket privado) via API REST.
 * O banco guarda apenas o caminho do objeto; a URL de acesso é assinada e temporária.
 */
@Slf4j
@Component
public class SupabaseStorage {

    private final RestClient client;
    private final String baseUrl;
    private final String bucket;
    private final int expiraEmSegundos;
    private final boolean configurado;

    public SupabaseStorage(@Value("${supabase.url:}") String url,
                           @Value("${supabase.service-key:}") String chave,
                           @Value("${supabase.bucket:fotos-casos}") String bucket,
                           @Value("${supabase.signed-url-seconds:3600}") int expiraEmSegundos) {
        this.baseUrl = url.replaceAll("/+$", "") + "/storage/v1";
        this.bucket = bucket;
        this.expiraEmSegundos = expiraEmSegundos;
        this.configurado = !url.isBlank() && !chave.isBlank();
        RestClient.Builder builder = RestClient.builder().baseUrl(baseUrl).defaultHeader("apikey", chave);
        if (!chave.startsWith("sb_")) { // chave legada (JWT) também vai no Authorization; a sb_secret_ só no apikey
            builder.defaultHeader("Authorization", "Bearer " + chave);
        }
        this.client = builder.build();
    }

    public void enviar(String caminho, byte[] conteudo, String tipo) {
        chamar(() -> client.post()
                .uri("/object/" + bucket + "/" + caminho)
                .contentType(MediaType.parseMediaType(tipo))
                .body(conteudo)
                .retrieve()
                .toBodilessEntity());
    }

    public String urlAssinada(String caminho) {
        Map<?, ?> resposta = chamar(() -> client.post()
                .uri("/object/sign/" + bucket + "/" + caminho)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("expiresIn", expiraEmSegundos))
                .retrieve()
                .body(Map.class));

        if (resposta == null || resposta.get("signedURL") == null) {
            throw new ArmazenamentoException("Serviço de imagens indisponível, tente novamente.");
        }
        return baseUrl + resposta.get("signedURL");
    }

    /** Remoção "melhor esforço": o registro no banco é a fonte da verdade, falha aqui só gera log. */
    public void remover(String caminho) {
        try {
            chamar(() -> client.delete().uri("/object/" + bucket + "/" + caminho).retrieve().toBodilessEntity());
        } catch (ArmazenamentoException e) {
            log.warn("Não foi possível remover o objeto {} do storage.", caminho);
        }
    }

    private <T> T chamar(Supplier<T> acao) {
        if (!configurado) {
            throw new ArmazenamentoException("Armazenamento de imagens não configurado.");
        }
        try {
            return acao.get();
        } catch (RestClientException e) {
            log.error("Falha ao acessar o Supabase Storage: {}", e.getMessage());
            throw new ArmazenamentoException("Serviço de imagens indisponível, tente novamente.");
        }
    }
}
