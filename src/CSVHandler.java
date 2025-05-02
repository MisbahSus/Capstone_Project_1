import java.io.*;
import java.util.*;

public class CSVHandler {
    private static final String FILE_PATH = "transactions.csv";

    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    try {
                        Transaction t = new Transaction(
                                parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4])
                        );
                        transactions.add(t);
                    } catch (NumberFormatException e) {
                        System.out.println("Warning: Invalid transaction record: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No existing transactions file found. Starting fresh.");
        }
        return transactions;
    }

    public static void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Transaction t : transactions) {
                bw.write(t.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }
}
