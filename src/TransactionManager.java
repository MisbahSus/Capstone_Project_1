import java.util.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class TransactionManager {
    private List<Transaction> transactions = CSVHandler.loadTransactions();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private double currentBalance = calculateCurrentBalance();

    private double calculateCurrentBalance() {
        // Calculate the balance based on loaded transactions
        if (transactions.isEmpty()) {
            return 0.0;
        } else {
            // Find the most recent transaction and get its balance
            return transactions.stream()
                    .sorted(Comparator.comparing((Transaction t) -> t.date + " " + t.time).reversed())
                    .findFirst()
                    .map(t -> t.balance)
                    .orElse(0.0);
        }
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void addTransaction(Transaction t) {
        // Calculate and set the new balance for this transaction
        currentBalance += t.amount;
        t.balance = currentBalance;

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
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-8s | %-30s | %-20s | %-10s | %-10s%n",
                "DATE", "TIME", "DESCRIPTION", "VENDOR", "AMOUNT", "BALANCE");
        System.out.println("---------------------------------------------------------------------------------------------");
    }

    private void printFormattedTransaction(Transaction t) {
        String amountStr = String.format("$%,.2f", t.amount);
        String balanceStr = String.format("$%,.2f", t.balance);
        System.out.printf("%-10s | %-8s | %-30s | %-20s | %-10s | %-10s%n",
                t.date, t.time,
                truncateString(t.description, 30),
                truncateString(t.vendor, 20),
                amountStr, balanceStr);
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

        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("SUMMARY:");
        System.out.printf("Total Transactions: %d (Deposits: %d, Payments: %d)%n",
                tList.size(), depositCount, paymentCount);
        System.out.printf("Deposits Total: $%,.2f%n", depositTotal);
        System.out.printf("Payments Total: $%,.2f%n", paymentTotal);
        System.out.printf("Net Total: $%,.2f%n", total);
        System.out.printf("Current Balance: $%,.2f%n", currentBalance);
        System.out.println("---------------------------------------------------------------------------------------------");
    }
}
