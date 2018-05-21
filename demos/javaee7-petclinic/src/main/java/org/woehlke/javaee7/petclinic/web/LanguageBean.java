package org.woehlke.javaee7.petclinic.web;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 14.01.14
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name="language")
@SessionScoped
public class LanguageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String localeCode = Locale.ENGLISH.toString();

    private static Map<String,Object> countries;
    static{
        countries = new LinkedHashMap<String,Object>();
        countries.put("English", Locale.ENGLISH);
        countries.put("Deutsch", Locale.GERMAN);
    }

    public Map<String, Object> getCountriesInMap() {
        return countries;
    }


    public String getLocaleCode() {
        return localeCode;
    }


    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    //value change event listener
    public void countryLocaleCodeChanged(ValueChangeEvent e){

        String newLocaleValue = e.getNewValue().toString();

        //loop country map to compare the locale code
        for (Map.Entry<String, Object> entry : countries.entrySet()) {

            if(entry.getValue().toString().equals(newLocaleValue)){

                FacesContext.getCurrentInstance()
                        .getViewRoot().setLocale((Locale)entry.getValue());

            }
        }
    }

    public String getMsgCantDeleteSpecialty() {
        String msg = "";
        if(localeCode.equals(Locale.ENGLISH.toString())){
            msg = "cannot delete, Specialty still in use";
        } else if(localeCode.equals(Locale.GERMAN.toString())){
            msg = "löschen nicht möglich, Fachrichtung wird noch ausgeübt";
        }
        return msg;
    }
}
