package com.backend.boilerplate;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Configuration
@ConfigurationProperties
@Data
public class BootstrapServiceApplicationProperties {

    private Sort sort = new Sort();

    @Data
    public static class Sort {
        private User user = new User();

        @Data
        public static class User {
            private String defaultParam;

            private Set<String> params;
        }
    }
}
