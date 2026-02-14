package ch.pingu.ui.views;

import ch.pingu.ui.components.labels.FieldLabel;
import ch.pingu.ui.components.labels.SubtitleLabel;
import ch.pingu.ui.components.labels.TitleLabel;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class AboutView extends BaseView {
    
    @Override
    protected void buildView() {
        TitleLabel title = new TitleLabel("About Pingu");
        
        Separator separator = new Separator();
        separator.setMaxWidth(500);
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        VBox infoBox = new VBox(10);
        infoBox.setMaxWidth(500);
        
        infoBox.getChildren().addAll(
            createInfoLabel("Version:", "1.0.0"),
            createInfoLabel("Build Date:", "February 13, 2026"),
            createInfoLabel("Java Version:", System.getProperty("java.version")),
            createInfoLabel("JavaFX Version:", System.getProperty("javafx.version", "21.0.1"))
        );
        
        SubtitleLabel description = new SubtitleLabel(
            "Pingu is a modular JavaFX application built with clean architecture principles. " +
            "It features a flexible navigation system and swappable views for easy extensibility."
        );
        description.setWrapText(true);
        description.setMaxWidth(500);
        description.setPadding(new Insets(20, 0, 0, 0));
        
        container.getChildren().addAll(title, separator, infoBox, description);
    }
    
    private VBox createInfoLabel(String label, String value) {
        VBox vbox = new VBox(5);
        
        FieldLabel labelText = new FieldLabel(label);
        SubtitleLabel valueText = new SubtitleLabel(value);
        
        vbox.getChildren().addAll(labelText, valueText);
        return vbox;
    }
}
