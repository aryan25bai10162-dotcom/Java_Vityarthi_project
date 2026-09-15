package core;

import models.*;

import java.io.*;
import java.util.*;

/**
 * Persists library state between runs using Java object serialization.
 * Keeps the four core collections in one bundled file so a single
 * read/write covers the whole application state.
 */
public class FileManager {

    private static final String DATA_FILE = "data/library_data.ser";

    private static class StateBundle implements Serializable {
        private static final long serialVersionUID = 1L;
        Map<String, Book> books;
        Map<String, Member> members;
        Map<String, BorrowRecord> loans;
        List<Transaction> history;
    }

    public void save(Library library) {
        StateBundle bundle = new StateBundle();
        bundle.books = library.getCatalogMap();
        bundle.members = library.getMembersMap();
        bundle.loans = library.getActiveLoansMap();
        bundle.history = library.getHistoryList();

        File file = new File(DATA_FILE);
        file.getParentFile().mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(bundle);
        } catch (IOException e) {
            System.out.println("Warning: could not save data (" + e.getMessage() + ")");
        }
    }

    @SuppressWarnings("unchecked")
    public void load(Library library) {
        File file = new File(DATA_FILE);
        if (!file.exists()) return; // first run, nothing to load

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            StateBundle bundle = (StateBundle) ois.readObject();
            library.loadState(bundle.books, bundle.members, bundle.loans, bundle.history);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Warning: could not load saved data (" + e.getMessage() + ")");
        }
    }
}
