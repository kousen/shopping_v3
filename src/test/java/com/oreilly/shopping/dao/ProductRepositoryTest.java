package com.oreilly.shopping.dao;

import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        List<Product> products = List.of(
                new Product("TV tray", BigDecimal.valueOf(4.95)),
                new Product("Toaster", BigDecimal.valueOf(19.95)),
                new Product("Sofa", BigDecimal.valueOf(249.95))
        );
        repository.saveAll(products);
    }

    @Test
    void threeProductsInDB() {
        assertThat(repository.count()).isEqualTo(3);
    }

//    @Test
//    void justSeeIfMySQLWorked() {
//        assertThat(mysql.isRunning()).isTrue();
//        System.out.println("Name: " + mysql.getContainerName());
//        System.out.println("Host: " + mysql.getHost());
//        System.out.println("Port: " + mysql.getFirstMappedPort());
//        System.out.println("JDBC URL: " + mysql.getJdbcUrl());
//        System.out.println("Username: " + mysql.getUsername());
//        System.out.println("Password: " + mysql.getPassword());
//        System.out.println("Database name: " + mysql.getDatabaseName());
//        System.out.println("Driver class name: " + mysql.getDriverClassName());
//        System.out.println("Docker image: " + mysql.getDockerImageName());
//        System.out.println("Container logs: " + mysql.getLogs());
//    }
//
//    @Test
//    void justSeeIfPostgresWorked() {
//        assertThat(postgres.isRunning()).isTrue();
//        System.out.println("Name: " + postgres.getContainerName());
//        System.out.println("Host: " + postgres.getHost());
//        System.out.println("Port: " + postgres.getFirstMappedPort());
//        System.out.println("JDBC URL: " + postgres.getJdbcUrl());
//        System.out.println("Username: " + postgres.getUsername());
//        System.out.println("Password: " + postgres.getPassword());
//        System.out.println("Database name: " + postgres.getDatabaseName());
//        System.out.println("Driver class name: " + postgres.getDriverClassName());
//        System.out.println("Docker image: " + postgres.getDockerImageName());
//        System.out.println("Container logs: " + postgres.getLogs());
//    }

}