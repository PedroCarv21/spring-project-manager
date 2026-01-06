package com.example.springprojectmanager.exceptions;

import com.example.springprojectmanager.dtos.CampoErro;
import com.example.springprojectmanager.dtos.ErroResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NaoEncontradoException.class)
    public ErroResponse tratarNaoEncontradoException(NaoEncontradoException e){
        return new ErroResponse(e.getMessage(), List.of());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErroResponse tratarConstraintViolationException(ConstraintViolationException e){

        List<CampoErro> campoErroList = e
                .getConstraintViolations()
                .stream()
                .map(cv -> new CampoErro(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();

        return new ErroResponse("Campos com erros semanticos", campoErroList);
    }
}
