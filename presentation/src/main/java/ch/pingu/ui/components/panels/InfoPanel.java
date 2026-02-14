package ch.pingu.ui.components.panels;

import ch.pingu.ui.components.UIColors;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class InfoPanel extends VBox {
    
    public InfoPanel() {
        this(10);
    }
    
    public InfoPanel(double spacing) {
        super(spacing);
        setPadding(new Insets(10));
        setStyle(
            String.format("-fx-background-color: %s; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;",
                UIColors.BACKGROUND)
        );
    }
}
