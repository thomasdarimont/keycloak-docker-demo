package org.woehlke.javaee7.petclinic.web;

import org.woehlke.javaee7.petclinic.dao.SpecialtyDao;
import org.woehlke.javaee7.petclinic.dao.VetDao;
import org.woehlke.javaee7.petclinic.entities.Specialty;
import org.woehlke.javaee7.petclinic.entities.Vet;


import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 01.01.14
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@SessionScoped
public class VetController implements Serializable {

    @EJB
    private VetDao vetDao;

    @EJB
    private SpecialtyDao specialtyDao;

    private Vet vet;

    private String searchterm;

    private List<Vet> vets;

    private List<Specialty> specialties;
    private List<Specialty> selectedSpecialties;

    private int scrollerPage = 1;

    public List<Specialty> getSpecialties() {
        specialties = specialtyDao.getAll();
        return specialties;
    }

    public void setSpecialties(List<Specialty> specialties) {
        this.specialties = specialties;
    }

    public List<Specialty> getSelectedSpecialties() {
        return selectedSpecialties;
    }

    public void setSelectedSpecialties(List<Specialty> selectedSpecialties) {
        this.selectedSpecialties = selectedSpecialties;
    }

    public Vet getVet() {
        return vet;
    }

    public void setVet(Vet vet) {
        this.vet = vet;
    }

    public String getNewVetForm() {
        this.vet = new Vet();
        this.specialties = specialtyDao.getAll();
        this.selectedSpecialties = new ArrayList<Specialty>();
        return "newVet.jsf";
    }

    public String saveNewVet() {
        for (Specialty specialty : selectedSpecialties) {
            this.vet.addSpecialty(specialty);
        }
        vetDao.addNew(this.vet);
        this.vets = vetDao.getAll();
        return "vets.jsf";
    }

    public List<Vet> getVets() {
        if (this.vets == null) {
            this.vets = vetDao.getAll();
        }
        return this.vets;
    }

    public String getEditForm(long id) {
        this.vet = vetDao.findById(id);
        selectedSpecialties = vet.getSpecialties();
        return "editVet.jsf";
    }

    public String saveEditedVet() {
        this.vet.removeSpecialties();
        for (Specialty specialty : selectedSpecialties) {
            this.vet.addSpecialty(specialty);
        }
        vetDao.update(this.vet);
        this.vets = vetDao.getAll();
        return "vets.jsf";
    }

    public String deleteVet(long id) {
        this.vet = vetDao.findById(id);
        this.vet.removeSpecialties();
        vetDao.update(this.vet);
        vetDao.delete(id);
        this.vets = vetDao.getAll();
        return "vets.jsf";
    }

    public String getSearchterm() {
        return searchterm;
    }

    public void setSearchterm(String searchterm) {
        this.searchterm = searchterm;
    }

    public String search() {
        if (searchterm == null || searchterm.isEmpty()) {
            this.vets = vetDao.getAll();
        } else {
            try {
                this.vets = vetDao.search(searchterm);
            } catch (Exception e) {
                this.vets = vetDao.getAll();
            }
        }
        return "vets.jsf";
    }

    public int getScrollerPage() {
        return scrollerPage;
    }

    public void setScrollerPage(int scrollerPage) {
        this.scrollerPage = scrollerPage;
    }
}
