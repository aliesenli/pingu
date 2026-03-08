package ch.pingu.ui.views;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public class ViewManager {
    
    private final BorderPane root;
    
    public ViewManager(BorderPane root) {
        this.root = root;
    }
    
    private void switchView(Region view) {
        root.setCenter(view);
    }
    
    public void navigateToHome() {
        switchView(new HomeView().getView());
    }
    
    public void navigateToAbout() {
        switchView(new AboutView().getView());
    }
    
    public void navigateToCurrencyConverter() {
        switchView(new CurrencyConverterView().getView());
    }
    
    public void navigateToTransactions() {
        switchView(new TransactionListView().getView());
    }
    
    public void navigateToExchangeRates() {
        switchView(new ExchangeRateManagementView().getView());
    }
    
    public void navigateToLogin(Runnable onLoginSuccess) {
        switchView(new LoginView(user -> {
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        }).getView());
    }
}
