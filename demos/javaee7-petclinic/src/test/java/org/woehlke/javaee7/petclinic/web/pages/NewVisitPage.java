package org.woehlke.javaee7.petclinic.web.pages;

import org.jboss.arquillian.graphene.Graphene;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.richfaces.fragment.calendar.RichFacesCalendar;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 28.01.14
 * Time: 13:40
 * To change this template use File | Settings | File Templates.
 */
public class NewVisitPage {

    @FindBy(id="addVisitForm")
    private WebElement addVisitForm;

    @FindBy(id="addVisitForm:petName")
    private WebElement petName;

    @FindBy(id="addVisitForm:petBirthDate")
    private WebElement petBirthDate;

    @FindBy(id="addVisitForm:petType")
    private WebElement petType;

    @FindBy(id="addVisitForm:ownerFirstName")
    private WebElement ownerFirstName;

    @FindBy(id="addVisitForm:ownerLastName")
    private WebElement ownerLastName;

    @FindBy(id="addVisitForm:visitDate")
    private RichFacesCalendar visitDate;

    @FindBy(id="addVisitForm:visitDescription")
    private WebElement visitDescription;

    @FindBy(id="addVisitForm:save")
    private WebElement save;

    public void assertPageIsLoaded() {
        Assert.assertTrue(addVisitForm.isDisplayed());
    }

    public void assertOwnerContent(String firstName, String lastName) {
        Assert.assertEquals(firstName,ownerFirstName.getText());
        Assert.assertEquals(lastName,ownerLastName.getText());
    }

    public void assertPetContent(String petName, Date birthDate, String petType) {
        Assert.assertEquals(petName,this.petName.getText());
        Assert.assertEquals(petType,this.petType.getText());
        Date birthDateTmp =  new DateTime(birthDate.getTime()).minusDays(1).toDate();
        Assert.assertEquals(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(birthDateTmp),petBirthDate.getText());
    }

    public void setNewContent(Date visitDate, String description) {
        DateTime dateTime = new  DateTime(visitDate.getTime());
        Graphene.waitModel().until().element(addVisitForm).is().visible();
        this.visitDescription.sendKeys(description);
        this.visitDate.setDate(dateTime);
        Graphene.guardHttp(save).click();
    }
}
