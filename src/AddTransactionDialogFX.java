import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.stage.Modality;
import java.util.Optional;

public class AddTransactionDialogFX extends Dialog<Transaction> {

    private ComboBox<String> typeCombo;
    private ComboBox<String> modeCombo;
    private TextField amountField;
    private TextField noteField;
    private TextField dateField;

    public AddTransactionDialogFX() {
        setTitle("Add Transaction");
        setHeaderText(null);
        initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Type:"), 0, 0);
        typeCombo = new ComboBox<>(FXCollections.observableArrayList("General", "Lent", "Borrowed"));
        typeCombo.setValue("General");
        grid.add(typeCombo, 1, 0);

        grid.add(new Label("Mode:"), 0, 1);
        modeCombo = new ComboBox<>(FXCollections.observableArrayList("Cash", "Online"));
        modeCombo.setValue("Cash");
        grid.add(modeCombo, 1, 1);

        grid.add(new Label("Amount:"), 0, 2);
        amountField = new TextField();
        amountField.setPromptText("e.g., 100.00");
        grid.add(amountField, 1, 2);

        grid.add(new Label("Note / Party Name:"), 0, 3);
        noteField = new TextField();
        grid.add(noteField, 1, 3);

        grid.add(new Label("Date (dd-MM-yyyy):"), 0, 4);
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        dateField = new TextField(todayStr);
        grid.add(dateField, 1, 4);

        getDialogPane().setContent(grid);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        Node addButton = getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        amountField.textProperty().addListener((obs, oldVal, newVal) -> addButton.setDisable(!isInputValid()));
        noteField.textProperty().addListener((obs, oldVal, newVal) -> addButton.setDisable(!isInputValid()));
        dateField.textProperty().addListener((obs, oldVal, newVal) -> addButton.setDisable(!isInputValid()));
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> addButton.setDisable(!isInputValid()));

        setResultConverter(new Callback<ButtonType, Transaction>() {
            @Override
            public Transaction call(ButtonType buttonType) {
                if (buttonType == addButtonType) {
                    String type = typeCombo.getValue().toLowerCase();
                    String mode = modeCombo.getValue().toLowerCase();
                    double amount = Double.parseDouble(amountField.getText().trim());
                    String note = noteField.getText().trim();
                    String date = dateField.getText().trim();
                    return new Transaction(type, mode, amount, note, date);
                }
                return null;
            }
        });
    }

    private boolean isInputValid() {
        String amtText = amountField.getText().trim();
        String noteText = noteField.getText().trim();
        String type = typeCombo.getValue().toLowerCase();
        try {
            double val = Double.parseDouble(amtText);
            if (val <= 0) return false;
        } catch (NumberFormatException ex) {
            return false;
        }
        if ((type.equals("lent") || type.equals("borrowed")) && noteText.isEmpty()) {
            return false;
        }
        if (dateField.getText().trim().isEmpty()) {
            return false;
        }
        return true;
    }
}
