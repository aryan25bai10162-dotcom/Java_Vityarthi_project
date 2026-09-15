package models;

public class Student extends Member {
    private static final long serialVersionUID = 1L;
    private static final int BORROW_LIMIT = 3;
    private static final double FINE_PER_DAY = 5.0; // currency units per day overdue

    public Student(String memberId, String name, String email) {
        super(memberId, name, email);
    }

    @Override
    public String getCategory() {
        return "Student";
    }

    @Override
    public double calculateFine(long daysOverdue) {
        if (daysOverdue <= 0) return 0.0;
        return daysOverdue * FINE_PER_DAY;
    }

    @Override
    public int getBorrowLimit() {
        return BORROW_LIMIT;
    }
}
