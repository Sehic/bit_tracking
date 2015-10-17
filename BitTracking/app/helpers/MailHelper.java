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

    public static void sendConfirmation(String subject, String message, String email) {

        try {
            HtmlEmail mail = new HtmlEmail();
            mail.setSubject(subject);
            mail.setFrom("bittracking2015@gmail.com");
            mail.addTo("bittracking2015@gmail.com");
            mail.addTo(email);
            mail.setMsg(message);
            mail.setHtmlMsg(String
                    .format("<html><body><strong> %s </strong> <p> %s </p> </body></html>",
                            subject, message));
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

    public static void sendPhoneValidationCode (String code, String userEmail) {
        String subject = "Phone Validation Code";
        MailHelper.sendConfirmation(subject, code, userEmail);
    }

    public static void sendVerificationMail(String token, String userLastName, String userEmail) {
        String subject = "Email Validation - BitTracking";
        String address = "http://localhost:9000/validate/" + token;
        String message = "Mr/Mrs. " + userLastName + ",<br><br>" +
                "Thank you for joining BitTracking community.<br>" +
                "To complete your registration, please follow the link bellow: <br>" +
                "<u><a href=\"" + address + "\">" + address + "</a></u><br><br>" +
                "<i>BitTracking Team</i>";
        MailHelper.sendConfirmation(subject, message, userEmail);
    }

    public static void packageDeliveredNotification(String userLastName, String trackingNum, String userEmail) {
        String subject = "BitTracking Notification!";
        String message = "Mr/Mrs. " + userLastName + ",<br><br>" +
                "Package with tracking number <strong>" + trackingNum + "</strong> has been successifuly delivered.<br>" +
                "Thank you for choosing BitTracking!<br><br>" +
                "<i>BitTracking Team!</i>";
        MailHelper.sendConfirmation(subject, message, userEmail);
    }

    public static void requestReceivedNotification(String userLastName, String userEmail) {
        String subject = "Thank You For Choosing BitTracking For Your Delivery Services";
        String message = "Mr/Mrs. " + userLastName + ",<br><br>" +
                "Your request for delivery services has been received.<br>" +
                "It will be processed as soon as possible,<br>" +
                "and you will be notified.<br><br>" +
                "<i>BitTracking Team</i>";
        MailHelper.sendConfirmation(subject, message, userEmail);
    }

    public static void approvedRequestNotification(String userLastName, String trackingNum, String userEmail) {
        String subject = "BitTracking Notification!";
        String message = "Mr/Mrs. " + userLastName + ",<br><br>" +
                "Your Latest Request For Delivery Service Has Been <strong>Approved</strong>.<br>" +
                "You can get status information of delivery by any time using this tracking number:<br><br> " + trackingNum + "<br><br>" +
                "As soon as the package is delivered, you will be notified.<br><br>" +
                "<i>BitTracking Team!</i>";
        MailHelper.sendConfirmation(subject, message, userEmail);
    }

    public static void rejectedRequestNotification(String userLastName, String userEmail) {
        String subject = "BitTracking Notification!";
        String message = "Mr/Mrs. " + userLastName + ",<br><br>" +
                "Your Latest Request For Delivery Service Has Been <strong>Rejected</strong>.<br>" +
                "For more information, contact us.<br><br>" +
                "<i>BitTracking Team!</i>";
        MailHelper.sendConfirmation(subject, message, userEmail);
    }

}
