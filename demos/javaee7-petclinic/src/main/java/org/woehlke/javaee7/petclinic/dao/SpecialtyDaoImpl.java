package org.woehlke.javaee7.petclinic.dao;

import org.woehlke.javaee7.petclinic.entities.Specialty;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 04.01.14
 * Time: 12:03
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SpecialtyDaoImpl implements SpecialtyDao {

    private static Logger log = Logger.getLogger(SpecialtyDaoImpl.class.getName());

    @PersistenceContext(unitName="javaee7petclinic")
    private EntityManager entityManager;

    @Override
    public List<Specialty> getAll() {
        TypedQuery<Specialty> q = entityManager.createQuery("select s from Specialty s order by s.name",Specialty.class);
        List<Specialty> list =  q.getResultList();
        return list;
    }

    @Override
    public void delete(long id) {
        Specialty specialty = entityManager.find(Specialty.class, id);
        entityManager.remove(specialty);
    }

    @Override
    public void addNew(Specialty specialty) {
        log.info("addNewSpecialty: "+specialty.toString());
        entityManager.persist(specialty);
    }

    @Override
    public Specialty findById(long id) {
        Specialty specialty = entityManager.find(Specialty.class, id);
        return specialty;
    }

    @Override
    public void update(Specialty specialty) {
        entityManager.merge(specialty);
    }
}
