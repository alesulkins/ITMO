package ru.alesulkins.auth.dto;

public record LoginResponse(UserDto user, String token) {
}
