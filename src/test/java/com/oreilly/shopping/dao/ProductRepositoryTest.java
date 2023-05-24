package com.oreilly.shopping.dao;

import com.oreilly.shopping.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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
}