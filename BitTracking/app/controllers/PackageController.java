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
        if (user.typeOfUser!=UserType.ADMIN) {
            return redirect("/");
        }
        String id = form.bindFromRequest().field("officePost").value();
        PostOffice po = PostOffice.findPostOffice(Long.parseLong(id));
        Package p = new Package();
        p.postOffice = po;
        p.destination = form.get("destination");
        p.save();
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
        Package p = Package.findPackageById(id);
        String officeid = form.bindFromRequest().field("officePost").value();
        PostOffice po = PostOffice.findPostOffice(Long.parseLong(officeid));
        p.postOffice = po;
        p.destination = form.get("destination");
        p.update();
        return ok(adminpackage.render(Package.finder.findList()));
    }

    public Result editPackage(Long id) {
        Package p = Package.findPackageById(id);
        return ok(packagedetails.render(p, PostOffice.findOffice.findList()));
    }
}
