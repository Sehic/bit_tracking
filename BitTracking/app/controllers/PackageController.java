package controllers;

import helpers.SessionHelper;
import models.PostOffice;
import models.User;
import models.UserType;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.adminpackage;
import views.html.packageadd;
import models.Package;
import views.html.packagedetails;

import java.util.List;

/**
 * Created by USER on 9.9.2015.
 */
public class PackageController extends Controller {

    public Result adminPackage(){
        return ok(adminpackage.render(Package.finder.findList()));
    }

    public Result addPackage(){
        return ok(packageadd.render(PostOffice.findOffice.findList()));
    }

    public Result savePackage(){
        DynamicForm form = Form.form().bindFromRequest();
        User user = SessionHelper.getCurrentUser(ctx());
        if (user.typeOfUser!=UserType.ADMIN || user.typeOfUser!=UserType.OFFICE_WORKER) {
            return redirect("/");
        }
        String id = form.bindFromRequest().field("officePost").value();
        PostOffice office = PostOffice.findPostOffice(Long.parseLong(id));
        Package pack = new Package();
        pack.postOffice = office;
        pack.destination = form.get("destination");
        pack.save();
        return ok(adminpackage.render(Package.finder.findList()));
    }

    public Result deletePackage(Long id){
        Package p = Package.findPackageById(id);
        User user = SessionHelper.getCurrentUser(ctx());
        if(user.typeOfUser==UserType.ADMIN) {
            p.delete();
            return ok(packageadd.render(PostOffice.findOffice.findList()));
        } else {
            return redirect("/");
        }
    }

    public Result updatePackage(Long id) {
        DynamicForm form = Form.form().bindFromRequest();
        User user = SessionHelper.getCurrentUser(ctx());
        if (user.typeOfUser!=UserType.ADMIN || user.typeOfUser!=UserType.OFFICE_WORKER) {
            return redirect("/");
        }
        Package pack = Package.findPackageById(id);
        String officeid = form.bindFromRequest().field("officePost").value();
        PostOffice office = PostOffice.findPostOffice(Long.parseLong(officeid));
        pack.postOffice = office;
        pack.destination = form.get("destination");
        pack.update();
        return ok(adminpackage.render(Package.finder.findList()));
    }

    public Result editPackage(Long id) {
        return ok(packagedetails.render(Package.findPackageById(id), PostOffice.findOffice.findList()));
    }
}
