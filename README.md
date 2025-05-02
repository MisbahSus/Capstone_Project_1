// Main.java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ScreenManager screenManager = new ScreenManager();
        screenManager.showHomeScreen();
    }
}

// ScreenManager.java
import java.util.Scanner;

public class ScreenManager {
    private final Scanner scanner = new Scanner(System.in);
    private final TransactionManager manager = new TransactionManager();

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
        try {
            System.out.print("Date (yyyy-mm-dd): ");
            String date = scanner.nextLine();
            System.out.print("Time (HH:mm:ss): ");
            String time = scanner.nextLine();
            System.out.print("Description: ");
            String description = scanner.nextLine();
            System.out.print("Vendor: ");
            String vendor = scanner.nextLine();
            System.out.print("Amount: ");
            double amount = Double.parseDouble(scanner.nextLine());

            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return;
            }

            if (!isDeposit) amount *= -1;

            Transaction t = new Transaction(date, time, description, vendor, amount);
            manager.addTransaction(t);
            System.out.println("Transaction added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
        }
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
                case "R" -> showReportsScreen();
                case "H" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void showReportsScreen() {
        boolean running = true;
        while (running) {
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
                case "1" -> manager.filterByMonthToDate();
                case "2" -> manager.filterByPreviousMonth();
                case "3" -> manager.filterByYearToDate();
                case "4" -> manager.filterByPreviousYear();
                case "5" -> {
                    System.out.print("Enter vendor name: ");
                    String vendor = scanner.nextLine();
                    manager.searchByVendor(vendor);
                }
                case "0" -> running = false;
                default -> System.out.println("Option not implemented yet.");
            }
        }
    }
}

// Transaction.java
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

    public String getDisplayString() {
        String amountStr = String.format("$%.2f", amount);
        return date + " | " + time + " | " + description + " | " + vendor + " | " + amountStr;
    }
}

// TransactionManager.java
import java.util.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TransactionManager {
    private List<Transaction> transactions = CSVHandler.loadTransactions();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void addTransaction(Transaction t) {
        transactions.add(t);
        CSVHandler.saveTransactions(transactions);
    }

    public void listAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\n--- All Transactions ---");
        transactions.stream()
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .forEach(t -> System.out.println(t.getDisplayString()));

        printBalance();
    }

    public void listDeposits() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\n--- Deposits ---");
        transactions.stream()
                .filter(t -> t.amount > 0)
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .forEach(t -> System.out.println(t.getDisplayString()));
    }

    public void listPayments() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\n--- Payments ---");
        transactions.stream()
                .filter(t -> t.amount < 0)
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .forEach(t -> System.out.println(t.getDisplayString()));
    }

    public void searchByVendor(String vendor) {
        System.out.println("\n--- Transactions for vendor: " + vendor + " ---");

        boolean found = false;
        for (Transaction t : transactions) {
            if (t.vendor.toLowerCase().contains(vendor.toLowerCase())) {
                System.out.println(t.getDisplayString());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No transactions found for this vendor.");
        }
    }

    public void filterByMonthToDate() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate startOfMonth = today.withDayOfMonth(1);

            System.out.println("\n--- Month To Date Transactions ---");
            filterByDateRange(startOfMonth.format(dateFormatter), today.format(dateFormatter));
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    public void filterByPreviousMonth() {
        try {
            LocalDate today = LocalDate.now();
            YearMonth previousMonth = YearMonth.from(today).minusMonths(1);
            LocalDate startDate = previousMonth.atDay(1);
            LocalDate endDate = previousMonth.atEndOfMonth();

            System.out.println("\n--- Previous Month Transactions ---");
            filterByDateRange(startDate.format(dateFormatter), endDate.format(dateFormatter));
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    public void filterByYearToDate() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate startOfYear = today.withDayOfYear(1);

            System.out.println("\n--- Year To Date Transactions ---");
            filterByDateRange(startOfYear.format(dateFormatter), today.format(dateFormatter));
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    public void filterByPreviousYear() {
        try {
            int previousYear = LocalDate.now().getYear() - 1;
            String startDate = previousYear + "-01-01";
            String endDate = previousYear + "-12-31";

            System.out.println("\n--- Previous Year Transactions ---");
            filterByDateRange(startDate, endDate);
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private void filterByDateRange(String startDateStr, String endDateStr) {
        try {
            LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);

            boolean found = false;
            for (Transaction t : transactions) {
                try {
                    LocalDate transactionDate = LocalDate.parse(t.date, dateFormatter);

                    if (!transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate)) {
                        System.out.println(t.getDisplayString());
                        found = true;
                    }
                } catch (DateTimeParseException e) {
                    // Skip transactions with invalid dates
                }
            }

            if (!found) {
                System.out.println("No transactions found in this date range.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
        }
    }

    private void printBalance() {
        double balance = transactions.stream().mapToDouble(t -> t.amount).sum();
        System.out.println("\nCurrent Balance: $" + String.format("%.2f", balance));
    }
}

// CSVHandler.java
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
            }
        }
    }
}
