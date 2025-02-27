package dev.moamenhady.ecommerce.api.service;

import dev.moamenhady.ecommerce.api.model.domain.Role;
import dev.moamenhady.ecommerce.api.model.domain.User;
import dev.moamenhady.ecommerce.api.model.enums.RoleEnum;
import dev.moamenhady.ecommerce.api.repository.RoleRepository;
import dev.moamenhady.ecommerce.api.repository.UserRepository;
import dev.moamenhady.ecommerce.api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }

    public String register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        Role role = roleRepository.findByName(RoleEnum.ROLE_CLIENT)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(role));

        User savedUser = userRepository.save(user);

        return jwtService.generateToken(savedUser);
    }

    public String authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return jwtService.generateToken(user);
    }

    public void update(String name) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setName(name);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
