package ch.pingu.ui.views;

import ch.pingu.AppContext;
import ch.pingu.domain.model.User;
import ch.pingu.ui.components.buttons.PrimaryButton;
import ch.pingu.ui.components.inputs.StyledPasswordField;
import ch.pingu.ui.components.inputs.StyledTextField;
import ch.pingu.ui.components.labels.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String loginBody = mapper.writeValueAsString(
                        java.util.Map.of("username", username, "password", password));

                String baseUrl = AppContext.getInstance().getBaseUrl();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest tokenRequest = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/auth/token"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                        .build();
                HttpResponse<String> tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

                if (tokenResponse.statusCode() == 401 || tokenResponse.statusCode() == 403) {
                    javafx.application.Platform.runLater(() -> showMessage("Invalid username or password", false));
                    return;
                }
                if (tokenResponse.statusCode() != 200) {
                    javafx.application.Platform.runLater(() -> showMessage("Login failed (HTTP " + tokenResponse.statusCode() + ")", false));
                    return;
                }

                JsonNode tokenJson = mapper.readTree(tokenResponse.body());
                JsonNode tokenNode = tokenJson.get("token");
                JsonNode userIdNode = tokenJson.get("userId");
                if (tokenNode == null || userIdNode == null) {
                    javafx.application.Platform.runLater(() -> showMessage("Unexpected response from server", false));
                    return;
                }
                String token = tokenNode.asText();
                String userId = userIdNode.asText();

                AppContext context = AppContext.getInstance();
                context.setJwtToken(token);

                Optional<User> userOpt = context.getUserRepository().findById(userId, token);
                if (userOpt.isEmpty()) {
                    javafx.application.Platform.runLater(() -> showMessage("Could not load user profile", false));
                    return;
                }

                User user = userOpt.get();
                context.setCurrentUser(user);

                javafx.application.Platform.runLater(() -> {
                    if (onLoginSuccess != null) {
                        onLoginSuccess.accept(user);
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> showMessage("Login error: " + e.getMessage(), false));
            }
        }).start();
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
