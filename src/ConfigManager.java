import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";

    public static boolean configExists() {
        return new File(CONFIG_FILE).exists();
    }

    public static void saveConfig(String passwordHash, String question, String answerHash) throws IOException {
        Properties props = new Properties();
        props.setProperty("passwordHash", passwordHash);
        props.setProperty("question", question);
        props.setProperty("answerHash", answerHash);
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Expense Manager Configuration");
        }
    }

    public static Properties loadConfig() throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        }
        return props;
    }
}
