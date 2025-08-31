package com.sobow.secureweb.domain.DTO;

import java.util.List;

public record CreateUserRequestDto(
    String username,
    String password,
    Long salary,
    List<String> roles
) {

}
