package com.backend.boilerplate;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@EnableAutoConfiguration
@EnableConfigurationProperties(value = {BootstrapServiceApplicationProperties.class})
public class TestBoilerplateServiceApplication {

}
