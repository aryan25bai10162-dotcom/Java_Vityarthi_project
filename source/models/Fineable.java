package models;

/**
 * Any member type that can incur overdue fines implements this.
 * Different member categories calculate fines differently (polymorphism).
 */
public interface Fineable {
    double FINE_FREE_GRACE_DAYS = 0; // default, overridden per implementation if needed

    /**
     * Calculates the fine owed given the number of days a book is overdue.
     */
    double calculateFine(long daysOverdue);

    /**
     * Maximum number of books this member type may borrow at once.
     */
    int getBorrowLimit();
}
