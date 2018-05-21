package org.woehlke.javaee7.petclinic.dao;

import org.woehlke.javaee7.petclinic.entities.PetType;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Fert
 * Date: 06.01.14
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class PetTypeDaoImpl implements PetTypeDao {

    private static Logger log = Logger.getLogger(PetTypeDaoImpl.class.getName());

    @PersistenceContext(unitName="javaee7petclinic")
    private EntityManager entityManager;


    @Override
    public List<PetType> getAll() {
        TypedQuery<PetType> q = entityManager.createQuery("select pt from PetType pt order by pt.name",PetType.class);
        List<PetType> list =  q.getResultList();
        return list;
    }

    @Override
    public void delete(long id) {
        PetType petType = entityManager.find(PetType.class, id);
        entityManager.remove(petType);
    }

    @Override
    public void addNew(PetType petType) {
        log.info("addNewPetType: "+petType.toString());
        entityManager.persist(petType);
    }

    @Override
    public PetType findById(long id) {
        PetType petType = entityManager.find(PetType.class, id);
        return petType;
    }

    @Override
    public void update(PetType petType) {
        entityManager.merge(petType);
    }
}
