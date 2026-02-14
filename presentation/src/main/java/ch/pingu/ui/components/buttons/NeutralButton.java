package ch.pingu.ui.components.buttons;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Button;

public class NeutralButton extends Button {
    
    public NeutralButton(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setStyle(UIColors.buttonStyle(UIColors.NEUTRAL, UIColors.ON_NEUTRAL));
        
        setOnMouseEntered(e -> setStyle(
            UIColors.buttonStyle(UIColors.NEUTRAL_HOVER, UIColors.ON_NEUTRAL)
        ));
        
        setOnMouseExited(e -> applyStyle());
    }
}
