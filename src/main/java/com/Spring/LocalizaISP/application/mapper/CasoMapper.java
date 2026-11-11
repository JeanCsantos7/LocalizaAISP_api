package com.Spring.LocalizaISP.application.mapper;

import com.Spring.LocalizaISP.application.dto.request.CasoRequest;
import com.Spring.LocalizaISP.application.dto.response.CasoPublicoResponse;
import com.Spring.LocalizaISP.application.dto.response.CasoResponse;
import com.Spring.LocalizaISP.domain.model.Caso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = InformacaoContatoMapper.class)
public interface CasoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fotos", ignore = true)
    @Mapping(target = "historicoStatus", ignore = true)
    @Mapping(target = "solicitacoes", ignore = true)
    @Mapping(target = "informacoesContato", ignore = true)
    Caso toEntity(CasoRequest dados);

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "informacoesContato", target = "informacaoContatos")
    CasoResponse toDTO(Caso caso);

    CasoPublicoResponse toPublicoDTO(Caso caso);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "origem", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fotos", ignore = true)
    @Mapping(target = "historicoStatus", ignore = true)
    @Mapping(target = "solicitacoes", ignore = true)
    @Mapping(target = "informacoesContato", ignore = true)
    void update(CasoRequest dados, @MappingTarget Caso caso);
}
