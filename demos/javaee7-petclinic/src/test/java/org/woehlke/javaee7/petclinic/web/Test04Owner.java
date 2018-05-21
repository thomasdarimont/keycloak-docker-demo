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
import java.util.Date;
import java.util.logging.Logger;

import static org.jboss.arquillian.graphene.Graphene.goTo;


/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 22.01.14
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */
@RunWith(Arquillian.class)
public class Test04Owner {

    private static Logger log = Logger.getLogger(Test04Owner.class.getName());

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return Deployments.createOwnerDeployment();
    }

    @Drone
    WebDriver driver;

    @ArquillianResource
    URL deploymentUrl;

    @Page
    private HelloPage helloPage;

    @Page
    private FindOwnersPage findOwnersPage;

    @Page
    private OwnersPage ownersPage;

    @Page
    private NewOwnerPage newOwnerPage;

    @Page
    private ShowOwnerPage showOwnerPage;

    @Page
    private EditOwnerPage editOwnerPage;

    @Page
    private NewPetPage newPetPage;

    @Page
    private NewVisitPage newVisitPage;

    @Page
    private EditPetPage editPetPage;

    @Page
    private PetTypesPage petTypesPage;

    @Page
    private NewPetTypePage newPetTypePage;

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
    public void testOpenFindOwnersPage() {
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
    }

    @Test
    @InSequence(3)
    @RunAsClient
    public void testOpenOwnersPage() {
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
        findOwnersPage.clickSearch();
        ownersPage.assertPageIsLoaded();
    }

    @Test
    @InSequence(4)
    @RunAsClient
    public void testOpenNewOwnerPage() {
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
        findOwnersPage.clickNewOwner();
        newOwnerPage.assertPageIsLoaded();
    }

    @Test
    @InSequence(5)
    @RunAsClient
    public void testOpenNewOwnerPageFromOwnersList() {
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
        findOwnersPage.clickSearch();
        ownersPage.assertPageIsLoaded();
        ownersPage.clickNewOwner();
        newOwnerPage.assertPageIsLoaded();
    }

    @Test
    @InSequence(6)
    @RunAsClient
    public void testAddNewOwner() {
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
        findOwnersPage.clickSearch();
        ownersPage.assertPageIsLoaded();
        ownersPage.clickNewOwner();
        newOwnerPage.assertPageIsLoaded();
        newOwnerPage.addNewContent("Thomas","Woehlke","Schoenhauser Allee 42","Berlin","03012345678");
        ownersPage.assertPageIsLoaded();
        ownersPage.assertNewContentFound("Thomas","Woehlke","Schoenhauser Allee 42","Berlin","03012345678");
    }

    @Test
    @InSequence(7)
    @RunAsClient
    public void testEditOwner() {
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
        findOwnersPage.clickSearch();
        ownersPage.assertPageIsLoaded();
        ownersPage.clickShowOwner();
        showOwnerPage.assertPageIsLoaded();
        showOwnerPage.clickEditOwner();
        editOwnerPage.assertPageIsLoaded();
        editOwnerPage.editContent("Willy","Wombel","Elbchaussee 242","Hamburg","04012345678");
        showOwnerPage.assertPageIsLoaded();
        showOwnerPage.assertContent("Willy","Wombel","Elbchaussee 242","Hamburg","04012345678");
    }

    @Test
    @InSequence(8)
    @RunAsClient
    public void testAddNewPet() {
        goTo(PetTypesPage.class);
        petTypesPage.assertPageIsLoaded();
        petTypesPage.clickAddNewPetType();
        newPetTypePage.addNewContent("cat");
        petTypesPage.clickAddNewPetType();
        newPetTypePage.addNewContent("dog");
        petTypesPage.clickAddNewPetType();
        newPetTypePage.addNewContent("mouse");
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
        findOwnersPage.clickSearch();
        ownersPage.assertPageIsLoaded();
        ownersPage.clickShowOwner();
        showOwnerPage.assertPageIsLoaded();
        showOwnerPage.clickAddNewPet();
        newPetPage.assertPageIsLoaded();
        Date birthDate1 = new Date(113, 04, 15); //15.05.2013
        Date birthDate2 = new Date(112, 07, 03); //03.08.2012
        newPetPage.setContent("Tomcat", birthDate1, "cat");
        showOwnerPage.assertPageIsLoaded();
        showOwnerPage.clickAddNewPet();
        newPetPage.setContent("Bully", birthDate2, "dog");
        showOwnerPage.assertPageIsLoaded();
        showOwnerPage.assertFirstPetContent("Bully", birthDate2, "dog");
        showOwnerPage.assertSecondPetContent("Tomcat", birthDate1, "cat");
    }

    @Test
    @InSequence(9)
    @RunAsClient
    public void testEditPet() {
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
        findOwnersPage.clickSearch();
        ownersPage.assertPageIsLoaded();
        ownersPage.clickShowOwner();
        showOwnerPage.assertPageIsLoaded();
        showOwnerPage.clickEditFirstPet();
        editPetPage.assertPageIsLoaded();
        Date birthDate = new Date(110, 05, 01); //01.06.2010
        editPetPage.setContent("Speedy", birthDate, "mouse");
        showOwnerPage.assertPageIsLoaded();
        showOwnerPage.assertFirstPetContent("Speedy", birthDate, "mouse");
    }

    @Test
    @InSequence(10)
    @RunAsClient
    public void testAddVisitToFirstPet() {
        goTo(FindOwnersPage.class);
        findOwnersPage.assertPageIsLoaded();
        findOwnersPage.clickSearch();
        ownersPage.assertPageIsLoaded();
        ownersPage.clickShowOwner();
        showOwnerPage.assertPageIsLoaded();
        showOwnerPage.addVisitToFirstPet();
        newVisitPage.assertPageIsLoaded();
        Date birthDate = new Date(110, 05, 01); //01.06.2010
        newVisitPage.assertOwnerContent("Willy","Wombel");
        newVisitPage.assertPetContent("Speedy", birthDate, "mouse");
        Date visitDate = new Date(114, 01, 16); //16.01.2014
        newVisitPage.setNewContent(visitDate,"get milk");
        showOwnerPage.assertFirstVisitToFirstPet(visitDate,"get milk");
    }
}
