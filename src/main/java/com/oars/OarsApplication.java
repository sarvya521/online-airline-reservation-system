package com.oars;

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
public class OarsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OarsApplication.class, args);
    }
}
