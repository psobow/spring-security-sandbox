package com.sobow.secureweb;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sobow.secureweb.security.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityAuthorizationTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserDetailsManager userDetailsManager;
    
    @Test
    public void userCannotDeleteAnotherUser() throws Exception {
        CustomUserDetails userOne = TestDataUtil.createTestUser("userOne", 123, "ROLE_USER");
        CustomUserDetails userTwo = TestDataUtil.createTestUser("userTwo", 123, "ROLE_USER");
        userDetailsManager.createUser(userTwo);
        assertThat(userDetailsManager.userExists(userTwo.getUsername())).isTrue();
        
        // with(user(...)) will make userone is authenticated for this request
        
        mockMvc.perform(post("/admin/users/delete")
                            .with(user(userOne))
                            .with(csrf())
                            .formField("username", userTwo.getUsername()))
               .andExpect(status().isForbidden());
    }
    
    @Test
    public void adminWithUserDeleteAuthCanDeleteAnotherUser() throws Exception {
        CustomUserDetails userOneAdmin = TestDataUtil.createTestUser("userOne", 123, "ROLE_ADMIN", "USER_DELETE");
        CustomUserDetails userTwo = TestDataUtil.createTestUser("userTwo", 123, "ROLE_USER");
        userDetailsManager.createUser(userTwo);
        assertThat(userDetailsManager.userExists(userTwo.getUsername())).isTrue();
        
        // with(user(...)) will make userone is authenticated for this request
        
        mockMvc.perform(post("/admin/users/delete")
                            .with(user(userOneAdmin))
                            .with(csrf())
                            .formField("username", userTwo.getUsername()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    public void userWithNineInSalaryCanViewDashboard() throws Exception {
        CustomUserDetails userOne = TestDataUtil.createTestUser("userOne", 999, "ROLE_USER");
        
        mockMvc.perform(get("/")
                            .with(user(userOne)))
               .andExpect(status().isOk());
    }
    
    @Test
    public void userWithoutNineInSalaryCannotViewDashboard() throws Exception {
        CustomUserDetails userOne = TestDataUtil.createTestUser("userOne", 123, "ROLE_USER");
        
        mockMvc.perform(get("/")
                            .with(user(userOne)))
               .andExpect(status().isForbidden());
    }
}
