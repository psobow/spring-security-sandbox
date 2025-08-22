package com.sobow.secureweb;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionManagementTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testSessionCreation() throws Exception {
        MvcResult result = mockMvc
            .perform(get("/").with(user(TestDataUtil.createTestUser("USER", 999999))))
            .andExpect(status().isOk())
            .andExpect(authenticated())
            .andReturn();
        
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        
        mockMvc.perform(get("/").session(session))
               .andExpect(status().isOk())
               .andExpect(authenticated());
    }
    
    @Test
    public void testConcurrentSessions() throws Exception {
        // create new session
        MvcResult resultOne = mockMvc.perform(formLogin("/login")
                                                  .user("user")
                                                  .password("password"))
                                     .andExpect(status().is3xxRedirection())
                                     .andExpect(authenticated())
                                     .andReturn();
        
        // get protected resource using sessionOne, should be successful
        MockHttpSession sessionOne = (MockHttpSession) resultOne.getRequest().getSession();
        mockMvc.perform(get("/").session(sessionOne))
               .andExpect(status().isOk());
        
        // create new session
        mockMvc.perform(formLogin("/login")
                            .user("user")
                            .password("password"))
               .andExpect(status().is3xxRedirection())
               .andExpect(authenticated());
        
        // get protected resource using sessionOne, should NOT be successful
        mockMvc.perform(get("/").session(sessionOne))
               .andExpect(status().is3xxRedirection())
               .andExpect(unauthenticated());
    }
    
    @Test
    public void testRememberMe() throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                                               .with(csrf())
                                               .param("username", "user")
                                               .param("password", "password")
                                               .param("remember-me", "true"))
                                  .andExpect(status().is3xxRedirection())
                                  .andExpect(authenticated())
                                  .andExpect(cookie().exists("remember-me"))
                                  .andReturn();
        
        Cookie rememberMe = result.getResponse().getCookie("remember-me");
        
        mockMvc.perform(get("/").cookie(rememberMe))
               .andExpect(status().isOk())
               .andExpect(authenticated());
    }
}
