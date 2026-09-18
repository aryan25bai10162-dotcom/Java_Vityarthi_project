# Library Management System (Java)

A console-based Library Management System built in core Java, demonstrating
Object-Oriented Programming, the Collections Framework, custom exception handling,
and file-based data persistence.

## Overview

The system lets a librarian manage a book catalog, register members (Students and
Faculty, each with different borrowing rules), and process book borrow/return
transactions with automatic overdue fine calculation. All data is saved to disk
between runs.

## Dataset

Sample data lives in `data/books.csv` (25 books across 7 genres) and `data/members.csv`
(13 members — 8 Students, 5 Faculty). On first run, `Main` automatically imports both
files via `DatasetLoader`; you can also trigger a re-import anytime from the menu
(option 10). This models the "Data input & processing" functional requirement with a
real bulk dataset rather than one book typed in at a time.

If the CSV files are missing, the app falls back to two hardcoded sample records so it
still runs out of the box.

## Features

- **Book Management** — add books, list catalog (alphabetically sorted), search by title
- **Member Management** — register Students and Faculty, each with distinct borrow
  limits and fine policies (polymorphism via the `Fineable` interface)
- **Bulk Dataset Import** — load a full catalog and member list from CSV in one step
- **Borrow / Return / Fines** — issue and return books, with automatic overdue fine
  calculation based on member category and a 14-day loan period
- **Overdue Report** — lists all currently overdue loans
- **Transaction History** — full chronological log of every borrow/return event
- **Persistence** — entire library state is serialized to `data/library_data.ser`
  on exit and reloaded automatically on the next run
- **Robust error handling** — custom checked exceptions for every failure case
  (book not found, member not found, no copies available, borrow limit exceeded)

## Architecture

```
src/
├── Main.java                # Console menu / application entry point
├── core/
│   ├── Library.java          # Core business logic + Collections-based storage
│   ├── FileManager.java      # Serialization-based persistence
│   └── DatasetLoader.java    # CSV bulk import (books.csv, members.csv)
├── models/
│   ├── Book.java
│   ├── Member.java           # abstract base class
│   ├── Student.java          # extends Member
│   ├── Faculty.java          # extends Member
│   ├── Fineable.java         # interface implemented by Member
│   ├── BorrowRecord.java     # tracks active loan + due date
│   └── Transaction.java      # immutable history entry
└── exceptions/
    ├── BookNotFoundException.java
    ├── MemberNotFoundException.java
    ├── BookNotAvailableException.java
    └── BorrowLimitExceededException.java
```

### Design notes (Collections choices)

| Collection | Used for | Why |
|---|---|---|
| `HashMap<String, Book>` | Catalog, keyed by ISBN | O(1) average lookup by ISBN |
| `HashMap<String, Member>` | Members, keyed by member ID | O(1) average lookup by ID |
| `LinkedHashMap<String, BorrowRecord>` | Active loans | Preserves issue order for reports |
| `TreeMap<String, Book>` | Sorted catalog view | Automatic alphabetical ordering by title |
| `ArrayList<Transaction>` | Full transaction history | Ordered, append-only log |

## Technologies / Tools Used

- Java 17+ (no external libraries — core Java only)
- `java.io.Serializable` for persistence
- `java.time.LocalDate` for due-date/fine calculations
- Git for version control

## Testing

Two test suites are provided under `test/`:

1. **`FineCalculationTest.java` / `BorrowLimitTest.java`** — real JUnit 5 tests
   (23 test cases covering fine calculation for both member categories, borrow-limit
   enforcement, and all four custom exceptions). Run with Maven:
   ```
   mvn test
   ```
   (requires internet access to resolve `junit-jupiter` from Maven Central on first run.)

2. **`SelfTestRunner.java`** — a dependency-free runner covering the same cases,
   for quick verification without setting up Maven/JUnit:
   ```
   javac -d bin src/Main.java src/models/*.java src/exceptions/*.java src/core/*.java test/SelfTestRunner.java
   java -cp bin SelfTestRunner
   ```
   This was run during development and passed **23/23**.

## How to Install & Run

1. Clone the repository:
   ```
   git clone <your-repo-url>
   cd LibraryManagementSystem
   ```
2. Compile:
   ```
   javac -d bin src/Main.java src/models/*.java src/exceptions/*.java src/core/*.java
   ```
3. Run:
   ```
   java -cp bin Main
   ```
4. On first run, sample books and members are seeded automatically. All data
   is saved to `data/library_data.ser` when you choose "Save & Exit" (option 0).

## Testing Instructions

Manual test flow to verify core functionality:
1. Choose option 2 to confirm the two seeded sample books appear.
2. Choose option 6, borrow ISBN001 as member S001 — confirm success.
3. Try borrowing the same ISBN as S001 again if copies run out — confirm
   `BookNotAvailableException` message appears cleanly (no crash).
4. Choose option 7 to return the book — confirm no fine (returned same day).
5. Choose option 8 — confirm overdue report is empty when nothing is overdue.
6. Exit with option 0, restart the app, and choose option 2 again to confirm
   the catalog state was persisted correctly.



- Migrate persistence to a database (JDBC + MySQL) instead of file serialization
- Add a book reservation queue for high-demand titles
- Add a simple JavaFX GUI on top of the existing `Library` core logic
