import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddTransactionDialog extends JDialog {

    private JComboBox<String> typeCombo, modeCombo;
    private JTextField amountField, noteField, dateField;
    private JButton addButton, cancelButton;
    private ExpenseManager manager;

    public AddTransactionDialog(JFrame parent, ExpenseManager manager) {
        super(parent, "Add Transaction", true);
        this.manager = manager;

        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        setLayout(new GridLayout(6, 2, 10, 10));
        setSize(400, 300);
        setLocationRelativeTo(parent);

        add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"General", "Lent", "Borrowed"});
        add(typeCombo);

        add(new JLabel("Mode:"));
        modeCombo = new JComboBox<>(new String[]{"Cash", "Online"});
        add(modeCombo);

        add(new JLabel("Amount:"));
        amountField = new JTextField();
        add(amountField);

        add(new JLabel("Note / Party Name:"));
        noteField = new JTextField();
        add(noteField);

        add(new JLabel("Date (dd-MM-yyyy):"));
        dateField = new JTextField(todayStr);
        add(dateField);

        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
        add(addButton);
        add(cancelButton);

        addButton.addActionListener(e -> {
            String typeSelected = ((String) typeCombo.getSelectedItem()).toLowerCase();
            String modeSelected = ((String) modeCombo.getSelectedItem()).toLowerCase();
            String amtText = amountField.getText().trim();
            String noteText = noteField.getText().trim();
            String dateText = dateField.getText().trim();

            double amount;
            try {
                amount = Double.parseDouble(amtText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Amount must be greater than 0.",
                            "Invalid Amount",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for Amount.",
                        "Invalid Amount",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ((typeSelected.equals("lent") || typeSelected.equals("borrowed")) && noteText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a Party Name in the Note field for Lent/Borrowed.",
                        "Missing Party Name",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Transaction t = new Transaction(typeSelected, modeSelected, amount, noteText, dateText);
            manager.addTransaction(t);
            JOptionPane.showMessageDialog(this, "✅ Transaction added!");
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }
}
