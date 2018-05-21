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
 * Time: 17:23
 * To change this template use File | Settings | File Templates.
 */
@Location("specialties.jsf")
public class SpecialtiesPage {

    @FindBy(id="specialties")
    private WebElement specialties;

    @FindBy(id="specialtiesForm:addNewSpecialty")
    private WebElement addNewSpecialty;

    @FindBy(id="specialtiesForm:specialtiesTable:5:name")
    private WebElement name5InTable;

    @FindBy(id="specialtiesForm:specialtiesTable:0:name")
    private WebElement nameInTable;

    @FindBy(id="specialtiesForm:specialtiesTable:0:edit")
    private WebElement editInTable;

    @FindBy(id="specialtiesForm:specialtiesTable:0:delete")
    private WebElement deleteInTable;

    @FindBy(id="specialtiesForm:scroller_ds_next")
    private WebElement scrollerNext;

    @FindBy(id="specialtiesForm:scroller_ds_prev")
    private WebElement scrollerPrev;

    @FindBy(id="specialtiesForm:specialtiesTable:colNameSort")
    private WebElement colNameSort;


    public void assertPageIsLoaded(){
        Assert.assertTrue(specialties.isDisplayed());
    }

    public void clickAddNewSpecialty(){
        addNewSpecialty.click();
    }

    public void assertNewContentFound(String content) {
        Assert.assertEquals(content, nameInTable.getText());
    }

    public void clickEditSpecialty() {
        editInTable.click();
    }

    public void assertEditedContentFound(String content) {
        Assert.assertEquals(content, nameInTable.getText());
    }

    public void clickDeleteSpecialty() {
        deleteInTable.click();
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
        Assert.assertTrue(nameInTable.getText().compareTo("specialty01")==0);
    }

    public void clickSorter() {
        Graphene.waitModel().until().element(colNameSort).is().visible();
        colNameSort.click();
    }

    public void assertReverseOrder() {
        Graphene.waitModel().until().element(name5InTable).is().visible();
        Assert.assertTrue(name5InTable.getText().compareTo("specialty06")==0);
    }
}
