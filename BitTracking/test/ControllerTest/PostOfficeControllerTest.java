package ControllerTest;

import controllers.routes;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

/**
 * Created by Kristina Pupavac on 10/7/2015.
 */
public class PostOfficeControllerTest {

    /**
     * Testing database
     */
    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    /**
     * Testing deleteOffice() redirect route
     * @param id - Post Office id
     */
    @Test
    public void testDeleteOffice(Long id){
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.deleteOffice(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/postoffice/:id/");
        });
    }

    /**
     * Testing addNewOffice() redirecting route
     */
    @Test
    public void testAddNewOffice(){
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.addNewOffice());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/postoffice");
        });
    }

    /**
     * Testing postOfficeDetails() redirecting route
     * @param id - Post Office id
     */
    @Test
    public void testPostOfficeDetails(Long id) {
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.postOfficeDetails(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/postoffice/details/:id");
        });
    }

    /**
     * Testing updateOffice() redirecting route
     * @param Id - Post Office id
     */
    @Test
    public void testUpdateOffice(Long Id) {
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.updateOffice(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/postoffice/details/:id");
        });
    }

    /**
     * Testing linkPostOffices() redirecting route
     * @param id
     */
    @Test
    public void testLinkPostOffices(Long id){
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.linkPostOffices(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/linkoffices/:id");
        });
    }

    /**
     * Testing savePostOffices() redirecting route
     */
    @Test
    public void testSavePostOffices(){
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.savePostOffices());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/linkoffices");
        });
    }

    /**
     * Testing listRoutes() redirecting route
     * @param id
     */
    @Test
    public void testListRoutes(Long id) {
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.listRoutes(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/makeroute/:id");
        });
    }

    /**
     * Testing createRoute() redirecting route
     */
    @Test
    public void testCreateRoute() {
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.createRoute());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/makeroute/create");
        });
    }

    /**
     * Testing saveRoute() redirecting route
     * @param id
     */
    @Test
    public void testSaveRoute(Long id) {
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.saveRoute(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/makeroute/save/id");
        });
    }

    /**
     * Testing checkOfficeName() redirecting route
     */
    @Test
    public void testCheckOfficeName() {
        running(fakeApplication(), () -> {
            Result result = route(routes.PostOfficeController.checkOfficeName());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/adminpanel/tables/addpostoffice/checkname");
        });
    }

}
