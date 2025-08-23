package com.sobow.secureweb.domain.DTO;

public record UpdateUserPasswordRequestDto(String oldPassword, String newPassword) {

}
