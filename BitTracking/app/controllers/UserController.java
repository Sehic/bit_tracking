package controllers;

import com.avaje.ebean.Ebean;
import helpers.Authenticators;
import helpers.CurrentUser;
import helpers.HashHelper;
import helpers.SessionHelper;
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
        //Getting values from form in (login.scala.html)
        String password = newUser.bindFromRequest().field("password").value();
        String email = newUser.bindFromRequest().field("email").value();
        String newPassword = HashHelper.getEncriptedPasswordMD5(password);

        User u = User.findEmailAndPassword(email, newPassword);

        if (u != null) {
            session("email", email);
            return redirect(routes.Application.index());
        }
        return badRequest(login.render("Wrong email or password!", newUser.bindFromRequest()));

    }

    /**
     * This method get user input from registration form and if every input is valid
     * saves it to database.
     *
     * @return redirect user to subpage login if everything is ok, otherwise ?????
     */
    public Result registrationCheck() {

        Form<User> boundForm = newUser.bindFromRequest();
        //Creating user using form values (register.scala.html)
        User userFromForm = new User();
        try {
            userFromForm = boundForm.get();
        }catch(Exception e){
            return badRequest(register.render(boundForm));
        }
        //Getting password confirmation field on this way because it is not attribute
        String repassword = boundForm.bindFromRequest().field("repassword").value();
        //Creating new user where we will check if mail exists in database
        User u = User.checkEmail(userFromForm.email);
        //If user is not found, that means we can proceed creating new user
        if (u != null) {
            flash("errorEmail", "E-mail address already exists!");
            return badRequest(register.render(boundForm));
        }

        u = new User(userFromForm.firstName, userFromForm.lastName, userFromForm.password, userFromForm.email);

        if (!u.checkName(u.firstName)) {
            flash("errorName", "Your name should have only letters.");
            return badRequest(register.render(boundForm));
        }

        if (!u.checkName(u.lastName)) {
            flash("errorLastName", "Your last name should have only letters.");
            return badRequest(register.render(boundForm));
        }

        if (!u.checkPassword(u.password)) {
            flash("errorPassword", "Couldn't accept password. Your password should contain at least 6 characters and one number");
            return badRequest(register.render(boundForm));
        }

        if (!u.password.equals(repassword)) {
            flash("errorTwoPasswords", "You entered different passwords");
            return badRequest(register.render(boundForm));
        }

        String newPassword = HashHelper.getEncriptedPasswordMD5(u.password);

        u.firstName = u.firstName.substring(0, 1).toUpperCase() + u.firstName.substring(1);
        u.lastName = u.lastName.substring(0, 1).toUpperCase() + u.lastName.substring(1);

        u = new User(u.firstName, u.lastName, newPassword, u.email);

        u.save();

        if (u.id == 1) {
            u.typeOfUser = UserType.ADMIN;
            u.update();
        }

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
        return ok(editprofile.render(user, path));
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

        Form<User> filledForm = newUser.bindFromRequest();

        user.firstName = filledForm.field("firstName").value();
        user.lastName = filledForm.field("lastName").value();
        user.password = filledForm.field("password").value();

        String repassword = filledForm.field("repassword").value();
        ImagePath path = ImagePath.findByUser(user);
        if (!User.checkName(user.firstName)) {
            flash("errorName", "Your name should have only letters.");
            return badRequest(editprofile.render(user, path));
        }

        if (!User.checkName(user.lastName)) {
            flash("errorLastName", "Your last name should have only letters.");
            return badRequest(editprofile.render(user, path));
        }

        if (!user.password.equals(repassword)) {

            return badRequest(editprofile.render(user, path));
        }
            user.password = HashHelper.getEncriptedPasswordMD5(user.password);
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

        if (userType.equals("Admin")) {

            user.typeOfUser = UserType.ADMIN;

        } else if (userType.equals("Office Worker")) {

            user.typeOfUser = UserType.OFFICE_WORKER;
            user.postOffice = PostOffice.findOffice.where().eq("name", postOffice).findUnique();

        } else if (userType.equals("Delivery Worker")) {

            user.typeOfUser = UserType.DELIVERY_WORKER;
            user.postOffice = PostOffice.findOffice.where().eq("name", postOffice).findUnique();

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

}
