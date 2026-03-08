package ch.pingu.ui.views;

import ch.pingu.AppContext;
import ch.pingu.domain.model.*;
import ch.pingu.domain.service.TransactionService;
import ch.pingu.ui.components.buttons.DangerButton;
import ch.pingu.ui.components.buttons.InfoButton;
import ch.pingu.ui.components.buttons.NeutralButton;
import ch.pingu.ui.components.buttons.SecondaryButton;
import ch.pingu.ui.components.dialogs.DialogUtils;
import ch.pingu.ui.components.inputs.StyledComboBox;
import ch.pingu.ui.components.inputs.StyledTextField;
import ch.pingu.ui.components.labels.FieldLabel;
import ch.pingu.ui.components.labels.TitleLabel;
import ch.pingu.ui.components.panels.FilterPanel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TransactionListView extends BaseView {
    
    private TableView<TransactionRow> tableView;
    private ObservableList<TransactionRow> transactionData;
    private StyledComboBox<String> statusFilter;
    private StyledComboBox<Currency> currencyFilter;
    private StyledTextField customerFilter;
    
    @Override
    protected void buildView() {
        transactionData = FXCollections.observableArrayList();
        
        TitleLabel title = new TitleLabel("Transactions");
        HBox filterBox = createFilterPanel();
        
        tableView = createTableView();
        HBox actionBox = createActionPanel();
        
        container.getChildren().addAll(title, filterBox, tableView, actionBox);
        
        loadTransactions();
    }
    
    private HBox createFilterPanel() {
        FilterPanel filterBox = new FilterPanel();
        
        FieldLabel filterLabel = new FieldLabel("Filters:");
        
        statusFilter = new StyledComboBox<>();
        statusFilter.getItems().add("All Statuses");
        for (TransactionStatus status : TransactionStatus.values()) {
            statusFilter.getItems().add(status.getDisplayName());
        }
        statusFilter.setValue("All Statuses");
        statusFilter.withWidth(150);
        
        currencyFilter = new StyledComboBox<>();
        currencyFilter.getItems().add(null);
        currencyFilter.getItems().addAll(Currency.values());
        currencyFilter.setPromptText("All Currencies");
        currencyFilter.withWidth(150);
        
        customerFilter = new StyledTextField("Customer ID");
        customerFilter.setPrefWidth(150);
        
        SecondaryButton applyButton = new SecondaryButton("Apply Filters");
        applyButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 5px 15px; " +
            "-fx-cursor: hand;"
        );
        applyButton.setOnAction(e -> applyFilters());
        
        NeutralButton resetButton = new NeutralButton("Reset");
        resetButton.setStyle(
            "-fx-background-color: #95a5a6; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 5px 15px; " +
            "-fx-cursor: hand;"
        );
        resetButton.setOnAction(e -> resetFilters());
        
        filterBox.getChildren().addAll(
            filterLabel,
            new Label("Status:"), statusFilter,
            new Label("Currency:"), currencyFilter,
            new Label("Customer:"), customerFilter,
            applyButton, resetButton
        );
        
        return filterBox;
    }
    
    private TableView<TransactionRow> createTableView() {
        TableView<TransactionRow> table = new TableView<>();
        table.setItems(transactionData);
        table.setPrefHeight(400);
        
        TableColumn<TransactionRow, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("displayId"));
        idCol.setPrefWidth(80);
        
        TableColumn<TransactionRow, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerCol.setPrefWidth(100);
        
        TableColumn<TransactionRow, String> sourceCol = new TableColumn<>("From");
        sourceCol.setCellValueFactory(new PropertyValueFactory<>("sourceAmount"));
        sourceCol.setPrefWidth(120);
        
        TableColumn<TransactionRow, String> targetCol = new TableColumn<>("To");
        targetCol.setCellValueFactory(new PropertyValueFactory<>("targetAmount"));
        targetCol.setPrefWidth(120);
        
        TableColumn<TransactionRow, String> rateCol = new TableColumn<>("Rate");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("exchangeRate"));
        rateCol.setPrefWidth(100);
        
        TableColumn<TransactionRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<TransactionRow, String> dateCol = new TableColumn<>("Execution Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("executionDate"));
        dateCol.setPrefWidth(150);
        
        TableColumn<TransactionRow, String> consultantCol = new TableColumn<>("Created By");
        consultantCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        consultantCol.setPrefWidth(120);
        
        table.getColumns().add(idCol);
        table.getColumns().add(customerCol);
        table.getColumns().add(sourceCol);
        table.getColumns().add(targetCol);
        table.getColumns().add(rateCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(dateCol);
        table.getColumns().add(consultantCol);
        
        return table;
    }
    
    private HBox createActionPanel() {
        HBox actionBox = new HBox(10);
        actionBox.setPadding(new Insets(10));
        actionBox.setAlignment(Pos.CENTER_LEFT);
        
        SecondaryButton refreshButton = new SecondaryButton("Refresh");
        refreshButton.setOnAction(e -> loadTransactions());
        
        InfoButton viewDetailsButton = new InfoButton("View Details");
        viewDetailsButton.setOnAction(e -> viewTransactionDetails());
        
        DangerButton revertButton = new DangerButton("Revert Transaction");
        revertButton.setOnAction(e -> revertTransaction());
        
        AppContext context = AppContext.getInstance();
        if (context.getCurrentUser() != null && context.getCurrentUser().isAdmin()) {
            actionBox.getChildren().addAll(refreshButton, viewDetailsButton, revertButton);
        } else {
            actionBox.getChildren().addAll(refreshButton, viewDetailsButton);
        }
        
        return actionBox;
    }
    
    private void loadTransactions() {
        AppContext context = AppContext.getInstance();
        User currentUser = context.getCurrentUser();
        
        if (currentUser == null) {
            showError("You must be logged in");
            return;
        }
        
        List<Transaction> transactions;
        
        if (currentUser.isAdmin()) {
            transactions = context.getTransactionRepository().findAll();
        } else {
            transactions = context.getTransactionRepository().findByConsultantId(currentUser.getId());
        }
        
        transactionData.clear();
        transactions.forEach(t -> transactionData.add(new TransactionRow(t)));
    }
    
    private void applyFilters() {
        AppContext context = AppContext.getInstance();
        User currentUser = context.getCurrentUser();
        
        if (currentUser == null) return;
        
        List<Transaction> transactions;
        if (currentUser.isAdmin()) {
            transactions = context.getTransactionRepository().findAll();
        } else {
            transactions = context.getTransactionRepository().findByConsultantId(currentUser.getId());
        }
        
        TransactionService transactionService = context.getTransactionService();
        
        String statusValue = statusFilter.getValue();
        if (statusValue != null && !statusValue.equals("All Statuses")) {
            TransactionStatus status = TransactionStatus.valueOf(
                statusValue.toUpperCase().replace(" ", "_")
            );
            transactions = transactionService.filterByStatus(transactions, status);
        }
        
        Currency currency = currencyFilter.getValue();
        if (currency != null) {
            transactions = transactionService.filterByCurrency(transactions, currency);
        }
        
        String customer = customerFilter.getText().trim();
        if (!customer.isEmpty()) {
            transactions = transactionService.filterByCustomer(transactions, customer);
        }
        
        transactionData.clear();
        transactions.forEach(t -> transactionData.add(new TransactionRow(t)));
    }
    
    private void resetFilters() {
        statusFilter.setValue("All Statuses");
        currencyFilter.setValue(null);
        customerFilter.clear();
        loadTransactions();
    }
    
    private void viewTransactionDetails() {
        TransactionRow selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a transaction");
            return;
        }
        
        Optional<Transaction> transactionOpt = AppContext.getInstance()
            .getTransactionRepository()
            .findById(selected.getId());
        
        if (transactionOpt.isEmpty()) {
            showError("Transaction not found");
            return;
        }
        
        Transaction transaction = transactionOpt.get();
        
        String details = String.format(
            "Customer: %s\n" +
            "Source: %s\n" +
            "Target: %s\n" +
            "Exchange Rate: %.6f\n" +
            "Rate Version: %s\n" +
            "Status: %s\n" +
            "Execution Date: %s\n" +
            "Created By: %s\n" +
            "Created At: %s\n" +
            "%s",
            transaction.getCustomerId(),
            transaction.getSourceAmount(),
            transaction.getTargetAmount(),
            transaction.getExchangeRate(),
            transaction.getExchangeRateVersionId(),
            transaction.getStatus().getDisplayName(),
            transaction.getExecutionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            transaction.getCreatedBy(),
            transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            transaction.isReverted() ? 
                String.format("\n\nREVERTED\nReason: %s\nReverted By: %s\nReverted At: %s",
                    transaction.getRevertReason(),
                    transaction.getRevertedBy(),
                    transaction.getRevertedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))) 
                : ""
        );
        
        DialogUtils.showInfo("Transaction Details - ID: " + transaction.getId(), details);
    }
    
    private void revertTransaction() {
        AppContext context = AppContext.getInstance();
        User currentUser = context.getCurrentUser();
        
        if (currentUser == null || !currentUser.isAdmin()) {
            showError("Only administrators can revert transactions");
            return;
        }
        
        TransactionRow selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a transaction");
            return;
        }
        
        Optional<Transaction> transactionOpt = context.getTransactionRepository().findById(selected.getId());
        
        if (transactionOpt.isEmpty()) {
            showError("Transaction not found");
            return;
        }
        
        Transaction transaction = transactionOpt.get();
        
        if (transaction.isReverted()) {
            showError("Transaction is already reverted");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Revert Transaction");
        dialog.setHeaderText("Revert transaction " + transaction.getId());
        dialog.setContentText("Please provide a reason:");
        
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                showError("Reason is required");
                return;
            }
            
            try {
                context.getTransactionService().revertTransaction(transaction, reason, currentUser);
                context.getTransactionRepository().save(transaction);
                loadTransactions();
                showSuccess("Transaction reverted successfully");
            } catch (Exception e) {
                showError("Failed to revert transaction: " + e.getMessage());
            }
        });
    }
    
    private void showError(String message) {
        DialogUtils.showError("Error", message);
    }
    
    private void showSuccess(String message) {
        DialogUtils.showSuccess(message);
    }
    
    public static class TransactionRow {
        private final Transaction transaction;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        public TransactionRow(Transaction transaction) {
            this.transaction = transaction;
        }
        
        public String getId() {
            return transaction.getId();
        }
        
        public String getDisplayId() {
            return transaction.getId().substring(0, Math.min(8, transaction.getId().length()));
        }
        
        public String getCustomerId() {
            return transaction.getCustomerId();
        }
        
        public String getSourceAmount() {
            return transaction.getSourceAmount().toString();
        }
        
        public String getTargetAmount() {
            return transaction.getTargetAmount().toString();
        }
        
        public String getExchangeRate() {
            return String.format("%.6f", transaction.getExchangeRate());
        }
        
        public String getStatus() {
            return transaction.getStatus().getDisplayName();
        }
        
        public String getExecutionDate() {
            return transaction.getExecutionDate().format(formatter);
        }
        
        public String getCreatedBy() {
            return transaction.getCreatedBy();
        }
    }
}
