package ch.pingu.ui.components.inputs;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.ComboBox;

public class StyledComboBox<T> extends ComboBox<T> {
    
    public StyledComboBox() {
        super();
        applyStyle();
    }
    
    private void applyStyle() {
        setPrefHeight(35);
        setPrefWidth(200);
        setStyle(UIColors.inputStyle());
    }
    
    public StyledComboBox<T> withWidth(double width) {
        setPrefWidth(width);
        return this;
    }
}
