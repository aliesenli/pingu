package ch.pingu.domain.util;

import ch.pingu.domain.service.AuthenticationService;

/**
 * Utility to generate password hashes
 */
public class PasswordHashUtil {
    
    public static void main(String[] args) {
        AuthenticationService authService = new AuthenticationService();
        
        String password = "password";
        String hash = authService.hashPassword(password);
        
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
    }
}
