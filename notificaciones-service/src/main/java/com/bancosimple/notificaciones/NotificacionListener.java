package com.bancosimple.notificaciones;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificacionListener {

    private final JsonMapper jsonMapper;

    // En memoria para mantener el ejemplo simple; en producción iría en una base de datos.
    private final Map<UUID, TransferenciaRealizada> procesadas = new ConcurrentHashMap<>();

    @KafkaListener(topics = "${banco.kafka.topic-transferencias}")
    public void alRecibir(String mensaje) {
        TransferenciaRealizada evento = jsonMapper.readValue(mensaje, TransferenciaRealizada.class);

        // Kafka garantiza "al menos una vez": el mismo evento puede llegar repetido.
        // Por eso el consumidor es idempotente y descarta los duplicados.
        if (procesadas.putIfAbsent(evento.transferenciaId(), evento) != null) {
            log.warn("Evento duplicado ignorado: {}", evento.transferenciaId());
            return;
        }
        log.info("Notificación: la cuenta {} envió {} a la cuenta {}",
                evento.cuentaOrigen(), evento.monto(), evento.cuentaDestino());
    }

    @GetMapping("/notificaciones")
    public Collection<TransferenciaRealizada> listar() {
        return procesadas.values();
    }
}
