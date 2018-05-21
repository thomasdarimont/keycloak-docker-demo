package org.woehlke.javaee7.petclinic.web.pages;

import org.jboss.arquillian.graphene.Graphene;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 27.01.14
 * Time: 09:27
 * To change this template use File | Settings | File Templates.
 */
public class EditOwnerPage {

    @FindBy(id="editOwnerForm")
    private WebElement editOwnerForm;

    @FindBy(id="editOwnerForm:firstName")
    private WebElement firstName;

    @FindBy(id="editOwnerForm:lastName")
    private WebElement lastName;

    @FindBy(id="editOwnerForm:address")
    private WebElement address;

    @FindBy(id="editOwnerForm:city")
    private WebElement city;

    @FindBy(id="editOwnerForm:telephone")
    private WebElement telephone;

    @FindBy(id="editOwnerForm:save")
    private WebElement save;

    public void assertPageIsLoaded() {
        Graphene.waitModel().until().element(editOwnerForm).is().visible();
        Assert.assertTrue(editOwnerForm.isDisplayed());
    }

    public void editContent(String firstName,
                            String lastName,
                            String address,
                            String city,
                            String telephone) {
        this.firstName.clear();
        this.lastName.clear();
        this.address.clear();
        this.city.clear();
        this.telephone.clear();
        this.firstName.sendKeys(firstName);
        this.lastName.sendKeys(lastName);
        this.address.sendKeys(address);
        this.city.sendKeys(city);
        this.telephone.sendKeys(telephone);
        Graphene.guardHttp(this.save).click();
    }
}
