package com.oreilly.shopping.dao;

import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTCTest {
    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresqlContainer =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private ProductRepository repository;

    @Test
    void testSave() {
        Product product = new Product("TV", BigDecimal.valueOf(500.00));
        repository.saveAndFlush(product);

        assertThat(repository.count()).isEqualTo(1L);
    }

    @Test
    void testRepository() {
        System.out.println("Test container running on port: " + postgresqlContainer.getFirstMappedPort());
        System.out.println("Test container running on host: " + postgresqlContainer.getHost());
        System.out.println("Test container running on JDBC URL: " + postgresqlContainer.getJdbcUrl());
        System.out.println("Test container running on username: " + postgresqlContainer.getUsername());
        System.out.println("Test container running on password: " + postgresqlContainer.getPassword());
        System.out.println("Test container running on database name: " + postgresqlContainer.getDatabaseName());
        System.out.println("Test container running on database driver: " + postgresqlContainer.getDriverClassName());
    }

}
