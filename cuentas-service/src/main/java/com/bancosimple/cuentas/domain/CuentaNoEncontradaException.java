package com.bancosimple.cuentas.domain;

public class CuentaNoEncontradaException extends RuntimeException {
    public CuentaNoEncontradaException(String numeroCuenta) {
        super("No existe la cuenta " + numeroCuenta);
    }
}
