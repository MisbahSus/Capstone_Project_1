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
