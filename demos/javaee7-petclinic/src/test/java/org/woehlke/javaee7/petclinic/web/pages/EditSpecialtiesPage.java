package org.woehlke.javaee7.petclinic.web.pages;

import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 26.01.14
 * Time: 18:14
 * To change this template use File | Settings | File Templates.
 */
public class EditSpecialtiesPage {

    @FindBy(id="editSpecialty")
    private WebElement editSpecialty;

    @FindBy(id="editSpecialtyForm:name")
    private WebElement name;

    @FindBy(id="editSpecialtyForm:save")
    private WebElement save;

    public void assertPageIsLoaded() {
        Assert.assertTrue(editSpecialty.isDisplayed());
    }

    public void editContent(String content) {
        name.clear();
        name.sendKeys(content);
        save.click();
    }
}
