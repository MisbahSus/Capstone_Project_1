import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public String date, time, description, vendor;
    public double amount;
    public double balance;

    public Transaction(String date, String time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
        this.balance = 0;
    }

    public Transaction(String date, String time, String description, String vendor, double amount, double balance) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
        this.balance = balance;
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
