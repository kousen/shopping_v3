package com.oreilly.shopping;

import org.springframework.boot.SpringApplication;

public class TestApplication {
    public static void main(String[] args) {
        SpringApplication
                .from(ShoppingApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}
