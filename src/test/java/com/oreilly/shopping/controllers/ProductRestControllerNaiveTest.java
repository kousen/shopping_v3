package com.oreilly.shopping.controllers;

import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

// @TestMethodOrder(MethodOrderer.MethodName.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductRestControllerNaiveTest {

    @Autowired
    private WebTestClient client;

    @Test
    void getAllProducts() {
        client.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .consumeWith(System.out::println);
    }

    @Test
    void insertProduct() {
        Product bat = new Product("cricket bat", BigDecimal.valueOf(35.00));
        client.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(bat), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class)
                .consumeWith(System.out::println);
    }

    @Test
    void countProducts() {
        client.get()
                .uri("/products/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .consumeWith(count -> assertThat(count.getResponseBody()).isEqualTo(3L));
    }

    @Test
    void deleteProducts() {
        client.delete()
                .uri("/products")
                .exchange()
                .expectStatus().isNoContent();
        client.get()
                .uri("/products/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .consumeWith(count -> assertThat(count.getResponseBody()).isEqualTo(0L));
    }

    @Test
    void findById() {
        client.get()
                .uri("/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .consumeWith(System.out::println);
    }
}