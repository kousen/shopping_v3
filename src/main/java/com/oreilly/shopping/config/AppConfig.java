package com.oreilly.shopping.config;

import com.oreilly.shopping.services.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AppConfig {
    @Bean
    public CommandLineRunner initializeDatabase(ProductService service) {
        return args -> service.initializeDatabase();
    }

}
