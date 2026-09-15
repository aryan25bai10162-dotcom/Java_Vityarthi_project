# Problem Statement

## Problem Statement

Small and mid-sized libraries (college department libraries, school libraries,
community reading rooms) often still track book issues and returns manually
using registers, making it slow to check book availability, hard to enforce
borrowing limits, and error-prone to calculate overdue fines fairly and
consistently. There is no easy way to see, at a glance, which books are
overdue or how much fine a member currently owes.

## Scope of the Project

This project implements a **console-based Library Management System in Java**
that digitizes the core day-to-day operations of a small library. Sample data
(25 books, 13 members) is provided in `data/*.csv` for a realistic first run
instead of a handful of hardcoded records:

- Maintaining a catalog of books with multiple copies per title
- Registering and managing two categories of members (Students and Faculty)
  with different borrowing privileges
- Issuing and returning books with automatic due-date tracking
- Calculating overdue fines automatically based on member category
- Persisting all data between sessions so the librarian doesn't lose records
  when the application is closed

**Out of scope:** multi-branch library networking, online member self-service
portals, payment gateway integration for fines, and barcode/RFID scanning —
these are noted as future enhancements rather than implemented here.

## Target Users

- **Librarians / library staff** — the primary users, who operate the
  console application to manage the catalog and process transactions
- **Students** — one of two member categories, with a borrowing limit of 3
  books and a fine rate of ₹5/day overdue
- **Faculty** — the second member category, with a higher borrowing limit
  of 6 books, a 3-day grace period, and a lower fine rate of ₹2/day overdue

## High-Level Features

1. Book Management — add, list, and search the catalog
2. Member Management — register and list Students and Faculty
3. Borrow / Return workflow with real-time availability checks
4. Automatic overdue fine calculation, varying by member category
5. Overdue report and full transaction history
6. Persistent storage across application restarts
