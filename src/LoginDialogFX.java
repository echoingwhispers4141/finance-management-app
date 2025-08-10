import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Properties;

/**
 * JavaFX dialog for Create‐Account / Login / Forgot‐Password flow
 */
public class LoginDialogFX {

    private SecretKey aesKey;
    private boolean succeeded = false;

    public boolean isSucceeded() {
        return succeeded;
    }

    public SecretKey getAesKey() {
        return aesKey;
    }

    public void show(Stage owner) {
        if (!ConfigManager.configExists()) {
            showCreateAccountDialog(owner);
        } else {
            showLoginDialog(owner);
        }
    }

    private void showCreateAccountDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create Password");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label title = new Label("Create Your Account");
        title.setFont(new Font(18));
        grid.add(title, 0, 0, 2, 1);

        Label passLabel = new Label("New Password:");
        PasswordField passField = new PasswordField();
        Label pass2Label = new Label("Confirm Password:");
        PasswordField pass2Field = new PasswordField();
        Label questionLabel = new Label("Security Question:");
        TextField questionField = new TextField();
        Label answerLabel = new Label("Answer:");
        PasswordField answerField = new PasswordField();

        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        grid.add(pass2Label, 0, 2);
        grid.add(pass2Field, 1, 2);
        grid.add(questionLabel, 0, 3);
        grid.add(questionField, 1, 3);
        grid.add(answerLabel, 0, 4);
        grid.add(answerField, 1, 4);

        Button createBtn = new Button("Create Account");
        Button cancelBtn = new Button("Cancel");
        grid.add(createBtn, 0, 5);
        grid.add(cancelBtn, 1, 5);

        createBtn.setOnAction(e -> {
            String p1 = passField.getText().trim();
            String p2 = pass2Field.getText().trim();
            String question = questionField.getText().trim();
            String answer = answerField.getText().trim();

            if (p1.isEmpty() || p2.isEmpty() || question.isEmpty() || answer.isEmpty()) {
                showAlert("Error", "All fields are required.", Alert.AlertType.ERROR);
                return;
            }
            if (!p1.equals(p2)) {
                showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
                return;
            }
            try {
                String passHash = CryptoUtils.hash(p1);
                String ansHash = CryptoUtils.hash(answer);
                ConfigManager.saveConfig(passHash, question, ansHash);
                aesKey = CryptoUtils.getKeyFromPassword(p1);
                succeeded = true;
                dialog.close();
            } catch (Exception ex) {
                showAlert("Error", "Failed to create account:\n" + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        cancelBtn.setOnAction(e -> {
            succeeded = false;
            dialog.close();
        });

        Scene scene = new Scene(grid);
        scene.getStylesheets().add("style.css");
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showLoginDialog(Stage owner) {
        Properties cfg;
        try {
            cfg = ConfigManager.loadConfig();
        } catch (IOException e) {
            showAlert("Error", "Failed to load config:\n" + e.getMessage(), Alert.AlertType.ERROR);
            succeeded = false;
            return;
        }
        String savedHash = cfg.getProperty("passwordHash");
        String question = cfg.getProperty("question");
        String savedAnsHash = cfg.getProperty("answerHash");

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Login");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label title = new Label("Enter Password");
        title.setFont(new Font(18));
        grid.add(title, 0, 0, 2, 1);

        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);

        Button loginBtn = new Button("Login");
        Button forgotBtn = new Button("Forgot Password?");
        grid.add(loginBtn, 0, 2);
        grid.add(forgotBtn, 1, 2);

        loginBtn.setOnAction(e -> {
            String p = passField.getText().trim();
            if (p.isEmpty()) {
                showAlert("Error", "Enter password.", Alert.AlertType.ERROR);
                return;
            }
            try {
                String pHash = CryptoUtils.hash(p);
                if (pHash.equals(savedHash)) {
                    aesKey = CryptoUtils.getKeyFromPassword(p);
                    succeeded = true;
                    dialog.close();
                } else {
                    showAlert("Error", "Incorrect password.", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        forgotBtn.setOnAction(e -> {
            TextInputDialog ti = new TextInputDialog();
            ti.setTitle("Forgot Password");
            ti.setHeaderText(question);
            ti.setContentText("Answer:");
            ti.showAndWait().ifPresent(ans -> {
                try {
                    if (CryptoUtils.hash(ans.trim()).equals(savedAnsHash)) {
                        // allow reset
                        Dialog<String[]> reset = new Dialog<>();
                        reset.setTitle("Reset Password");
                        GridPane g2 = new GridPane();
                        g2.setPadding(new Insets(20));
                        g2.setHgap(10);
                        g2.setVgap(10);
                        PasswordField newP1 = new PasswordField();
                        PasswordField newP2 = new PasswordField();
                        g2.add(new Label("New Password:"), 0, 0);
                        g2.add(newP1, 1, 0);
                        g2.add(new Label("Confirm Password:"), 0, 1);
                        g2.add(newP2, 1, 1);
                        ButtonType okBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        reset.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);
                        reset.getDialogPane().setContent(g2);
                        reset.setResultConverter(r -> {
                            if (r == okBtn) return new String[]{newP1.getText(), newP2.getText()};
                            return null;
                        });
                        reset.showAndWait().ifPresent(res -> {
                            String p1 = res[0].trim();
                            String p2 = res[1].trim();
                            if (p1.isEmpty() || p2.isEmpty()) {
                                showAlert("Error", "Fields cannot be empty.", Alert.AlertType.ERROR);
                                return;
                            }
                            if (!p1.equals(p2)) {
                                showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
                                return;
                            }
                            try {
                                ConfigManager.saveConfig(CryptoUtils.hash(p1), question, savedAnsHash);
                                aesKey = CryptoUtils.getKeyFromPassword(p1);
                                succeeded = true;
                                dialog.close();
                            } catch (Exception ex) {
                                showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                            }
                        });
                    } else {
                        showAlert("Error", "Incorrect answer.", Alert.AlertType.ERROR);
                    }
                } catch (Exception ex) {
                    showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        });

        Scene scene = new Scene(grid);
        scene.getStylesheets().add("style.css");
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }
}
