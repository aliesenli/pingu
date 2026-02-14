package ch.pingu.ui.views;

import ch.pingu.AppContext;
import ch.pingu.domain.model.Currency;
import ch.pingu.domain.model.ExchangeRateVersion;
import ch.pingu.domain.model.User;
import ch.pingu.ui.components.buttons.NeutralButton;
import ch.pingu.ui.components.buttons.PrimaryButton;
import ch.pingu.ui.components.buttons.SecondaryButton;
import ch.pingu.ui.components.buttons.WarningButton;
import ch.pingu.ui.components.dialogs.DialogUtils;
import ch.pingu.ui.components.inputs.StyledTextArea;
import ch.pingu.ui.components.labels.FieldLabel;
import ch.pingu.ui.components.labels.InfoLabel;
import ch.pingu.ui.components.labels.SectionLabel;
import ch.pingu.ui.components.labels.TitleLabel;
import ch.pingu.ui.components.panels.CardPanel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ExchangeRateManagementView extends BaseView {
    
    private StyledTextArea previewArea;
    private ListView<String> versionsListView;
    private File selectedFile;
    
    @Override
    protected void buildView() {
        this.previewArea = new StyledTextArea();
        this.previewArea.asReadOnly();
        this.versionsListView = new ListView<>();
        
        AppContext context = AppContext.getInstance();
        User currentUser = context.getCurrentUser();
        
        if (currentUser == null || !currentUser.isAdmin()) {
            TitleLabel accessDenied = new TitleLabel("Access Denied: Admin privileges required");
            accessDenied.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 24));
            accessDenied.setStyle("-fx-text-fill: #e74c3c;");
            container.getChildren().add(accessDenied);
            return;
        }
        
        TitleLabel title = new TitleLabel("Exchange Rate Management");
        
        VBox uploadBox = createUploadSection();
        
        VBox versionsBox = createVersionsSection();
        
        container.getChildren().addAll(title, uploadBox, versionsBox);
        
        loadVersions();
    }
    
    private VBox createUploadSection() {
        CardPanel box = new CardPanel();
        
        SectionLabel sectionTitle = new SectionLabel("Upload New Exchange Rates");
        
        InfoLabel instructionLabel = new InfoLabel(
            "Upload a JSON file with exchange rates. Format:\n" +
            "{\n" +
            "  \"versionName\": \"2026-02-13 Daily Rates\",\n" +
            "  \"baseCurrency\": \"CHF\",\n" +
            "  \"rates\": {\n" +
            "    \"CHF\": 1.0,\n" +
            "    \"EUR\": 1.05,\n" +
            "    \"USD\": 1.15\n" +
            "  }\n" +
            "}"
        );
        instructionLabel.setFont(Font.font("Monospace", 12));
        instructionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-background-color: #ecf0f1; -fx-padding: 10;");
        
        HBox fileBox = new HBox(10);
        fileBox.setAlignment(Pos.CENTER_LEFT);

        PrimaryButton selectFileButton = new PrimaryButton("Select JSON File");
        selectFileButton.setOnAction(e -> selectFile());
        
        InfoLabel fileLabel = new InfoLabel("No file selected");
        fileBox.getChildren().addAll(selectFileButton, fileLabel);
        FieldLabel previewLabel = new FieldLabel("Preview:");
        
        previewArea.setPrefRowCount(10);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        PrimaryButton uploadButton = new PrimaryButton("Upload and Activate");
        uploadButton.setOnAction(e -> uploadExchangeRates());
        
        WarningButton validateButton = new WarningButton("Validate Only");
        validateButton.setOnAction(e -> validateFile());
        
        buttonBox.getChildren().addAll(uploadButton, validateButton);
        
        box.getChildren().addAll(
            sectionTitle,
            instructionLabel,
            fileBox,
            previewLabel,
            previewArea,
            buttonBox
        );
        
        return box;
    }
    
    private VBox createVersionsSection() {
        CardPanel box = new CardPanel();
        
        SectionLabel sectionTitle = new SectionLabel("Existing Exchange Rate Versions");
        
        versionsListView.setPrefHeight(200);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        PrimaryButton activateButton = new PrimaryButton("Activate Selected");
        activateButton.setOnAction(e -> activateVersion());
        
        NeutralButton refreshButton = new NeutralButton("Refresh");
        refreshButton.setOnAction(e -> loadVersions());
        
        buttonBox.getChildren().addAll(activateButton, refreshButton);
        
        box.getChildren().addAll(sectionTitle, versionsListView, buttonBox);
        
        return box;
    }
    
    private void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Exchange Rate JSON File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        
        selectedFile = fileChooser.showOpenDialog(container.getScene().getWindow());
        
        if (selectedFile != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(selectedFile);
                previewArea.setText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));
            } catch (Exception e) {
                showError("Failed to read file: " + e.getMessage());
            }
        }
    }
    
    private void validateFile() {
        if (selectedFile == null) {
            showError("Please select a file first");
            return;
        }
        
        try {
            parseExchangeRateFile(selectedFile);
            showSuccess("File is valid!");
        } catch (Exception e) {
            showError("Validation failed: " + e.getMessage());
        }
    }
    
    private void uploadExchangeRates() {
        if (selectedFile == null) {
            showError("Please select a file first");
            return;
        }
        
        try {
            ExchangeRateVersion newVersion = parseExchangeRateFile(selectedFile);
            
            AppContext context = AppContext.getInstance();
            
            context.getExchangeRateRepository().save(newVersion);
            
            context.getExchangeRateRepository().setActiveVersion(newVersion.getId());
            
            showSuccess("Exchange rates uploaded and activated successfully!");
            
            selectedFile = null;
            previewArea.clear();
            loadVersions();
            
        } catch (Exception e) {
            showError("Upload failed: " + e.getMessage());
        }
    }
    
    private ExchangeRateVersion parseExchangeRateFile(File file) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);
        
        if (!root.has("versionName")) {
            throw new IllegalArgumentException("Missing 'versionName' field");
        }
        if (!root.has("baseCurrency")) {
            throw new IllegalArgumentException("Missing 'baseCurrency' field");
        }
        if (!root.has("rates")) {
            throw new IllegalArgumentException("Missing 'rates' field");
        }
        
        String versionName = root.get("versionName").asText();
        String baseCurrencyCode = root.get("baseCurrency").asText();
        Currency baseCurrency = Currency.fromCode(baseCurrencyCode);
        
        JsonNode ratesNode = root.get("rates");
        Map<Currency, Double> rates = new HashMap<>();
        
        ratesNode.fields().forEachRemaining(entry -> {
            try {
                Currency currency = Currency.fromCode(entry.getKey());
                double rate = entry.getValue().asDouble();
                
                if (rate <= 0) {
                    throw new IllegalArgumentException("Rate for " + currency + " must be positive");
                }
                
                rates.put(currency, rate);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid currency: " + entry.getKey(), e);
            }
        });
        
        if (!rates.containsKey(baseCurrency) || Math.abs(rates.get(baseCurrency) - 1.0) > 0.0001) {
            throw new IllegalArgumentException("Base currency rate must be 1.0");
        }
        
        AppContext context = AppContext.getInstance();
        User currentUser = context.getCurrentUser();
        
        return ExchangeRateVersion.create(versionName, baseCurrency, rates, currentUser.getUsername());
    }
    
    private void loadVersions() {
        AppContext context = AppContext.getInstance();
        versionsListView.getItems().clear();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        context.getExchangeRateRepository().findAll().forEach(version -> {
            String display = String.format("%s%s - %s (Base: %s, %d rates)",
                version.isActive() ? "★ " : "  ",
                version.getVersionName(),
                version.getUploadedAt().format(formatter),
                version.getBaseCurrency().getCode(),
                version.getRates().size()
            );
            versionsListView.getItems().add(version.getId() + "|" + display);
        });
    }
    
    private void activateVersion() {
        String selected = versionsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a version to activate");
            return;
        }
        
        String versionId = selected.split("\\|")[0];
        
        AppContext context = AppContext.getInstance();
        context.getExchangeRateRepository().setActiveVersion(versionId);
        
        showSuccess("Exchange rate version activated successfully!");
        loadVersions();
    }
    
    private void showError(String message) {
        DialogUtils.showError("Error", message);
    }
    
    private void showSuccess(String message) {
        DialogUtils.showSuccess(message);
    }
}
