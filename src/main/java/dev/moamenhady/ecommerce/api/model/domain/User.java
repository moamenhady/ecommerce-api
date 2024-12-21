package dev.moamenhady.ecommerce.api.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "\"user\"")
public class User implements UserDetails {

    public static final String NAME_PATTERN = "^[a-zA-Z\\s]{3,50}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Email(message = "Email address is not valid")
    private String email;

    @NotBlank
    private String password;

    @Pattern(regexp = NAME_PATTERN, message = "Name is not valid")
    private String name;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    public User() {
    }

    public User(Long id, String email, String password, String name, LocalDateTime createdAt, LocalDateTime updatedAt, Set<Role> roles, List<Token> tokens) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
        this.tokens = tokens;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof User user)) return false;

        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getName(), user.getName()) && Objects.equals(getCreatedAt(), user.getCreatedAt()) && Objects.equals(getUpdatedAt(), user.getUpdatedAt()) && Objects.equals(getRoles(), user.getRoles());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getEmail());
        result = 31 * result + Objects.hashCode(getPassword());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getCreatedAt());
        result = 31 * result + Objects.hashCode(getUpdatedAt());
        result = 31 * result + Objects.hashCode(getRoles());
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", roles=" + roles +
                '}';
    }
}
