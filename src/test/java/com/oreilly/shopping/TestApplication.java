package com.oreilly.shopping;
import com.oreilly.shopping.dao.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class TestApplication {
    public static void main(String[] args) {
        SpringApplication
                .from(ShoppingApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}