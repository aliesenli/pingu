package ch.pingu.domain.util;

import ch.pingu.domain.service.AuthenticationService;

/**
 * Utility to generate password hashes
 *
 * usage cli:
 * cd domain
 * mvn compile
 * java -cp target/classes ch.pingu.domain.util.PasswordHashUtil
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
