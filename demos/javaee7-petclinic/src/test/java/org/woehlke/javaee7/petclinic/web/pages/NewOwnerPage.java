package org.woehlke.javaee7.petclinic.web.pages;

import org.jboss.arquillian.graphene.Graphene;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 26.01.14
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class NewOwnerPage {

    @FindBy(id="addNewOwner")
    private WebElement addNewOwner;

    @FindBy(id="addNewOwnerForm:firstName")
    private WebElement firstName;

    @FindBy(id="addNewOwnerForm:lastName")
    private WebElement lastName;

    @FindBy(id="addNewOwnerForm:address")
    private WebElement address;

    @FindBy(id="addNewOwnerForm:city")
    private WebElement city;

    @FindBy(id="addNewOwnerForm:telephone")
    private WebElement telephone;

    @FindBy(id="addNewOwnerForm:save")
    private WebElement save;

    public void assertPageIsLoaded() {
        Graphene.waitModel().until().element(addNewOwner).is().visible();
        Assert.assertTrue(addNewOwner.isDisplayed());
    }

    public void addNewContent(String firstName,
                              String lastName,
                              String address,
                              String city,
                              String telephone) {
        this.firstName.sendKeys(firstName);
        this.lastName.sendKeys(lastName);
        this.address.sendKeys(address);
        this.city.sendKeys(city);
        this.telephone.sendKeys(telephone);
        Graphene.guardHttp(this.save).click();
    }
}
