package models;

public class Faculty extends Member {
    private static final long serialVersionUID = 1L;
    private static final int BORROW_LIMIT = 6;
    private static final double FINE_PER_DAY = 2.0;
    private static final int GRACE_DAYS = 3; // faculty get a few grace days before fines start

    public Faculty(String memberId, String name, String email) {
        super(memberId, name, email);
    }

    @Override
    public String getCategory() {
        return "Faculty";
    }

    @Override
    public double calculateFine(long daysOverdue) {
        long chargeableDays = daysOverdue - GRACE_DAYS;
        if (chargeableDays <= 0) return 0.0;
        return chargeableDays * FINE_PER_DAY;
    }

    @Override
    public int getBorrowLimit() {
        return BORROW_LIMIT;
    }
}
