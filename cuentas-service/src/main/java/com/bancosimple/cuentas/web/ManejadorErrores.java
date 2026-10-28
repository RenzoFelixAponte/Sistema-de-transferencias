package com.bancosimple.cuentas.web;

import com.bancosimple.cuentas.domain.CuentaNoEncontradaException;
import com.bancosimple.cuentas.domain.ReglaNegocioException;
import com.bancosimple.cuentas.domain.SaldoInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Traduce las excepciones de negocio a respuestas HTTP con formato estándar (RFC 9457).
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(CuentaNoEncontradaException.class)
    ProblemDetail noEncontrada(CuentaNoEncontradaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({SaldoInsuficienteException.class, ReglaNegocioException.class})
    ProblemDetail reglaNegocio(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail conflicto() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "La cuenta fue modificada por otra operación al mismo tiempo; reintente");
    }
}
