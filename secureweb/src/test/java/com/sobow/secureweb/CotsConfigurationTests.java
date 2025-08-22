package com.sobow.secureweb;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CotsConfigurationTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testAllowedOrigin() throws Exception {
        mockMvc.perform(get("/api/public-data")
                            .header("Origin", "https://a-different-origin"))
               .andExpect(status().isOk())
               .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
