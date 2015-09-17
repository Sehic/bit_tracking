package ModelTest;

import models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.api.test.TestBrowser;
import play.libs.F;
import play.test.WithApplication;

import javax.validation.Validation;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

/**
 * Created by emina on 15.9.2015.
 */
public class UserModelTest extends WithApplication{

    @Before
    public void setUpApplication(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void testDatabase(){
        List<User> userList = User.findOfficeWorkers();
        assertNotNull(userList);
    }

    @Test
    public void testSaveInDatabase() {

        User u = new User();
        u.firstName = "Alma";
        u.lastName ="Atic";
        u.email ="alma@mail.com";
        u.password = "usertest!12";
        u.save();
        assertNotNull(u);
    }

    @Test
    public void testNonExistingUser(){
        User u = User.findById(1L);
        assertNull(u);
    }

    @Test
    public void testSavingAndLoading(){
        User u = new User();
        u.firstName = "Alma";
        u.lastName ="Atic";
        u.email ="alma@mail.com";
        u.password = "usertest!12";
        u.save();
        User u1 = User.findById(1L);
        assertNotNull(u1);
    }

    @Test
    public void testDeleteUser(){
        User u = new User();
        u.firstName = "Alma";
        u.lastName ="Atic";
        u.email ="alma@mail.com";
        u.password = "usertest!12";
        u.save();
        u.delete();
        assertNotNull(u);
    }

    @Test
    public void testFindByEmailAndPassword(){
        User u = new User("Alma", "Atic","usertest!12", "alma@mail.com");
        u.save();
        User testUser = User.findEmailAndPassword("alma@mail.com", "usertest!12");
        assertEquals("User email comparison", u.email, testUser.email);
        assertEquals("User password comparison", u.password, testUser.password);
        assertEquals(u, testUser);
    }

    @Test
    public void testFindById(){
        User u = new User("Alma", "Atic","usertest!12", "alma@mail.com");
        u.save();
        User testUser = User.findById(1L);
        assertEquals("User id comparison", u.id, testUser.id);
        assertEquals(u, testUser);
    }

    @Test
    public void testCheckEmail(){
        User u = new User("Boki","Tomic","boki123!","boki@ba.ba");
        u.save();
        User testUser = User.checkEmail("boki@ba.ba");
        assertEquals("User email comparison", u.email, testUser.email);
        assertEquals(u,testUser);
    }

    @Test
    public void testCheckName(){
        boolean result = User.checkName("1235jZ");
        assertFalse(result);
    }

    @Test
    public void testCheckPassword(){
        boolean result = User.checkPassword("aaaa");
        assertFalse(result);
    }



}
