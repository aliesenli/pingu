package ch.pingu.domain.model;

/**
 * Supported currencies in the system
 */
public enum Currency {
    CHF("Swiss Franc", "CHF"),
    EUR("Euro", "EUR"),
    USD("US Dollar", "USD"),
    GBP("British Pound", "GBP"),
    JPY("Japanese Yen", "JPY"),
    CAD("Canadian Dollar", "CAD"),
    AUD("Australian Dollar", "AUD"),
    CNY("Chinese Yuan", "CNY"),
    INR("Indian Rupee", "INR"),
    SEK("Swedish Krona", "SEK");
    
    private final String displayName;
    private final String code;
    
    Currency(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return code;
    }
    
    public static Currency fromCode(String code) {
        for (Currency currency : values()) {
            if (currency.code.equalsIgnoreCase(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency code: " + code);
    }
}
