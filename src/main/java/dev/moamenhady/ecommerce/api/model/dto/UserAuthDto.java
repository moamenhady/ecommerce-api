package dev.moamenhady.ecommerce.api.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserAuthDto(@Email @NotBlank String email, @NotBlank String password) {
}
