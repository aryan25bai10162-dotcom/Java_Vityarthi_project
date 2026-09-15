package exceptions;

/**
 * Thrown when a requested member ID does not exist in the system.
 */
public class MemberNotFoundException extends Exception {
    public MemberNotFoundException(String memberId) {
        super("Member with ID '" + memberId + "' was not found.");
    }
}
