package dev.moamenhady.ecommerce.api.model.dto;

public record ProductUpdateDto(String name, String description, double price, boolean active) {
}
