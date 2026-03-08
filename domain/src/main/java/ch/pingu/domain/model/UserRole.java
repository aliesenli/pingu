package ch.pingu.domain.model;

/**
 * Roles available in the system
 */
public enum UserRole {
    CONSULTANT("Consultant"),
    ADMIN("Administrator");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
