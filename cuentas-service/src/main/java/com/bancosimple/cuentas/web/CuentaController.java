package com.bancosimple.cuentas.web;

import com.bancosimple.cuentas.service.CuentaService;
import com.bancosimple.cuentas.service.TransferenciaService;
import com.bancosimple.cuentas.web.Dtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;
    private final TransferenciaService transferenciaService;

    @PostMapping("/cuentas")
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponse crear(@Valid @RequestBody CrearCuentaRequest req) {
        return CuentaResponse.de(cuentaService.crear(req.numero(), req.titular(), req.saldoInicial()));
    }

    @GetMapping("/cuentas/{numero}")
    public CuentaResponse obtener(@PathVariable String numero) {
        return CuentaResponse.de(cuentaService.obtener(numero));
    }

    @PostMapping("/transferencias")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse transferir(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                            @Valid @RequestBody TransferenciaRequest req) {
        return TransferenciaResponse.de(transferenciaService.transferir(
                idempotencyKey, req.cuentaOrigen(), req.cuentaDestino(), req.monto()));
    }
}
