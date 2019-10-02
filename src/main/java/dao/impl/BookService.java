package dao.impl;

import dao.BookDao;
import entyties.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.BookRepository;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Service
@Slf4j
@Transactional
public class BookService implements BookDao {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @Override
    public List<Book> getAll() {
        log.info("Get books");
        List<Book> books = bookRepository.findAll();
        log.trace("Found books", getBooksCount(books));
        return books;
    }

    @Override
    public List<Book> getAll(Sort sort) {
        log.info("Get sorted books");
        List<Book> books = bookRepository.findAll(sort);
        log.trace("Found books", getBooksCount(books));
        return books;
    }

    @Override
    public Page<Book> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        log.info("Get sorted books by page");
        Page<Book> books = bookRepository.findAllWithoutContent(buildPageRequest(pageSize, pageNumber, sortDirection, sortField));
        log.trace("Found books", getBooksCount(books.getContent()));
        return books;
    }

    @Override
    public List<Book> search(String... searchString) {
        return emptyList();
    }


    @Override
    public Page<Book> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String... searchString) {
        log.info("Search sorted books by page");
        String name = searchString[0];
        Page<Book> books = bookRepository.findByNameContainingIgnoreCaseOrAuthorFioContainingIgnoreCaseOrderByName(name, name, buildPageRequest(pageSize, pageNumber, sortDirection, sortField));
        log.trace("Found books", getBooksCount(books.getContent()));
        return books;
    }


    @Override
    public Book save(Book book) {
        log.info("Try to save book");
        bookRepository.save(book);

        if (nonNull(book.getContent())) {
            bookRepository.updateContent(book.getContent(), book.getId());
        }
        log.trace("Book was saved");
        return book;
    }

    @Override
    public void delete(Book book) {
        log.info("Try to delete book");
        bookRepository.delete(book);
        log.trace("Book was deleted");
    }

    @Override
    public Book getById(long id) {
        log.info("Get book by id", id);
        Book book = bookRepository.findOne(id);
        log.trace("Got book", id);
        return book;
    }

    @Override
    public byte[] getContentById(long id) {
        log.info("Get content by id", id);
        byte[] content = bookRepository.getContent(id);
        log.trace("Got content by id", id);
        return content;
    }

    @Override
    public List<Book> findTopBooks(int limit) {
        log.info("Find top books limit:", limit);
        List<Book> books = bookRepository.findTopBooks(
                buildPageRequest(limit, 0, Sort.Direction.DESC, "viewCount"));
        log.trace("Found top books ", getBooksCount(books));
        return books;
    }

    @Override
    public Page<Book> findByGenre(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, long genreId) {
        log.info("Find books by genre", genreId);
        Page<Book> books = bookRepository.findByGenre(genreId, buildPageRequest(pageSize, pageNumber, sortDirection, sortField));
        log.trace("Found books by genre", getBooksCount(books.getContent()));
        return books;
    }

    @Override
    public void updateViewCount(long viewCount, long id) {
        log.info("Try to update view count", id);
        bookRepository.updateViewCount(viewCount, id);
        log.trace("View count was updated", id);
    }

    @Override
    public void updateRating(long totalRating, long totalVoteCount, int avgRating, long id) {
        log.info("Try to update rating", id);
        bookRepository.updateRating(totalRating, totalVoteCount, avgRating, id);
        log.trace("Rating was updated", id);

    }

    private PageRequest buildPageRequest(int limit, int i, Sort.Direction desc, String viewCount) {
        return new PageRequest(i, limit, new Sort(desc, viewCount));
    }

    private Integer getBooksCount(List<Book> books) {
        return ofNullable(books)
                .map(List::size)
                .orElse(0);
    }

}
