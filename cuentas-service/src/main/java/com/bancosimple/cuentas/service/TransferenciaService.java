package com.bancosimple.cuentas.service;

import com.bancosimple.cuentas.domain.Cuenta;
import com.bancosimple.cuentas.domain.CuentaNoEncontradaException;
import com.bancosimple.cuentas.domain.ReglaNegocioException;
import com.bancosimple.cuentas.domain.Transferencia;
import com.bancosimple.cuentas.events.TransferenciaRealizada;
import com.bancosimple.cuentas.repository.CuentaRepository;
import com.bancosimple.cuentas.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class TransferenciaService {

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");

    private final CuentaRepository cuentaRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final ApplicationEventPublisher eventos;

    // Hoy viene de application.properties; en la fase de Azure vendrá de App Configuration.
    @Value("${banco.transferencias.limite-diario}")
    private BigDecimal limiteDiario;

    // Todo o nada: si falla el crédito, el débito también se revierte.
    @Transactional
    public Transferencia transferir(String idempotencyKey, String origen, String destino, BigDecimal monto) {
        var existente = transferenciaRepository.findByIdempotencyKey(idempotencyKey);
        if (existente.isPresent()) {
            return existente.get();
        }
        if (origen.equals(destino)) {
            throw new ReglaNegocioException("La cuenta origen y destino no pueden ser la misma");
        }

        Cuenta cuentaOrigen = buscar(origen);
        Cuenta cuentaDestino = buscar(destino);
        validarLimiteDiario(origen, monto);

        cuentaOrigen.debitar(monto);
        cuentaDestino.acreditar(monto);

        var transferencia = transferenciaRepository.save(new Transferencia(idempotencyKey, origen, destino, monto));
        eventos.publishEvent(new TransferenciaRealizada(
                transferencia.getId(), origen, destino, monto, transferencia.getFecha()));
        return transferencia;
    }

    private void validarLimiteDiario(String origen, BigDecimal monto) {
        Instant inicioDelDia = LocalDate.now(ZONA_LIMA).atStartOfDay(ZONA_LIMA).toInstant();
        BigDecimal enviadoHoy = transferenciaRepository.totalEnviadoDesde(origen, inicioDelDia);
        if (enviadoHoy.add(monto).compareTo(limiteDiario) > 0) {
            throw new ReglaNegocioException(
                    "Supera el límite diario de " + limiteDiario + " (ya enviado hoy: " + enviadoHoy + ")");
        }
    }

    private Cuenta buscar(String numero) {
        return cuentaRepository.findByNumero(numero).orElseThrow(() -> new CuentaNoEncontradaException(numero));
    }
}
