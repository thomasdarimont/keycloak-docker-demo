package org.woehlke.javaee7.petclinic.model;

import org.woehlke.javaee7.petclinic.entities.Vet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 01.01.14
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
public class Vets {

    private List<Vet> vetList;

    @XmlElement
    public List<Vet> getVetList() {
        if (vetList == null) {
            vetList = new ArrayList<Vet>();
        }
        return vetList;
    }

    public void setVetList(List<Vet> vetList) {
        this.vetList = vetList;
    }
}
