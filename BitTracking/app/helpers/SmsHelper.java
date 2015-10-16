package helpers;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import play.Logger;
import play.Play;
import views.html.index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 16.10.2015.
 */
public class SmsHelper {

    public static void sendSms(String smsBody, String smsTo) {

        String account = Play.application().configuration().getString("account_sid");
        String token = Play.application().configuration().getString("auth_token");

        TwilioRestClient client = new TwilioRestClient(account, token);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Body", smsBody));
        params.add(new BasicNameValuePair("To", smsTo));
        params.add(new BasicNameValuePair("From", "+12054097706"));

        SmsFactory smsFactory = client.getAccount().getSmsFactory();
        Sms sms = null;
        try {
            sms = smsFactory.create(params);
            Logger.info(sms.getTo());
            Logger.info("Success");
        } catch (TwilioRestException e) {
            e.printStackTrace();
        }
    }
}
