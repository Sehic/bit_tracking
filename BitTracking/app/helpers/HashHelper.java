package helpers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Mladen13 on 9.10.2015.
 */
public class HashHelper {

    /**
     * This method is used for password encryption.
     *
     * @param password - that would be inserted into database
     * @return - encrypted password
     */
    public static String getEncriptedPasswordMD5(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes(), 0, password.length());
            String result = new BigInteger(1, md5.digest()).toString(16);
            md5.reset();
            return result;
        } catch (NoSuchAlgorithmException e) {
            // TODO add to logger
        }
        return "INVALID PASSWORD";
    }

}
