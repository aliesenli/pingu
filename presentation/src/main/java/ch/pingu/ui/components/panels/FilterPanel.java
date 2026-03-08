package ch.pingu.ui.components.panels;

import ch.pingu.ui.components.UIColors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class FilterPanel extends HBox {
    
    public FilterPanel() {
        this(15);
    }
    
    public FilterPanel(double spacing) {
        super(spacing);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);
        setStyle(
            String.format("-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1;",
                UIColors.SURFACE, UIColors.BORDER)
        );
    }
}
