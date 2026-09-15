import core.Library;
import core.FileManager;
import core.DatasetLoader;
import exceptions.*;
import models.*;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point: a menu-driven console application demonstrating
 * Book Management, Member Management, and Borrow/Return + Fine modules.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final Library library = new Library();
    private static final FileManager fileManager = new FileManager();
    private static final DatasetLoader datasetLoader = new DatasetLoader();

    public static void main(String[] args) {
        fileManager.load(library);
        seedFromDatasetIfEmpty();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": addBookFlow(); break;
                    case "2": listBooksFlow(); break;
                    case "3": searchBookFlow(); break;
                    case "4": addMemberFlow(); break;
                    case "5": listMembersFlow(); break;
                    case "6": borrowFlow(); break;
                    case "7": returnFlow(); break;
                    case "8": overdueReportFlow(); break;
                    case "9": historyFlow(); break;
                    case "10": importDatasetFlow(); break;
                    case "0":
                        fileManager.save(library);
                        System.out.println("Data saved. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } catch (Exception e) {
                // Centralized error handling: never let a bad input crash the app.
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Book");
        System.out.println("2. List All Books");
        System.out.println("3. Search Book by Title");
        System.out.println("4. Add Member");
        System.out.println("5. List All Members");
        System.out.println("6. Borrow Book");
        System.out.println("7. Return Book");
        System.out.println("8. Overdue Report");
        System.out.println("9. Transaction History");
        System.out.println("10. Import Dataset (data/books.csv, data/members.csv)");
        System.out.println("0. Save & Exit");
        System.out.print("Choose an option: ");
    }

    private static void importDatasetFlow() {
        datasetLoader.loadAll(library);
    }

    private static void addBookFlow() {
        System.out.print("ISBN: ");
        String isbn = sc.nextLine().trim();
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Author: ");
        String author = sc.nextLine().trim();
        System.out.print("Genre: ");
        String genre = sc.nextLine().trim();
        int copies = readInt("Number of copies: ");

        library.addBook(new Book(isbn, title, author, genre, copies));
        System.out.println("Book added successfully.");
    }

    private static void listBooksFlow() {
        Collection<Book> books = library.getAllBooksSortedByTitle();
        if (books.isEmpty()) { System.out.println("No books in catalog."); return; }
        books.forEach(System.out::println);
    }

    private static void searchBookFlow() {
        System.out.print("Enter keyword: ");
        String keyword = sc.nextLine().trim();
        List<Book> results = library.searchByTitle(keyword);
        if (results.isEmpty()) System.out.println("No matches found.");
        else results.forEach(System.out::println);
    }

    private static void addMemberFlow() {
        System.out.print("Member ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Category (1=Student, 2=Faculty): ");
        String cat = sc.nextLine().trim();

        Member member = cat.equals("2") ? new Faculty(id, name, email) : new Student(id, name, email);
        library.addMember(member);
        System.out.println(member.getCategory() + " added successfully.");
    }

    private static void listMembersFlow() {
        Collection<Member> members = library.getAllMembers();
        if (members.isEmpty()) { System.out.println("No members registered."); return; }
        members.forEach(System.out::println);
    }

    private static void borrowFlow() throws BookNotFoundException, MemberNotFoundException,
            BookNotAvailableException, BorrowLimitExceededException {
        System.out.print("Member ID: ");
        String memberId = sc.nextLine().trim();
        System.out.print("Book ISBN: ");
        String isbn = sc.nextLine().trim();
        library.borrowBook(isbn, memberId);
        System.out.println("Book issued successfully.");
    }

    private static void returnFlow() throws BookNotFoundException, MemberNotFoundException {
        System.out.print("Member ID: ");
        String memberId = sc.nextLine().trim();
        System.out.print("Book ISBN: ");
        String isbn = sc.nextLine().trim();
        double fine = library.returnBook(isbn, memberId);
        if (fine > 0) System.out.printf("Book returned. Fine due: %.2f%n", fine);
        else System.out.println("Book returned on time. No fine.");
    }

    private static void overdueReportFlow() {
        List<BorrowRecord> overdue = library.getOverdueLoans();
        if (overdue.isEmpty()) { System.out.println("No overdue books."); return; }
        for (BorrowRecord r : overdue) {
            System.out.printf("ISBN:%s | Member:%s | Due:%s | Days overdue:%d%n",
                    r.getIsbn(), r.getMemberId(), r.getDueDate(), r.daysOverdue(java.time.LocalDate.now()));
        }
    }

    private static void historyFlow() {
        List<Transaction> history = library.getHistory();
        if (history.isEmpty()) { System.out.println("No transactions yet."); return; }
        history.forEach(System.out::println);
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /** Seeds the library from the CSV dataset on first run so the app isn't empty. */
    private static void seedFromDatasetIfEmpty() {
        if (library.getAllMembers().isEmpty() && library.getAllBooksSortedByTitle().isEmpty()) {
            int books = datasetLoader.loadBooks("data/books.csv", library);
            int members = datasetLoader.loadMembers("data/members.csv", library);
            if (books == 0 && members == 0) {
                // Fallback if the dataset files are missing, so the app still has something to show.
                library.addBook(new Book("ISBN001", "Effective Java", "Joshua Bloch", "Programming", 2));
                library.addBook(new Book("ISBN002", "Clean Code", "Robert C. Martin", "Programming", 1));
                library.addMember(new Student("S001", "Aditi Sharma", "aditi@example.com"));
                library.addMember(new Faculty("F001", "Dr. R. Verma", "rverma@example.com"));
            } else {
                System.out.println("Loaded " + books + " books and " + members + " members from dataset.");
            }
        }
    }
}
