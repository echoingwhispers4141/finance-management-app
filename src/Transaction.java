import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String type;
    private String mode;
    private double amount;
    private String note;
    private String date;

    public Transaction(String type, String mode, double amount, String note, String date) {
        this.type = type;
        this.mode = mode;
        this.amount = amount;
        this.note = note;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getMode() {
        return mode;
    }

    public double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date + " | " + type + " | " + mode + " | ₹" + amount + " | " + note;
    }
}
