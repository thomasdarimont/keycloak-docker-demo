package org.woehlke.javaee7.petclinic.web;


import org.richfaces.component.SortOrder;
import org.woehlke.javaee7.petclinic.dao.SpecialtyDao;
import org.woehlke.javaee7.petclinic.entities.Specialty;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 04.01.14
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@SessionScoped
public class SpecialtyController implements Serializable {

    @EJB
    private SpecialtyDao specialtyDao;

    private Specialty specialty;

    private SortOrder specialtySortOrder=SortOrder.ascending;

    @ManagedProperty(value = "#{language}")
    private LanguageBean languageBean;

    private int scrollerPage;

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public LanguageBean getLanguageBean() {
        return languageBean;
    }

    public void setLanguageBean(LanguageBean languageBean) {
        this.languageBean = languageBean;
    }

    public List<Specialty> getSpecialties(){
        return specialtyDao.getAll();
    }

    public String getNewSpecialtyForm(){
        specialty = new Specialty();
        return "newSpecialty.jsf";
    }

    public String saveNewSpecialty(){
        specialtyDao.addNew(this.specialty);
        return "specialties.jsf";
    }

    public String getEditForm(long id){
        this.specialty = specialtyDao.findById(id);
        return "editSpecialty.jsf";
    }

    public String saveEditedSpecialty(){
        specialtyDao.update(this.specialty);
        return "specialties.jsf";
    }

    public String delete(long id){
        try {
            specialtyDao.delete(id);
        } catch (EJBException e) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(languageBean.getMsgCantDeleteSpecialty()));
        }
        return "specialties.jsf";
    }

    public SortOrder getSpecialtySortOrder() {
        return specialtySortOrder;
    }

    public void setSpecialtySortOrder(SortOrder specialtySortOrder) {
        this.specialtySortOrder = specialtySortOrder;
    }

    public void switchSortOrder(){
        if(specialtySortOrder==SortOrder.ascending){
            specialtySortOrder=SortOrder.descending;
        } else {
            specialtySortOrder=SortOrder.ascending;
        }
    }

    public void setScrollerPage(int scrollerPage) {
        this.scrollerPage = scrollerPage;
    }

    public int getScrollerPage() {
        return scrollerPage;
    }
}
