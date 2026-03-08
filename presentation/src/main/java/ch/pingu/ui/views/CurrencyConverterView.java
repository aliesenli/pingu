package ch.pingu.ui.views;

import ch.pingu.AppContext;
import ch.pingu.domain.model.*;
import ch.pingu.domain.service.CurrencyConversionService;
import ch.pingu.ui.components.buttons.NeutralButton;
import ch.pingu.ui.components.buttons.PrimaryButton;
import ch.pingu.ui.components.buttons.SecondaryButton;
import ch.pingu.ui.components.dialogs.DialogUtils;
import ch.pingu.ui.components.inputs.StyledComboBox;
import ch.pingu.ui.components.inputs.StyledTextField;
import ch.pingu.ui.components.labels.FieldLabel;
import ch.pingu.ui.components.labels.TitleLabel;
import ch.pingu.ui.components.panels.FormPanel;
import ch.pingu.ui.components.panels.ResultPanel;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.util.Optional;

public class CurrencyConverterView extends BaseView {
    
    private StyledTextField amountField;
    private StyledComboBox<Currency> sourceCurrencyCombo;
    private StyledComboBox<Currency> targetCurrencyCombo;
    private Label resultLabel;
    private Label exchangeRateLabel;
    private StyledTextField customerIdField;
    private StyledComboBox<TransactionStatus> statusCombo;
    
    @Override
    protected void buildView() {
        TitleLabel title = new TitleLabel("Currency Converter");
        
        FormPanel grid = new FormPanel();
        grid.setMaxWidth(600);
        
        FieldLabel amountLabel = new FieldLabel("Amount:");
        amountField = new StyledTextField("Enter amount");
        amountField.setPrefWidth(200);
        
        FieldLabel sourceLabel = new FieldLabel("From:");
        sourceCurrencyCombo = new StyledComboBox<>();
        sourceCurrencyCombo.setItems(FXCollections.observableArrayList(Currency.values()));
        sourceCurrencyCombo.setValue(Currency.CHF);
        
        FieldLabel targetLabel = new FieldLabel("To:");
        targetCurrencyCombo = new StyledComboBox<>();
        targetCurrencyCombo.setItems(FXCollections.observableArrayList(Currency.values()));
        targetCurrencyCombo.setValue(Currency.EUR);
        
        FieldLabel customerLabel = new FieldLabel("Customer ID:");
        customerIdField = new StyledTextField("Enter customer ID");
        customerIdField.setPrefWidth(200);
        
        FieldLabel statusLabel = new FieldLabel("Status:");
        statusCombo = new StyledComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(
            TransactionStatus.NOT_STARTED,
            TransactionStatus.EXECUTED,
            TransactionStatus.COMPLETED
        ));
        statusCombo.setValue(TransactionStatus.NOT_STARTED);
        
        grid.add(amountLabel, 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(sourceLabel, 0, 1);
        grid.add(sourceCurrencyCombo, 1, 1);
        grid.add(targetLabel, 0, 2);
        grid.add(targetCurrencyCombo, 1, 2);
        grid.add(customerLabel, 0, 3);
        grid.add(customerIdField, 1, 3);
        grid.add(statusLabel, 0, 4);
        grid.add(statusCombo, 1, 4);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(0, 0, 0, 15));
        
        SecondaryButton convertButton = new SecondaryButton("Convert");
        convertButton.setOnAction(e -> handleConvert());
        
        PrimaryButton saveTransactionButton = new PrimaryButton("Save as Transaction");
        saveTransactionButton.setOnAction(e -> handleSaveTransaction());
        
        NeutralButton clearButton = new NeutralButton("Clear");
        clearButton.setOnAction(e -> handleClear());
        
        buttonBox.getChildren().addAll(convertButton, saveTransactionButton, clearButton);
        
        ResultPanel resultsBox = new ResultPanel();
        resultsBox.setMaxWidth(600);
        
        exchangeRateLabel = new Label("Exchange Rate: -");
        exchangeRateLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        
        resultLabel = new Label("Result: -");
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        resultLabel.setStyle("-fx-text-fill: #27ae60;");
        
