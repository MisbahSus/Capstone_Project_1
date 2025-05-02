//Main.java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ScreenManager screenManager = new ScreenManager();
        screenManager.showHomeScreen();
    }
}

//ScreenManager.java
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenManager {
    private final Scanner scanner = new Scanner(System.in);
    private final TransactionManager manager = new TransactionManager();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void showHomeScreen() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Home Screen ---");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "D" -> addTransaction(true);
                case "P" -> addTransaction(false);
                case "L" -> showLedgerScreen();
                case "X" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addTransaction(boolean isDeposit) {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(dateFormatter);
        String currentTime = now.format(timeFormatter);

        System.out.print("Date (yyyy-MM-dd) [" + currentDate + "]: ");
        String dateInput = scanner.nextLine().trim();
        String date = dateInput.isEmpty() ? currentDate : dateInput;

        System.out.print("Time (HH:mm:ss) [" + currentTime + "]: ");
        String timeInput = scanner.nextLine().trim();
        String time = timeInput.isEmpty() ? currentTime : timeInput;

        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (!isDeposit) amount *= -1;

        Transaction t = new Transaction(date, time, description, vendor, amount);
        manager.addTransaction(t);
    }

    private void showLedgerScreen() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Ledger Screen ---");
            System.out.println("A) All Entries");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "A" -> manager.listAllTransactions();
                case "D" -> manager.listDeposits();
                case "P" -> manager.listPayments();
                case "R" -> manager.showReports(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
//Transaction.java
public class Transaction {
    public String date, time, description, vendor;
    public double amount;

    public Transaction(String date, String time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return date + "|" + time + "|" + description + "|" + vendor + "|" + amount;
    }
}
//TransactionManager.java
import java.util.*;
import java.util.stream.Collectors;

public class TransactionManager {
    private List<Transaction> transactions = CSVHandler.loadTransactions();

    public void addTransaction(Transaction t) {
        transactions.add(t);
        CSVHandler.saveTransactions(transactions);
    }

    public void listAllTransactions() {
        transactions.stream()
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .forEach(System.out::println);
    }

    public void listDeposits() {
        transactions.stream().filter(t -> t.amount > 0)
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .forEach(System.out::println);
    }

    public void listPayments() {
        transactions.stream().filter(t -> t.amount < 0)
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .forEach(System.out::println);
    }

    public void showReports(Scanner scanner) {
        System.out.println("\n--- Reports Screen ---");
        System.out.println("1) Month To Date");
        System.out.println("2) Previous Month");
        System.out.println("3) Year To Date");
        System.out.println("4) Previous Year");
        System.out.println("5) Search by Vendor");
        System.out.println("0) Back");
        System.out.print("Choose an option: ");
        String input = scanner.nextLine();

        switch (input) {
            case "5" -> {
                System.out.print("Enter vendor name: ");
                String vendor = scanner.nextLine();
                transactions.stream()
                        .filter(t -> t.vendor.equalsIgnoreCase(vendor))
                        .forEach(System.out::println);
            }
            case "0" -> {}
            default -> System.out.println("Option not yet implemented.");
        }
    }
}
//CSVHandler.java
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
                    Transaction t = new Transaction(
                            parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4])
                    );
                    transactions.add(t);
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
            System.out.println("Error saving transactions.");
        }
    }
}
