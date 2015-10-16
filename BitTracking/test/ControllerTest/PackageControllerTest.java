package ControllerTest;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

import controllers.PackageController;
import controllers.routes;
import javaguide.tests.controllers.Application;

import models.User;
import org.junit.Before;
import org.junit.Test;

import play.libs.ws.WS;
import play.mvc.Result;


/**
 * Created by Kristina Pupavac on 10/7/2015.
 * This method testing PackageController routes
 */
public class PackageControllerTest {

    /**
     * Testing database
     */
    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    /**
     * Testing adminPackage() redirect route
     */
    @Test
    public void testAdminPackage(){
        running(fakeApplication(), () -> {
            Result result = route(routes.PackageController.adminPackage());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/package/");
        });
    }

    /**
     * Testing addPackage() redirect route
     */
    @Test
    public void testAddPackage() {
        running(fakeApplication(),()->{
            Result result = route(routes.PackageController.addPackage());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/package/addpackage");
        });
    }

    /**
     * Testing savePackage() redirect route
     */
    @Test
    public void testSavePackage() {
        running(fakeApplication(),()->{
            Result result = route(routes.PackageController.savePackage());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/package/addpackage");
        });
    }

    /**
     * Testing deletePackage() redirect route
     * @param id - Package id
     */
    @Test
    public void  testDeletePackage(Long id){
        running(fakeApplication(),()->{
            Result result = route(routes.PackageController.deletePackage(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/package/delete/:id");
        });
    }

    /**
     * Testing editPackage() redirect route
     * @param id - Package id
     */
    @Test
    public void TestEditPackage(Long id) {
        running(fakeApplication(),()->{
            Result result = route(routes.PackageController.editPackage(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/package/update/:id");
        });
    }

    /**
     * Testing updateStatus() redirect route
     * @param id - Package id
     */
    @Test
    public void TestUpdateStatus(Long id){
        running(fakeApplication(),()->{
            Result result = route(routes.PackageController.updateStatus(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/deliveryworkers/status/:id");
        });
    }

    /**
     * Testing changePackageStatus() redirect route
     * @param id - Package id
     */
    @Test
    public void TestChangePackageStatus(Long id){
        running(fakeApplication(),()->{
            Result result = route(routes.PackageController.updateStatus(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/deliveryworkers/status/:id ");
        });
    }
}