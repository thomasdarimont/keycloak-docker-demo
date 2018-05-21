package org.woehlke.javaee7.petclinic.entities;

import org.hibernate.search.annotations.Field;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 01.01.14
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pets")
public class Pet implements Comparable<Pet> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Field
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "birth_date")
    @Temporal( TemporalType.DATE )
    private Date birthDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "type_id")
    private PetType type;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = FetchType.EAGER)
    private Set<Visit> visits = new HashSet<Visit>();

    public void addVisit(Visit visit) {
        visits.add(visit);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public PetType getType() {

        return type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public List<Visit> getVisits() {
        List<Visit> list = new ArrayList<>();
        for(Visit visit:visits){
            list.add(visit);
        }
        Collections.sort(list);
        return list;
    }

    public void setVisits(Set<Visit> visits) {
        this.visits = visits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet)) return false;

        Pet pet = (Pet) o;

        if (birthDate != null ? !birthDate.equals(pet.birthDate) : pet.birthDate != null) return false;
        if (id != null ? !id.equals(pet.id) : pet.id != null) return false;
        if (name != null ? !name.equals(pet.name) : pet.name != null) return false;
        if (type != null ? !type.equals(pet.type) : pet.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", type=" + type +
                ", visits=" + visits +
                '}';
    }

    @Override
    public int compareTo(Pet o) {
        return name.compareTo(o.getName());
    }
}
