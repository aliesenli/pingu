package ch.pingu.ui.components.labels;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SubtitleLabel extends Label {
    
    public SubtitleLabel(String text) {
        super(text);
        setFont(Font.font("System", FontWeight.NORMAL, 16));
        setStyle("-fx-text-fill: " + UIColors.TEXT_SECONDARY + ";");
    }
}
