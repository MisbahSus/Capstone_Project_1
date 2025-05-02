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
