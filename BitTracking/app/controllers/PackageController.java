package controllers;

import com.avaje.ebean.Ebean;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.PostOffice;
import models.User;
import models.UserType;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import models.Package;
import play.Logger;


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
      //  pack.shipmentPackages.get(0).packagePostOffice =office;
      //  pack.packageRoutes.add(office);
     //   pack.postOffice = office;
        pack.destination = form.get("destination");
        pack.trackingNum = (UUID.randomUUID().toString());
        pack.status = StatusHelper.READY_FOR_SHIPPING;


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
        if (user == null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER && user.typeOfUser != UserType.DELIVERY_WORKER) {
            return redirect("/");
        }
        DynamicForm form = Form.form().bindFromRequest();
        Package pack = Package.findPackageById(id);
        Logger.info(form.bindFromRequest().field("drop").value());
        String officeid = form.bindFromRequest().field("officePost").value();
        PostOffice office = PostOffice.findPostOffice(Long.parseLong(officeid));
        pack.shipmentPackages.get(0).postOfficeId = office;
     //   pack.packageRoutes.add(office);
    //    pack.postOffice = office;
        pack.destination = form.get("destination");
        String status = form.bindFromRequest().field("drop").value();
        if (status.equals("1")){
            pack.status = StatusHelper.READY_FOR_SHIPPING;
        } else if (status.equals("2")){
            pack.status = StatusHelper.ON_ROUTE;
        } else if (status.equals("3")){
            pack.status = StatusHelper.OUT_FOR_DELIVERY;
        } else if (status.equals("4")){
            pack.status = StatusHelper.DELIVERED;
        }
        pack.deliveryWorkers.add(user);
        user.packages.add(pack);
        Ebean.update(pack);
        Ebean.update(user);
        return ok(deliveryworkerpanel.render(user.packages, Package.finder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).findList()));
    }

    /**
     * Method that is used for opening up form for editing package (adminpackage.scala.html)
     * @param id
     * @return
     */
    public Result editPackage(Long id) {

        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER && user.typeOfUser != UserType.DELIVERY_WORKER) {
            return redirect("/");
        }

        return ok(packagedetails.render(Package.findPackageById(id), PostOffice.findOffice.findList()));
    }

    public Result updateStatus(Long id){
        User user = SessionHelper.getCurrentUser(ctx());
        if (user == null || user.typeOfUser != UserType.ADMIN && user.typeOfUser != UserType.OFFICE_WORKER && user.typeOfUser != UserType.DELIVERY_WORKER) {
            return redirect("/");
        }
        DynamicForm form = Form.form().bindFromRequest();
        Package pack = Package.findPackageById(id);
        String status = form.bindFromRequest().field("drop").value();
        if (status.equals("1")){
            pack.status = StatusHelper.READY_FOR_SHIPPING;
        } else if (status.equals("2")){
            pack.status = StatusHelper.ON_ROUTE;
        } else if (status.equals("3")){
            pack.status = StatusHelper.OUT_FOR_DELIVERY;
        } else if (status.equals("4")){
            pack.status = StatusHelper.DELIVERED;
        }
        Ebean.update(pack);
        return ok(deliveryworkerpanel.render(user.packages, Package.finder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).findList()));
    }

    public Result changePackageStatus(Long id){
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || (u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.DELIVERY_WORKER)) {
            return redirect(routes.Application.index());
        }
        return ok(packagestatus.render(Package.findPackageById(id)));
    }
}
