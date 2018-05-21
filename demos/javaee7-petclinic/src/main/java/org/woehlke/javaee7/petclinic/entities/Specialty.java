package org.woehlke.javaee7.petclinic.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 01.01.14
 * Time: 21:11
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "specialties")
public class Specialty implements Comparable<Specialty> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(name = "name")
    private String name;

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

    @Override
	public int compareTo(Specialty other) {
		return this.name.compareTo(other.getName());
	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Specialty)) return false;

        Specialty specialty = (Specialty) o;

        if (id != null ? !id.equals(specialty.id) : specialty.id != null) return false;
        if (name != null ? !name.equals(specialty.name) : specialty.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Specialty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

	
}
