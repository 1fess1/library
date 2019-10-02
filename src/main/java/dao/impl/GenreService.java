package dao.impl;

import dao.GenreDao;
import entyties.Genre;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.GenreRepository;

import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Transactional
@Slf4j
@Service
public class GenreService implements GenreDao {

    private final GenreRepository genreRepository;

    static final String EMPTY_STRING = "";

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Genre> getAll() {
        log.info("Getting all genres");
        List<Genre> genres = genreRepository.findAll();
        log.info("Got all genres", getGenreCount(genres));
        return genres;
    }

    public List<Genre> getAll(Sort sort) {
        log.info("Getting all sorted genres");
        List<Genre> genres = genreRepository.findAll(sort);
        log.info("Got all sorted genres", getGenreCount(genres));
        return genres;
    }


    @Override
    public Page<Genre> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        log.info("Getting all sorted genres by page");
        Page<Genre> genres = genreRepository.findAll(buildPageRequest(pageNumber, pageSize, sortField, sortDirection));
        log.info("Got all sorted genres", getGenreCount(genres.getContent()));
        return genres;
    }


    @Override
    public List<Genre> search(String... searchString) {
        log.info("Searching genres");
        List<Genre> genres = genreRepository.findByNameContainingIgnoreCaseOrderByName(getSearchString(searchString));
        log.info("Found genres", getGenreCount(genres));
        return genres;
    }

    @Override
    public Page<Genre> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String... searchString) {
        log.info("Searching genres by page");
        Page<Genre> genres = genreRepository.findByNameContainingIgnoreCaseOrderByName(searchString[0], buildPageRequest(pageNumber, pageSize, sortField, sortDirection));
        log.info("Found genres", getGenreCount(genres.getContent()));
        return genres;
    }


    @Override
    public Genre save(Genre genre) {
        log.trace("Try to save genre");
        Genre savedGenre = genreRepository.save(genre);
        log.info("Genre was saved");
        return savedGenre;
    }

    @Override
    public void delete(Genre genre) {
        log.trace("Try to delete genre");
        genreRepository.delete(genre);
        log.trace("Genre was deleted");
    }

    @Override
    public Genre getById(long id) {
        log.trace("Get genre by id", id);
        Genre genre = genreRepository.findOne(id);
        log.trace("Got genre by id", id);
        return genre;
    }

    private PageRequest buildPageRequest(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        return new PageRequest(pageNumber, pageSize, new Sort(sortDirection, sortField));
    }

    private Integer getGenreCount(List<Genre> genres) {
        return ofNullable(genres)
                .map(List::size)
                .orElse(0);
    }

    private String getSearchString(String[] searchString) {
        return Arrays
                .stream(searchString)
                .findFirst().
                        orElse(EMPTY_STRING);
    }
}
