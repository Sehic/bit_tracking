package controllers;

import com.avaje.ebean.Ebean;
import helpers.SessionHelper;
import models.PostOffice;
import models.User;
import models.UserType;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import models.Package;


import java.util.List;
import java.util.UUID;

/**
 * Created by USER on 9.9.2015.
 */
public class PackageController extends Controller {

    /**
     * Method that shows up list of all packages in post office
     *
     * @return
     */
    public Result adminPackage() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect(routes.Application.index());
        }

        return ok(adminpackage.render(Package.finder.findList()));
    }

    /**
     * Method that opens up add package form (packageadd.scala.html)
     *
     * @return
     */
    public Result addPackage() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect(routes.Application.index());
        }

        List<PostOffice> offices = PostOffice.findOffice.findList();
        if (offices == null || offices.size() == 0) {
            return ok(postofficeadd.render());
        }

        return ok(packageadd.render(PostOffice.findOffice.findList()));
    }

    /**
     * Method that saves package to database using (packageadd.scala.html) form
     * @return
     */
    public Result savePackage() {

        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect("/");
        }
        DynamicForm form = Form.form().bindFromRequest();

        String id = form.bindFromRequest().field("officePost").value();

        PostOffice office = PostOffice.findPostOffice(Long.parseLong(id));

        Package pack = new Package();
        pack.postOffice = office;
        pack.destination = form.get("destination");
        pack.trackingNum = (UUID.randomUUID().toString());


        Ebean.save(pack);
        return ok(adminpackage.render(Package.finder.findList()));
    }

    /**
     * Method that deletes package from database
     * @param id - tracking number
     * @return
     */
    public Result deletePackage(Long id) {

        Package p = Package.findPackageById(id);
        User user = SessionHelper.getCurrentUser(ctx());
        if (user != null || user.typeOfUser == UserType.OFFICE_WORKER || user.typeOfUser == UserType.ADMIN) {
            Ebean.delete(p);
            return ok(packageadd.render(PostOffice.findOffice.findList()));
        } else {
            return redirect("/");
        }
    }

    /**
     * Method that updates package informations (packagedetails.scala.html)
     * @param id
     * @return
     */
    public Result updatePackage(Long id) {

        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect("/");
        }
        DynamicForm form = Form.form().bindFromRequest();
        Package pack = Package.findPackageById(id);
        String officeid = form.bindFromRequest().field("officePost").value();
        PostOffice office = PostOffice.findPostOffice(Long.parseLong(officeid));
        pack.postOffice = office;
        pack.destination = form.get("destination");
        Ebean.update(pack);
        return ok(adminpackage.render(Package.finder.findList()));
    }

    /**
     * Method that is used for opening up form for editing package (adminpackage.scala.html)
     * @param id
     * @return
     */
    public Result editPackage(Long id) {

        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER) {
            return redirect("/");
        }

        return ok(packagedetails.render(Package.findPackageById(id), PostOffice.findOffice.findList()));
    }

    public Result packagesByOffice(){
        DynamicForm form = Form.form().bindFromRequest();
        PostOffice office = PostOffice.findPostOfficeByName(form.data().get("office"));
        Logger.info(form.data().get("office"));
        List<Package> list = Package.findByPostOffice(office);
        String offices = "";
        for (int i = 0; i < list.size(); i++) {
            offices += list.get(i).destination + " ";
        }
        return ok(offices);
    }

    public Result takePackage(){
        DynamicForm form = Form.form().bindFromRequest();
        String destination = form.bindFromRequest().get("package");
        Package pack = Package.finder.where().eq("destination", destination).findUnique();
        if (pack == null) {
            return badRequest(index.render());
        }
        pack.status = Package.Status.OUT_FOR_DELIVERY;
        Ebean.update(pack);
        return redirect("/adminpanel/package/");
    }
}
