package com.backend.boilerplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@SpringBootTest
@ActiveProfiles("test")
public class GenerateSwaggerTest {
    private static final String GROUP_NAME = "USERMANAGEMENT";

    @Autowired
    WebApplicationContext context;

    @Test
    public void generateSwaggerV1() throws Exception {
        final int API_VERSION = 1;
        final String OUTPUT_DIR = "./src/main/resources/";
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .build();
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/v2/api-docs?group=%s-v%d", GROUP_NAME, API_VERSION))
            .accept(MediaType.APPLICATION_JSON))
            .andDo((result) ->
            {
                String swaggerJson = result.getResponse().getContentAsString();
                assertTrue(swaggerJson.length() > 0);
                Files.createDirectories(Paths.get(OUTPUT_DIR));
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(OUTPUT_DIR, "swagger2_v1.json"),
                    StandardCharsets.UTF_8)) {
                    writer.write(swaggerJson);
                }
            });

    }
}
