package ch.pingu.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public abstract class BaseView {
    
    protected final VBox container;
    
    public BaseView() {
        container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_LEFT);
        container.setStyle("-fx-background-color: #ecf0f1;");
        
        buildView();
    }
    
    protected abstract void buildView();
    
    public VBox getView() {
        return container;
    }
}
