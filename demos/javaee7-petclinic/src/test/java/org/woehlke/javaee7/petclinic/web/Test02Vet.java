package org.woehlke.javaee7.petclinic.web;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.woehlke.javaee7.petclinic.web.pages.*;

import java.net.URL;
import java.util.logging.Logger;

import static org.jboss.arquillian.graphene.Graphene.goTo;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 21.01.14
 * Time: 21:54
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Arquillian.class)
public class Test02Vet {

    private static Logger log = Logger.getLogger(Test02Vet.class.getName());

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return Deployments.createVetDeployment();
    }

    @Drone
    WebDriver driver;

    @ArquillianResource
    URL deploymentUrl;

    @Page
    private HelloPage helloPage;

    @Page
    private VetsPage vetsPage;

    @Page
    private NewVetPage newVetPage;

    @Page
    private EditVetPage editVetPage;

    @Page
    private SpecialtiesPage specialtiesPage;

    @Page
    private NewSpecialtiesPage newSpecialtiesPage;

    @Test
    @InSequence(1)
    @RunAsClient
    public void testOpeningHomePage() {
        goTo(HelloPage.class);
        helloPage.assertTitle();
    }


    @Test
    @InSequence(2)
    @RunAsClient
    public void testOpeningVetPage() {
        goTo(VetsPage.class);
        vetsPage.assertPageIsLoaded();
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void testNewVetPage() {
        goTo(VetsPage.class);
        vetsPage.assertPageIsLoaded();
        vetsPage.clickAddNewVet();
        newVetPage.assertPageIsLoaded();
        newVetPage.addNewContent("Thomas","Woehlke");
        vetsPage.assertPageIsLoaded();
        vetsPage.assertNewContentFound("Thomas", "Woehlke");
    }

    @Test
    @InSequence(4)
    @RunAsClient
    public void testEditVetPage() {
        goTo(VetsPage.class);
        vetsPage.assertPageIsLoaded();
        vetsPage.clickEditVet();
        editVetPage.assertPageIsLoaded();
        editVetPage.editContent("Willy", "Wacker");
        vetsPage.assertPageIsLoaded();
        vetsPage.assertEditedContentFound("Willy", "Wacker");
    }

    @Test
    @InSequence(5)
    @RunAsClient
    public void testDeleteVetPage() {
        goTo(VetsPage.class);
        vetsPage.assertPageIsLoaded();
        vetsPage.clickDeleteVet();
        vetsPage.assertPageIsLoaded();
        vetsPage.assertDeletedContentNotFound();
    }

    @Test
    @InSequence(6)
    @RunAsClient
    public void testNewVetPageWithSpecialties() {
        goTo(SpecialtiesPage.class);
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("dentist");
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("anesthetist");
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("radiology");
        goTo(VetsPage.class);
        vetsPage.assertPageIsLoaded();
        vetsPage.clickAddNewVet();
        newVetPage.assertPageIsLoaded();
        newVetPage.addNewContentWithAllSpecialties("Thomas", "Woehlke");
        vetsPage.assertPageIsLoaded();
        vetsPage.assertContentFoundWithSpecialties("Thomas", "Woehlke", "anesthetist dentist radiology");
    }

    @Test
    @InSequence(7)
    @RunAsClient
    public void testEditVetPageWithSpecialties() {
        goTo(VetsPage.class);
        vetsPage.clickEditVet();
        editVetPage.assertPageIsLoaded();
        editVetPage.editContentWithNoneSpecialties("Henry", "Jones");
        vetsPage.assertPageIsLoaded();
        vetsPage.assertContentFoundWithSpecialties("Henry", "Jones", "none");
    }

    /**
     * Test for #24 new specialties not visible in veterinarians editmode
     */
    @Test
    @InSequence(8)
    @RunAsClient
    public void testEditVetPageWithNewSpecialties(){
        goTo(VetsPage.class);
        vetsPage.assertPageIsLoaded();
        goTo(SpecialtiesPage.class);
        specialtiesPage.assertPageIsLoaded();
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.assertPageIsLoaded();
        newSpecialtiesPage.addNewContent("hero");
        goTo(VetsPage.class);
        vetsPage.assertPageIsLoaded();
        vetsPage.clickEditVet();
        editVetPage.assertPageIsLoaded();
        editVetPage.editContentWithAllSpecialties("Thomas", "Woehlke");
        vetsPage.assertPageIsLoaded();
        vetsPage.assertContentFoundWithSpecialties("Thomas", "Woehlke", "anesthetist dentist hero radiology");
    }

    @Test
    @InSequence(9)
    @RunAsClient
    public void testFillNewVetPageWithSpecialties() {
        goTo(SpecialtiesPage.class);
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("s01");
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("s02");
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("s03");
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("s04");
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("s05");
        specialtiesPage.clickAddNewSpecialty();
        newSpecialtiesPage.addNewContent("s06");
    }

    @Test
    @InSequence(10)
    @RunAsClient
    public void testFillNewVetsPage() {
        goTo(VetsPage.class);
        vetsPage.assertPageIsLoaded();
        vetsPage.clickDeleteVet();
        vetsPage.assertPageIsLoaded();
        vetsPage.clickAddNewVet();
        newVetPage.assertPageIsLoaded();
        newVetPage.addNewContentWithOneSpecialty("Vorname01", "Nachname06","s03");
        vetsPage.clickAddNewVet();
        newVetPage.assertPageIsLoaded();
        newVetPage.addNewContentWithOneSpecialty("Vorname02", "Nachname05","s02");
        vetsPage.clickAddNewVet();
        newVetPage.assertPageIsLoaded();
        newVetPage.addNewContentWithOneSpecialty("Vorname03", "Nachname04", "s01");
        vetsPage.clickAddNewVet();
        newVetPage.assertPageIsLoaded();
        newVetPage.addNewContentWithOneSpecialty("Vorname04", "Nachname03", "s06");
        vetsPage.clickAddNewVet();
        newVetPage.assertPageIsLoaded();
        newVetPage.addNewContentWithOneSpecialty("Vorname05", "Nachname02", "s05");
        vetsPage.clickAddNewVet();
        newVetPage.assertPageIsLoaded();
        newVetPage.addNewContentWithOneSpecialty("Vorname06", "Nachname01", "s04");
        vetsPage.assertPageIsLoaded();
    }

    @Test
    @InSequence(11)
    @RunAsClient
    public void testVetsPager() {
        vetsPage.assertPagerNextIsLoaded();
        vetsPage.clickPagerNext();
        vetsPage.assertPagerPrevIsLoaded();
        vetsPage.clickPagerPrev();
        vetsPage.assertPagerNextIsLoaded();
    }

    @Test
    @InSequence(12)
    @RunAsClient
    public void testVetsSorterFirstname() {
        vetsPage.assertSorterIsLoaded();
        vetsPage.assertOrder();
        vetsPage.clickSorterFirstname();
        vetsPage.assertFirstnameOrder();
        vetsPage.clickSorterFirstname();
        vetsPage.assertFirstnameReverseOrder();
    }

    @Test
    @InSequence(13)
    @RunAsClient
    public void testVetsSorterLastname() {
        vetsPage.clickSorterLastname();
        vetsPage.assertLastnameOrder();
        vetsPage.clickSorterLastname();
        vetsPage.assertLastnameReverseOrder();
    }

    @Test
    @InSequence(14)
    @RunAsClient
    public void testVetsSorterSpecialty() {
        vetsPage.clickSorterSpecialty();
        vetsPage.assertSpecialtyOrder();
        vetsPage.clickSorterSpecialty();
        vetsPage.assertSpecialtyReverseOrder();
    }

}
