package org.woehlke.javaee7.petclinic.entities;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.validation.constraints.Digits;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 01.01.14
 * Time: 21:08
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "owners")
@Indexed
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "first_name")
    @NotEmpty
    @Field(index= Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
    private String lastName;

    @Column(name = "address")
    @NotEmpty
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
    private String address;

    @Column(name = "city")
    @NotEmpty
    @Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
    private String city;

    @Column(name = "telephone")
    @NotEmpty
    @Digits(fraction = 0, integer = 10)
    private String telephone;

    @IndexedEmbedded
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner",fetch = FetchType.EAGER)
    private Set<Pet> pets = new TreeSet<Pet>();

    public void addPet(Pet pet){
        pets.add(pet);
        pet.setOwner(this);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<Pet> getPets() {
        List<Pet> list = new ArrayList<>();
        for(Pet pet:pets){
            list.add(pet);
        }
        Collections.sort(list);
        return list;
    }

    public void setPets(Set<Pet> pets) {
        this.pets = pets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner)) return false;

        Owner owner = (Owner) o;

        if (address != null ? !address.equals(owner.address) : owner.address != null) return false;
        if (city != null ? !city.equals(owner.city) : owner.city != null) return false;
        if (firstName != null ? !firstName.equals(owner.firstName) : owner.firstName != null) return false;
        if (id != null ? !id.equals(owner.id) : owner.id != null) return false;
        if (lastName != null ? !lastName.equals(owner.lastName) : owner.lastName != null) return false;
        if (pets != null ? !pets.equals(owner.pets) : owner.pets != null) return false;
        if (telephone != null ? !telephone.equals(owner.telephone) : owner.telephone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (pets != null ? pets.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
                ", pets=" + pets +
                '}';
    }
}
