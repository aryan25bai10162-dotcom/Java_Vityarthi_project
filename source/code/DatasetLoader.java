package core;

import models.Book;
import models.Faculty;
import models.Member;
import models.Student;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads bulk sample data from CSV files (data/books.csv, data/members.csv) into the Library.
 * This models the "Data input & processing" functional requirement: importing a real dataset
 * instead of hand-typing every book/member through the console one at a time.
 *
 * Expected formats:
 *   books.csv   -> isbn,title,author,genre,copies
 *   members.csv -> memberId,name,email,category   (category = STUDENT or FACULTY)
 */
public class DatasetLoader {

    public int loadBooks(String path, Library library) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] f = splitCsvLine(line);
                if (f.length < 5) continue;
                try {
                    Book book = new Book(f[0].trim(), f[1].trim(), f[2].trim(), f[3].trim(),
                            Integer.parseInt(f[4].trim()));
                    library.addBook(book);
                    count++;
                } catch (NumberFormatException nfe) {
                    System.out.println("Skipping malformed book row: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read books dataset at " + path + ": " + e.getMessage());
        }
        return count;
    }

    public int loadMembers(String path, Library library) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] f = splitCsvLine(line);
                if (f.length < 4) continue;
                String id = f[0].trim(), name = f[1].trim(), email = f[2].trim(), category = f[3].trim();
                Member member = category.equalsIgnoreCase("FACULTY")
                        ? new Faculty(id, name, email)
                        : new Student(id, name, email);
                library.addMember(member);
                count++;
            }
        } catch (IOException e) {
            System.out.println("Could not read members dataset at " + path + ": " + e.getMessage());
        }
        return count;
    }

    /** Minimal CSV split (no quoted-comma support needed for this dataset's plain fields). */
    private String[] splitCsvLine(String line) {
        return line.split(",", -1);
    }

    /** Convenience: load both datasets from the standard data/ folder locations. */
    public void loadAll(Library library) {
        int books = loadBooks("data/books.csv", library);
        int members = loadMembers("data/members.csv", library);
        if (books > 0 || members > 0) {
            System.out.println("Imported " + books + " books and " + members + " members from dataset.");
        }
    }
}
