package com.Spring.LocalizaISP.application.mapper;


import com.Spring.LocalizaISP.application.dto.request.SolicitacaoRequest;
import com.Spring.LocalizaISP.application.dto.response.SolicitacaoResponse;
import com.Spring.LocalizaISP.domain.model.Solicitacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "respondidoEm", ignore = true)
    @Mapping(target = "resposta", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "caso", ignore = true)
    Solicitacao toEntity(SolicitacaoRequest dados);

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "caso.id", target = "casoId")
    SolicitacaoResponse toDTO(Solicitacao solicitacao);
}
