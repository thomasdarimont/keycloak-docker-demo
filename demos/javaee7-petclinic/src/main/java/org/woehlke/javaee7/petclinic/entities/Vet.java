package org.woehlke.javaee7.petclinic.entities;


import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.xml.bind.annotation.XmlElement;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 01.01.14
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "vets")
@Indexed
public class Vet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "first_name")
    @NotEmpty
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    @Field(index= Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vet_specialties",
            joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id"))
    private java.util.Set<Specialty> specialties;

    protected Set<Specialty> getSpecialtiesInternal() {
        if (this.specialties == null) {
            this.specialties = new HashSet<Specialty>();
        }
        return this.specialties;
    }

    @XmlElement
    public List<Specialty> getSpecialties() {
        List<Specialty> list = new ArrayList<Specialty>();
        for(Specialty s : getSpecialtiesInternal()){
            list.add(s);
        }
        Collections.sort(list);
        return list;
    }

    public String getSpecialtiesAsString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(getNrOfSpecialties()==0){
            stringBuilder.append("none");
        } else {
            for(Specialty specialty : getSpecialties()){
                stringBuilder.append(specialty.getName());
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }

    public int getNrOfSpecialties() {
        return getSpecialtiesInternal().size();
    }

    public void addSpecialty(Specialty specialty) {
        getSpecialtiesInternal().add(specialty);
    }

    public void removeSpecialties(){
        this.specialties = new HashSet<Specialty>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }

    @Override
    public String toString() {
        return "Vet{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialties=" + specialties +
                '}';
    }
}
