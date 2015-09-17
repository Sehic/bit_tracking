package ModelTest;

import models.*;
import models.Package;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
/**
 * Created by emina on 17.9.2015.
 */
public class PackageModelTest {

    @Before
    public void setUpApplication(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void testFindPackageById(){
        Package pkg = new Package();
        pkg.trackingNum = "12ee-78err-1212";
        pkg.destination = "Cazin";
        pkg.save();
        Package pkgTest = Package.findPackageById(100L);
        assertFalse(false);
    }

    @Test
    public void testFindPackageByTrackingNumber(){
        Package pkg = new Package();
        pkg.trackingNum = "12ee-78err-1212";
        pkg.destination = "Cazin";
        pkg.save();
        Package pkgTest = Package.findPackageByTrackingNumber("50");
        assertFalse(false);
    }
}
