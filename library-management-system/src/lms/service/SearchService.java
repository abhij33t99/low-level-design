package lms.service;

import lms.model.Book;
import lms.repository.InMemoryRepositories;

import java.time.LocalDate;
import java.util.List;

/**
 * Dedicated service for book search operations (R14).
 * Extracted from LibraryService to separate concerns.
 */
public class SearchService {
    private final InMemoryRepositories repo;

    public SearchService(InMemoryRepositories repo) {
        this.repo = repo;
    }

    public List<Book> searchByTitle(String title) { return repo.findBooksByTitle(title); }
    public List<Book> searchByAuthor(String author) { return repo.findBooksByAuthor(author); }
    public List<Book> searchBySubject(String subject) { return repo.findBooksBySubject(subject); }
    public List<Book> searchByPublicationDate(LocalDate date) { return repo.findBooksByPublicationDate(date); }
}
