package com.backend.boilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@SpringBootApplication
@EnableTransactionManagement
public class BoilerplateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoilerplateServiceApplication.class, args);
    }
}
