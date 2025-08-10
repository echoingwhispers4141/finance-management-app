import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.crypto.SecretKey;

public class ExpenseManagerGUI extends JFrame {

    private JTextArea textArea;
    private JButton addButton, saveButton, loadButton, exitButton;
    private ExpenseManager manager;

    public ExpenseManagerGUI() {
        setTitle("Expense Manager");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        manager = new ExpenseManager();

        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Transaction");
        saveButton = new JButton("Save");
        loadButton = new JButton("Load");
        exitButton = new JButton("Exit");

        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> openAddTransactionDialog());

        saveButton.addActionListener(e -> {
            String pwd = JOptionPane.showInputDialog(this, "Enter password to encrypt:");
            if (pwd == null || pwd.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Encryption cancelled or no password entered.");
                return;
            }
            try {
                SecretKey key = CryptoUtils.getKeyFromPassword(pwd.trim());
                manager.saveEncrypted("data.enc", key);
                JOptionPane.showMessageDialog(this, "✅ Data saved (encrypted).");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "❌ Error encrypting data:\n" + ex.getMessage(),
                        "Encryption Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        loadButton.addActionListener(e -> {
            String pwd = JOptionPane.showInputDialog(this, "Enter password to decrypt:");
            if (pwd == null || pwd.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Decryption cancelled or no password entered.");
                return;
            }
            try {
                SecretKey key = CryptoUtils.getKeyFromPassword(pwd.trim());
                manager.loadEncrypted("data.enc", key);
                refreshTextArea();
                JOptionPane.showMessageDialog(this, "📁 Data loaded (decrypted).");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "❌ Error decrypting/loading data:\n" + ex.getMessage(),
                        "Decryption Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        int choice = JOptionPane.showConfirmDialog(this,
                "Load existing encrypted data (if any)?", "Load Data",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            String pwd = JOptionPane.showInputDialog(this, "Enter password to decrypt existing data:");
            if (pwd != null && !pwd.trim().isEmpty()) {
                try {
                    SecretKey key = CryptoUtils.getKeyFromPassword(pwd.trim());
                    manager.loadEncrypted("data.enc", key);
                    refreshTextArea();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "❌ Failed to load existing data:\n" + ex.getMessage(),
                            "Decryption Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void refreshTextArea() {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : manager.transactions) {
            sb.append(t.toString()).append("\n");
        }
        textArea.setText(sb.toString());
    }

    private void openAddTransactionDialog() {
        AddTransactionDialog dialog = new AddTransactionDialog(this, manager);
        dialog.setVisible(true);
        refreshTextArea();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseManagerGUI gui = new ExpenseManagerGUI();
            gui.setVisible(true);
        });
    }
}
