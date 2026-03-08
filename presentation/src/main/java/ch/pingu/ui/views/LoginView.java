package ch.pingu.ui.views;

import ch.pingu.AppContext;
import ch.pingu.domain.model.User;
import ch.pingu.domain.service.AuthenticationService;
import ch.pingu.ui.components.buttons.PrimaryButton;
import ch.pingu.ui.components.inputs.StyledPasswordField;
import ch.pingu.ui.components.inputs.StyledTextField;
import ch.pingu.ui.components.labels.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;
import java.util.function.Consumer;

public class LoginView extends BaseView {
    
    private StyledTextField usernameField;
    private StyledPasswordField passwordField;
    private ErrorLabel messageLabel;
    private Consumer<User> onLoginSuccess;
    
    public LoginView(Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }
    
    @Override
    protected void buildView() {
        usernameField = new StyledTextField("Enter your username");
        passwordField = new StyledPasswordField("Enter your password");
        messageLabel = new ErrorLabel();
        
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(400);
        container.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        TitleLabel title = new TitleLabel("Pingu Finance");
        SubtitleLabel subtitle = new SubtitleLabel("Currency Transaction Management");
        FieldLabel usernameLabel = new FieldLabel("Username");
        FieldLabel passwordLabel = new FieldLabel("Password");
        
        PrimaryButton loginButton = new PrimaryButton("Login");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(40);
        loginButton.setOnAction(e -> handleLogin());
        
        passwordField.setOnAction(e -> handleLogin());
        
        InfoLabel infoLabel = new InfoLabel("Demo: admin/password or consultant1/password");
        infoLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        container.getChildren().addAll(
            title,
            subtitle,
            separator,
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            messageLabel,
            loginButton,
            new Label(""),
            infoLabel
        );
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter both username and password", false);
            return;
        }
        
        AppContext context = AppContext.getInstance();
        Optional<User> userOpt = context.getUserRepository().findByUsername(username);
        
        if (userOpt.isEmpty()) {
            showMessage("User not found", false);
            return;
        }
        
        User user = userOpt.get();
        AuthenticationService.AuthenticationResult result = 
            context.getAuthenticationService().authenticate(user, password);
        
        if (result.isSuccess()) {
            context.setCurrentUser(user);
            if (onLoginSuccess != null) {
                onLoginSuccess.accept(user);
            }
        } else {
            showMessage(result.getMessage(), false);
        }
    }
    
    private void showMessage(String message, boolean success) {
        if (success) {
            messageLabel.setStyle("-fx-text-fill: #27ae60;");
            messageLabel.setText(message);
            messageLabel.setVisible(true);
        } else {
            messageLabel.showError(message);
        }
    }
}
