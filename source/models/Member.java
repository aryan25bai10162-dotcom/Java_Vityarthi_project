package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all library members.
 * Concrete subtypes (Student, Faculty) define their own borrow limits and fine rates.
 */
public abstract class Member implements Fineable, Serializable {
    private static final long serialVersionUID = 1L;

    private String memberId;
    private String name;
    private String email;
    private List<String> borrowedIsbns = new ArrayList<>();

    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getBorrowedIsbns() { return borrowedIsbns; }

    public void addBorrowedBook(String isbn) { borrowedIsbns.add(isbn); }
    public void removeBorrowedBook(String isbn) { borrowedIsbns.remove(isbn); }

    /** Each member type describes itself differently (e.g. "Student", "Faculty"). */
    public abstract String getCategory();

    @Override
    public String toString() {
        return String.format("%-8s | %-20s | %-25s | %-8s | Borrowed: %d/%d",
                memberId, name, email, getCategory(), borrowedIsbns.size(), getBorrowLimit());
    }
}
