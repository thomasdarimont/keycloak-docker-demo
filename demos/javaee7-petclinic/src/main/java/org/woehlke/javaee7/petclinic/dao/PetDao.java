package org.woehlke.javaee7.petclinic.dao;

import org.woehlke.javaee7.petclinic.entities.Pet;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 06.01.14
 * Time: 20:34
 * To change this template use File | Settings | File Templates.
 */
public interface PetDao {

    void addNew(Pet pet);

    Pet findById(long petId);

    void update(Pet pet);
}
