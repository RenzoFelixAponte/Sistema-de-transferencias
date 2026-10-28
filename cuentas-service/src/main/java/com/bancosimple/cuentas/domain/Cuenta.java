package com.bancosimple.cuentas.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cuentas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(nullable = false)
    private String titular;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    // Bloqueo optimista: si dos transferencias modifican la misma cuenta a la vez,
    // la segunda falla en lugar de sobrescribir el saldo de la primera.
    @Version
    private Long version;

    public Cuenta(String numero, String titular, BigDecimal saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public void debitar(BigDecimal monto) {
        if (saldo.compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(numero);
        }
        saldo = saldo.subtract(monto);
    }

    public void acreditar(BigDecimal monto) {
        saldo = saldo.add(monto);
    }
}
