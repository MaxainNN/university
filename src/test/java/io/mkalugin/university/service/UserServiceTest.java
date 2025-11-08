package io.mkalugin.university.service;

import io.mkalugin.university.entity.User;
import io.mkalugin.university.exception.AuthenticationFailedException;
import io.mkalugin.university.exception.EmailExistException;
import io.mkalugin.university.exception.UserExistException;
import io.mkalugin.university.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тест на {@link UserService}
 *
 * <p> Сервис для регистрации
 * и авторизации пользователя </p>
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User service tests")
class UserServiceTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpSession httpSession;

    @InjectMocks
    private UserService userService;

    @Test
    @Order(1)
    @DisplayName("Success registration")
    void registerUserSuccess() {
        log.info("registerUserSuccess test started");

        String username = "john";
        String email = "john@example.com";
        String password = "pass123";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPass");

        User savedUser = new User(username, email, "encodedPass");
        savedUser.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(username, email, password);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals("encodedPass", result.getPassword());

        verify(userRepository).save(any(User.class));

        log.info("registerUserSuccess test finished");
    }

    @Test
    @Order(2)
    @DisplayName("UsernameExistsException throws if user exists")
    void registerUserUsernameExistsThrowsException() {
        log.info("registerUserUsernameExistsThrowsException test started");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(UserExistException.class,
                () -> userService.registerUser("john", "john@example.com", "pass123"));

        log.info("registerUserUsernameExistsThrowsException test finished");
    }

    @Test
    @Order(3)
    @DisplayName("EmailExistsException throws if user exists")
    void registerUserEmailExistsThrowsException() {
        log.info("registerUserEmailExistsThrowsException test started");

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(EmailExistException.class,
                () -> userService.registerUser("john", "john@example.com", "pass123"));

        log.info("registerUserEmailExistsThrowsException test finished");
    }

    @Test
    @Order(4)
    @DisplayName("Success authentication")
    void authenticateUserSuccess() {
        log.info("authenticateUserSuccess test started");

        String username = "john";
        String password = "pass123";

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(servletRequest.getSession(true)).thenReturn(httpSession);

        userService.authenticateUser(username, password, servletRequest);

        verify(servletRequest).getSession(true);
        verify(httpSession).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());

        log.info("authenticateUserSuccess test finished");
    }

    @Test
    @Order(5)
    @DisplayName("AuthenticationFailedException throws if bad creds")
    void authenticateUserFailThrowsException() {
        log.info("authenticateUserFailThrowsException test started");

        String username = "john";
        String password = "wrong";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad creds"));

        assertThrows(AuthenticationFailedException.class,
                () -> userService.authenticateUser(username, password, servletRequest));

        log.info("authenticateUserFailThrowsException test finished");
    }

    @Test
    @Order(6)
    @DisplayName("Update user success")
    void updateUserSuccess() {
        log.info("updateUserSuccess test started");

        Long userId = 1L;
        User existingUser = new User("oldName", "old@example.com", "oldPass");
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("newName")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateUser(userId, "newName", "new@example.com", "newPass");

        assertEquals("newName", updated.getUsername());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("encodedNewPass", updated.getPassword());

        log.info("updateUserSuccess test finished");
    }
}
