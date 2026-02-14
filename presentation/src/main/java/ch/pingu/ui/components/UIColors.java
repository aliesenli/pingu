package ch.pingu.ui.components;

public final class UIColors {
    
    private UIColors() {}
    
    public static final String PRIMARY = "#1abc9c";
    public static final String PRIMARY_HOVER = "#16a085";
    public static final String ON_PRIMARY = "white";
    
    public static final String SECONDARY = "#3498db";
    public static final String SECONDARY_HOVER = "#2980b9";
    public static final String ON_SECONDARY = "white";
    
    public static final String DANGER = "#e74c3c";
    public static final String DANGER_HOVER = "#c0392b";
    public static final String ON_DANGER = "white";
    
    public static final String WARNING = "#f39c12";
    public static final String WARNING_HOVER = "#e67e22";
    public static final String ON_WARNING = "white";
    
    public static final String INFO = "#9b59b6";
    public static final String INFO_HOVER = "#8e44ad";
    public static final String ON_INFO = "white";
    
    public static final String NEUTRAL = "#95a5a6";
    public static final String NEUTRAL_HOVER = "#7f8c8d";
    public static final String ON_NEUTRAL = "white";
    
    public static final String SUCCESS = "#27ae60";
    
    public static final String TEXT_PRIMARY = "#2c3e50";
    public static final String TEXT_SECONDARY = "#7f8c8d";
    public static final String TEXT_TERTIARY = "#95a5a6";
    
    public static final String SURFACE = "white";
    public static final String BACKGROUND = "#ecf0f1";
    public static final String BORDER = "#bdc3c7";
    
    public static String buttonStyle(String bgColor, String textColor) {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 4;",
            bgColor, textColor
        );
    }
    
    public static String inputStyle() {
        return "-fx-font-size: 14px; " +
               "-fx-padding: 4px 10px; " +
               "-fx-background-radius: 4; " +
               "-fx-border-radius: 4;";
    }
    
    public static String inputErrorStyle() {
        return inputStyle() + 
               String.format(" -fx-border-color: %s; -fx-border-width: 2px;", DANGER);
    }
    
    public static String cardStyle() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 4; " +
            "-fx-background-radius: 4;",
            SURFACE, BORDER
        );
    }
    
    public static String resultPanelStyle() {
        return String.format(
            "-fx-background-color: %s; " +
            "-fx-border-radius: 4; " +
            "-fx-background-radius: 4;",
            BACKGROUND
        );
    }
}
