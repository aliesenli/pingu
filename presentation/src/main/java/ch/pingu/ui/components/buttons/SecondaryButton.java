package ch.pingu.ui.components.buttons;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Button;

public class SecondaryButton extends Button {
    
    public SecondaryButton(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setStyle(UIColors.buttonStyle(UIColors.SECONDARY, UIColors.ON_SECONDARY));
        
        setOnMouseEntered(e -> setStyle(
            UIColors.buttonStyle(UIColors.SECONDARY_HOVER, UIColors.ON_SECONDARY)
        ));
        
        setOnMouseExited(e -> applyStyle());
    }
}
