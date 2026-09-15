package models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Immutable record of one borrow/return event, used for history and reporting.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { BORROW, RETURN }

    private final String isbn;
    private final String memberId;
    private final Type type;
    private final LocalDate date;
    private final double fineCharged;

    public Transaction(String isbn, String memberId, Type type, LocalDate date, double fineCharged) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.type = type;
        this.date = date;
        this.fineCharged = fineCharged;
    }

    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public Type getType() { return type; }
    public LocalDate getDate() { return date; }
    public double getFineCharged() { return fineCharged; }

    @Override
    public String toString() {
        String fineStr = fineCharged > 0 ? String.format(" | Fine: %.2f", fineCharged) : "";
        return String.format("[%s] %s - ISBN:%s - Member:%s%s", date, type, isbn, memberId, fineStr);
    }
}
