package com.Spring.LocalizaISP.application.mapper;

import com.Spring.LocalizaISP.application.dto.response.HistoricoStatusResponse;
import com.Spring.LocalizaISP.domain.model.HistoricoStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoricoStatusMapper {

    @Mapping(source = "caso.id", target = "casoId")
    @Mapping(source = "responsavel.id", target = "responsavelId")
    @Mapping(source = "responsavel.nome", target = "responsavelNome")
    HistoricoStatusResponse toDTO(HistoricoStatus historico);
}
