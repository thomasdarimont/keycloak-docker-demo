package org.woehlke.javaee7.petclinic.dao;

import org.woehlke.javaee7.petclinic.entities.Visit;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 07.01.14
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class VisitDaoImpl implements VisitDao {

    private static Logger log = Logger.getLogger(VisitDaoImpl.class.getName());

    @PersistenceContext(unitName="javaee7petclinic")
    private EntityManager entityManager;

    @Override
    public void addNew(Visit visit) {
        log.info("addNewVisit: "+visit.toString());
        entityManager.persist(visit);
    }

    @Override
    public void update(Visit visit) {
        visit= entityManager.merge(visit);
    }
}
