package exceptions;

/**
 * Thrown when a book exists but has zero copies currently available to borrow.
 */
public class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String title) {
        super("Book '" + title + "' is currently not available (all copies are issued).");
    }
}
