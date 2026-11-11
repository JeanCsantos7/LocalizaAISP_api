package com.Spring.LocalizaISP.application.mapper;

import com.Spring.LocalizaISP.application.dto.request.InformacaoContatoRequest;
import com.Spring.LocalizaISP.application.dto.response.InformacaoContatoResponse;
import com.Spring.LocalizaISP.domain.model.InformacaoContato;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface InformacaoContatoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mensagem", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "caso", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    InformacaoContato toEntity(InformacaoContatoRequest dto);

    @Mapping(source = "caso.id", target = "casoId")
    InformacaoContatoResponse toDTO(InformacaoContato entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mensagem", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "caso", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    void update(InformacaoContatoRequest dto, @MappingTarget InformacaoContato entity);

}
