package jsfiu.controller;

import jsfiu.enums.SearchType;
import jsfiu.model.LazyDataTable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RateEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import dao.BookDao;
import dao.GenreDao;
import entyties.Book;
import jsfiu.enums.SearchType;
import jsfiu.model.LazyDataTable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@ManagedBean
@SessionScoped
@Component
@Getter
@Setter
@Log
public class BookController extends AbstractController<Book> {

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int TOP_BOOKS_LIMIT = 5;
    private int rowsCount = DEFAULT_PAGE_SIZE;

    private SearchType searchType;

    private final BookDao bookDao;

    private final GenreDao genreDao;

    private final GenreController genreController;

    private Book selectedBook;

    private LazyDataTable<Book> lazyModel;
    private byte[] uploadedImage;
    private byte[] uploadedContent;

    private Page<Book> bookPages;
    private List<Book> topBooks;

    private String searchText;
    private long selectedGenreId;

    public BookController(BookDao bookDao, GenreDao genreDao, GenreController genreController) {
        this.bookDao = bookDao;
        this.genreDao = genreDao;
        this.genreController = genreController;
    }

    @PostConstruct
    public void init() {
        lazyModel = new LazyDataTable(this);
    }

    public void save() {

        if (uploadedImage != null) {
            selectedBook.setImage(uploadedImage);
        }

        if (uploadedContent != null) {
            selectedBook.setContent(uploadedContent);
        }

        bookDao.save(selectedBook);
        RequestContext.getCurrentInstance().execute("PF('dialogEditBook').hide()");

    }

    private byte[] loadDefaultIcon(){
        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/images/no-cover.jpg");
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public Page<Book> search(int pageNumber, int pageSize, String sortField, Sort.Direction sortDirection) {

        if (sortField == null) {
            sortField = "name";
        }

        if (searchType == null){
            bookPages = bookDao.getAll(pageNumber, pageSize, sortField, sortDirection);
        } else {

            switch (searchType) {
                case SEARCH_GENRE:
                    bookPages = bookDao.findByGenre(pageNumber, pageSize, sortField, sortDirection, selectedGenreId);
                    break;
                case SEARCH_TEXT:
                    bookPages = bookDao.search(pageNumber, pageSize, sortField, sortDirection, searchText);
                    break;
                case ALL:
                    bookPages = bookDao.getAll(pageNumber, pageSize, sortField, sortDirection);
                    break;

            }
        }

        return bookPages;
    }

    @Override
    public void addAction() {
        selectedBook = new Book();
        uploadedImage = loadDefaultIcon();
        uploadedContent = null;

        RequestContext.getCurrentInstance().execute("PF('dialogEditBook').show()");
    }

    public void onCloseDialog(CloseEvent event) {
        uploadedContent = null;
    }

    @Override
    public void editAction() {
        uploadedImage = selectedBook.getImage();

        RequestContext.getCurrentInstance().execute("PF('dialogEditBook').show()");
    }

    @Override
    public void deleteAction() {
        bookDao.delete(selectedBook);
    }

    public String getSearchMessage(){

        ResourceBundle bundle = ResourceBundle.getBundle("library", FacesContext.getCurrentInstance().getViewRoot().getLocale());

        String message=null;

        if (searchType==null){
            return null;
        }
        switch (searchType) {
            case SEARCH_GENRE:
                message = bundle.getString("genre")+ ": '"+genreDao.getById(selectedGenreId)+"'";
                break;
            case SEARCH_TEXT:

                if (searchText==null || searchText.trim().length()==0){
                    return null;
                }

                message = bundle.getString("search")+ ": '"+searchText+"'";
                break;
        }

        return message;
    }


    public byte[] getContent(long id) {

        byte[] content;

        if (uploadedContent != null) {
            content = uploadedContent;
        } else {

            content = bookDao.getContentById(id);

        }

        return content;
    }

    public void uploadImage(FileUploadEvent event) {
        if (event.getFile() != null) {
            uploadedImage = event.getFile().getContents();
        }
    }

    public void uploadContent(FileUploadEvent event) {
        if (event.getFile() != null) {
            uploadedContent = event.getFile().getContents();
        }
    }

    public List<Book> getTopBooks() {
        topBooks = bookDao.findTopBooks(TOP_BOOKS_LIMIT);
        return topBooks;
    }

    public int calcAverageRating(long totalRating, long totalVoteCount) {
        if (totalRating == 0 || totalVoteCount == 0) {
            return 0;
        }

        int avgRating = Long.valueOf(totalRating / totalVoteCount).intValue();

        return avgRating;
    }

    public void showBooksByGenre(long genreId){
        searchType = SearchType.SEARCH_GENRE;
        this.selectedGenreId = genreId;
    }

    public void showAll(){
        searchType = SearchType.ALL;
    }

    public void searchAction(){
        searchType = SearchType.SEARCH_TEXT;
    }

    public Page<Book> getBookPages(){
        return bookPages;
    }

    public void updateViewCount(long viewCount, long id){
        bookDao.updateViewCount(viewCount+1, id);
    }

    public void onRate(RateEvent rateEvent) {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        int bookIndex = Integer.parseInt(params.get("bookIndex"));

        Book book = bookPages.getContent().get(bookIndex);

        long currentRating = Long.parseLong(rateEvent.getRating().toString());

        long newRating = book.getTotalRating() + currentRating;

        long newVoteCount = book.getTotalVoteCount()+1;

        int newAvgRating = calcAverageRating(newRating, newVoteCount);

        bookDao.updateRating(newRating, newVoteCount, newAvgRating, book.getId());

    }

}
