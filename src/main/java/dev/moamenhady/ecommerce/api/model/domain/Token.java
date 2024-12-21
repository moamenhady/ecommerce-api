package dev.moamenhady.ecommerce.api.model.domain;

import dev.moamenhady.ecommerce.api.model.enums.TokenTypeEnum;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenTypeEnum tokenType = TokenTypeEnum.BEARER;

    public boolean revoked;

    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    public Token() {
    }

    public Token(Integer id, String token, TokenTypeEnum tokenType, boolean revoked, boolean expired, User user) {
        this.id = id;
        this.token = token;
        this.tokenType = tokenType;
        this.revoked = revoked;
        this.expired = expired;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenTypeEnum getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenTypeEnum tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Token token1)) return false;

        return isRevoked() == token1.isRevoked() && isExpired() == token1.isExpired() && Objects.equals(getId(), token1.getId()) && Objects.equals(getToken(), token1.getToken()) && getTokenType() == token1.getTokenType() && Objects.equals(getUser(), token1.getUser());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getToken());
        result = 31 * result + Objects.hashCode(getTokenType());
        result = 31 * result + Boolean.hashCode(isRevoked());
        result = 31 * result + Boolean.hashCode(isExpired());
        result = 31 * result + Objects.hashCode(getUser());
        return result;
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", tokenType=" + tokenType +
                ", revoked=" + revoked +
                ", expired=" + expired +
                ", user=" + user +
                '}';
    }
}