package ModelTest;

import models.Location;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
/**
 * Created by emina on 17.9.2015.
 */
public class LocationModelTest {

    @Before
    public void setUpApplication(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void testFindById(){
        Location location = new Location(44.816667, 15.866667);
        location.save();
        Location locationTest = Location.findLocationById(1L);
        assertNotNull(locationTest);
    }
}
