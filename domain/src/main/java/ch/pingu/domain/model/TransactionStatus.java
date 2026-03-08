package ch.pingu.domain.model;

/**
 * Status of a currency transaction
 */
public enum TransactionStatus {
    NOT_STARTED("Not Started"),
    EXECUTED("Executed"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    REVERTED("Reverted");
    
    private final String displayName;
    
    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
