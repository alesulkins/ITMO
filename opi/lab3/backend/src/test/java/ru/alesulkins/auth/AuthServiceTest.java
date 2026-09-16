package ru.alesulkins.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.alesulkins.auth.dto.LoginResponse;
import ru.alesulkins.auth.dto.RegisterRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
class AuthServiceTest {

@Mock
private UserRepository repository;
@Mock
private PasswordEncoder passwordEncoder;

@InjectMocks
private AuthService service;
private UserEntity existingUser;

@BeforeEach
void setUp() {
    existingUser = new UserEntity();
    existingUser.setUsername("alesulkins");
    existingUser.setPasswordHash("password");
    when(repository.save(any(UserEntity.class))).thenAnswer(invokation -> invokation.getArgument(0));
    when(passwordEncoder.encode(anyString())).thenReturn("hashed password");
}
@Test
void register_emptyUsername_throws() {
    assertThrows(IllegalArgumentException.class, () -> 
        service.register(new RegisterRequest("", "password")));
}
@Test
void register_validData_returnsLginResponse() {
    when(repository.findByUsername("alesulkins")).thenReturn(Optional.empty());
    LoginResponse response = service.register(new RegisterRequest("alesulkins", "password"));
    assertNotNull(response.token(), "При регистрации должен возвращаться токен");
    assertFalse(response.token().isBlank(), "При регистрации должен возвращаться НЕПУСТОЙ токен");
    assertEquals("alesulkins", response.user().username(), "В ответе должен быть правильный username");
}
}