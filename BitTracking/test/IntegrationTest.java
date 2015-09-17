import models.User;
import org.junit.*;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.libs.F;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

import static play.test.Helpers.fakeApplication;
import static org.fest.assertions.Assertions.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;

public class IntegrationTest extends WithApplication{

    /**
     * add your integration test here
     * in this example we just check if the welcome page is being shown
     */
  /*  @Test
    public void testIndexPage() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");
                assertTrue(browser.pageSource().contains("Log"));
                boolean b = browser.pageSource().contains("Please ");
                assertFalse(b);
            }
        });
    }*/


 /*   @Test
    public void testLoginForm() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/login");
                User u = new User();
                u.email ="alma@mail.com";
                u.password = "usertest!12";
                u.save();

                browser.fill("#form01").with("Alma");
                browser.fill("#form02").with("usertest!12");
                browser.submit("submit");

                boolean b = browser.pageSource().contains("<div id=\"info01\">Log");
                assertTrue(b);
            }
        });}*/

   /* @Test
    public void testProfileAdmin() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())),
                HTMLUNIT, new Callback<TestBrowser>() {
                    public void invoke(TestBrowser browser) {
                        // LogIn
                        browser.goTo("http://localhost:3333/login");
                        browser.fill("#inputEmail3").with("admin@admin.ba");
                        browser.fill("#password").with("admin123!");
                        browser.submit("#forma");
                        // Going to the profile page; (checking for data of the
                        // admin user, some Strings);
                        browser.goTo("http://localhost:3333/profile");
                        assertThat(browser.pageSource()).contains("admin");
                        assertThat(browser.pageSource()).contains(
                                "admin@admin.ba");

                    }
                });
    }*/

}
