package jsfiu.converter;

import com.google.common.base.Strings;
import dao.PublisherDao;
import entyties.Publisher;
import org.springframework.stereotype.Component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter(forClass = Publisher.class)
@Component
public class PublisherConverter implements Converter {

    private final PublisherDao publisherDao;

    public PublisherConverter(PublisherDao publisherDao) {
        this.publisherDao = publisherDao;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (Strings.isNullOrEmpty(value)) {

            return null;
        }
        return publisherDao.getById(Integer.parseInt(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        return ((Publisher)value).getId().toString();

    }

}
