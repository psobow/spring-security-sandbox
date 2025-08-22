package com.sobow.secureweb;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityFormAuthenticationTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void loginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }
    
    @Test
    public void loginWithValidUserThenAuthenticated() throws Exception {
        FormLoginRequestBuilder loginRequestBuilder = formLogin()
            .user("user")
            .password("password");
        
        mockMvc.perform(loginRequestBuilder)
               .andExpect(authenticated())
               .andExpect(redirectedUrl("/"));
    }
    
    @Test
    public void loginWithInvalidUserThenUnauthenticated() throws Exception {
        FormLoginRequestBuilder loginRequestBuilder = formLogin()
            .user("fails")
            .password("fails");
        
        mockMvc.perform(loginRequestBuilder)
               .andExpect(unauthenticated())
               .andExpect(redirectedUrl("/login?error"));
    }
    
    @Test
    public void accessProtectedURLWhenNotAuthenticatedThenRedirectsToLogin() throws Exception {
        /*
        You’re using MediaType.TEXT_HTML here because you want Spring Security (and MockMvc) to simulate a real
        browser request for an HTML page, not an API call.
        
        Spring Security’s behavior depends on the Accept header:
        
        If the client sends: Accept: text/html
            Spring Security assumes it’s a web browser request and responds with a 302 redirect to the login page.
        If the client sends: Accept: application/json
            Spring Security assumes it’s an API request and will respond with a 401 Unauthorized or 403 Forbidden instead of redirecting.
         */
        mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("http://localhost/login"));
    }
    
    @Test
    public void accessProtectedURLWhenAuthenticatedOk() throws Exception {
        mockMvc.perform(get("/")
                            .with(user(TestDataUtil.createTestUser("USER", 999999))))
               .andExpect(status().isOk())
               .andExpect(view().name("dashboard"));
    }
    
    @Test
    @WithMockUser
    public void logoutRedirectsToLoginPage() throws Exception {
        mockMvc.perform(logout())
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/login?logout"));
    }
}
