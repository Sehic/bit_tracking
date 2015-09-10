package controllers;

import helpers.SessionHelper;
import models.PostOffice;
import models.User;
import models.UserType;
import play.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import com.avaje.ebean.Ebean;
import play.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class Application extends Controller {

    public Result index() {
        return ok(index.render());
    }

    public Result login() {
        return ok(login.render(""));
    }

    public Result logout() {
        session().clear();
        return redirect(routes.Application.login());
    }

    public Result register() {
        return ok(register.render());
    }


    public Result adminPanel() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }
        return ok(adminindex.render(User.find.findList()));
    }

    public Result adminMaps() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        return ok(adminmaps.render());
    }

    public Result adminTables() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        return ok(admintables.render(User.find.findList()));
    }

    public Result officeWorkersList() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        return ok(officeworkerlist.render(User.findOfficeWorkers()));
    }

    public Result adminPostOffice() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        List<PostOffice> list = PostOffice.findOffice.findList();
        return ok(adminpostoffice.render(list));
    }

    public Result addPostOffice() {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }


        return ok(postofficeadd.render());
    }

    public Result registerOfficeWorker() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        return ok(officeworkeradd.render(postOffices));
    }


}
