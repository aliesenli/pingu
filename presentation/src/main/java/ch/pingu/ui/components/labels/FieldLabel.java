package ch.pingu.ui.components.labels;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class FieldLabel extends Label {
    
    public FieldLabel(String text) {
        super(text);
        setFont(Font.font("System", FontWeight.BOLD, 14));
        setStyle("-fx-text-fill: " + UIColors.TEXT_PRIMARY + ";");
    }
}
