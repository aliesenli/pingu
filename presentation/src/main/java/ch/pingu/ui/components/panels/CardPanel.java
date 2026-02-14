package ch.pingu.ui.components.panels;

import ch.pingu.ui.components.UIColors;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class CardPanel extends VBox {
    
    public CardPanel() {
        this(20);
    }
    
    public CardPanel(double padding) {
        super(10);
        setPadding(new Insets(padding));
        setStyle(UIColors.cardStyle());
    }
    
    public CardPanel(double spacing, double padding) {
        super(spacing);
        setPadding(new Insets(padding));
        setStyle(UIColors.cardStyle());
    }
    
    public CardPanel withMaxWidth(double width) {
        setMaxWidth(width);
        return this;
    }
}
