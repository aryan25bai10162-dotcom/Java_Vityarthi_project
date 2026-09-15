package exceptions;

/**
 * Thrown when a member tries to borrow more books than their category allows.
 */
public class BorrowLimitExceededException extends Exception {
    public BorrowLimitExceededException(String memberName, int limit) {
        super(memberName + " has reached their borrowing limit of " + limit + " book(s).");
    }
}
