package com.bancosimple.cuentas.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferenciaRealizada(
        UUID transferenciaId,
        String cuentaOrigen,
        String cuentaDestino,
        BigDecimal monto,
        Instant fecha) {
}
