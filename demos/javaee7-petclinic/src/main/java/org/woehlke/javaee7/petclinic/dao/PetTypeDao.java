package org.woehlke.javaee7.petclinic.dao;

import org.woehlke.javaee7.petclinic.entities.PetType;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Fert
 * Date: 06.01.14
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public interface PetTypeDao {

    List<PetType> getAll();

    void delete(long id);

    void addNew(PetType petType);

    PetType findById(long id);

    void update(PetType petType);

}
