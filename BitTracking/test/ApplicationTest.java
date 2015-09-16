import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import controllers.PostOfficeController;
import models.PostOffice;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Before
    public void configureDatabase() {
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void addOfficecheck(){
        PostOffice office = new PostOffice();
        office.id= new Long(1);
        office.name= "Office1";
        office.address="Adresa 1";

        office.save();
    }

    @Test
    public void testSavingAndLoading(){
        PostOffice office = new PostOffice();
        office.id= new Long(1);
        office.name= "Office1";
        office.address="Adresa 1";

        office.save();

        PostOffice o = PostOffice.findPostOffice(office.id);
        assertNotNull(o);
    }

    @Test
    public void testNonExistancePackage(){
        Package p = models.Package.findPackageById(new Long(2));
        assertNotNull(p);
    }

   @Test
    public void testInsert(){
       Http.RequestBuilder rb = new Http.RequestBuilder().method(POST).uri("/products").bodyForm(ImmutableMap.of(
       ));

       Result result = route (rb);
       assertThat(result.status()).isEqualTo(SEE_OTHER);
       assertThat(result.redirectLocation()).isEqualTo("/products");
       assertThat(result.flash().get("success")).contains("Sussc added");
   }

}
