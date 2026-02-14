package ch.pingu.ui.components.buttons;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Button;

public class InfoButton extends Button {
    
    public InfoButton(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setStyle(UIColors.buttonStyle(UIColors.INFO, UIColors.ON_INFO));
        
        setOnMouseEntered(e -> setStyle(
            UIColors.buttonStyle(UIColors.INFO_HOVER, UIColors.ON_INFO)
        ));
        
        setOnMouseExited(e -> applyStyle());
    }
}
