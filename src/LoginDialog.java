import javax.swing.*;

public class LoginDialog extends JDialog {
    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);
        add(new JLabel("Login Placeholder"));
        setSize(200, 100);
        setLocationRelativeTo(parent);
    }
}
