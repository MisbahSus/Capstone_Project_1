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
