package com.oreilly.shopping.controllers;

import com.oreilly.shopping.dao.ProductRepository;
import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Transactional
class ProductRestControllerFunctionalTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductRepository repository;

    private final List<Product> products = List.of(
            new Product("TV tray", BigDecimal.valueOf(4.95)),
            new Product("Toothbrush", BigDecimal.valueOf(0.99)),
            new Product("Soccer ball", BigDecimal.valueOf(18.49)));

    private List<Long> ids;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.saveAll(products);
        ids = repository.findAll().stream().map(Product::getId).toList();
    }

    @Test
    void getAllProducts() {
        client.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .consumeWith(
                        response -> assertThat(response.getResponseBody())
                                .hasSize(3)
                                .containsAll(products)
                );
    }

    @Test
    void getProductsThatExist() {
        products.forEach(product -> client.get()
                .uri("/products/{id}", product.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class).isEqualTo(product)
        );
    }

    @Test
    void getProductThatDoesNotExist() {
        assertThat(ids).doesNotContain(999L);
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
                .expectBodyList(Product.class)
                .consumeWith(
                        response -> assertThat(response.getResponseBody())
                                .allSatisfy(product -> assertThat(product.getPrice())
                                        .isGreaterThanOrEqualTo(BigDecimal.valueOf(5.00))));
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
        Product product = products.get(0);
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
        // when:
        client.delete()
                .uri("/products")
                .exchange()
                .expectStatus().isNoContent();

        // then:
        client.get()
                .uri("/products")
                .exchange()
                .expectBodyList(Product.class)
                .hasSize(0);
    }
}