package org.woehlke.javaee7.petclinic.web.pages;

import org.jboss.arquillian.graphene.Graphene;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 26.01.14
 * Time: 18:56
 * To change this template use File | Settings | File Templates.
 */
public class NewPetTypePage {

    @FindBy(id="addNewPetType")
    private WebElement addNewPetType;

    @FindBy(id="addNewPetTypeForm:name")
    private WebElement name;

    @FindBy(id="addNewPetTypeForm:save")
    private WebElement save;

    public void assertPageIsLoaded() {
        Graphene.waitModel().until().element(addNewPetType).is().visible();
        Assert.assertTrue(addNewPetType.isDisplayed());
    }

    public void addNewContent(String content) {
        name.clear();
        name.sendKeys(content);
        Graphene.guardHttp(save).click();
    }
}
