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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ScreenManager {
    private final Scanner scanner = new Scanner(System.in);
    private final TransactionManager manager = new TransactionManager();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void showHomeScreen() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Personal Finance Tracker ===");
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
                case "X" -> {
                    System.out.println("Thank you for using Personal Finance Tracker!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addTransaction(boolean isDeposit) {
        String transactionType = isDeposit ? "Deposit" : "Payment";
        System.out.println("\n=== Add " + transactionType + " ===");
        
        // Get date with validation
        LocalDate date = null;
        while (date == null) {
            // Show current date as default option
            LocalDate currentDate = LocalDate.now();
            System.out.print("Date (yyyy-MM-dd) or press Enter for today [" + currentDate.format(dateFormatter) + "]: ");
            String dateInput = scanner.nextLine().trim();
            
            if (dateInput.isEmpty()) {
                date = currentDate;
            } else {
                try {
                    date = LocalDate.parse(dateInput, dateFormatter);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                }
            }
        }
        
        // Get time with validation
        LocalTime time = null;
        while (time == null) {
            // Show current time as default option
            LocalTime currentTime = LocalTime.now();
            System.out.print("Time (HH:mm:ss) or press Enter for now [" + currentTime.format(timeFormatter) + "]: ");
            String timeInput = scanner.nextLine().trim();
            
            if (timeInput.isEmpty()) {
                time = currentTime;
            } else {
                try {
                    time = LocalTime.parse(timeInput, timeFormatter);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid time format. Please use HH:mm:ss.");
                }
            }
        }
        
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        while (description.isEmpty()) {
            System.out.println("Description cannot be empty.");
            System.out.print("Description: ");
            description = scanner.nextLine().trim();
        }
        
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine().trim();
        while (vendor.isEmpty()) {
            System.out.println("Vendor cannot be empty.");
            System.out.print("Vendor: ");
            vendor = scanner.nextLine().trim();
        }
        
        // Get amount with validation
        double amount = 0;
        boolean validAmount = false;
        while (!validAmount) {
            System.out.print("Amount: ");
            String amountInput = scanner.nextLine().trim();
            try {
                amount = Double.parseDouble(amountInput);
                if (amount <= 0) {
                    System.out.println("Amount must be greater than zero.");
                } else {
                    validAmount = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
            }
        }
        
        if (!isDeposit) amount *= -1;

        Transaction t = new Transaction(
            date.format(dateFormatter), 
            time.format(timeFormatter), 
            description, 
            vendor, 
            amount
        );
        
        manager.addTransaction(t);
        System.out.println(transactionType + " added successfully!");
    }

    private void showLedgerScreen() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Ledger ===");
            System.out.println("A) All Entries");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "A" -> {
                    System.out.println("\n=== All Transactions ===");
                    manager.listAllTransactions();
                }
                case "D" -> {
                    System.out.println("\n=== Deposits ===");
                    manager.listDeposits();
                }
                case "P" -> {
                    System.out.println("\n=== Payments ===");
                    manager.listPayments();
                }
                case "R" -> showReportsScreen();
                case "H" -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void showReportsScreen() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Reports ===");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Date Range");
            System.out.println("0) Back");
            System.out.print("Choose an option: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> manager.monthToDateReport();
                case "2" -> manager.previousMonthReport();
                case "3" -> manager.yearToDateReport();
                case "4" -> manager.previousYearReport();
                case "5" -> {
                    System.out.print("Enter vendor name: ");
                    String vendor = scanner.nextLine().trim();
                    System.out.println("\n=== Transactions for Vendor: " + vendor + " ===");
                    manager.searchByVendor(vendor);
                }
                case "6" -> {
                    LocalDate startDate = null;
                    while (startDate == null) {
                        System.out.print("Start date (yyyy-MM-dd): ");
                        try {
                            startDate = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                        }
                    }
                    
                    LocalDate endDate = null;
                    while (endDate == null) {
                        System.out.print("End date (yyyy-MM-dd): ");
                        try {
                            endDate = LocalDate.parse(scanner.nextLine().trim(), dateFormatter);
                            if (endDate.isBefore(startDate)) {
                                System.out.println("End date must be after start date.");
                                endDate = null;
                            }
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                        }
                    }
                    
                    System.out.println("\n=== Transactions from " + startDate + " to " + endDate + " ===");
                    manager.customDateRangeReport(startDate, endDate);
                }
                case "0" -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}

// Transaction.java
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
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
        return String.format("%s | %s | %-30s | %-20s | $%,.2f", 
                             date, time, description, vendor, amount);
    }
    
    public String toCsvString() {
        return date + "|" + time + "|" + description + "|" + vendor + "|" + amount;
    }
    
    public LocalDate getLocalDate() {
        return LocalDate.parse(date, DATE_FORMATTER);
    }
    
    public LocalTime getLocalTime() {
        return LocalTime.parse(time, TIME_FORMATTER);
    }
}

