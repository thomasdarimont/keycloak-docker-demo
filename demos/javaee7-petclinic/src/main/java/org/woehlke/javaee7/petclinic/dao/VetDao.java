package org.woehlke.javaee7.petclinic.dao;

import org.woehlke.javaee7.petclinic.entities.Vet;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 02.01.14
 * Time: 08:37
 * To change this template use File | Settings | File Templates.
 */
public interface VetDao {

    List<Vet> getAll();

    void delete(long id);

    void addNew(Vet vet);

    Vet findById(long id);

    void update(Vet vet);

    List<Vet> search(String searchterm);
}
