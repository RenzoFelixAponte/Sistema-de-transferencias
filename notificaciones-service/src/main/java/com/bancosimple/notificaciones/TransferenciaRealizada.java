package com.bancosimple.notificaciones;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Copia del contrato del evento que publica cuentas-service.
// Cada microservicio tiene su propia copia para no acoplarse por código compartido.
public record TransferenciaRealizada(
        UUID transferenciaId,
        String cuentaOrigen,
        String cuentaDestino,
        BigDecimal monto,
        Instant fecha) {
}
