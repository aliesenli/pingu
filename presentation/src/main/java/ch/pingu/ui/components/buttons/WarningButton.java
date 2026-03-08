package ch.pingu.ui.components.buttons;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Button;

public class WarningButton extends Button {
    
    public WarningButton(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setStyle(UIColors.buttonStyle(UIColors.WARNING, UIColors.ON_WARNING));
        
        setOnMouseEntered(e -> setStyle(
            UIColors.buttonStyle(UIColors.WARNING_HOVER, UIColors.ON_WARNING)
        ));
        
        setOnMouseExited(e -> applyStyle());
    }
}
