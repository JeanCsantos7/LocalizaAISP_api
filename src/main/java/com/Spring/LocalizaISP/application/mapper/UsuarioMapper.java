package com.Spring.LocalizaISP.application.mapper;

import com.Spring.LocalizaISP.application.dto.request.UsuarioAtualizacaoRequest;
import com.Spring.LocalizaISP.application.dto.request.UsuarioCadastroRequest;
import com.Spring.LocalizaISP.application.dto.response.UsuarioResponse;
import com.Spring.LocalizaISP.domain.model.Usuario;

import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.*;


@Mapper(componentModel = "spring")

public interface UsuarioMapper {



    Usuario toEntity(UsuarioCadastroRequest dtoRequest);


    @Mapping(source = "role", target = "permissao")

    UsuarioResponse toDTO(Usuario entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(
            UsuarioAtualizacaoRequest dto,
            @MappingTarget Usuario entity
    );

}
