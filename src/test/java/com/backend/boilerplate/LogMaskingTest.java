package com.backend.boilerplate;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
@SpringBootTest(classes = {TestBoilerplateServiceApplication.class})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
class LogMaskingTest {

    @BeforeAll
    public static void setLogger() {
        System.setProperty("log4j.configurationFile", "log4j2.yml");
    }

    @Test
    void testPlainStringLog() {
        log.info("Passed to server::LICENSE:S12345678 SSN:123456789 PASSPORT:1234567890");
    }

    @Test
    void testOnlyObjectLog() {
        //@formatter:off
        /* can be compiled in Java 11 */
//        Map<String, String> demoMaskMap = Map.of(
//                "LICENSE", "S12345678",
//                "SSN", "123456789",
//                "PASSPORT", "1234567890");
        //@formatter:on
        Map<String, String> demoMaskMap = new HashMap<>();
        demoMaskMap.put("LICENSE", "S12345678");
        demoMaskMap.put("SSN", "123456789");
        demoMaskMap.put("PASSPORT", "1234567890");

        log.info(demoMaskMap);
    }

    @Test
    void testParameterizedLog() {
        //@formatter:off
        /* can be compiled in Java 11 */
//        Map<String, String> demoMaskMap = Map.of(
//                "LICENSE", "S12345678",
//                "SSN", "123456789",
//                "PASSPORT", "1234567890");
        //@formatter:on
        Map<String, String> demoMaskMap = new HashMap<>();
        demoMaskMap.put("LICENSE", "S12345678");
        demoMaskMap.put("SSN", "123456789");
        demoMaskMap.put("PASSPORT", "1234567890");
        log.info("demo string with placeholder this {} is my object", demoMaskMap);
    }

}
