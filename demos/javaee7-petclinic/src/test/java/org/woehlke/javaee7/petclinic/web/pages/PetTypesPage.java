package org.woehlke.javaee7.petclinic.web.pages;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Location;
import org.junit.Assert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 26.01.14
 * Time: 18:56
 * To change this template use File | Settings | File Templates.
 */
@Location("petTypes.jsf")
public class PetTypesPage {

    @FindBy(id="petTypes")
    private WebElement petTypes;

    @FindBy(id="petTypesForm:getNewPetTypeForm")
    private WebElement getNewPetTypeForm;

    @FindBy(id="petTypesForm:petTypesTable:5:name")
    private WebElement name5InTable;

    @FindBy(id="petTypesForm:petTypesTable:0:name")
    private WebElement nameInTable;

    @FindBy(id="petTypesForm:petTypesTable:0:edit")
    private WebElement editInTable;

    @FindBy(id="petTypesForm:petTypesTable:0:delete")
    private WebElement deleteInTable;

    @FindBy(id="petTypesForm:scroller_ds_next")
    private WebElement scrollerNext;

    @FindBy(id="petTypesForm:scroller_ds_prev")
    private WebElement scrollerPrev;

    @FindBy(id="petTypesForm:petTypesTable:colNameSort")
    private WebElement colNameSort;

    public void assertPageIsLoaded() {
        Graphene.waitModel().until().element(petTypes).is().visible();
        Assert.assertTrue(petTypes.isDisplayed());
    }

    public void clickAddNewPetType() {
        Graphene.guardHttp(getNewPetTypeForm).click();
    }

    public void assertNewContentFound(String content) {
        Assert.assertEquals(content, nameInTable.getText());
    }

    public void clickEditSpecialty() {
        Graphene.guardHttp(editInTable).click();
    }

    public void assertEditedContentFound(String content) {
        Assert.assertEquals(content, nameInTable.getText());
    }

    public void clickDeleteSpecialty() {
        Graphene.guardHttp(deleteInTable).click();
    }

    public void assertDeletedContentNotFound() {
        boolean isDeleted = false;
        try {
            Assert.assertEquals(null,nameInTable);
        } catch (NoSuchElementException elementException) {
            isDeleted = true;
        }
        Assert.assertTrue(isDeleted);
    }

    public void assertPagerNextIsLoaded(){
        Graphene.waitModel().until().element(scrollerNext).is().visible();
        Assert.assertTrue(scrollerNext.isDisplayed());
    }

    public void assertPagerPrevIsLoaded(){
        Graphene.waitModel().until().element(scrollerPrev).is().visible();
        Assert.assertTrue(scrollerPrev.isDisplayed());
    }

    public void clickPagerNext() {
        scrollerNext.click();
    }


    public void clickPagerPrev() {
        scrollerPrev.click();
    }

    public void assertSorterIsLoaded(){
        Graphene.waitModel().until().element(colNameSort).is().visible();
        Assert.assertTrue(colNameSort.isDisplayed());
    }

    public void assertOrder() {
        Graphene.waitModel().until().element(nameInTable).is().visible();
        Assert.assertTrue(nameInTable.getText().compareTo("pet01")==0);
    }

    public void clickSorter() {
        Graphene.waitModel().until().element(colNameSort).is().visible();
        colNameSort.click();
    }

    public void assertReverseOrder() {
        Graphene.waitModel().until().element(name5InTable).is().visible();
        Assert.assertTrue(name5InTable.getText().compareTo("pet06")==0);
    }
}
