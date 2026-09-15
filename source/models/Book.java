package models;

import java.io.Serializable;

/**
 * Represents a book title in the library catalog.
 * totalCopies vs availableCopies allows multiple physical copies of one title.
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void decrementAvailable() {
        if (availableCopies > 0) availableCopies--;
    }

    public void incrementAvailable() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-30s | %-18s | %-12s | %d/%d available",
                isbn, title, author, genre, availableCopies, totalCopies);
    }
}
