package org.woehlke.javaee7.petclinic.dao;

import org.woehlke.javaee7.petclinic.entities.Visit;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 07.01.14
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public interface VisitDao {

    void addNew(Visit visit);

    void update(Visit visit);
}
