import java.io.*;
import java.util.*;
import java.nio.file.*;

public class CSVHandler {
    private static final String FILE_PATH = "transactions.csv";
    private static final String BACKUP_DIR = "backups";
    private static final int MAX_BACKUPS = 5;

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("No existing transactions file found. Starting fresh.");
            return transactions;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) { // Support both old and new format
                    try {
                        Transaction t;
                        if (parts.length == 6) {
                            // New format with balance
                            t = new Transaction(
                                    parts[0], parts[1], parts[2], parts[3],
                                    Double.parseDouble(parts[4]), Double.parseDouble(parts[5])
                            );
                        } else {
                            // Old format without balance - calculate it during loading
                            double amount = Double.parseDouble(parts[4]);
                            double prevBalance = transactions.isEmpty() ? 0 :
                                    transactions.get(transactions.size() - 1).balance;
                            t = new Transaction(
                                    parts[0], parts[1], parts[2], parts[3], amount
                            );
                            t.balance = prevBalance + amount;
                        }
                        transactions.add(t);
                    } catch (NumberFormatException e) {
                        System.out.println("Warning: Skipped invalid transaction record: " + line);
                    }
                }
            }
            System.out.println("Loaded " + transactions.size() + " transactions.");

            // Sort transactions by date/time before calculating balances
            if (!transactions.isEmpty()) {
                transactions.sort(Comparator.comparing((Transaction t) -> t.date + " " + t.time));

                // Recalculate all balances to ensure consistency
                double runningBalance = 0;
                for (Transaction t : transactions) {
                    runningBalance += t.amount;
                    t.balance = runningBalance;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
        return transactions;
    }

    public static void saveTransactions(List<Transaction> transactions) {
        // Create backup before saving
        createBackup();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Transaction t : transactions) {
                bw.write(t.toCsvString());
                bw.newLine();
            }
            System.out.println("Saved " + transactions.size() + " transactions.");
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    private static void createBackup() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try {
            // Create backup directory if it doesn't exist
            File backupDir = new File(BACKUP_DIR);
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }

            // Create backup with timestamp
            String timestamp = String.valueOf(System.currentTimeMillis());
            String backupFileName = BACKUP_DIR + File.separator + "transactions_" + timestamp + ".csv";

            Files.copy(file.toPath(), Paths.get(backupFileName), StandardCopyOption.REPLACE_EXISTING);

            // Maintain only MAX_BACKUPS recent backups
            pruneOldBackups();

        } catch (IOException e) {
            System.out.println("Failed to create backup: " + e.getMessage());
        }
    }

    private static void pruneOldBackups() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) return;

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith("transactions_") && name.endsWith(".csv"));

        if (backupFiles != null && backupFiles.length > MAX_BACKUPS) {
            // Sort files by last modified time (oldest first)
            Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));

            // Delete oldest files to keep only MAX_BACKUPS
            for (int i = 0; i < backupFiles.length - MAX_BACKUPS; i++) {
                backupFiles[i].delete();
            }
        }
    }
}
