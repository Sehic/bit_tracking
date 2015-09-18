import org.junit.Before;
import org.junit.Test;

import play.api.test.TestServer;
import play.libs.F.*;
import play.test.TestBrowser;

import static org.junit.Assert.*;

import static play.test.Helpers.*;


public class IntegrationTest {

    @Before
    public void setUp(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void test(){
        running(testServer(3333, fakeApplication(inMemoryDatabase()))),HTMLUNIT, new Callback<TestBrowser>(){

            public void invoke(TestBrowser browser){
                browser.goTo("http://localhost:3333");

                boolean b = browser.pageSource().contains("Our Organization");

                assertTrue(b);

            }
        };
    }

    @Test
    public void testLogin(){
        running(testServer(3333, fakeApplication(inMemoryDatabase()))), HTMLUNIT, new Callback<TestBrowser>(){


            public void invoke(TestBrowser browser){
                browser.goTo("http://localhost:3333");
                browser.fill("#inputEmail3").with("admin@admin.com");
                browser.fill("#inputPassword3").with("admin123");
                browser.click("#loginButton");

                boolean b = browser.pageSource().contains("Admin panel");

                assertTrue(b);
            }
        };
    }

    @Test
    public void testRegisterRoute(){
       running(testServer(3333, fakeApplication(inMemoryDatabase()))), HTMLUNIT,new Callback<TestBrowser>(){
           public void invoke(TestBrowser browser){
               browser.goTo("http://localhost:9000/register");
               assertTrue(browser.pageSource().contains("Register for a BIT"));

           }
       };
    }

    @Test
    public void testRegistration(){

        running(testServer(3333,fakeApplication(inMemoryDatabase()))), HTMLUNIT,new Callback<TestBrowser>(){

            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/register");
                browser.fill("#inputName").with("Dado");
                browser.fill("#inputLastName").with("Dado");
                browser.fill("#inputEmail3").with("dado@dado.com");
                browser.fill("#inputPassword3").with("dadodado");
                browser.fill("#inputPassword4").with("dadodado");
                browser.click("#registerButton");

                assertTrue(browser.pageSource().contains("Log into an Existing Account"));
            }
        };
    }

    @Test
    public void testLogOut(){
        running(testServer(3333, fakeApplication(inMemoryDatabase()))), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:3333");
                browser.click("#singout");
                assertTrue(browser.pageSource().contains("Log In"));
            }
        };
    }

    @Test
    public void testAdminPanelRoute(){
        running(testServer(3333, fakeApplication(inMemoryDatabase()))), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/adminpanel");
                assertTrue(browser.pageSource().contains("Dashboard"));
            }
        };
    }

    @Test
    public void testAddingPostOffice(){
        running(testServer(3333, fakeApplication(inMemoryDatabase()))), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/adminpanel/tables/addpostoffice");
                browser.fill("#name").with("Office1");
                browser.fill("#address").with("Sarajevo");
                browser.click("#submit");

                assertTrue(browser.pageSource().contains("post offices"));
            }
        };
    }
}
