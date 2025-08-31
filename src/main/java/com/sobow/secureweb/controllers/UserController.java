package com.sobow.secureweb.controllers;

import com.sobow.secureweb.domain.Authority;
import com.sobow.secureweb.domain.DTO.CreateUserRequestDto;
import com.sobow.secureweb.domain.DTO.UpdateUserPasswordRequestDto;
import com.sobow.secureweb.domain.DTO.UserDto;
import com.sobow.secureweb.domain.User;
import com.sobow.secureweb.security.CustomUserDetails;
import com.sobow.secureweb.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping(path = "/me")
    public ResponseEntity<UserDto> getLoggedInUser(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        UserDto userDto = entityToDto(user);
        return ResponseEntity.ok(userDto);
    }
    
    @PostMapping(path = "/me/passwords")
    public ResponseEntity<Void> changeLoggedInUserPassword(
        @RequestBody UpdateUserPasswordRequestDto requestDto) {
        userService.changePassword(requestDto.oldPassword(), requestDto.newPassword());
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        List<User> userList = userService.listUsers();
        List<UserDto> userDtoList = userList.stream().map(this::entityToDto).toList();
        return ResponseEntity.ok(userDtoList);
    }
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequestDto dto) {
        User user = userService.createUser(
            dto.username(),
            dto.password(),
            new BigDecimal(dto.salary()),
            dto.roles().toArray(new String[0])
        );
        return new ResponseEntity<>(entityToDto(user), HttpStatus.CREATED);
    }
    
    @DeleteMapping(path = "/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
    
    private UserDto entityToDto(User user) {
        return new UserDto(
            user.getUsername(),
            user.getProfile().getSalary().longValue(),
            user.getAuthorities()
                .stream()
                .map(Authority::getAuthority)
                .toList());
    }
}
