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
        Flux<Integer> source = Flux.<Integer>create(emitter -> {
            emitter.next(1);
            emitter.next(2);
            emitter.next(3);
            emitter.complete();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            emitter.next(4);
        }).filter(number -> number % 2 == 0);

        StepVerifier.create(source)
            .expectNext(2)
            .expectComplete()
            .verifyThenAssertThat()
            .hasDropped(4)
            .tookLessThan(Duration.ofMillis(1045));


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
