import core.Library;
import exceptions.BookNotAvailableException;
import exceptions.BookNotFoundException;
import exceptions.BorrowLimitExceededException;
import exceptions.MemberNotFoundException;
import models.Book;
import models.Faculty;
import models.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests exercising Library.borrowBook() / returnBook() against
 * the four custom checked exceptions, and confirming that borrow-limit
 * enforcement differs correctly between Student and Faculty.
 *
 * Run with Maven:   mvn test
 */
public class BorrowLimitTest {

    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();

        // A generous catalog so availability never blocks the limit tests below.
        library.addBook(new Book("B1", "Book One", "Author A", "Fiction", 5));
        library.addBook(new Book("B2", "Book Two", "Author B", "Fiction", 5));
        library.addBook(new Book("B3", "Book Three", "Author C", "Fiction", 5));
        library.addBook(new Book("B4", "Book Four", "Author D", "Fiction", 5));
        library.addBook(new Book("SoloCopy", "Only One Copy", "Author E", "Fiction", 1));

        library.addMember(new Student("S001", "Test Student", "student@example.com"));
        library.addMember(new Faculty("F001", "Test Faculty", "faculty@example.com"));
    }

    @Test
    @DisplayName("Borrowing within the limit succeeds without throwing")
    void borrowWithinLimitSucceeds() throws Exception {
        library.borrowBook("B1", "S001");
        library.borrowBook("B2", "S001");
        assertEquals(2, library.findMember("S001").getBorrowedIsbns().size());
    }

    @Test
    @DisplayName("A Student cannot borrow a 4th book (limit = 3)")
    void studentCannotExceedBorrowLimitOfThree() throws Exception {
        library.borrowBook("B1", "S001");
        library.borrowBook("B2", "S001");
        library.borrowBook("B3", "S001");

        assertThrows(BorrowLimitExceededException.class,
                () -> library.borrowBook("B4", "S001"));
    }

    @Test
    @DisplayName("A Faculty member can borrow up to 6 books without exception")
    void facultyCanBorrowUpToSixBooks() throws Exception {
        library.addBook(new Book("B5", "Book Five", "Author F", "Fiction", 2));
        library.addBook(new Book("B6", "Book Six", "Author G", "Fiction", 2));

        library.borrowBook("B1", "F001");
        library.borrowBook("B2", "F001");
        library.borrowBook("B3", "F001");
        library.borrowBook("B4", "F001");
        library.borrowBook("B5", "F001");
        library.borrowBook("B6", "F001");

        assertEquals(6, library.findMember("F001").getBorrowedIsbns().size());
    }

    @Test
    @DisplayName("Borrowing an unknown ISBN throws BookNotFoundException")
    void borrowingUnknownIsbnThrows() {
        assertThrows(BookNotFoundException.class,
                () -> library.borrowBook("DOES-NOT-EXIST", "S001"));
    }

    @Test
    @DisplayName("Borrowing with an unknown member ID throws MemberNotFoundException")
    void borrowingWithUnknownMemberThrows() {
        assertThrows(MemberNotFoundException.class,
                () -> library.borrowBook("B1", "ZZZ"));
    }

    @Test
    @DisplayName("Borrowing a book with zero available copies throws BookNotAvailableException")
    void borrowingUnavailableBookThrows() throws Exception {
        library.borrowBook("SoloCopy", "S001"); // takes the only copy

        assertThrows(BookNotAvailableException.class,
                () -> library.borrowBook("SoloCopy", "F001"));
    }

    @Test
    @DisplayName("Returning a book frees up a slot so another book can be borrowed")
    void returningABookFreesUpBorrowSlot() throws Exception {
        library.borrowBook("B1", "S001");
        library.borrowBook("B2", "S001");
        library.borrowBook("B3", "S001");

        library.returnBook("B1", "S001"); // now back under the limit of 3

        assertDoesNotThrow(() -> library.borrowBook("B4", "S001"));
        assertEquals(3, library.findMember("S001").getBorrowedIsbns().size());
    }
}
