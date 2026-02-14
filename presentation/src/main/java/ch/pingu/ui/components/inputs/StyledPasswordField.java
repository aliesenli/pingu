package ch.pingu.ui.components.inputs;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.PasswordField;


public class StyledPasswordField extends PasswordField {
    
    public StyledPasswordField() {
        super();
        applyStyle();
    }
    
    public StyledPasswordField(String promptText) {
        super();
        setPromptText(promptText);
        applyStyle();
    }
    
    private void applyStyle() {
        setPrefHeight(35);
        setStyle(UIColors.inputStyle());
    }
    
    public void showError() {
        setStyle(UIColors.inputErrorStyle());
    }
    
    public void clearError() {
        applyStyle();
    }
}
