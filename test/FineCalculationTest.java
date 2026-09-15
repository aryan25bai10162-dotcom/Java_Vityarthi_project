import models.Faculty;
import models.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the polymorphic fine calculation and borrow-limit logic
 * defined by the Fineable interface, as implemented by Student and Faculty.
 *
 * Run with Maven:   mvn test
 * Run with an IDE:  right-click this class -> Run Tests
 */
public class FineCalculationTest {

    private Student student;
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        student = new Student("S001", "Test Student", "student@example.com");
        faculty = new Faculty("F001", "Test Faculty", "faculty@example.com");
    }

    // ---------- Student fine calculation ----------

    @Test
    @DisplayName("Student returning on time (0 days overdue) owes no fine")
    void studentOnTimeReturnHasNoFine() {
        assertEquals(0.0, student.calculateFine(0));
    }

    @Test
    @DisplayName("Student returning early (negative days overdue) owes no fine")
    void studentEarlyReturnHasNoFine() {
        assertEquals(0.0, student.calculateFine(-3));
    }

    @Test
    @DisplayName("Student fine accrues at 5.0/day with no grace period")
    void studentFineAccruesAtFiveRupeesPerDay() {
        assertEquals(5.0, student.calculateFine(1));
        assertEquals(10.0, student.calculateFine(2));
        assertEquals(50.0, student.calculateFine(10));
    }

    @Test
    @DisplayName("Student borrow limit is 3 books")
    void studentBorrowLimitIsThree() {
        assertEquals(3, student.getBorrowLimit());
    }

    // ---------- Faculty fine calculation ----------

    @Test
    @DisplayName("Faculty within the 3-day grace period owes no fine")
    void facultyWithinGracePeriodHasNoFine() {
        assertEquals(0.0, faculty.calculateFine(0));
        assertEquals(0.0, faculty.calculateFine(1));
        assertEquals(0.0, faculty.calculateFine(3)); // exactly at the grace boundary
    }

    @Test
    @DisplayName("Faculty fine accrues at 2.0/day only after the grace period")
    void facultyFineAccruesAfterGracePeriod() {
        assertEquals(2.0, faculty.calculateFine(4));  // 1 chargeable day
        assertEquals(4.0, faculty.calculateFine(5));  // 2 chargeable days
        assertEquals(14.0, faculty.calculateFine(10)); // 7 chargeable days
    }

    @Test
    @DisplayName("Faculty borrow limit is 6 books")
    void facultyBorrowLimitIsSix() {
        assertEquals(6, faculty.getBorrowLimit());
    }

    // ---------- Category identity ----------

    @Test
    @DisplayName("getCategory() correctly identifies each member type")
    void categoriesAreCorrectlyIdentified() {
        assertEquals("Student", student.getCategory());
        assertEquals("Faculty", faculty.getCategory());
    }

    @Test
    @DisplayName("Faculty pays less than a Student for the same overdue duration beyond grace")
    void facultyFineIsLowerThanStudentForSameOverdueDays() {
        long daysOverdue = 8;
        double studentFine = student.calculateFine(daysOverdue);
        double facultyFine = faculty.calculateFine(daysOverdue);
        assert facultyFine < studentFine
                : "Faculty fine (" + facultyFine + ") should be lower than Student fine (" + studentFine + ")";
        assertEquals(40.0, studentFine);
        assertEquals(10.0, facultyFine);
    }
}
