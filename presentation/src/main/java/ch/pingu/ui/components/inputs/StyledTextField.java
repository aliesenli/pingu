package ch.pingu.ui.components.inputs;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.TextField;


public class StyledTextField extends TextField {
    
    public StyledTextField() {
        super();
        applyStyle();
    }
    
    public StyledTextField(String promptText) {
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
