package com.bancosimple.cuentas.repository;

import com.bancosimple.cuentas.domain.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface TransferenciaRepository extends JpaRepository<Transferencia, UUID> {
    Optional<Transferencia> findByIdempotencyKey(String idempotencyKey);

    @Query("""
            select coalesce(sum(t.monto), 0) from Transferencia t
            where t.cuentaOrigen = :cuenta and t.fecha >= :desde
            """)
    BigDecimal totalEnviadoDesde(String cuenta, Instant desde);
}
