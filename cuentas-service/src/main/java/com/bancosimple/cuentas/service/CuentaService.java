package com.bancosimple.cuentas.service;

import com.bancosimple.cuentas.domain.Cuenta;
import com.bancosimple.cuentas.domain.CuentaNoEncontradaException;
import com.bancosimple.cuentas.domain.ReglaNegocioException;
import com.bancosimple.cuentas.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    @Transactional
    public Cuenta crear(String numero, String titular, BigDecimal saldoInicial) {
        if (cuentaRepository.existsByNumero(numero)) {
            throw new ReglaNegocioException("Ya existe la cuenta " + numero);
        }
        return cuentaRepository.save(new Cuenta(numero, titular, saldoInicial));
    }

    @Transactional(readOnly = true)
    public Cuenta obtener(String numero) {
        return cuentaRepository.findByNumero(numero).orElseThrow(() -> new CuentaNoEncontradaException(numero));
    }
}
