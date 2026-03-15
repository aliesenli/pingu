package ch.pingu.infrastructure.repository;

import ch.pingu.domain.model.User;
import ch.pingu.domain.model.UserRole;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserRepository(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Optional<User> findByUsername(String username, String token) {
        return findAll(token).stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public Optional<User> findById(String id, String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/users/" + id))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) return Optional.empty();
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            return Optional.of(mapToDomain(objectMapper.readValue(response.body(), UserDTO.class)));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user " + id, e);
        }
    }

    public List<User> findAll(String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/users"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new RuntimeException("HTTP " + response.statusCode());
            List<UserDTO> dtos = objectMapper.readValue(response.body(), new TypeReference<>() {});
            return dtos.stream().map(this::mapToDomain).toList();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users", e);
        }
    }

    private User mapToDomain(UserDTO dto) {
        return new User(
                dto.id,
                dto.username,
                "", // password hash not returned by backend
                UserRole.valueOf(dto.role),
                dto.createdAt
        );
    }

    // DTO needs no-arg constructor for Jackson deserialization
    static class UserDTO {
        public UserDTO() {}
        public String id;
        public String username;
        public String role;
        public LocalDateTime createdAt;
    }
}
