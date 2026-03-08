package ch.pingu.ui.components.buttons;

import ch.pingu.ui.components.UIColors;
import javafx.scene.control.Button;

public class PrimaryButton extends Button {
    
    public PrimaryButton(String text) {
        super(text);
        applyStyle();
    }
    
    private void applyStyle() {
        setStyle(UIColors.buttonStyle(UIColors.PRIMARY, UIColors.ON_PRIMARY));
        
        setOnMouseEntered(e -> setStyle(
            UIColors.buttonStyle(UIColors.PRIMARY_HOVER, UIColors.ON_PRIMARY)
        ));
        
        setOnMouseExited(e -> applyStyle());
    }
}
