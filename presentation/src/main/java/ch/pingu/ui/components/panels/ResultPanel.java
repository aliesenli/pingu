package ch.pingu.ui.components.panels;

import ch.pingu.ui.components.UIColors;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class ResultPanel extends VBox {
    
    public ResultPanel() {
        this(10);
    }
    
    public ResultPanel(double spacing) {
        super(spacing);
        setPadding(new Insets(20));
        setStyle(UIColors.resultPanelStyle());
    }
    
    public ResultPanel withMaxWidth(double width) {
        setMaxWidth(width);
        return this;
    }
}
