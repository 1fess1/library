package jsfiu.converter;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;
import dao.AuthorDao;
import entyties.Author;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(forClass = Author.class)
@Component
public class AuthorConverter implements Converter {

    private final AuthorDao authorDao;

    public AuthorConverter(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        return authorDao.getById(Integer.parseInt(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        return ((Author)value).getId().toString();
    }

}
