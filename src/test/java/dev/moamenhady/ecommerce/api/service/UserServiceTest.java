package dev.moamenhady.ecommerce.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import dev.moamenhady.ecommerce.api.model.domain.Role;
import dev.moamenhady.ecommerce.api.model.domain.User;
import dev.moamenhady.ecommerce.api.model.enums.RoleEnum;
import dev.moamenhady.ecommerce.api.repository.RoleRepository;
import dev.moamenhady.ecommerce.api.repository.UserRepository;
import dev.moamenhady.ecommerce.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        role = new Role();
        role.setName(RoleEnum.ROLE_CLIENT);
    }

    @Test
    public void testRegister_Success() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(RoleEnum.ROLE_CLIENT)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // Act
        String token = userService.register("test@example.com", "password");

        // Assert
        assertNotNull(token);
        assertEquals("jwtToken", token);
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findByName(RoleEnum.ROLE_CLIENT);
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    public void testRegister_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register("test@example.com", "password");
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(roleRepository, never()).findByName(any(RoleEnum.class));
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    public void testAuthenticate_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // Act
        String token = userService.authenticate("test@example.com", "password");

        // Assert
        assertNotNull(token);
        assertEquals("jwtToken", token);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    public void testAuthenticate_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.authenticate("test@example.com", "password");
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    public void testUpdate_Success() {
        // Arrange
        User authenticatedUser = new User();
        authenticatedUser.setEmail("test@example.com");
        authenticatedUser.setName("oldName");
        authenticatedUser.setUpdatedAt(LocalDateTime.now().minusDays(1));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authenticatedUser, null)
        );

        when(userRepository.save(any(User.class))).thenReturn(authenticatedUser);

        // Act
        userService.update("newName");

        // Assert
        assertEquals("newName", authenticatedUser.getName());
        assertTrue(authenticatedUser.getUpdatedAt().isAfter(LocalDateTime.now().minusSeconds(1)));
        verify(userRepository, times(1)).save(authenticatedUser);
    }
}