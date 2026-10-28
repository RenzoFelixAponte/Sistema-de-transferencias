package com.bancosimple.cuentas.web;

import com.bancosimple.cuentas.domain.Cuenta;
import com.bancosimple.cuentas.domain.Transferencia;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Los DTO separan lo que expone la API de las entidades JPA.
public final class Dtos {

    private Dtos() {
    }

    public record CrearCuentaRequest(
            @NotBlank String numero,
            @NotBlank String titular,
            @NotNull @DecimalMin("0.00") BigDecimal saldoInicial) {
    }

    public record CuentaResponse(String numero, String titular, BigDecimal saldo) {
        static CuentaResponse de(Cuenta c) {
            return new CuentaResponse(c.getNumero(), c.getTitular(), c.getSaldo());
        }
    }

    public record TransferenciaRequest(
            @NotBlank String cuentaOrigen,
            @NotBlank String cuentaDestino,
            @NotNull @DecimalMin("0.01") BigDecimal monto) {
    }

    public record TransferenciaResponse(
            UUID id, String cuentaOrigen, String cuentaDestino, BigDecimal monto, Instant fecha) {
        static TransferenciaResponse de(Transferencia t) {
            return new TransferenciaResponse(
                    t.getId(), t.getCuentaOrigen(), t.getCuentaDestino(), t.getMonto(), t.getFecha());
        }
    }
}
