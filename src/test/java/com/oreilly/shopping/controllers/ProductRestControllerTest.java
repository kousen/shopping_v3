package com.oreilly.shopping.controllers;

import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductRestControllerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Long> getAllProductIds() {
        return jdbcTemplate.queryForList("SELECT id FROM product", Long.class);
    }

    @Test
    void getAllProducts() {
        client.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(3);
    }

    @Test
    void getProductsThatExist() {
        System.out.println(getAllProductIds());
        client.get()
                .uri("/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("TV tray");
    }

    @Test
    void getProductThatDoesNotExist() {
        client.get()
                .uri("/products/999")
                .exchange()
                .expectStatus().isNotFound();
    }
}