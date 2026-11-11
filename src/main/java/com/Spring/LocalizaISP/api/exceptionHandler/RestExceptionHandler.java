package com.Spring.LocalizaISP.api.exceptionHandler;

import com.Spring.LocalizaISP.application.dto.exceptionsDTO.*;
import com.Spring.LocalizaISP.infrastructure.exception.ArmazenamentoException;
import com.Spring.LocalizaISP.infrastructure.exception.CasoNaoEncontrado;
import com.Spring.LocalizaISP.infrastructure.exception.EmailExistente;
import com.Spring.LocalizaISP.infrastructure.exception.RegraNegocioException;
import com.Spring.LocalizaISP.infrastructure.exception.SolicitacaoNaoEncontrada;
import com.Spring.LocalizaISP.infrastructure.exception.UsuarioNaoEncontrado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrosDTO> validacaoDados(MethodArgumentNotValidException ex){
        Map<String, String> erros = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.putIfAbsent(erro.getField(), erro.getDefaultMessage())
        );

        ErrosDTO resposta = new ErrosDTO(
                "Dados inválidos",
                400,
                LocalDateTime.now(),
                erros
        );

        return ResponseEntity.badRequest().body(resposta);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrosDTO> CredenciaisInvalidas() {

        return ResponseEntity.status(401).body(new ErrosDTO("Credenciais Inválidas, verifique seu email ou senha", 401));


    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrosDTO> AcessoNegado() {

        return ResponseEntity.status(403).body(new ErrosDTO("Acesso Negado, você não tem permissão pra acessar esse recurso!", 403));

    }





    @ExceptionHandler(UsuarioNaoEncontrado.class)
    public ResponseEntity<ErrosDTO> UsuarioNaoEncontrado(UsuarioNaoEncontrado exception) {

      return ResponseEntity.status(404).body(new ErrosDTO(exception.getMessage(), 404));



    }



    @ExceptionHandler(CasoNaoEncontrado.class)
    public ResponseEntity<ErrosDTO> casoNaoEncontrado(CasoNaoEncontrado exception) {

        return ResponseEntity.status(404).body(new ErrosDTO(exception.getMessage(), 404));
    }

    @ExceptionHandler(SolicitacaoNaoEncontrada.class)
    public ResponseEntity<ErrosDTO> solicitacaoNaoEncontrada(SolicitacaoNaoEncontrada exception) {

        return ResponseEntity.status(404).body(new ErrosDTO(exception.getMessage(), 404));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrosDTO> regraNegocio(RegraNegocioException exception) {

        return ResponseEntity.status(422).body(new ErrosDTO(exception.getMessage(), 422));
    }

    @ExceptionHandler(EmailExistente.class)
    public ResponseEntity<ErrosDTO> emailExistente(EmailExistente exception) {

       return ResponseEntity.status(409).body(new ErrosDTO(exception.getMessage(), 409));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrosDTO> arquivoGrande(MaxUploadSizeExceededException exception) {

        return ResponseEntity.status(413).body(new ErrosDTO("A imagem excede o tamanho máximo de 5MB.", 413));
    }

    @ExceptionHandler(ArmazenamentoException.class)
    public ResponseEntity<ErrosDTO> armazenamento(ArmazenamentoException exception) {

        return ResponseEntity.status(502).body(new ErrosDTO(exception.getMessage(), 502));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExcessaoGenericaDTO> ExcessaoGenerica(Exception exception) {

        log.error("Erro inesperado, tente novamente!!");
        ExcessaoGenericaDTO excessaoPersonalizada = new ExcessaoGenericaDTO(
                exception.getMessage(),
                500,
                LocalDateTime.now()
        );

        return ResponseEntity.status(500).body(excessaoPersonalizada);
    }

}

