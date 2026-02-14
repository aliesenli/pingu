package ch.pingu.ui.components.labels;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class InfoLabel extends Label {
    
    public InfoLabel(String text) {
        super(text);
        setFont(Font.font("System", FontWeight.NORMAL, 14));
        setStyle("-fx-text-fill: " + UIColors.TEXT_TERTIARY + ";");
        setWrapText(true);
    }
}
