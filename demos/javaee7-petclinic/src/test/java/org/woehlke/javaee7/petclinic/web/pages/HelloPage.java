package org.woehlke.javaee7.petclinic.web.pages;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Location;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 26.01.14
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
@Location("hello.jsf")
public class HelloPage {

    @Drone
    private WebDriver driver;

    public void assertTitle(){
        Assert.assertEquals("Java EE 7 Petclinic",driver.getTitle());
    }
}
