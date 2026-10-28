package com.bancosimple.cuentas.repository;

import com.bancosimple.cuentas.domain.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumero(String numero);

    boolean existsByNumero(String numero);
}
