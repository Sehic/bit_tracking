package controllers;

import com.avaje.ebean.Ebean;
import helpers.SessionHelper;
import models.PostOffice;
import models.User;
import models.UserType;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import models.Package;


import java.util.List;

/**
 * Created by USER on 9.9.2015.
 */
public class PackageController extends Controller {

    public Result adminPackage() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect(routes.Application.index());
        }

        return ok(adminpackage.render(Package.finder.findList()));
    }

    public Result addPackage() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect(routes.Application.index());
        }

        return ok(packageadd.render(PostOffice.findOffice.findList()));
    }

    public Result savePackage() {
        DynamicForm form = Form.form().bindFromRequest();
        User user = SessionHelper.getCurrentUser(ctx());
        if (user ==null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect("/");
        }
        String id = form.bindFromRequest().field("officePost").value();
        PostOffice office = PostOffice.findPostOffice(Long.parseLong(id));
        Package pack = new Package();
        pack.postOffice = office;
        pack.destination = form.get("destination");
        Ebean.save(pack);
        return ok(adminpackage.render(Package.finder.findList()));
    }

    public Result deletePackage(Long id) {
        Package p = Package.findPackageById(id);
        User user = SessionHelper.getCurrentUser(ctx());
        if (user !=null || user.typeOfUser==UserType.OFFICE_WORKER || user.typeOfUser == UserType.ADMIN) {
            Ebean.delete(p);
            return ok(packageadd.render(PostOffice.findOffice.findList()));
        } else {
            return redirect("/");
        }
    }

    public Result updatePackage(Long id) {
        DynamicForm form = Form.form().bindFromRequest();
        User user = SessionHelper.getCurrentUser(ctx());
        if (user ==null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect("/");
        }
        Package pack = Package.findPackageById(id);
        String officeid = form.bindFromRequest().field("officePost").value();
        PostOffice office = PostOffice.findPostOffice(Long.parseLong(officeid));
        pack.postOffice = office;
        pack.destination = form.get("destination");
        Ebean.update(pack);
        return ok(adminpackage.render(Package.finder.findList()));
    }

    public Result editPackage(Long id) {

        User user = SessionHelper.getCurrentUser(ctx());
        if (user ==null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect("/");
        }

        return ok(packagedetails.render(Package.findPackageById(id), PostOffice.findOffice.findList()));
    }
}
