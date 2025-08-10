import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class ExpenseManagerAppFX extends Application {

    private ExpenseManager manager = new ExpenseManager();
    private SecretKey aesKey;
    private TableView<Transaction> tableView;
    private ObservableList<Transaction> transactionList;
    private Label monthSummaryLabel;
    private static final String ENCRYPTED_DATA_FILE = "data.enc";

    @Override
    public void start(Stage primaryStage) {
        LoginDialogFX login = new LoginDialogFX();
        login.show(primaryStage);
        if (!login.isSucceeded()) {
            primaryStage.close();
            return;
        }
        aesKey = login.getAesKey();

        manager.loadEncrypted(ENCRYPTED_DATA_FILE, aesKey);
        transactionList = FXCollections.observableArrayList(manager.transactions);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label title = new Label("Expense Manager");
        title.setFont(new Font("Segoe UI", 24));
        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 0, 20, 10));
        root.setTop(topBar);

        tableView = new TableView<>();
        tableView.setItems(transactionList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Transaction, String> modeCol = new TableColumn<>("Mode");
        modeCol.setCellValueFactory(new PropertyValueFactory<>("mode"));
        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Transaction, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        dateCol.setMinWidth(100);
        typeCol.setMinWidth(100);
        modeCol.setMinWidth(100);
        amountCol.setMinWidth(100);
        noteCol.setMinWidth(200);

        tableView.getColumns().addAll(dateCol, typeCol, modeCol, amountCol, noteCol);
        root.setCenter(tableView);

        VBox rightPane = new VBox(15);
        rightPane.setPadding(new Insets(0, 10, 0, 10));
        rightPane.setPrefWidth(250);

        monthSummaryLabel = new Label();
        monthSummaryLabel.setFont(new Font("Segoe UI", 16));
        updateMonthSummary();

        Button lentBtn = new Button("Money Lent (This Month)");
        Button borrowedBtn = new Button("Money Borrowed (This Month)");
        lentBtn.setPrefWidth(Double.MAX_VALUE);
        borrowedBtn.setPrefWidth(Double.MAX_VALUE);

        rightPane.getChildren().addAll(monthSummaryLabel, lentBtn, borrowedBtn);
        root.setRight(rightPane);

        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(20, 10, 10, 10));
        bottomBar.setAlignment(Pos.CENTER);

        Button addBtn = new Button("➕ Add");
        Button saveBtn = new Button("💾 Save");
        Button loadBtn = new Button("📂 Load");
        Button deleteBtn = new Button("🗑️ Delete");
        Button allBalBtn = new Button("💡 All Balances");
        Button exitBtn = new Button("🚪 Exit");

        addBtn.setPrefWidth(100);
        saveBtn.setPrefWidth(100);
        loadBtn.setPrefWidth(100);
        deleteBtn.setPrefWidth(100);
        allBalBtn.setPrefWidth(120);
        exitBtn.setPrefWidth(100);

        bottomBar.getChildren().addAll(addBtn, saveBtn, loadBtn, deleteBtn, allBalBtn, exitBtn);
        root.setBottom(bottomBar);

        addBtn.setOnAction(e -> {
            AddTransactionDialogFX dlg = new AddTransactionDialogFX();
            Optional<Transaction> result = dlg.showAndWait();
            result.ifPresent(t -> {
                manager.addTransaction(t);
                transactionList.add(t);
                updateMonthSummary();
            });
        });

        saveBtn.setOnAction(e -> {
            manager.saveEncrypted(ENCRYPTED_DATA_FILE, aesKey);
            showAlert("Success", "✅ Data saved (encrypted).");
        });

        loadBtn.setOnAction(e -> {
            manager.loadEncrypted(ENCRYPTED_DATA_FILE, aesKey);
            transactionList.setAll(manager.transactions);
            updateMonthSummary();
            showAlert("Loaded", "📁 Data loaded (decrypted).");
        });

        deleteBtn.setOnAction(e -> {
            Transaction sel = tableView.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("No Selection", "⚠️ Select a transaction first.");
                return;
            }
            int idx = tableView.getSelectionModel().getSelectedIndex();
            manager.deleteTransaction(idx);
            transactionList.remove(idx);
            updateMonthSummary();
        });

        allBalBtn.setOnAction(e -> showAllTimeBalances());

        lentBtn.setOnAction(e -> showMonthBalances(true));
        borrowedBtn.setOnAction(e -> showMonthBalances(false));

        exitBtn.setOnAction(e -> {
            manager.saveEncrypted(ENCRYPTED_DATA_FILE, aesKey);
            showAlert("Exit", "👋 Exiting. Data saved.");
            primaryStage.close();
        });

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Expense Manager");
        primaryStage.show();
    }

    private void updateMonthSummary() {
        LocalDate now = LocalDate.now();
        double total = manager.totalGeneralForMonth(now.getMonthValue(), now.getYear());
        String monthName = now.getMonth().toString().substring(0, 1)
                + now.getMonth().toString().substring(1).toLowerCase();
        monthSummaryLabel.setText(
                monthName + " " + now.getYear() + " → General: ₹" +
                        String.format("%.2f", total)
        );
    }

    private void showAllTimeBalances() {
        StringBuilder sb = new StringBuilder();
        Map<String, Double> netMap = new java.util.HashMap<>();
        for (Transaction t : manager.transactions) {
            String type = t.getType();
            if ("lent".equalsIgnoreCase(type) || "borrowed".equalsIgnoreCase(type)) {
                String party = t.getNote().trim();
                if (party.isEmpty()) continue;
                double amt = t.getAmount();
                double curr = netMap.getOrDefault(party, 0.0);
                if ("lent".equalsIgnoreCase(type)) curr += amt;
                else curr -= amt;
                netMap.put(party, curr);
            }
        }
        if (netMap.isEmpty()) {
            showAlert("All Balances", "No borrowed/lent transactions.");
            return;
        }
        for (Map.Entry<String, Double> entry : netMap.entrySet()) {
            String party = entry.getKey();
            double net = entry.getValue();
            if (net > 0) sb.append(party).append(" owes you ₹").append(net).append("\n");
            else if (net < 0) sb.append("You owe ").append(party).append(" ₹").append(-net).append("\n");
            else sb.append("Settled with ").append(party).append("\n");
        }
        showLargeAlert("All-Time Balances", sb.toString());
    }

    private void showMonthBalances(boolean isLent) {
        LocalDate now = LocalDate.now();
        int m = now.getMonthValue(), y = now.getYear();
        Map<String, Double> netMap = manager.balancesForMonth(m, y, isLent);
        if (netMap.isEmpty()) {
            showAlert(
                    isLent ? "Money Lent" : "Money Borrowed",
                    "No data this month."
            );
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(isLent ? "People who owe you:\n" : "People you owe:\n").append("\n");
        for (Map.Entry<String, Double> entry : netMap.entrySet()) {
            String party = entry.getKey();
            double net = entry.getValue();
            if (isLent && net > 0) sb.append(party).append(" owes you ₹").append(net).append("\n");
            else if (!isLent && net < 0) sb.append("You owe ").append(party).append(" ₹").append(-net).append("\n");
        }
        showLargeAlert(
                isLent ? "This Month’s Money Lent" : "This Month’s Money Borrowed",
                sb.toString()
        );
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }

    private void showLargeAlert(String title, String msg) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle(title);
        TextArea area = new TextArea(msg);
        area.setEditable(false);
        area.setWrapText(true);
        area.setFont(new Font("Segoe UI", 14));
        dlg.getDialogPane().setContent(area);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dlg.getDialogPane().setPrefSize(400, 300);
        dlg.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
