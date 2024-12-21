package dev.moamenhady.ecommerce.api.controller;

import dev.moamenhady.ecommerce.api.model.dto.UserAuthDto;
import dev.moamenhady.ecommerce.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserAuthDto request) {
        String token = userService.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserAuthDto request) {
        String token = userService.authenticate(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PutMapping ("/update")
    public ResponseEntity<String> updateName(@RequestParam String name) {
        userService.update(name);
        return ResponseEntity.status(HttpStatus.OK).body("Name updated successfully");
    }

}
