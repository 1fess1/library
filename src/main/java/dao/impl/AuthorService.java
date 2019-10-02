package dao.impl;

import dao.AuthorDao;
import entyties.Author;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AuthorRepository;

import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Transactional
@Slf4j
@Service
public class AuthorService implements AuthorDao {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<Author> getAll() {
        log.trace("Getting all authors");
        List<Author> authors = authorRepository.findAll();
        log.info("Got all authors", getAuthorCount(authors));
        return authors;
    }

    public List<Author> getAll(Sort sort) {
        log.trace("Getting all sorted authors");
        List<Author> authors = authorRepository.findAll(sort);
        log.info("Got all sorted authors",  getAuthorCount(authors));
        return authors;

    }

    @Override
    public Page<Author> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        log.trace("Getting all sorted authors by page");
        Page<Author> authors = authorRepository.findAll(buildPageRequest(pageNumber, pageSize, sortField, sortDirection));
        log.info("Got all sorted authors",  getAuthorCount(authors.getContent()));
        return authors;
    }

    @Override
    public List<Author> search(String... searchString) {
        log.info("Searching authors");
        List<Author> authors = authorRepository.findByFioContainingIgnoreCaseOrderByFio(getSearchString(searchString));
        log.info("Found authors", getAuthorCount(authors));
        return authors;
    }

    @Override
    public Page<Author> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String... searchString) {
        log.info("Searching authors by page");
        Page<Author> authors = authorRepository.findByFioContainingIgnoreCaseOrderByFio(searchString[0], buildPageRequest(pageNumber, pageSize, sortField, sortDirection));
        log.trace("Found authors by page", getAuthorCount(authors.getContent()));
        return authors;
    }

    @Override
    public Author save(Author author) {
        log.info("Try to save author");
        Author savedAuthor = authorRepository.save(author);
        log.trace("Author was saved");
        return savedAuthor;
    }

    @Override
    public void delete(Author author) {
        log.info("Try to delete author");
        authorRepository.delete(author);
        log.trace("Author was deleted");
    }

    @Override
    public Author getById(long id) {
        log.info("Get author by id", id);
        Author author = authorRepository.findOne(id);
        log.trace("Got author by id", id);
        return author;
    }

    private Integer getAuthorCount(List<Author> authors) {
        return ofNullable(authors)
                .map(List::size)
                .orElse(0);
    }

    private String getSearchString(String[] searchString) {
        return Arrays
                .stream(searchString)
                .findFirst().
                        orElse("");
    }

    private PageRequest buildPageRequest(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        return new PageRequest(pageNumber, pageSize, new Sort(sortDirection, sortField));
    }

}
