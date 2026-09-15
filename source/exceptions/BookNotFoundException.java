package exceptions;

/**
 * Thrown when a requested book ISBN does not exist in the library catalog.
 */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String isbn) {
        super("Book with ISBN '" + isbn + "' was not found in the catalog.");
    }
}
