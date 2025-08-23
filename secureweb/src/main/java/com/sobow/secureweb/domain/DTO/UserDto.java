package com.sobow.secureweb.domain.DTO;

import java.util.List;

public record UserDto(
    String username,
    Long salary,
    List<String> authorities
) {

}
