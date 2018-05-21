package org.woehlke.javaee7.petclinic.web;

import org.woehlke.javaee7.petclinic.entities.Specialty;

import javax.el.ELContext;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 04.01.14
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
@FacesConverter("SpecialtyConverter")
public class SpecialtyConverter  implements Converter {

    private VetController vetController;

    public Object getAsObject(FacesContext facesContext, UIComponent component, String s) {
        for (Specialty specialty : getCapitalsParser(facesContext).getSpecialties()) {
            if (specialty.getName().equals(s)) {
                return specialty;
            }
        }
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object o) {
        if (o == null) return null;
        return ((Specialty) o).getName();
    }

    private VetController getCapitalsParser(FacesContext facesContext) {
        if (vetController == null) {
            ELContext elContext = facesContext.getELContext();
            vetController = (VetController) elContext.getELResolver().getValue(elContext, null, "vetController");
        }
        return vetController;
    }


}
