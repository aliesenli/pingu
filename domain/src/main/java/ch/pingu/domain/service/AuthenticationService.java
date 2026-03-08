package ch.pingu.domain.service;

import ch.pingu.domain.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Domain service for authentication operations
 */
public class AuthenticationService {
    
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    public boolean verifyPassword(String password, User user) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(user.getPasswordHash());
    }
    
    public AuthenticationResult authenticate(User user, String password) {
        if (user == null) {
            return new AuthenticationResult(false, "User not found");
        }
        
        if (verifyPassword(password, user)) {
            return new AuthenticationResult(true, "Authentication successful");
        } else {
            return new AuthenticationResult(false, "Invalid password");
        }
    }
    
    public static class AuthenticationResult {
        private final boolean success;
        private final String message;
        
        public AuthenticationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
