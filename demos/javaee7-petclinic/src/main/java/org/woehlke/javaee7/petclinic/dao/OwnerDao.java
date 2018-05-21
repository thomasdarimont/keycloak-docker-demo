package org.woehlke.javaee7.petclinic.dao;

import org.woehlke.javaee7.petclinic.entities.Owner;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 06.01.14
 * Time: 09:38
 * To change this template use File | Settings | File Templates.
 */
public interface OwnerDao {

    List<Owner> getAll();

    void delete(long id);

    void addNew(Owner owner);

    Owner findById(long id);

    void update(Owner owner);

    List<Owner> search(String searchterm);

}
