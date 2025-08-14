package com.sobow.secureweb;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
public class SecurityBasicAuthTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void accessProtectedURLWithoutAuthShouldResult401() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isUnauthorized())
               .andExpect(header().string("WWW-Authenticate", "Basic realm=\"Realm\""));
    }
    
    @Test
    public void accessProtectedURLWithValidCredentialsShouldResult200() throws Exception {
        mockMvc.perform(get("/").with(httpBasic("user", "password")))
               .andExpect(status().isOk());
    }
    
    @Test
    public void accessProtectedURLWithInvalidCredentialsShouldResult401() throws Exception {
        mockMvc.perform(get("/").with(httpBasic("fails", "fails")))
               .andExpect(status().isUnauthorized());
    }
}
