package ch.pingu.ui;

import ch.pingu.AppContext;
import ch.pingu.ui.navigation.NavigationController;
import ch.pingu.ui.views.ViewManager;
import javafx.scene.layout.BorderPane;

public class MainLayout {
    
    private final BorderPane root;
    private final ViewManager viewManager;
    private final NavigationController navigationController;
    
    public MainLayout() {
        root = new BorderPane();
        viewManager = new ViewManager(root);
        navigationController = new NavigationController(viewManager);
        
        initializeLayout();
    }
    
    private void initializeLayout() {
        root.setLeft(navigationController.getNavigationPanel());
        
        AppContext context = AppContext.getInstance();
        if (context.getCurrentUser() == null) {
            viewManager.navigateToLogin(() -> {
                navigationController.updateNavigationForUser(context.getCurrentUser());
                viewManager.navigateToHome();
            });
        } else {
            navigationController.updateNavigationForUser(context.getCurrentUser());
            viewManager.navigateToHome();
        }
    }
    
    public BorderPane getRoot() {
        return root;
    }
}

