package org.woehlke.javaee7.petclinic.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 01.01.14
 * Time: 21:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "types")
public class PetType {

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
    public String toString() {
        return "PetType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
