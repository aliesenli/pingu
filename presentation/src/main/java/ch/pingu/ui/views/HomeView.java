package ch.pingu.ui.views;

import ch.pingu.ui.components.labels.InfoLabel;
import ch.pingu.ui.components.labels.SubtitleLabel;
import ch.pingu.ui.components.labels.TitleLabel;

public class HomeView extends BaseView {
    
    @Override
    protected void buildView() {
        TitleLabel title = new TitleLabel("Welcome to Pingu");
        
        SubtitleLabel subtitle = new SubtitleLabel("This is your home page");
        
        InfoLabel description = new InfoLabel(
            "Use the navigation menu on the left to explore different sections of the application."
        );
        description.setMaxWidth(600);
        
        container.getChildren().addAll(title, subtitle, description);
    }
}
