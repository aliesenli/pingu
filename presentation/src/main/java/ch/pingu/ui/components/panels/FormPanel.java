package ch.pingu.ui.components.panels;

import ch.pingu.ui.components.UIColors;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class FormPanel extends GridPane {
    
    public FormPanel() {
        this(15, 15);
    }
    
    public FormPanel(double hGap, double vGap) {
        super();
        setHgap(hGap);
        setVgap(vGap);
        setPadding(new Insets(20));
        setStyle(UIColors.cardStyle());
    }
    
    public FormPanel withMaxWidth(double width) {
        setMaxWidth(width);
        return this;
    }
}
