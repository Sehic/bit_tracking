package ControllerTest;

import controllers.routes;
import org.junit.Test;
import play.mvc.Result;

import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.*;
/**
 * Created by emina on 16.9.2015.
 */
public class UserControllerTest {


    /**
     * Test editProfile() redirect route
     */
    @Test
    public void testEditProfile(){
        running(fakeApplication(),()->{
            Result result = route(routes.UserController.editProfile(1L));
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/");
        });
    }

    /**
     * Test registrationCheck() redirect route
     */
    @Test
    public void testRegistrationCheck(){
        running(fakeApplication(),()->{
            Result result = route(routes.UserController.registrationCheck());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/login");
        });
    }


    /**
     * Test RegistrationCheck redirect route
     */
  /*  @Test
    public void testRegistration(){
        running(fakeApplication(),()-> {
            Http.RequestBuilder rb = new Http.RequestBuilder().method(GET).uri("/register").bodyForm(ImmutableMap.of(
                    "firstName", "Boki",
                    "lastName", "Tomic",
                    "email","boki@ba.ba",
                    "password","boki123!",
                    "repassword", "boki123!"
            ));
            Result result = route(rb);
            assertThat(result.status()).isEqualTo(SEE_OTHER);
            assertThat(result.redirectLocation()).isEqualTo("/login");
           // assertThat(result.flash().get("success")).contains("l");
        });
    }
  /*  @Test
    public void testAddNewOffice(){
        running(fakeApplication(),()->{
            Result result = route(routes.PostOfficeController.addNewOffice());
            assertThat(result.status()).isEqualTo(SEE_OTHER);
        });
    }*/
}
