package helpers;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;

/**
 * Created by Mladen13 on 5.10.2015.
 */
public class MailHelper {

    public static void sendContactMessage(String name, String email, String message) {
        if (message != null) {

            try {
                HtmlEmail mail = new HtmlEmail();
                mail.setSubject("Contact BitTracking");
                mail.setFrom("bittracking2015@gmail.com");
                mail.addTo("bittracking2015@gmail.com");
                mail.addTo(email);
                mail.setMsg(message);
                mail.setHtmlMsg(String
                        .format("<html><body><strong> %s </strong>: <h4>%s:</h4><p> %s </p> </body></html>",
                                email, name, message));
                mail.setHostName("smtp.gmail.com");
                mail.setStartTLSEnabled(true);
                mail.setSSLOnConnect(true);
                mail.setAuthenticator(new DefaultAuthenticator(
                        Play.application().configuration().getString("mailFromUsername"),
                        Play.application().configuration().getString("mailFromPassword")
                ));
                mail.send();
            } catch (EmailException e) {

                e.printStackTrace();
            }

        }
    }

}
