package models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents one currently-active loan: which member has which book, and when it's due.
 */
public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int LOAN_PERIOD_DAYS = 14;

    private final String isbn;
    private final String memberId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;

    public BorrowRecord(String isbn, String memberId, LocalDate borrowDate) {
        this.isbn = isbn;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(LOAN_PERIOD_DAYS);
    }

    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }

    public long daysOverdue(LocalDate onDate) {
        long diff = java.time.temporal.ChronoUnit.DAYS.between(dueDate, onDate);
        return Math.max(diff, 0);
    }

    /** Composite key used to look this record up quickly (isbn+memberId is unique per active loan). */
    public String key() {
        return isbn + "::" + memberId;
    }
}
