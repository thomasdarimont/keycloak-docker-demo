package org.woehlke.javaee7.petclinic.services;

import org.woehlke.javaee7.petclinic.dao.VetDao;
import org.woehlke.javaee7.petclinic.entities.Specialty;
import org.woehlke.javaee7.petclinic.entities.Vet;
import org.woehlke.javaee7.petclinic.model.Vets;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.atom.Link;
import org.jboss.resteasy.plugins.providers.atom.Person;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 05.01.14
 * Time: 09:27
 * To change this template use File | Settings | File Templates.
 */
@Stateless
@Path("/vets")
public class VetWebservice {

    @EJB
    private VetDao vetDao;

    @GET
    @Produces("application/xml")
    @Path("/xml")
    public Vets getXml(){
        Vets vets = new Vets();
        vets.setVetList(vetDao.getAll());
        return vets;
    }

    @GET
    @Produces("application/json")
    @Path("/json")
    public Vets getJson(){
        Vets vets = new Vets();
        vets.setVetList(vetDao.getAll());
        return vets;
    }

    @GET
    @Path("/feed")
    @Produces("application/atom+xml")
    public Feed getFeed() throws URISyntaxException
    {
        Feed feed = new Feed();
        feed.setId(new URI("http://example.com/42"));
        feed.setTitle("Veterinarians");
        feed.setUpdated(new Date());
        Link link = new Link();
        link.setHref(new URI("http://localhost"));
        link.setRel("edit");
        feed.getLinks().add(link);
        feed.getAuthors().add(new Person("Thomas Woehlke"));
        for(Vet vet:vetDao.getAll()){
            Entry entry = new Entry();
            entry.setTitle("Vet: "+vet.getFirstName()+" "+vet.getLastName());
            Content content = new Content();
            content.setType(MediaType.TEXT_HTML_TYPE);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            Iterator<Specialty> i = vet.getSpecialties().iterator();
            while(i.hasNext() ){
                stringBuilder.append(i.next().getName());
                if(i.hasNext()){
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("]");
            content.setText(stringBuilder.toString());
            entry.setContent(content);
            feed.getEntries().add(entry);
        }
        return feed;
    }

}
