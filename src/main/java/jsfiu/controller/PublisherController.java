package jsfiu.controller;

import com.google.common.base.Strings;
import dao.PublisherDao;
import entyties.Author;
import entyties.Publisher;
import jsfiu.model.LazyDataTable;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.util.List;

import static java.util.Objects.isNull;


@ManagedBean
@SessionScoped
@Component
@Getter
@Setter
public class PublisherController extends AbstractController<Publisher> {

    private int rowsCount = 20;
    private int first;
    private Page<Publisher> publisherPages;

    private final PublisherDao publisherDao;

    private final SprController sprController;

    private Publisher selectedPublisher;

    private LazyDataTable<Author> lazyModel;

    public PublisherController(PublisherDao publisherDao, SprController sprController) {
        this.publisherDao = publisherDao;
        this.sprController = sprController;
    }

    @PostConstruct
    public void init() {
        lazyModel = new LazyDataTable(this);

    }

    public void save() {
        publisherDao.save(selectedPublisher);
        RequestContext.getCurrentInstance().execute("PF('dialogPublisher').hide()");
    }

    @Override
    public Page<Publisher> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {


        if (isNull(sortField)) {
            sortField = "name";
        }

        if (Strings.isNullOrEmpty(sprController.getSearchText())) {
            publisherPages = publisherDao.getAll(pageNumber, pageSize, sortField, sortDirection);
        } else {
            publisherPages = publisherDao.search(pageNumber, pageSize, sortField, sortDirection, sprController.getSearchText());
        }

        return publisherPages;

    }

    @Override
    public void addAction() {
        selectedPublisher = new Publisher();

        showEditDialog();

    }

    @Override
    public void editAction() {
        showEditDialog();
    }

    @Override
    public void deleteAction() {
        publisherDao.delete(selectedPublisher);
    }

    private void showEditDialog() {

        RequestContext.getCurrentInstance().execute("PF('dialogPublisher').show()");
    }

    public List<Publisher> find(String name) {
        return publisherDao.search(name);
    }

}
