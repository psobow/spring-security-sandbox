package com.sobow.secureweb;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobow.secureweb.domain.DTO.JwtAuthRequestDto;
import com.sobow.secureweb.domain.DTO.JwtAuthResponseDto;
import com.sobow.secureweb.domain.DTO.RefreshTokenRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    public void testAuthenticationFlow() throws Exception {
        JwtAuthRequestDto jwtAuthRequestDto = new JwtAuthRequestDto("user", "password");
        
        String jwtAuthRequestJson = objectMapper.writeValueAsString(jwtAuthRequestDto);
        
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(jwtAuthRequestJson)
                                                  .with(csrf()))
                                     .andExpect(status().isOk())
                                     .andReturn();
        
        String loginResponseContent = mvcResult.getResponse().getContentAsString();
        
        JwtAuthResponseDto jwtAuthResponseDto = objectMapper.readValue(loginResponseContent, JwtAuthResponseDto.class);
        
        String accessTokenValue = jwtAuthResponseDto.access();
        
        mockMvc.perform(get("/api/users/me")
                            .header("Authorization", "Bearer " + accessTokenValue))
               .andExpect(status().isOk());
    }
    
    @Test
    public void testRefreshFlow() throws Exception {
        JwtAuthRequestDto jwtAuthRequestDto = new JwtAuthRequestDto("user", "password");
        String jwtAuthRequestJson = objectMapper.writeValueAsString(jwtAuthRequestDto);
        
        MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(jwtAuthRequestJson)
                                                  .with(csrf()))
                                     .andExpect(status().isOk())
                                     .andReturn();
        
        String loginResponseContent = mvcResult.getResponse().getContentAsString();
        JwtAuthResponseDto accessResponse = objectMapper.readValue(loginResponseContent, JwtAuthResponseDto.class);
        String refreshTokenValue = accessResponse.refresh();
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(refreshTokenValue);
        String refreshTokenJson = objectMapper.writeValueAsString(refreshTokenRequest);
        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(refreshTokenJson)
                                                  .with(csrf()))
                                     .andExpect(status().isOk())
                                     .andReturn();
        
        String refreshResponseContent = refreshResult.getResponse().getContentAsString();
        JwtAuthResponseDto refreshResponse = objectMapper.readValue(refreshResponseContent, JwtAuthResponseDto.class);
        
        String newAccessToken = refreshResponse.access();
        mockMvc.perform(get("/api/users/me")
                            .header("Authorization", "Bearer " + newAccessToken))
               .andExpect(status().isOk());
        
    }
}
