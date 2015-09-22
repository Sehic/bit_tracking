package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.libs.Json;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;


import java.util.*;

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
            return ok(adminpostofficeadd.render());
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

        pack.destination = form.get("destination");
        pack.trackingNum = (UUID.randomUUID().toString());
        Ebean.save(pack);

        Shipment ship = new Shipment();
        ship.packageId = pack;
        ship.postOfficeId=office;
        ship.save();
        if (user.typeOfUser == UserType.ADMIN)
            return redirect(routes.PackageController.adminPackage());
        else
            return redirect(routes.PostOfficeController.listRoutes(pack.id));
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
            for(int i = 0; i < p.shipmentPackages.size(); i++) {

                p.shipmentPackages.remove(i);
                Ebean.delete(p.shipmentPackages.get(i));
            }

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
        Package pack = Package.finder.byId(id);

        if (u1 != null && (u1.typeOfUser == UserType.ADMIN || u1.typeOfUser == UserType.DELIVERY_WORKER)) {

        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("packageId", pack).findList();
        List<Package> packages = new ArrayList<>();
        for (int i=0;i<shipments.size();i++){
            if(shipments.get(i).status == StatusHelper.READY_FOR_SHIPPING){
                shipments.get(i).status = StatusHelper.OUT_FOR_DELIVERY;
                System.out.println(shipments.get(i).postOfficeId.name);
                Calendar c = Calendar.getInstance();
                Date date = c.getTime();
                shipments.get(i).dateCreated = date;
                Ebean.update(shipments.get(i));
                shipments.get(i + 1).status = StatusHelper.RECEIVED;
                Ebean.update(shipments.get(i + 1));
                break;
            }
        }
        List<Shipment> shipments1 = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", u1.postOffice).findList();
        for (int i=0; i<shipments1.size();i++){
            packages.add(shipments1.get(i).packageId);
        }
        return ok(deliveryworkerpanel.render(packages));
    }else if(u1 != null && u1.typeOfUser == UserType.OFFICE_WORKER){
            List<Shipment> shipments = Shipment.shipmentFinder.where().eq("postOfficeId", u1.postOffice).findList();
            List<Package> packages = new ArrayList<>();
            for (int i=0;i<shipments.size();i++){
                packages.add(shipments.get(i).packageId);
                if(shipments.get(i).packageId.id == pack.id){
                    if(pack.destination.equals(u1.postOffice.address)){
                        packages.remove(i);
                        List<Shipment> shipmentss = Shipment.shipmentFinder.where().eq("packageId", pack).findList();
                        for (int j=0;j<shipmentss.size();j++){
                            shipmentss.get(j).status = StatusHelper.DELIVERED;
                            Ebean.update(shipmentss.get(j));
                        }
                        int last = shipmentss.size()-1;
                        Calendar c = Calendar.getInstance();
                        Date date = c.getTime();
                        shipmentss.get(last).dateCreated = date;
                        Ebean.update(shipmentss.get(last));
                        packages.add(shipmentss.get(last).packageId);
                        break;
                    }
                    shipments.get(i).status = StatusHelper.READY_FOR_SHIPPING;
                    Ebean.update(shipments.get(i));
                    }
                }
                return ok(officeworkerpanel.render(packages, u1.postOffice));
        }
        return redirect(routes.Application.index());
}

    public Result changePackageStatus(Long id){
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || (u1.typeOfUser != UserType.ADMIN && u1.typeOfUser != UserType.DELIVERY_WORKER)) {
            return redirect(routes.Application.index());
        }
        return ok(packagestatus.render(Package.findPackageById(id)));
    }

    public Result allIntoJson(){
        List<Package> packs = Package.finder.findList();
        List<Package> packages = new ArrayList<>();
        for (int i = 0; i < packs.size(); i++) {
            Package p = new Package();
            p.id = packs.get(i).id;
            p.trackingNum = packs.get(i).trackingNum;
            p.destination = packs.get(i).destination;
            packages.add(p);
        }
        JsonNode json = Json.toJson(packages);
        return ok(json);
    }
}
