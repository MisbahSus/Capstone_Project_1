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
