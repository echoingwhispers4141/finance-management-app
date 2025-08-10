import javax.swing.JOptionPane;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseManager {
    ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String TEMP_PLAIN_FILE = "data_temp.txt";

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public void deleteTransaction(int index) {
        if (index >= 0 && index < transactions.size()) {
            transactions.remove(index);
        }
    }

    public void saveEncrypted(String encryptedFileName, javax.crypto.SecretKey key) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TEMP_PLAIN_FILE))) {
            for (Transaction t : transactions) {
                pw.println(t.getType() + "," + t.getMode() + "," + t.getAmount() + "," + t.getNote() + "," + t.getDate());
            }
        } catch (IOException e) {
            System.out.println("Error writing temp file: " + e.getMessage());
            return;
        }

        try {
            CryptoUtils.encryptFile(
                    new File(TEMP_PLAIN_FILE),
                    new File(encryptedFileName),
                    key
            );
        } catch (Exception ex) {
            System.out.println("Error encrypting file: " + ex.getMessage());
        }

        try {
            Files.deleteIfExists(Paths.get(TEMP_PLAIN_FILE));
        } catch (IOException e) {
        }
    }

    public void loadEncrypted(String encryptedFileName, javax.crypto.SecretKey key) {
        transactions.clear();
        File encFile = new File(encryptedFileName);
        if (!encFile.exists()) {
            return;
        }

        try {
            CryptoUtils.decryptFile(
                    new File(encryptedFileName),
                    new File(TEMP_PLAIN_FILE),
                    key
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "❌ Failed to decrypt data. Wrong password or corrupted file.",
                    "Decryption Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(TEMP_PLAIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length == 5) {
                    Transaction t = new Transaction(
                            parts[0],
                            parts[1],
                            Double.parseDouble(parts[2]),
                            parts[3],
                            parts[4]
                    );
                    transactions.add(t);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading temp file: " + e.getMessage());
        }

        try {
            Files.deleteIfExists(Paths.get(TEMP_PLAIN_FILE));
        } catch (IOException e) {
        }
    }

    public double totalGeneralForMonth(int month, int year) {
        double sum = 0.0;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Transaction t : transactions) {
            if ("general".equalsIgnoreCase(t.getType())) {
                try {
                    LocalDate d = LocalDate.parse(t.getDate(), fmt);
                    if (d.getMonthValue() == month && d.getYear() == year) {
                        sum += t.getAmount();
                    }
                } catch (Exception ignore) {
                }
            }
        }
        return sum;
    }

    public Map<String, Double> balancesForMonth(int month, int year, boolean isLent) {
        HashMap<String, Double> netMap = new HashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Transaction t : transactions) {
            boolean isTransactionLent = t.getType().equalsIgnoreCase("lent");
            boolean isTransactionBorrowed = t.getType().equalsIgnoreCase("borrowed");
            if ((isTransactionLent && isLent) || (isTransactionBorrowed && !isLent)) {
                try {
                    LocalDate d = LocalDate.parse(t.getDate(), fmt);
                    if (d.getMonthValue() == month && d.getYear() == year) {
                        String party = t.getNote().trim();
                        if (party.isEmpty()) continue;
                        double amt = t.getAmount();
                        double curr = netMap.getOrDefault(party, 0.0);
                        if (isTransactionLent) {
                            curr += amt;
                        } else {
                            curr -= amt;
                        }
                        netMap.put(party, curr);
                    }
                } catch (Exception ignore) {
                }
            }
        }
        return netMap;
    }
}
