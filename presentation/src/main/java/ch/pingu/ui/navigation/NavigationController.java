package ch.pingu.ui.navigation;

import ch.pingu.AppContext;
import ch.pingu.domain.model.User;
import ch.pingu.ui.views.ViewManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for the navigation sidebar
 */
public class NavigationController {
    
    private final VBox navigationPanel;
    private final ViewManager viewManager;
    private final Label userLabel;
    private final VBox financeSection;
    private final VBox adminSection;
    
    public NavigationController(ViewManager viewManager) {
        this.viewManager = viewManager;
        this.navigationPanel = new VBox(10);
        this.userLabel = new Label();
        this.financeSection = new VBox(5);
        this.adminSection = new VBox(5);
        setupNavigation();
    }
    
    private void setupNavigation() {
        navigationPanel.setPadding(new Insets(20));
        navigationPanel.setStyle("-fx-background-color: #2c3e50; -fx-min-width: 220;");
        navigationPanel.setAlignment(Pos.TOP_CENTER);
        
        Label appTitle = new Label("Pingu Finance");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        appTitle.setStyle("-fx-text-fill: white;");
        
        userLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        userLabel.setStyle("-fx-text-fill: #95a5a6;");
        userLabel.setWrapText(true);
        
        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: #34495e;");
        
        Button homeButton = createNavButton("Home");
        homeButton.setOnAction(e -> viewManager.navigateToHome());
        
        Label financeLabel = new Label("FINANCE");
        financeLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        financeLabel.setStyle("-fx-text-fill: #7f8c8d;");
        financeLabel.setPadding(new Insets(10, 0, 5, 10));
        
        Button converterButton = createNavButton("Currency Converter");
        converterButton.setOnAction(e -> viewManager.navigateToCurrencyConverter());
        
        Button transactionsButton = createNavButton("Transactions");
        transactionsButton.setOnAction(e -> viewManager.navigateToTransactions());
        
        financeSection.getChildren().addAll(financeLabel, converterButton, transactionsButton);
        
        Label adminLabel = new Label("ADMIN");
        adminLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        adminLabel.setStyle("-fx-text-fill: #7f8c8d;");
        adminLabel.setPadding(new Insets(10, 0, 5, 10));
        
        Button exchangeRatesButton = createNavButton("Exchange Rates");
        exchangeRatesButton.setOnAction(e -> viewManager.navigateToExchangeRates());
        
        adminSection.getChildren().addAll(adminLabel, exchangeRatesButton);
        
        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: #34495e;");
        
        Button aboutButton = createNavButton("About");
        aboutButton.setOnAction(e -> viewManager.navigateToAbout());
        
        Button logoutButton = createNavButton("Logout");
        logoutButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10px; " +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnAction(e -> handleLogout());
        
        navigationPanel.getChildren().addAll(
            appTitle,
            userLabel,
            separator1,
            homeButton
        );
        
        navigationPanel.getChildren().add(financeSection);
        
        navigationPanel.getChildren().addAll(
            separator2,
            aboutButton,
            new VBox(), // Spacer
            logoutButton
        );
        
        updateNavigationForUser(null);
    }
    
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle(
            "-fx-background-color: #34495e; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10px; " +
            "-fx-cursor: hand;"
        );
        
        button.setOnMouseEntered(e ->
            button.setStyle(
                "-fx-background-color: #1abc9c; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px; " +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e -> {
            String baseStyle = button.getText().contains("Logout") 
                ? "-fx-background-color: #e74c3c; "
                : "-fx-background-color: #34495e; ";
            button.setStyle(
                baseStyle +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 10px; " +
                "-fx-cursor: hand;"
            );
        });
        
        return button;
    }
    
    public void updateNavigationForUser(User user) {
        if (user == null) {
            userLabel.setText("Not logged in");
            financeSection.setVisible(false);
            financeSection.setManaged(false);
            adminSection.setVisible(false);
            adminSection.setManaged(false);
        } else {
            userLabel.setText(String.format("Logged in as:\n%s (%s)", 
                user.getUsername(), 
                user.getRole().getDisplayName()));
            
            financeSection.setVisible(true);
            financeSection.setManaged(true);
            
            // Show admin section only for admins
            boolean isAdmin = user.isAdmin();
            if (isAdmin && !navigationPanel.getChildren().contains(adminSection)) {
                // Insert admin section after finance section
                int financeIndex = navigationPanel.getChildren().indexOf(financeSection);
                navigationPanel.getChildren().add(financeIndex + 1, adminSection);
            } else if (!isAdmin && navigationPanel.getChildren().contains(adminSection)) {
                navigationPanel.getChildren().remove(adminSection);
            }
            
            adminSection.setVisible(isAdmin);
            adminSection.setManaged(isAdmin);
        }
    }
    
    private void handleLogout() {
        AppContext.getInstance().logout();
        updateNavigationForUser(null);
        viewManager.navigateToLogin(() -> {
            updateNavigationForUser(AppContext.getInstance().getCurrentUser());
            viewManager.navigateToHome();
        });
    }
    
    public VBox getNavigationPanel() {
        return navigationPanel;
    }
}

