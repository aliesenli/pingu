package ch.pingu.ui.components.labels;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SuccessLabel extends Label {
    
    public SuccessLabel() {
        super();
        applyStyle();
    }
    
    public SuccessLabel(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setFont(Font.font("System", FontWeight.NORMAL, 14));
        setStyle("-fx-text-fill: " + UIColors.SUCCESS + ";");
        setWrapText(true);
        setVisible(false); // Hidden by default
    }
    
    public void showSuccess(String message) {
        setText(message);
        setVisible(true);
    }
    
    public void hideSuccess() {
        setVisible(false);
    }
}
