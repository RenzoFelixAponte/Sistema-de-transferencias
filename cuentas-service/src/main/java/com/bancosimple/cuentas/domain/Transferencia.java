package com.bancosimple.cuentas.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transferencias")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transferencia {

    @Id
    private UUID id;

    // La envía el cliente; si repite la misma petición (reintento por timeout),
    // devolvemos la transferencia ya hecha en vez de cobrar dos veces.
    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "cuenta_origen", nullable = false)
    private String cuentaOrigen;

    @Column(name = "cuenta_destino", nullable = false)
    private String cuentaDestino;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private Instant fecha;

    public Transferencia(String idempotencyKey, String cuentaOrigen, String cuentaDestino, BigDecimal monto) {
        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.monto = monto;
        this.fecha = Instant.now();
    }
}
