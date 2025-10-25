package lms.service;

import lms.model.BookItem;
import lms.repository.InMemoryRepositories;

import java.util.Optional;

/**
 * Dedicated service for reservation operations (R9, R10, R13).
 * Extracted from LibraryService to separate concerns.
 */
public class ReservationService {
    private final InMemoryRepositories repo;

    public ReservationService(InMemoryRepositories repo) {
        this.repo = repo;
    }

    private BookItem requireBookItem(String bookItemId) {
        Optional<BookItem> opt = repo.getBookItem(bookItemId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Book item not found: " + bookItemId);
        return opt.get();
    }
}
