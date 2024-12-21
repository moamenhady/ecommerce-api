package dev.moamenhady.ecommerce.api.repository;

import java.util.Optional;

import dev.moamenhady.ecommerce.api.model.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);
}
