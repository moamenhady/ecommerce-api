package dev.moamenhady.ecommerce.api.repository;

import dev.moamenhady.ecommerce.api.model.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
