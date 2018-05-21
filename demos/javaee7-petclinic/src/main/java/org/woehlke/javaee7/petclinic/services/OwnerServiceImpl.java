package org.woehlke.javaee7.petclinic.services;

import org.woehlke.javaee7.petclinic.dao.OwnerDao;
import org.woehlke.javaee7.petclinic.dao.PetDao;
import org.woehlke.javaee7.petclinic.dao.VisitDao;
import org.woehlke.javaee7.petclinic.entities.Visit;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Created by tw on 10.03.14.
 */
@Stateless
public class OwnerServiceImpl implements OwnerService {

    @EJB
    private OwnerDao ownerDao;

    @EJB
    private PetDao petDao;

    @EJB
    private VisitDao visitDao;


    @Override
    public void addNewVisit(Visit visit) {
        visitDao.addNew(visit);
        petDao.update(visit.getPet());
        ownerDao.update(visit.getPet().getOwner());
    }
}
