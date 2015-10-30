package controllers;

import com.avaje.ebean.Ebean;
import helpers.*;
import models.*;
import models.Package;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by mladen.teofilovic on 04/09/15.
 * This class controls user registration and login functions.
 */

public class UserController extends Controller {

    private static final Form<User> newUser = new Form<User>(User.class);

    /**
     * This method is used for checking if user inserted data is valid.
     *
     * @return - positive message if true, else negative message
     */
    public Result loginCheck() {

        if (!request().accepts("text/html")) {
            return ApiUserController.login();
        }
        //Getting values from form in (login.scala.html)
        String password = newUser.bindFromRequest().field("password").value();
        String email = newUser.bindFromRequest().field("email").value();
        String newPassword = HashHelper.getEncriptedPasswordMD5(password);

        User u = User.findEmailAndPassword(email, newPassword);

        if (u == null) {
            return badRequest(login.render("Wrong email or password!", newUser.bindFromRequest()));
        }

        if (u.validated == false) {
            return badRequest(login.render("Please check your email and validate this account!", newUser.bindFromRequest()));
        }

        session("email", email);

        if(u.token != null) {
            u.token = null;
            u.update();
        }

        if (u.phoneNumber != null && !u.numberValidated){
            return ok(validatephone.render());
        }

        return redirect(routes.Application.index());
    }

    /**
     * This method get user input from registration form and if every input is valid
     * saves it to database.
     *
     * @return redirect user to subpage login if everything is ok, otherwise ?????
     */
    public Result registrationCheck() {

        if (!request().accepts("text/html")) {
            return ApiUserController.registration();
        }

        Form<User> boundForm = newUser.bindFromRequest();
        //Creating user using form values (register.scala.html)
        User userFromForm = new User();
        List<Country> countryList = Country.findCountry.findList();
        try {
            userFromForm = boundForm.get();
        }catch(Exception e){
            return badRequest(register.render(boundForm, countryList));
        }
        //Getting password confirmation field on this way because it is not attribute
        String repassword = boundForm.bindFromRequest().field("repassword").value();
        //Creating new user where we will check if mail exists in database
        User u = User.checkEmail(userFromForm.email);
        //If user is not found, that means we can proceed creating new user
        if (u != null) {
            flash("errorEmail", "E-mail address already exists!");
            return badRequest(register.render(boundForm, countryList));
        }

        Country country = Country.findCountryByCallingCode(boundForm.bindFromRequest().field("userCountry").value());
        String phoneNumber = boundForm.bindFromRequest().field("phoneNumber").value();
        Random rand = new Random();
        String validationCode = rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9);

        u = new User(userFromForm.firstName, userFromForm.lastName, userFromForm.password, userFromForm.email);

        if (!u.checkName(u.firstName)) {
            flash("errorName", "Your name should have only letters.");
            return badRequest(register.render(boundForm, countryList));
        }

        if (!u.checkName(u.lastName)) {
            flash("errorLastName", "Your last name should have only letters.");
            return badRequest(register.render(boundForm, countryList));
        }

        if (country == null) {
            flash("errorCountry", "Please select your country!");
            return badRequest(register.render(boundForm, countryList));
        }

        if (!"".equals(phoneNumber) && !User.checkPhoneNumber(phoneNumber)) {
            flash("errorPhoneNumber", "Your phone number should have only numbers, and minimum length is 8 digits.");
            return badRequest(register.render(boundForm, countryList));
        }

        if (!u.checkPassword(u.password)) {
            flash("errorPassword", "Couldn't accept password. Your password should contain at least 6 characters and one number");
            return badRequest(register.render(boundForm, countryList));
        }

        if (!u.password.equals(repassword)) {
            flash("errorTwoPasswords", "You entered different passwords");
            return badRequest(register.render(boundForm, countryList));
        }

        String newPassword = HashHelper.getEncriptedPasswordMD5(u.password);

        u.firstName = u.firstName.substring(0, 1).toUpperCase() + u.firstName.substring(1);
        u.lastName = u.lastName.substring(0, 1).toUpperCase() + u.lastName.substring(1);

