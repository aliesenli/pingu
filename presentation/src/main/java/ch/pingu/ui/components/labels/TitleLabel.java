package ch.pingu.ui.components.labels;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TitleLabel extends Label {
    
    public TitleLabel(String text) {
        super(text);
        setFont(Font.font("System", FontWeight.BOLD, 32));
        setStyle("-fx-text-fill: " + UIColors.TEXT_PRIMARY + ";");
    }
}
