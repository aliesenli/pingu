package ch.pingu.ui.components.inputs;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.TextArea;


public class StyledTextArea extends TextArea {
    
    public StyledTextArea() {
        super();
        applyStyle();
    }
    
    public StyledTextArea(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-family: monospace; " +
            "-fx-padding: 10px; " +
            "-fx-background-radius: 4; " +
            "-fx-border-radius: 4;"
        );
    }
    
    public StyledTextArea asReadOnly() {
        setEditable(false);
        setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-family: monospace; " +
            "-fx-padding: 10px; " +
            "-fx-background-radius: 4; " +
            "-fx-border-radius: 4; " +
            String.format("-fx-background-color: %s;", UIColors.BACKGROUND)
        );
        return this;
    }
}
