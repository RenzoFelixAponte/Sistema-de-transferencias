package com.bancosimple.cuentas.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicadorEventos {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    @Value("${banco.kafka.topic-transferencias}")
    private String topic;

    // Solo publica si la transacción en la base de datos se confirmó (AFTER_COMMIT por defecto):
    // así nunca avisamos de una transferencia que luego se revirtió.
    @TransactionalEventListener
    public void publicar(TransferenciaRealizada evento) {
        // La clave es la cuenta origen: Kafka mantiene el orden de los eventos de una misma cuenta.
        kafkaTemplate.send(topic, evento.cuentaOrigen(), jsonMapper.writeValueAsString(evento))
                .whenComplete((resultado, error) -> {
                    if (error != null) {
                        log.error("No se pudo publicar la transferencia {}", evento.transferenciaId(), error);
                    } else {
                        log.info("Evento publicado: transferencia {}", evento.transferenciaId());
                    }
                });
    }
}
