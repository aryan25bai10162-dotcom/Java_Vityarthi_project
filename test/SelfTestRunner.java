import core.Library;
import exceptions.BookNotAvailableException;
import exceptions.BookNotFoundException;
import exceptions.BorrowLimitExceededException;
import exceptions.MemberNotFoundException;
import models.Book;
import models.Faculty;
import models.Student;

/**
 * A minimal, dependency-free test runner that exercises the same cases as
 * FineCalculationTest.java and BorrowLimitTest.java, for environments where
 * Maven/JUnit are not set up. Run it directly with:
 *
 *   javac -d bin src/Main.java src/models/*.java src/exceptions/*.java src/core/*.java test/SelfTestRunner.java
 *   java -cp bin SelfTestRunner
 *
 * This is a convenience check, not a replacement for the real JUnit 5 suite
 * (FineCalculationTest.java, BorrowLimitTest.java), which is the version
 * intended for grading via `mvn test`.
 */
public class SelfTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("Running self-test suite (no external dependencies)...\n");

        testStudentOnTimeReturnHasNoFine();
        testStudentEarlyReturnHasNoFine();
        testStudentFineAccrual();
        testStudentBorrowLimit();
        testFacultyWithinGracePeriod();
        testFacultyFineAccrualAfterGrace();
        testFacultyBorrowLimit();
        testCategoriesIdentifiedCorrectly();
        testFacultyFineLowerThanStudent();

        testBorrowWithinLimitSucceeds();
        testStudentCannotExceedLimitOfThree();
        testFacultyCanBorrowUpToSix();
        testBorrowingUnknownIsbnThrows();
        testBorrowingWithUnknownMemberThrows();
        testBorrowingUnavailableBookThrows();
        testReturningFreesUpSlot();

        System.out.println("\n==============================");
        System.out.println("RESULTS: " + passed + " passed, " + failed + " failed");
        System.out.println("==============================");
        if (failed > 0) System.exit(1);
    }

    // ---------- fine calculation ----------

    private static void testStudentOnTimeReturnHasNoFine() {
        Student s = new Student("S001", "Test", "t@example.com");
        check("Student on-time return has no fine", s.calculateFine(0) == 0.0);
    }

    private static void testStudentEarlyReturnHasNoFine() {
        Student s = new Student("S001", "Test", "t@example.com");
        check("Student early return has no fine", s.calculateFine(-3) == 0.0);
    }

    private static void testStudentFineAccrual() {
        Student s = new Student("S001", "Test", "t@example.com");
        check("Student fine: 1 day = 5.0", s.calculateFine(1) == 5.0);
        check("Student fine: 2 days = 10.0", s.calculateFine(2) == 10.0);
        check("Student fine: 10 days = 50.0", s.calculateFine(10) == 50.0);
    }

    private static void testStudentBorrowLimit() {
        Student s = new Student("S001", "Test", "t@example.com");
        check("Student borrow limit is 3", s.getBorrowLimit() == 3);
    }

    private static void testFacultyWithinGracePeriod() {
        Faculty f = new Faculty("F001", "Test", "t@example.com");
        check("Faculty 0 days overdue = no fine", f.calculateFine(0) == 0.0);
        check("Faculty 1 day overdue (within grace) = no fine", f.calculateFine(1) == 0.0);
        check("Faculty 3 days overdue (grace boundary) = no fine", f.calculateFine(3) == 0.0);
    }

    private static void testFacultyFineAccrualAfterGrace() {
        Faculty f = new Faculty("F001", "Test", "t@example.com");
        check("Faculty 4 days overdue = 2.0 (1 chargeable day)", f.calculateFine(4) == 2.0);
        check("Faculty 5 days overdue = 4.0 (2 chargeable days)", f.calculateFine(5) == 4.0);
        check("Faculty 10 days overdue = 14.0 (7 chargeable days)", f.calculateFine(10) == 14.0);
    }

    private static void testFacultyBorrowLimit() {
        Faculty f = new Faculty("F001", "Test", "t@example.com");
        check("Faculty borrow limit is 6", f.getBorrowLimit() == 6);
    }

    private static void testCategoriesIdentifiedCorrectly() {
        Student s = new Student("S001", "Test", "t@example.com");
        Faculty f = new Faculty("F001", "Test", "t@example.com");
        check("Student category is 'Student'", s.getCategory().equals("Student"));
        check("Faculty category is 'Faculty'", f.getCategory().equals("Faculty"));
    }

    private static void testFacultyFineLowerThanStudent() {
        Student s = new Student("S001", "Test", "t@example.com");
        Faculty f = new Faculty("F001", "Test", "t@example.com");
        double studentFine = s.calculateFine(8);
        double facultyFine = f.calculateFine(8);
        check("Faculty fine (10.0) is lower than Student fine (40.0) at 8 days overdue",
                facultyFine == 10.0 && studentFine == 40.0 && facultyFine < studentFine);
    }

    // ---------- borrow-limit / exception integration ----------

    private static Library freshLibrary() {
        Library library = new Library();
        library.addBook(new Book("B1", "Book One", "Author A", "Fiction", 5));
        library.addBook(new Book("B2", "Book Two", "Author B", "Fiction", 5));
        library.addBook(new Book("B3", "Book Three", "Author C", "Fiction", 5));
        library.addBook(new Book("B4", "Book Four", "Author D", "Fiction", 5));
        library.addBook(new Book("B5", "Book Five", "Author F", "Fiction", 2));
        library.addBook(new Book("B6", "Book Six", "Author G", "Fiction", 2));
        library.addBook(new Book("SoloCopy", "Only One Copy", "Author E", "Fiction", 1));
        library.addMember(new Student("S001", "Test Student", "student@example.com"));
        library.addMember(new Faculty("F001", "Test Faculty", "faculty@example.com"));
        return library;
    }

    private static void testBorrowWithinLimitSucceeds() {
        Library library = freshLibrary();
        try {
            library.borrowBook("B1", "S001");
            library.borrowBook("B2", "S001");
            check("Borrowing 2 books within limit succeeds",
                    library.findMember("S001").getBorrowedIsbns().size() == 2);
        } catch (Exception e) {
            check("Borrowing within limit succeeds (" + e.getMessage() + ")", false);
        }
    }

    private static void testStudentCannotExceedLimitOfThree() {
        Library library = freshLibrary();
        try {
            library.borrowBook("B1", "S001");
            library.borrowBook("B2", "S001");
            library.borrowBook("B3", "S001");
            library.borrowBook("B4", "S001");
            check("Student borrowing a 4th book should throw BorrowLimitExceededException", false);
        } catch (BorrowLimitExceededException e) {
            check("Student cannot exceed borrow limit of 3", true);
        } catch (Exception e) {
            check("Unexpected exception type: " + e.getClass().getSimpleName(), false);
        }
    }

    private static void testFacultyCanBorrowUpToSix() {
        Library library = freshLibrary();
        try {
            library.borrowBook("B1", "F001");
            library.borrowBook("B2", "F001");
            library.borrowBook("B3", "F001");
            library.borrowBook("B4", "F001");
            library.borrowBook("B5", "F001");
            library.borrowBook("B6", "F001");
            check("Faculty can borrow up to 6 books",
                    library.findMember("F001").getBorrowedIsbns().size() == 6);
        } catch (Exception e) {
            check("Faculty borrowing up to limit should not throw (" + e.getMessage() + ")", false);
        }
    }

    private static void testBorrowingUnknownIsbnThrows() {
        Library library = freshLibrary();
        try {
            library.borrowBook("DOES-NOT-EXIST", "S001");
            check("Borrowing unknown ISBN should throw BookNotFoundException", false);
        } catch (BookNotFoundException e) {
            check("Borrowing unknown ISBN throws BookNotFoundException", true);
        } catch (Exception e) {
            check("Unexpected exception type: " + e.getClass().getSimpleName(), false);
        }
    }

    private static void testBorrowingWithUnknownMemberThrows() {
        Library library = freshLibrary();
        try {
            library.borrowBook("B1", "ZZZ");
            check("Borrowing with unknown member should throw MemberNotFoundException", false);
        } catch (MemberNotFoundException e) {
            check("Borrowing with unknown member throws MemberNotFoundException", true);
        } catch (Exception e) {
            check("Unexpected exception type: " + e.getClass().getSimpleName(), false);
        }
    }

    private static void testBorrowingUnavailableBookThrows() {
        Library library = freshLibrary();
        try {
            library.borrowBook("SoloCopy", "S001");
            try {
                library.borrowBook("SoloCopy", "F001");
                check("Borrowing an unavailable book should throw BookNotAvailableException", false);
            } catch (BookNotAvailableException e) {
                check("Borrowing unavailable book throws BookNotAvailableException", true);
            }
        } catch (Exception e) {
            check("Setup for unavailable-book test failed: " + e.getMessage(), false);
        }
    }

    private static void testReturningFreesUpSlot() {
        Library library = freshLibrary();
        try {
            library.borrowBook("B1", "S001");
            library.borrowBook("B2", "S001");
            library.borrowBook("B3", "S001");
            library.returnBook("B1", "S001");
            library.borrowBook("B4", "S001"); // should now succeed
            check("Returning a book frees a slot for another borrow",
                    library.findMember("S001").getBorrowedIsbns().size() == 3);
        } catch (Exception e) {
            check("Returning a book should free up a slot (" + e.getMessage() + ")", false);
        }
    }

    // ---------- helper ----------

    private static void check(String description, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + description);
            passed++;
        } else {
            System.out.println("  [FAIL] " + description);
            failed++;
        }
    }
}
