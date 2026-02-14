package ch.pingu.ui.components.buttons;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Button;

public class DangerButton extends Button {
    
    public DangerButton(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setStyle(UIColors.buttonStyle(UIColors.DANGER, UIColors.ON_DANGER));
        
        setOnMouseEntered(e -> setStyle(
            UIColors.buttonStyle(UIColors.DANGER_HOVER, UIColors.ON_DANGER)
        ));
        
        setOnMouseExited(e -> applyStyle());
    }
}