        u = new User(u.firstName, u.lastName, newPassword, u.email);
        u.token = UUID.randomUUID().toString();

        u.country = country;
        if (!"".equals(phoneNumber)) {
            if (phoneNumber.charAt(0) == '0') {
                phoneNumber = phoneNumber.substring(1, phoneNumber.length());
            }
            u.phoneNumber = "+" + u.country.callingCode + phoneNumber;
            u.validationCode = validationCode;
            String smsBody = "Validation code: " + u.validationCode;
            SmsHelper.sendSms(smsBody, u.phoneNumber);
            /**
             * Due to limitations caused by trial version of Twilio, we can send only 5 SMS messages per day.
             * That's why we use MailHelper in this testing period.
             */
            MailHelper.sendPhoneValidationCode(u.validationCode, u.email);
        }

        u.save();

        if (u.id == 1) {
            u.typeOfUser = UserType.ADMIN;
            u.validated = true;
            u.token = null;
            u.update();
        }
        MailHelper.sendVerificationMail(u.token, u.lastName, u.email);
        return redirect(routes.Application.login());
    }

    /**
     * This method is used for editing user profile
     *
     * @param id - user id
     * @return - ok  if everything goes fine, redirect to index page if user is not part of session.
     */
    public Result editProfile(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        User user = User.findById(id);

        ImagePath path = ImagePath.findByUser(u1);

        if (u1 == null || user == null || u1.id != user.id) {
            return redirect(routes.Application.index());
        }
        return ok(editprofile.render(user, path, Country.findCountry.findList()));
    }

    /**
     * This method is used for uploading picture when clicking on button
     *
     * @return
     */
    public Result uploadPicture() {

        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null) {
            return redirect(routes.Application.index());
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("picture");

        if (picture != null) {
            String fileName = picture.getFilename();
            File file = picture.getFile();
            try {
                FileUtils.moveFile(file, new File("./public/images/" + fileName));
                ImagePath path = ImagePath.findByUser(user);
                if (path == null) {
                    path = new ImagePath();
                    path.image_url = "/assets/images/" + fileName;
                    path.profilePhoto = user;
                    path.save();
                    user.imagePath = path;
                    user.update();
                } else {
                    String deletePic = "./public/images/" + path.image_url.split("/", 4)[3];
                    Logger.info(deletePic);
                    path.image_url = "/assets/images/" + fileName;
                    path.update();
                    FileUtils.deleteQuietly(new File(deletePic));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return redirect("/mybt/editprofile/" + user.id);
        } else {
            return redirect(routes.Application.index());
        }
    }

    /**
     * Method that updates user firstname, lastname and password
     *
     * @param id - user on that id
     * @return
     */
    public Result updateUser(Long id) {
        User u1 = SessionHelper.getCurrentUser(ctx());
        User user = User.findById(id);
        if (u1 == null || user == null || u1.id != user.id) {
            return redirect(routes.Application.index());
        }

        DynamicForm form = Form.form().bindFromRequest();
        String firstName = form.get("firstName");
        String lastName = form.get("lastName");
        String countryCode = form.get("userCountry");
        String phoneNumber = form.get("phoneNumber");

        ImagePath path = ImagePath.findByUser(user);
        List<Country> countryList = Country.findCountry.findList();

        if (!User.checkName(firstName)) {
            flash("errorName", "Your name should have only letters.");
            return badRequest(editprofile.render(user, path, countryList));
        }

        if (!User.checkName(lastName)) {
            flash("errorLastName", "Your last name should have only letters.");
            return badRequest(editprofile.render(user, path, countryList));
        }

        if (countryCode != null && phoneNumber != null) {
            Country country = null;
            if (!"".equals(countryCode)) {
                country = Country.findCountryByCallingCode(countryCode);
            }

            if (!"".equals(phoneNumber) && !User.checkPhoneNumber(phoneNumber)) {
                flash("errorPhoneNumber", "Your phone number should have only numbers, and minimum length is 8 digits.");
                return badRequest(editprofile.render(user, path, countryList));
            }

            String editedPhoneNumber = "";

            if (!"".equals(phoneNumber)) {
                if (phoneNumber.charAt(0) == '0') {
                    phoneNumber = phoneNumber.substring(1, phoneNumber.length());
                }
                editedPhoneNumber = "+" + countryCode + phoneNumber;
            }

            if ((user.phoneNumber == null || !phoneNumber.equals(user.phoneNumber)) && User.checkPhoneNumber(phoneNumber)) {
                Random rand = new Random();
                String validationCode = rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9);
                user.phoneNumber = editedPhoneNumber;
                user.numberValidated = false;
                user.validationCode = validationCode;
                String smsBody = "Validation code: " + user.validationCode;
                //SmsHelper.sendSms(smsBody, u.phoneNumber);
                /**
                 * Due to limitations caused by trial version of Twilio, we can send only 5 SMS messages per day.
                 * That's why we use MailHelper in this testing period.
                 */
                MailHelper.sendPhoneValidationCode(user.validationCode, user.email);
            }

            if (country != null) {
                user.country = country;
            }
        }

        user.firstName = firstName;
        user.lastName = lastName;
        user.update();

        return redirect(routes.UserController.userProfile(user.id));
    }

    /**
     * Method that deletes user from database
     *
     * @param id - user id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteUser(Long id) {

        User user = User.findById(id);
        if (user == null || user.typeOfUser == UserType.ADMIN) {
            return redirect(routes.Application.index());
        }
        user.delete();
        return redirect(routes.Application.adminTables());
    }

    /**
     * Method that opens up admin profile (adminpreferences.scala.html)
     *
     * @param id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result adminPreferences(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());

        User user = User.findById(id);
        if (user == null || u1.id != user.id) {
            return redirect(routes.Application.adminPanel());
        }

        return ok(adminpreferences.render(user));
    }

    /**
     * Method that opens up user profile (userprofile.scala.html)
     *
     * @param id - edited user id
     * @return
     */
    public Result userProfile(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        User user = User.findById(id);

        if (user == null) {
            return redirect(routes.Application.index());
        }

        if (u1.id != user.id && u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        ImagePath path = ImagePath.findByUser(user);

        return ok(userprofile.render(user, PostOffice.findOffice.findList(), path, u1));
    }

    /**
     * Method that is used for updating type of user (userprofile.scala.html)
     *
     * @param id - edited user id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result updateUserType(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        User user = User.findById(id);

        Form<User> boundForm = newUser.bindFromRequest();
        //Getting values from combo boxes
        String userType = boundForm.bindFromRequest().field("userType").value();
        String postOffice = boundForm.bindFromRequest().field("postOffice").value();
        String drivingPostOffice = boundForm.field("drivingPostOffice").value();

        if (userType.equals("Admin")) {

            user.typeOfUser = UserType.ADMIN;

        } else if (userType.equals("Office Worker")) {

            user.typeOfUser = UserType.OFFICE_WORKER;
            user.postOffice = PostOffice.findOffice.where().eq("name", postOffice).findUnique();

        } else if (userType.equals("Delivery Worker")) {

            user.typeOfUser = UserType.DELIVERY_WORKER;
            user.postOffice = PostOffice.findOffice.where().eq("name", postOffice).findUnique();
            user.drivingOffice = drivingPostOffice;

        } else if(userType.equals("Registered User")) {
            user.typeOfUser = UserType.REGISTERED_USER;
            user.postOffice = null;
        }
        user.update();

        return redirect(routes.Application.adminPanel());
    }

    public Result findEmail() {
        DynamicForm form = Form.form().bindFromRequest();
        String email = form.data().get("email");
        User user = User.checkEmail(email);
        if (user != null) {
            return badRequest();
        }
        return ok();
    }

    public Result emailValidation(String token) {
        User user = User.findByToken(token);
        if (user == null || token == null) {
            return redirect("/");
        }
        user.token = null;
        user.validated = true;
        user.update();
        return redirect("/login");
    }

    public Result validatePhone() {
        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null || user.numberValidated) {
            return redirect("/");
        }
        return ok(validatephone.render());
    }

    public Result validatePhoneNumber() {
        DynamicForm form = Form.form().bindFromRequest();
        String code = form.data().get("enteredCode");
        User user = User.findByValidationCode(code);
        if (user == null) {
            return badRequest();
        }
        user.numberValidated = true;
        user.validationCode = null;
        user.update();
        return ok(user.phoneNumber);
    }

    public Result newCode() {
        User user = SessionHelper.getCurrentUser(ctx());
        DynamicForm form = Form.form().bindFromRequest();
        String number = form.data().get("newNumber");
        if(number.charAt(0) == '+') {
            number = number.substring(1, number.length());
        }
        if(!User.checkPhoneNumber(number)) {
            return badRequest();
        }
        String fixNumber = "+" + number;
        Random rand = new Random();
        String validationCode = rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9) + "" + rand.nextInt(9);
        user.phoneNumber = fixNumber;
        user.validationCode = validationCode;
        user.update();
        //SmsHelper.sendSms("Validation code: ", fixNumber);
        /**
         * Due to limitations caused by trial version of Twilio, we can send only 5 SMS messages per day.
         * That's why we use MailHelper in this testing period.
         */
        MailHelper.sendPhoneValidationCode(user.validationCode, user.email);
        return ok();
    }

    public Result sendPassword() {
        DynamicForm form = Form.form().bindFromRequest();
        String email = form.data().get("email");
        User user = User.findUserByEmail(email);
        if (user == null) {
            return badRequest();
        }
        String uuid = UUID.randomUUID().toString();
        user.token = uuid;
        user.update();
        String address = "http://localhost:9000/changepassword/" + uuid;
        String subject = "Change password";
        String message = "To change your password, please follow the link bellow <br> <a href=\"" + address + "\">" + address + "</a>";
        MailHelper.sendConfirmation(subject, message, email);
        return ok();
    }

    public Result userChangePassword() {
        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null) {
            return badRequest(index.render());
        }
        return ok(changepassword.render(user));
    }

    public Result changePassword(String token) {
        User activeUser = SessionHelper.getCurrentUser(ctx());
        if (activeUser != null) {
            activeUser.token = null;
            activeUser.update();
            return badRequest(index.render());
        }
        User user = User.findByToken(token);
        if(user == null) {
            return badRequest(index.render());
        }
        return ok(changepassword.render(user));
    }

    public Result makePasswordChange() {
        DynamicForm form = Form.form().bindFromRequest();
        String userId = form.data().get("userId");
        String oldPassword = form.data().get("oldPass");
        String newPassword = form.data().get("newPass");
        String reNewPassword = form.data().get("reNewPass");

        User activeUser = SessionHelper.getCurrentUser(ctx());
        if (activeUser != null) {
            if (oldPassword == "") {
                return badRequest("wrongoldpassword");
            }
            String hashedOldPassword = HashHelper.getEncriptedPasswordMD5(oldPassword);
            if (!activeUser.password.equals(hashedOldPassword)) {
                return badRequest("wrongoldpassword2");
            }
            if (!newPassword.equals(reNewPassword)) {
                return badRequest("passwordsdontmatch");
            }
            if(!User.checkPassword(newPassword)) {
                return badRequest("errorpassword");
            }
            activeUser.password = HashHelper.getEncriptedPasswordMD5(newPassword);
            activeUser.update();
            return ok();
        }

        User user = User.findById(Long.parseLong(userId));
        if (user != null && user.token != null) {
            if (!User.checkPassword(newPassword)) {
                return badRequest("errorpassword");
            }
            if(!newPassword.equals(reNewPassword)) {
                return badRequest("passwordsdontmatch");
            }
            user.password = HashHelper.getEncriptedPasswordMD5(newPassword);
            user.token = null;
            user.update();
            return ok();
        }
        return badRequest();
    }


}
