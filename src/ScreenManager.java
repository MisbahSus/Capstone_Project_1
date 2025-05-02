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
