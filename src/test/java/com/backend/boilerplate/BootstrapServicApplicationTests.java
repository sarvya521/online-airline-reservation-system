package com.backend.boilerplate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BootstrapServicApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    public void verify_NoExceptions_whenSpringContextIsBootstrapped() {
        assertThat(this.context).isNotNull();
    }
}
