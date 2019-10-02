package dao.impl;

import dao.PublisherDao;
import entyties.Publisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.PublisherRepository;

import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@Transactional
@Service
public class PublisherService implements PublisherDao {

    private final PublisherRepository publisherRepository;

    static final String EMPTY_STRING = "";

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public List<Publisher> getAll() {
        log.trace("Get all publishers");
        List<Publisher> publishers = publisherRepository.findAll();
        log.trace("Got all publishers", getPublisherCount(publishers));
        return publishers;
    }

    @Override
    public List<Publisher> getAll(Sort sort) {
        log.trace("Get all sorted publishers");
        List<Publisher> publishers = publisherRepository.findAll(sort);
        log.trace("Got all sorted publishers", getPublisherCount(publishers));
        return publishers;
    }

    @Override
    public Page<Publisher> getAll(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        log.trace("Get sorted publishers by page");
        Page<Publisher> publishers = publisherRepository.findAll(buildPageRequest(pageNumber, pageSize, sortField, sortDirection));
        log.trace("Got sorted publishers by page", getPublisherCount(publishers.getContent()));
        return publishers;
    }

    @Override
    public List<Publisher> search(String... searchString) {
        log.trace("Search publishers by name");
        List<Publisher> publishers = publisherRepository.findByNameContainingIgnoreCaseOrderByName(getSearchString(searchString));
        log.trace("Found publishers by name", getPublisherCount(publishers));
        return publishers;
    }

    @Override
    public Page<Publisher> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection, String... searchString) {
        log.trace("Search publishers by name in page");
        Page<Publisher> publishers = publisherRepository.findByNameContainingIgnoreCaseOrderByName(searchString[0], buildPageRequest(pageNumber, pageSize, sortField, sortDirection));
        return publishers;
    }

    @Override
    public Publisher save(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    @Override
    public void delete(Publisher publisher) {
        publisherRepository.delete(publisher);
    }

    @Override
    public Publisher getById(long id) {
        return publisherRepository.findOne(id);
    }

    private PageRequest buildPageRequest(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {
        return new PageRequest(pageNumber, pageSize, new Sort(sortDirection, sortField));
    }

    private Integer getPublisherCount(List<Publisher> publishers) {
        return ofNullable(publishers)
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
