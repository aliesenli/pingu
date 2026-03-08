package ch.pingu.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user in the system (Consultant or Admin)
 */
public class User {
    
    private final String id;
    private final String username;
    private final String passwordHash;
    private final UserRole role;
    private final LocalDateTime createdAt;
    
    public User(String id, String username, String passwordHash, UserRole role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }
    
    public static User create(String username, String passwordHash, UserRole role) {
        return new User(
            UUID.randomUUID().toString(),
            username,
            passwordHash,
            role,
            LocalDateTime.now()
        );
    }
    
    public String getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
    
    public boolean isConsultant() {
        return role == UserRole.CONSULTANT;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
