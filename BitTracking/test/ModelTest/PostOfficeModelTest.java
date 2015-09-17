package ModelTest;

import models.PostOffice;
import org.junit.Before;
import org.junit.Test;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

import static org.junit.Assert.*;
/**
 * Created by USER on 17.9.2015.
 */
public class PostOfficeModelTest {

    @Before
    public void setUpApplication(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void testFindOffice(){
        PostOffice po = new PostOffice();
        po.name = "Post Office Sarajevo";
        po.address = "Branka Mikulica";
        po.save();
        assertTrue(true);
    }

    @Test
    public void testFindPostOfficeById(){
        PostOffice po = PostOffice.findPostOffice(800L);
        assertFalse(false);
    }

}
