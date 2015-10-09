package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.MailHelper;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.contact;

/**
 * Created by Mladen13 on 9.10.2015.
 */
public class MailController extends Controller {

    public Result contact() {

        return ok(contact.render(new Form<Contact>(Contact.class)));
    }

    public F.Promise<Result> sendMail() {

        //Getting recaptcha values
        final DynamicForm temp = DynamicForm.form().bindFromRequest();

        F.Promise<Result> promiseHolder = WS
                .url("https://www.google.com/recaptcha/api/siteverify")
                .setContentType("application/x-www-form-urlencoded")
                .post(String.format(
                        "secret=%s&response=%s",
                        //getting API key from the config file
                        Play.application().configuration()
                                .getString("recaptchaKey"),
                        temp.get("g-recaptcha-response")))
                .map(new F.Function<WSResponse, Result>() {
                    public Result apply(WSResponse response) {

                        JsonNode json = response.asJson();
                        Form<Contact> contactForm = Form.form(Contact.class)
                                .bindFromRequest();

                        if (json.findValue("success").asBoolean() == true
                                && !contactForm.hasErrors()) {
                            Contact newMessage = contactForm.get();
                            String name = newMessage.name;
                            String email = newMessage.email;
                            String message = newMessage.message;

                            if (message.equals("")) {
                                flash("messageError", "Please fill message field!");
                                return redirect("/contact");
                            }
                            flash("success", "Message was sent successfuly!");
                            MailHelper.sendContactMessage(name, email, message);

                            return redirect("/contact");
                        } else {
                            flash("errorMail", "Please verify that you are not a robot!");
                            return ok(contact.render(contactForm));
                        }
                    }
                });

        return promiseHolder;
    }

    /**
     * Inner class that is used for sending mail from user to bittracking
     */
    public static class Contact {

        public String name;
        public String email;
        public String message;

        /**
         * Default constructor that sets everything to NN;
         */
        public Contact() {
            this.name= "NN";
            this.email = "NN";
            this.message = "NN";
        }
        /**
         * Constructor with parameters;
         * @param name
         * @param email
         * @param message
         */
        public Contact(String name, String email, String message) {
            super();
            this.name = name;
            this.email = email;
            this.message = message;
        }
    }

}
