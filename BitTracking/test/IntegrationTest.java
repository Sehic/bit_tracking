import org.junit.Before;
import org.junit.Test;
import play.libs.F.Callback;
import play.test.TestBrowser;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;


public class IntegrationTest {

    @Before
    public void setUp(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void test(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {

            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333");

                boolean b = browser.pageSource().contains("Our Organization");

                assertTrue(b);

            }
        });
    }

    @Test
    public void testLogin(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>(){


            public void invoke(TestBrowser browser){
                browser.goTo("http://localhost:3333");
                browser.fill("#inputEmail3").with("admin@admin.com");
                browser.fill("#inputPassword3").with("admin123");
                browser.click("#loginButton");

                boolean b = browser.pageSource().contains("Admin panel");

                assertTrue(b);
            }
        });
    }

    @Test
    public void testRegisterRoute(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT,new Callback<TestBrowser>(){
            public void invoke(TestBrowser browser){
                browser.goTo("http://localhost:9000/register");
                assertTrue(browser.pageSource().contains("Register for a BIT"));

            }
        });
    }

    @Test
    public void testRegistration(){

        running(testServer(3333,fakeApplication(inMemoryDatabase())), HTMLUNIT,new Callback<TestBrowser>(){

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
        });
    }

    @Test
    public void testLogOut(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:3333");
                browser.click("#singout");
                assertTrue(browser.pageSource().contains("Log In"));
            }
        });
    }

    @Test
    public void testAdminPanelRoute(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/adminpanel");
                assertTrue(browser.pageSource().contains("Dashboard"));
            }
        });
    }

    @Test
    public void testAddingPostOffice(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/adminpanel/tables/addpostoffice");
                browser.fill("#name").with("Office1");
                browser.fill("#address").with("Sarajevo");
                browser.click("#submit");

                assertTrue(browser.pageSource().contains("post offices"));
            }
        });
    }

    @Test
    public void testShowOfficeWorkers(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>(){
            public void invoke(TestBrowser browser){
                browser.goTo("http://localhost:9000/adminpanel/tables");
                browser.click("#showWorkers");
                assertTrue(browser.pageSource().contains("OFFICE_WORKER"));
            }
        });
    }

    @Test
    public void testShowDeliveryWorkers(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>(){
            public void invoke(TestBrowser browser){
                browser.goTo("http://localhost:9000/adminpanel/tables");
                browser.click("#showDelivery");
                assertTrue(browser.pageSource().contains("DELIVERY_WORKER"));
            }
        });
    }

    @Test
    public void testAddWorkers(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/adminpanel/officeworkers");
                browser.click("#addOfficeWorker");
                browser.fill("#inputName").with("Officeworker1");
                browser.fill("#inputLastName").with("lastname");
                browser.fill("#inputEmail3").with("email3@mail.com");
                browser.fill("#inputPassword3").with("dado123");
                browser.fill("#inputPassword4").with("dado123");
                browser.click("#registerWorker");
                assertTrue(browser.pageSource().contains("Officeworker1"));
            }
        });
    }

    @Test
    public void testAddingPackage(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/adminpanel/package/");
                browser.click("#addPackage");
                browser.fill("#destination").with("Sarajevo");
                browser.fill("#weight").with("4");
                browser.fill("#price").with("10");
                browser.click("#finalAdd");
                assertTrue(browser.pageSource().contains("10"));
            }
        });
    }

    @Test
    public void testTrackPackage(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())),HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/trackpackage/");
                browser.fill("#trackingNumber").with("112396fe-4e12-43c9-9c50-37004d9d15da");
                browser.click("#trackSubmit");
                assertTrue(browser.pageSource().contains("112396fe-4e12-43c9-9c50-37004d9d15da"));
            }
        });
    }

    @Test
    public void testEditProfileRoute(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/");
                browser.click("#editProfile");
                assertTrue(browser.pageSource().contains("First Name"));
            }
        });
    }

    @Test
    public void testEditProfile(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/");
                browser.click("#editProfile");
                browser.fill("#inputName").with("changed");
                browser.click("#saveChanges");
                assertTrue(browser.pageSource().contains("changed"));
            }
        });
    }

    @Test
    public void testSearch(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/adminpanel");
                browser.fill("templatemo_search_box").with("aaaa");
                assertTrue(browser.pageSource().contains("aaaa@mail.com"));
            }
        });
    }

    @Test
    public void testUserAddPackage(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/user");
                browser.click("#userCreatePackage");
                browser.fill("#recipientName").with("Ajdin");
                browser.fill("#recipientAddress").with("Zenica");
                browser.fill("#weight").with("2");
                browser.fill("#price").with("2");
                browser.click("#packageType");
                browser.click("#envelope");
                browser.click("#userSendPackage");
                assertTrue(browser.pageSource().contains("Zenica"));
            }
        });
    }

    @Test
    public void testApprovePackage(){
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws Throwable {
                browser.goTo("http://localhost:9000/officeworkerpanel");
                browser.click("#approvalId");
                browser.click("#approveReject");
                browser.click("#approvePack");
                browser.click("#initialPostOffice");
                browser.click("#Zenica");
                browser.click("#destinationPostOffice");
                browser.click("#Sarajevo");
                browser.click("#saveAndCreate");
                assertTrue(browser.pageSource().contains("Finalize"));
            }
        });
    }
}