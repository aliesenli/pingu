package ch.pingu.ui.components.labels;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ErrorLabel extends Label {
    
    public ErrorLabel() {
        super();
        applyStyle();
    }
    
    public ErrorLabel(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setFont(Font.font("System", FontWeight.NORMAL, 14));
        setStyle("-fx-text-fill: " + UIColors.DANGER + ";");
        setWrapText(true);
        setVisible(false); // Hidden by default
    }
    
    public void showError(String message) {
        setText(message);
        setVisible(true);
    }
    
    public void hideError() {
        setVisible(false);
    }
}
