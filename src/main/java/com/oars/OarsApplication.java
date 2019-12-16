package com.oars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EntityScan("com.oars.entity")
@EnableJpaRepositories("com.oars.dao")
@EnableTransactionManagement
public class OarsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OarsApplication.class, args);
    }
}
