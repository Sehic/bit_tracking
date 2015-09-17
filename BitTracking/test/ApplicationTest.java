import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest extends  WithApplication{


    @Test
    public void testHome() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:9000");
                assertThat(browser.pageSource()).contains("BIT Tracking ");
            }
        });
    }

  /*  @Test
    public void testLogIn() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:9000/login");
                assertThat(browser.pageSource()).contains("Log In");
                assertThat(browser.title().equals("Sign In"));
            }
        });
    }*/

  /*  @Test
    public void testRegister(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:9000/register");
                browser.fill("#inputName").with("Bruce");
                browser.fill("#inputLastName").with("Eckel");
                browser.fill("inputEmail3").with("be@mail.de");
                browser.fill("#inputPassword3").with("123456");
                browser.fill("#inputPassword4").with("123456");
                browser.submit("#submituserform");

            }
        });
    }*/


}
