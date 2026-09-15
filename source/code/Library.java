package core;

import exceptions.*;
import models.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Core engine of the system. Demonstrates several Collections choices deliberately:
 *  - HashMap for O(1) average lookup of books/members by key
 *  - LinkedHashMap for active loans to preserve insertion order for reporting
 *  - ArrayList for chronological transaction history
 *  - TreeMap for an alphabetically-sorted catalog report
 */
public class Library {

    private final Map<String, Book> catalog = new HashMap<>();          // isbn -> Book
    private final Map<String, Member> members = new HashMap<>();        // memberId -> Member
    private final Map<String, BorrowRecord> activeLoans = new LinkedHashMap<>(); // key -> record
    private final List<Transaction> history = new ArrayList<>();

    // ---------- Book Management Module ----------

    public void addBook(Book book) {
        catalog.merge(book.getIsbn(), book, (existing, incoming) -> {
            // if the ISBN already exists, treat this as adding more copies
            for (int i = 0; i < incoming.getTotalCopies(); i++) existing.incrementAvailable();
            return existing;
        });
    }

    public Book findBook(String isbn) throws BookNotFoundException {
        Book b = catalog.get(isbn);
        if (b == null) throw new BookNotFoundException(isbn);
        return b;
    }

    public List<Book> searchByTitle(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : catalog.values()) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) results.add(b);
        }
        return results;
    }

    public Collection<Book> getAllBooksSortedByTitle() {
        Map<String, Book> sorted = new TreeMap<>();
        for (Book b : catalog.values()) sorted.put(b.getTitle(), b);
        return sorted.values();
    }

    // ---------- Member Management Module ----------

    public void addMember(Member member) {
        members.put(member.getMemberId(), member);
    }

    public Member findMember(String memberId) throws MemberNotFoundException {
        Member m = members.get(memberId);
        if (m == null) throw new MemberNotFoundException(memberId);
        return m;
    }

    public Collection<Member> getAllMembers() {
        return members.values();
    }

    // ---------- Borrow / Return / Fine Module ----------

    public void borrowBook(String isbn, String memberId)
            throws BookNotFoundException, MemberNotFoundException,
                   BookNotAvailableException, BorrowLimitExceededException {

        Book book = findBook(isbn);
        Member member = findMember(memberId);

        if (!book.isAvailable()) throw new BookNotAvailableException(book.getTitle());
        if (member.getBorrowedIsbns().size() >= member.getBorrowLimit()) {
            throw new BorrowLimitExceededException(member.getName(), member.getBorrowLimit());
        }

        book.decrementAvailable();
        member.addBorrowedBook(isbn);

        BorrowRecord record = new BorrowRecord(isbn, memberId, LocalDate.now());
        activeLoans.put(record.key(), record);
        history.add(new Transaction(isbn, memberId, Transaction.Type.BORROW, LocalDate.now(), 0.0));
    }

    public double returnBook(String isbn, String memberId)
            throws BookNotFoundException, MemberNotFoundException {

        Book book = findBook(isbn);
        Member member = findMember(memberId);

        String key = isbn + "::" + memberId;
        BorrowRecord record = activeLoans.remove(key);

        double fine = 0.0;
        if (record != null) {
            long overdue = record.daysOverdue(LocalDate.now());
            fine = member.calculateFine(overdue);
        }

        book.incrementAvailable();
        member.removeBorrowedBook(isbn);
        history.add(new Transaction(isbn, memberId, Transaction.Type.RETURN, LocalDate.now(), fine));
        return fine;
    }

    public List<BorrowRecord> getOverdueLoans() {
        List<BorrowRecord> overdue = new ArrayList<>();
        for (BorrowRecord r : activeLoans.values()) {
            if (r.daysOverdue(LocalDate.now()) > 0) overdue.add(r);
        }
        return overdue;
    }

    // ---------- Reporting Module ----------

    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public Collection<BorrowRecord> getActiveLoans() {
        return activeLoans.values();
    }

    // ---------- Bulk accessors used by persistence layer ----------

    public Map<String, Book> getCatalogMap() { return catalog; }
    public Map<String, Member> getMembersMap() { return members; }
    public Map<String, BorrowRecord> getActiveLoansMap() { return activeLoans; }
    public List<Transaction> getHistoryList() { return history; }

    public void loadState(Map<String, Book> books, Map<String, Member> mem,
                           Map<String, BorrowRecord> loans, List<Transaction> hist) {
        catalog.clear(); catalog.putAll(books);
        members.clear(); members.putAll(mem);
        activeLoans.clear(); activeLoans.putAll(loans);
        history.clear(); history.addAll(hist);
    }
}
