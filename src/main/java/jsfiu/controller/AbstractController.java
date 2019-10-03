package jsfiu.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

public abstract class AbstractController<T> implements Serializable {

    public abstract Page<T> search(int first, int count, String sortField, Sort.Direction sortDirection);

    public abstract void addAction();
    public abstract void editAction();
    public abstract void deleteAction();

}
