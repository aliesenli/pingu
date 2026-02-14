package ch.pingu.ui.components.labels;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SectionLabel extends Label {
    
    public SectionLabel(String text) {
        super(text);
        setFont(Font.font("System", FontWeight.BOLD, 18));
        setStyle("-fx-text-fill: " + UIColors.TEXT_PRIMARY + ";");
    }
}