// TransactionManager.java
import java.util.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

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

        printTransactionHeader();
        transactions.stream()
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .forEach(this::printFormattedTransaction);
        printTransactionSummary(transactions);
    }

    public void listDeposits() {
        List<Transaction> deposits = transactions.stream()
                .filter(t -> t.amount > 0)
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .toList();
                
        if (deposits.isEmpty()) {
            System.out.println("No deposits found.");
            return;
        }

        printTransactionHeader();
        deposits.forEach(this::printFormattedTransaction);
        printTransactionSummary(deposits);
    }

    public void listPayments() {
        List<Transaction> payments = transactions.stream()
                .filter(t -> t.amount < 0)
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .toList();
                
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        printTransactionHeader();
        payments.forEach(this::printFormattedTransaction);
        printTransactionSummary(payments);
    }
    
    public void searchByVendor(String vendor) {
        List<Transaction> results = transactions.stream()
                .filter(t -> t.vendor.toLowerCase().contains(vendor.toLowerCase()))
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .toList();
                
        if (results.isEmpty()) {
            System.out.println("No transactions found for vendor: " + vendor);
            return;
        }

        printTransactionHeader();
        results.forEach(this::printFormattedTransaction);
        printTransactionSummary(results);
    }
    
    public void monthToDateReport() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        System.out.println("\n=== Month To Date Report (" + startOfMonth + " to " + today + ") ===");
        filterTransactionsByDateRange(startOfMonth, today);
    }
    
    public void previousMonthReport() {
        LocalDate today = LocalDate.now();
        YearMonth previousMonth = YearMonth.from(today).minusMonths(1);
        LocalDate startDate = previousMonth.atDay(1);
        LocalDate endDate = previousMonth.atEndOfMonth();
        
        System.out.println("\n=== Previous Month Report (" + startDate + " to " + endDate + ") ===");
        filterTransactionsByDateRange(startDate, endDate);
    }
    
    public void yearToDateReport() {
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.withDayOfYear(1);
        
        System.out.println("\n=== Year To Date Report (" + startOfYear + " to " + today + ") ===");
        filterTransactionsByDateRange(startOfYear, today);
    }
    
    public void previousYearReport() {
        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1;
        LocalDate startDate = LocalDate.of(previousYear, 1, 1);
        LocalDate endDate = LocalDate.of(previousYear, 12, 31);
        
        System.out.println("\n=== Previous Year Report (" + startDate + " to " + endDate + ") ===");
        filterTransactionsByDateRange(startDate, endDate);
    }
    
    public void customDateRangeReport(LocalDate startDate, LocalDate endDate) {
        filterTransactionsByDateRange(startDate, endDate);
    }
    
    private void filterTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> {
                    LocalDate transactionDate = t.getLocalDate();
                    return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
                })
                .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                .toList();
                
        if (filteredTransactions.isEmpty()) {
            System.out.println("No transactions found for this period.");
            return;
        }

        printTransactionHeader();
        filteredTransactions.forEach(this::printFormattedTransaction);
        printTransactionSummary(filteredTransactions);
    }
    
    private void printTransactionHeader() {
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-10s | %-8s | %-30s | %-20s | %-10s%n", 
                         "DATE", "TIME", "DESCRIPTION", "VENDOR", "AMOUNT");
        System.out.println("----------------------------------------------------------------------");
    }
    
    private void printFormattedTransaction(Transaction t) {
        String amountStr = String.format("$%,.2f", t.amount);
        System.out.printf("%-10s | %-8s | %-30s | %-20s | %-10s%n", 
                         t.date, t.time, 
                         truncateString(t.description, 30),
                         truncateString(t.vendor, 20),
                         amountStr);
    }
    
    private String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
    private void printTransactionSummary(List<Transaction> tList) {
        double total = tList.stream().mapToDouble(t -> t.amount).sum();
        long depositCount = tList.stream().filter(t -> t.amount > 0).count();
        long paymentCount = tList.stream().filter(t -> t.amount < 0).count();
        double depositTotal = tList.stream().filter(t -> t.amount > 0).mapToDouble(t -> t.amount).sum();
        double paymentTotal = tList.stream().filter(t -> t.amount < 0).mapToDouble(t -> t.amount).sum();
        
        System.out.println("----------------------------------------------------------------------");
        System.out.println("SUMMARY:");
        System.out.printf("Total Transactions: %d (Deposits: %d, Payments: %d)%n", 
                         tList.size(), depositCount, paymentCount);
        System.out.printf("Deposits Total: $%,.2f%n", depositTotal);
        System.out.printf("Payments Total: $%,.2f%n", paymentTotal);
        System.out.printf("Net Total: $%,.2f%n", total);
        System.out.println("----------------------------------------------------------------------");
    }
}

// CSVHandler.java
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
                if (parts.length == 5) {
                    try {
                        Transaction t = new Transaction(
                            parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4])
                        );
                        transactions.add(t);
                    } catch (NumberFormatException e) {
                        System.out.println("Warning: Skipped invalid transaction record: " + line);
                    }
                }
            }
            System.out.println("Loaded " + transactions.size() + " transactions.");
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
