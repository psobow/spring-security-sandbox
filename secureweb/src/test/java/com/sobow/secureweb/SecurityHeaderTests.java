package com.sobow.secureweb;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.sobow.secureweb.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityHeaderTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void defaultSecurityHeadersAreAvailable() throws Exception {
        CustomUserDetails testUser = TestDataUtil.createTestUser("userone", 123, "ROLE_USER");
        mockMvc.perform(get("/api").with(user(testUser)))
               .andExpect(header().string("X-Frame-Options", "DENY"))
               .andExpect(header().string("X-Content-Type-Options", "nosniff"))
               .andExpect(header().string("X-XSS-Protection", "0"));
    }
}
