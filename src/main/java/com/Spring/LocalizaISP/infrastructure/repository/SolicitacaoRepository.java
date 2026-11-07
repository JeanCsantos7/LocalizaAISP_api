package com.Spring.LocalizaISP.infrastructure.repository;

import com.Spring.LocalizaISP.domain.enums.StatusSolicitacao;
import com.Spring.LocalizaISP.domain.enums.TipoSolicitacao;
import com.Spring.LocalizaISP.domain.model.Solicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    Page<Solicitacao> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Solicitacao> findByUsuarioIdAndStatus(Long usuarioId, StatusSolicitacao status, Pageable pageable);

    Page<Solicitacao> findByCasoId(Long casoId, Pageable pageable);

    Optional<Solicitacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndCasoIdAndTipoAndStatusIn(Long usuarioId, Long casoId, TipoSolicitacao tipo,
                                                         Collection<StatusSolicitacao> status);

    long countByUsuarioIdAndStatusIn(Long usuarioId, Collection<StatusSolicitacao> status);

    // so retirada aberta pelo titular do caso ou por administrador oculta o caso; a de terceiro apenas entra na fila
    @Query("""
            select count(s) > 0 from Solicitacao s
            where s.caso.id = :casoId
              and s.tipo = com.Spring.LocalizaISP.domain.enums.TipoSolicitacao.RETIRADA
              and s.status in :status
              and (s.usuario.id = s.caso.usuario.id
                   or s.usuario.role = com.Spring.LocalizaISP.domain.enums.TipoUsuario.ADMIN)
            """)
    boolean existeRetiradaQueOculta(@Param("casoId") Long casoId, @Param("status") Collection<StatusSolicitacao> status);

    // RN10: retirada primeiro (risco de exposicao indevida), depois correcao, depois informacao; mais antigas antes
    @Query(value = """
            select s from Solicitacao s
            where s.status in :status
            order by case s.tipo
                when com.Spring.LocalizaISP.domain.enums.TipoSolicitacao.RETIRADA then 0
                when com.Spring.LocalizaISP.domain.enums.TipoSolicitacao.CORRECAO then 1
                else 2 end,
                s.criadoEm asc
            """,
            countQuery = "select count(s) from Solicitacao s where s.status in :status")
    Page<Solicitacao> buscarFila(@Param("status") Collection<StatusSolicitacao> status, Pageable pageable);

}
