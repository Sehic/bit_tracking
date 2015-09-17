package controllers;

import com.avaje.ebean.Ebean;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;


import java.util.ArrayList;
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

        return TODO;
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

    public Result updateStatus(Long id){

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || (u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.DELIVERY_WORKER)) {
            return redirect(routes.Application.index());
        }

        Package pack = Package.finder.byId(id);
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("packageId", pack).findList();
        List<Package> packages = new ArrayList<>();
        for (int i=0;i<shipments.size();i++){
            if(shipments.get(i).status == StatusHelper.READY_FOR_SHIPPING && i!=shipments.size()-1){
                shipments.get(i).status = StatusHelper.OUT_FOR_DELIVERY;
                Ebean.update(shipments.get(i));
                shipments.get(i+1).status = StatusHelper.READY_FOR_SHIPPING;
                Ebean.update(shipments.get(i+1));
                break;
            }else{
                for (int j=0;j<shipments.size();j++){
                    shipments.get(j).status=StatusHelper.DELIVERED;
                    Ebean.update(shipments.get(j));
                }
            }
        }

        List<Shipment> shipments1 = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", u1.postOffice).findList();

        for (int i=0; i<shipments1.size();i++){

            packages.add(shipments1.get(i).packageId);

        }

        return ok(deliveryworkerpanel.render(packages));
    }

    public Result changePackageStatus(Long id){
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || (u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.DELIVERY_WORKER)) {
            return redirect(routes.Application.index());
        }
        return ok(packagestatus.render(Package.findPackageById(id)));
    }
}