        resultsBox.getChildren().addAll(exchangeRateLabel, resultLabel);
        
        container.getChildren().addAll(title, grid, buttonBox, resultsBox);
    }
    
    private void handleConvert() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            Currency source = sourceCurrencyCombo.getValue();
            Currency target = targetCurrencyCombo.getValue();
            
            if (amount <= 0) {
                showError("Amount must be greater than 0");
                return;
            }
            
            AppContext context = AppContext.getInstance();
            Optional<ExchangeRateVersion> rateVersionOpt = context.getExchangeRateRepository().findActiveVersion();
            
            if (rateVersionOpt.isEmpty()) {
                showError("No active exchange rate version found");
                return;
            }
            
            ExchangeRateVersion rateVersion = rateVersionOpt.get();
            CurrencyConversionService conversionService = context.getCurrencyConversionService();
            
            Money sourceMoney = new Money(amount, source);
            Money targetMoney = conversionService.convert(sourceMoney, target, rateVersion);
            double exchangeRate = conversionService.getExchangeRateAsDouble(source, target, rateVersion);
            
            exchangeRateLabel.setText(String.format("Exchange Rate: 1 %s = %.6f %s", 
                source.getCode(), exchangeRate, target.getCode()));
            resultLabel.setText(String.format("Result: %s %.2f = %s %.2f", 
                source.getCode(), amount, target.getCode(), targetMoney.getAmount()));
            resultLabel.setStyle("-fx-text-fill: #27ae60;");
            
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
        } catch (Exception e) {
            showError("Conversion failed: " + e.getMessage());
        }
    }
    
    private void handleSaveTransaction() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            Currency source = sourceCurrencyCombo.getValue();
            Currency target = targetCurrencyCombo.getValue();
            String customerId = customerIdField.getText().trim();
            TransactionStatus status = statusCombo.getValue();
            
            if (amount <= 0) {
                showError("Amount must be greater than 0");
                return;
            }
            
            if (customerId.isEmpty()) {
                showError("Customer ID is required");
                return;
            }
            
            AppContext context = AppContext.getInstance();
            User currentUser = context.getCurrentUser();
            
            if (currentUser == null) {
                showError("You must be logged in to save transactions");
                return;
            }
            
            Optional<ExchangeRateVersion> rateVersionOpt = context.getExchangeRateRepository().findActiveVersion();
            
            if (rateVersionOpt.isEmpty()) {
                showError("No active exchange rate version found");
                return;
            }
            
            ExchangeRateVersion rateVersion = rateVersionOpt.get();
            CurrencyConversionService conversionService = context.getCurrencyConversionService();
            
            Money sourceMoney = new Money(amount, source);
            Money targetMoney = conversionService.convert(sourceMoney, target, rateVersion);
            double exchangeRate = conversionService.getExchangeRateAsDouble(source, target, rateVersion);
            
            Transaction transaction = Transaction.create(
                currentUser.getId(),
                customerId,
                sourceMoney,
                targetMoney,
                exchangeRate,
                rateVersion.getId(),
                LocalDateTime.now(),
                currentUser.getUsername()
            );
            
            transaction.updateStatus(status);
            
            context.getTransactionRepository().save(transaction);
            
            showSuccess("Transaction saved successfully!");
            handleClear();
            
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
        } catch (Exception e) {
            showError("Failed to save transaction: " + e.getMessage());
        }
    }
    
    private void handleClear() {
        amountField.clear();
        customerIdField.clear();
        sourceCurrencyCombo.setValue(Currency.CHF);
        targetCurrencyCombo.setValue(Currency.EUR);
        statusCombo.setValue(TransactionStatus.NOT_STARTED);
        exchangeRateLabel.setText("Exchange Rate: -");
        resultLabel.setText("Result: -");
        resultLabel.setStyle("-fx-text-fill: #27ae60;");
    }
    
    private void showError(String message) {
        resultLabel.setText("Error: " + message);
        resultLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
    
    private void showSuccess(String message) {
        DialogUtils.showSuccess(message);
    }
}
