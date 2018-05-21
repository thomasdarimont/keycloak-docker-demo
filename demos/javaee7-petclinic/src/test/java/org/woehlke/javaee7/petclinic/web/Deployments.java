package org.woehlke.javaee7.petclinic.web;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.woehlke.javaee7.petclinic.dao.*;
import org.woehlke.javaee7.petclinic.entities.*;
import org.woehlke.javaee7.petclinic.services.OwnerService;
import org.woehlke.javaee7.petclinic.services.OwnerServiceImpl;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: tw
 * Date: 20.01.14
 * Time: 08:45
 * To change this template use File | Settings | File Templates.
 */
public class Deployments {

    private static final String WEBAPP_SRC = "src/main/webapp";

    public static WebArchive createSpecialtiesDeployment() {
        File[] deps = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
        WebArchive war = null;
        try {
            war = ShrinkWrap.create(WebArchive.class, "specialties.war")
                .addClasses(SpecialtyController.class, LanguageBean.class,
                        SpecialtyDao.class, SpecialtyDaoImpl.class,
                        Owner.class, Pet.class, PetType.class,
                        Specialty.class, Vet.class, Visit.class,
                        net.sourceforge.cobertura.coveragedata.LightClassmapListener.class)
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$|.*\\.html$"))
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("messages_de.properties")
                .addAsResource("messages_en.properties")
                .addAsLibraries(deps)
                .addAsWebInfResource(
                        new StringAsset("<faces-config version=\"2.2\"/>"),
                        "faces-config.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return war;
    }

    public static WebArchive createPetTypeDeployment() {
        File[] deps = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
        return ShrinkWrap.create(WebArchive.class, "pettypes.war")
                .addClasses(PetTypeController.class, LanguageBean.class,
                        PetTypeDao.class, PetTypeDaoImpl.class,
                        Owner.class, Pet.class, PetType.class,
                        Specialty.class, Vet.class, Visit.class,
                        net.sourceforge.cobertura.coveragedata.LightClassmapListener.class)
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$|.*\\.html$"))
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("messages_de.properties")
                .addAsResource("messages_en.properties")
                .addAsLibraries(deps)
                .addAsWebInfResource(
                        new StringAsset("<faces-config version=\"2.2\"/>"),
                        "faces-config.xml");
    }

    public static WebArchive createVetDeployment() {
        File[] deps = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
        return ShrinkWrap.create(WebArchive.class, "vet.war")
                .addClasses(
                        SpecialtyController.class, VetController.class, LanguageBean.class,
                        SpecialtyConverter.class,
                        SpecialtyDao.class, SpecialtyDaoImpl.class,
                        VetDao.class, VetDaoImpl.class,
                        Owner.class, Pet.class, PetType.class,
                        Specialty.class, Vet.class, Visit.class,
                        VetSortingBean.class,
                        net.sourceforge.cobertura.coveragedata.LightClassmapListener.class)
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$|.*\\.html$"))
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("messages_de.properties")
                .addAsResource("messages_en.properties")
                .addAsLibraries(deps)
                .addAsWebInfResource(
                        new StringAsset("<faces-config version=\"2.2\"/>"),
                        "faces-config.xml");
    }

    public static WebArchive createOwnerDeployment() {
        File[] deps = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
        WebArchive war = null;
        try {
                war = ShrinkWrap.create(WebArchive.class, "owner.war")
                .addClasses(OwnerController.class, PetTypeController.class, LanguageBean.class,
                        OwnerService.class, OwnerServiceImpl.class,
                        OwnerDao.class, OwnerDaoImpl.class, PetDao.class, PetDaoImpl.class,
                        VisitDao.class, VisitDaoImpl.class,
                        PetTypeDao.class, PetTypeDaoImpl.class,
                        Owner.class, Pet.class, PetType.class,
                        Specialty.class, Vet.class, Visit.class,
                        net.sourceforge.cobertura.coveragedata.LightClassmapListener.class)
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$"))
                .addAsResource("META-INF/persistence.xml")
                .addAsResource("messages_de.properties")
                .addAsResource("messages_en.properties")
                .addAsLibraries(deps)
                .addAsWebInfResource(
                                new StringAsset("<faces-config version=\"2.2\"/>"),
                                "faces-config.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return war;
    }
}
