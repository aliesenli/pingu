package ch.pingu.infrastructure.repository;

import ch.pingu.domain.model.User;
import ch.pingu.domain.model.UserRole;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JSON-based repository for User entities
 */
public class UserRepository {
    
    private final ObjectMapper objectMapper;
    private final String filePath;
    private List<User> users;
    
    public UserRepository(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.users = new ArrayList<>();
        loadUsers();
    }
    
    private void loadUsers() {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                List<UserDTO> dtos = objectMapper.readValue(file, new TypeReference<List<UserDTO>>() {});
                users = dtos.stream()
                    .map(UserDTO::toDomain)
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            } catch (IOException e) {
                System.err.println("Error loading users: " + e.getMessage());
                users = new ArrayList<>();
            }
        }
    }
    
    public Optional<User> findByUsername(String username) {
        return users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst();
    }
    
    public Optional<User> findById(String id) {
        return users.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }
    
    private static class UserDTO {
        public String id;
        public String username;
        public String passwordHash;
        public String role;
        public String createdAt;
        
        public static UserDTO fromDomain(User user) {
            UserDTO dto = new UserDTO();
            dto.id = user.getId();
            dto.username = user.getUsername();
            dto.passwordHash = user.getPasswordHash();
            dto.role = user.getRole().name();
            dto.createdAt = user.getCreatedAt().toString();
            return dto;
        }
        
        public User toDomain() {
            return new User(
                id,
                username,
                passwordHash,
                UserRole.valueOf(role),
                java.time.LocalDateTime.parse(createdAt)
            );
        }
    }
}
