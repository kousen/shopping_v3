package com.oreilly.shopping.controllers;

import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ProductRestControllerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private JdbcClient jdbcClient;

    private List<Long> getIds() {
        return jdbcClient.sql("SELECT id FROM product")
                .query(Long.class)
                .list();
    }

    private Product getProduct(Long id) {
        return jdbcClient.sql("SELECT * FROM product WHERE id = ?")
                .param(id)
                .query(Product.class)
                .single();
    }

    @Test
    void getAllProducts() {
        List<Long> productIds = getIds();
        System.out.println("There are " + productIds.size() + " products in the database.");
        client.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class).hasSize(3)
                .consumeWith(System.out::println);
    }

    @ParameterizedTest(name = "Product ID: {0}")
    @MethodSource("getIds")
    void getProductsThatExist(Long id) {
        client.get()
                .uri("/products/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id);
    }

    @Test
    void getProductThatDoesNotExist() {
        List<Long> productIds = getIds();
        assertThat(productIds).doesNotContain(999L);
        System.out.println("There are " + productIds.size() + " products in the database.");
        client.get()
                .uri("/products/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void productsWithValidMinPrice() {
        client.get()
                .uri("/products?min=5.00")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class).hasSize(3)
                .consumeWith(System.out::println);
    }

    @Test
    void productsWithInvalidMinPrice() {
        client.get()
                .uri("/products?min=-1.00")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void insertProduct() {
        List<Long> productIds = getIds();
        assertThat(productIds).doesNotContain(999L);
        System.out.println("There are " + productIds.size() + " products in the database.");
        Product product = new Product("Chair", BigDecimal.valueOf(49.99));
        client.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Chair")
                .jsonPath("$.price").isEqualTo(49.99);
    }

    @Test
    void updateProduct() {
        Product product = getProduct(getIds().get(0));
        product.setPrice(product.getPrice().add(BigDecimal.ONE));

        client.put()
                .uri("/products/{id}", product.getId())
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .consumeWith(System.out::println);
    }

    @Test
    void deleteSingleProduct() {
        List<Long> ids = getIds();
        System.out.println("There are " + ids.size() + " products in the database.");
        if (ids.isEmpty()) {
            System.out.println("No ids found");
            return;
        }

        // given:
        client.get()
                .uri("/products/{id}", ids.get(0))
                .exchange()
                .expectStatus().isOk();

        // when:
        client.delete()
                .uri("/products/{id}", ids.get(0))
                .exchange()
                .expectStatus().isNoContent();

        // then:
        client.get()
                .uri("/products/{id}", ids.get(0))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteAllProducts() {
        List<Long> ids = getIds();
        System.out.println("There are " + ids.size() + " products in the database.");

        // when:
        client.delete()
                .uri("/products")
                .exchange()
                .expectStatus().isNoContent();

        // then:
        client.get()
                .uri("/products")
                .exchange()
                .expectBodyList(Product.class).hasSize(0);
    }
}