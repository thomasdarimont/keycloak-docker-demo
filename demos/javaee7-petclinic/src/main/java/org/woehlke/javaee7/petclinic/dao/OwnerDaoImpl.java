package org.woehlke.javaee7.petclinic.dao;


import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.woehlke.javaee7.petclinic.entities.Owner;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 06.01.14
 * Time: 09:38
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class OwnerDaoImpl implements OwnerDao {

    private static Logger log = Logger.getLogger(OwnerDaoImpl.class.getName());

    @PersistenceContext(unitName="javaee7petclinic")
    private EntityManager entityManager;

    @Override
    public List<Owner> getAll() {
        TypedQuery<Owner> q = entityManager.createQuery("select o from Owner o order by o.lastName,o.firstName", Owner.class);
        List<Owner> list =  q.getResultList();
        return list;
    }

    @Override
    public void delete(long id) {
        Owner owner = entityManager.find(Owner.class, id);
        entityManager.remove(owner);
    }

    @Override
    public void addNew(Owner owner) {
        log.info("addNewOwner: "+owner.toString());
        entityManager.persist(owner);
    }

    @Override
    public Owner findById(long id) {
        return entityManager.find(Owner.class, id);
    }

    @Override
    public void update(Owner owner) {
        log.info("updateOwner: "+owner.toString());
        owner=entityManager.merge(owner);
    }

    @Override
    public List<Owner> search(String searchterm) {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity( Owner.class ).get();
        org.apache.lucene.search.Query query = qb
                .keyword()
                .onFields("firstName", "lastName", "city", "pets.name")
                .matching(searchterm)
                .createQuery();
        // wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Owner.class);
        // execute search
        @SuppressWarnings("unchecked")
		List<Owner> result = persistenceQuery.getResultList();
        return  result;
    }

}
