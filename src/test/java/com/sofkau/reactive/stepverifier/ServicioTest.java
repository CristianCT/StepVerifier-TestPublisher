package com.sofkau.reactive.stepverifier;

import com.sofkau.reactive.stepverifier.services.Servicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
public class ServicioTest {

    @Autowired
    Servicio servicio;

    @Test
    void testMono() {
        Mono<String> uno = servicio.buscarUno();
        StepVerifier.create(uno).expectNext("Pedro").verifyComplete();
    }
    @Test
    void testVarios() {
        Flux<String> uno = servicio.buscarTodos();
        StepVerifier.create(uno).expectNext("Pedro").expectNext("Maria").expectNext("Jesus").expectNext("Carmen").verifyComplete();
    }
    @Test
    void testVariosLento() {
        Flux<String> uno = servicio.buscarTodosLento();
        StepVerifier.create(uno)
                .expectNext("Pedro")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Maria")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Jesus")
                .thenAwait(Duration.ofSeconds(1))
                .expectNext("Carmen")
                .thenAwait(Duration.ofSeconds(1)).verifyComplete();
    }
    @Test
    void testTodosFiltro() {
        Flux<String> source = servicio.buscarTodosFiltro();
        StepVerifier
            .create(source)
            .expectNextCount(4)
            .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().equals("Mensaje de Error")
            );

        /* Publicadores Basados en Tiempo
        Por ejemplo, suponga que en nuestra aplicación de la vida real, tenemos un retraso de un día entre eventos.
        Ahora, obviamente, no queremos que nuestras pruebas se ejecuten durante todoun día para verificar el
        comportamiento esperado con tal retraso. El constructor StepVerifier.withVirtualTime está diseñado para
        evitar pruebas de larga duración.
        
        StepVerifier
          .withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1)).take(2))
          .expectSubscription()
          .expectNoEvent(Duration.ofSeconds(1))
          .expectNext(0L)
          .thenAwait(Duration.ofSeconds(1))
          .expectNext(1L)
          .verifyComplete(); */

    }
}
