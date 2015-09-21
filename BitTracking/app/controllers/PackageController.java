package controllers;

import com.avaje.ebean.Ebean;
import helpers.Authenticators;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.Package;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
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
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result adminPackage() {

        return ok(adminpackage.render(Package.finder.findList()));
    }

    /**
     * Method that opens up add package form (packageadd.scala.html)
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result addPackage() {

        List<PostOffice> offices = PostOffice.findOffice.findList();
        if (offices == null || offices.size() == 0) {
            return ok(adminpostofficeadd.render());
        }

        return ok(packageadd.render(PostOffice.findOffice.findList()));
    }

    /**
     * Method that saves package to database using (packageadd.scala.html) form
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result savePackage() {

        DynamicForm form = Form.form().bindFromRequest();

        String id = form.bindFromRequest().field("officePost").value();

        PostOffice office = PostOffice.findPostOffice(Long.parseLong(id));

        Package pack = new Package();

        pack.destination = form.get("destination");
        pack.trackingNum = (UUID.randomUUID().toString());
        Ebean.save(pack);

        Shipment ship = new Shipment();
        ship.packageId = pack;
        ship.postOfficeId = office;
        ship.save();
        User user = SessionHelper.getCurrentUser(ctx());
        if (user.typeOfUser == UserType.ADMIN)
            return redirect(routes.PackageController.adminPackage());
        else
            return redirect(routes.PostOfficeController.listRoutes(pack.id));
    }

    /**
     * Method that deletes package from database
     *
     * @param id - tracking number
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result deletePackage(Long id) {

        Package p = Package.findPackageById(id);

            for (int i = 0; i < p.shipmentPackages.size(); i++) {

                p.shipmentPackages.remove(i);
                Ebean.delete(p.shipmentPackages.get(i));
            }

            Ebean.delete(p);
            return ok(packageadd.render(PostOffice.findOffice.findList()));

    }

    /**
     * Method that updates package informations (packagedetails.scala.html)
     *
     * @param id
     * @return
     */
    public Result updatePackage(Long id) {

        return TODO;
    }

    /**
     * Method that is used for opening up form for editing package (adminpackage.scala.html)
     *
     * @param id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result editPackage(Long id) {
        return ok(packagedetails.render(Package.findPackageById(id), PostOffice.findOffice.findList()));
    }
    @Security.Authenticated(Authenticators.AdminDeliveryWorkerFilter.class)
    public Result updateStatus(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());

        Package pack = Package.finder.byId(id);
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("packageId", pack).findList();

        for (int i = 0; i < shipments.size(); i++) {
            if (shipments.get(i).status == StatusHelper.READY_FOR_SHIPPING) {
                shipments.get(i).status = StatusHelper.OUT_FOR_DELIVERY;
                System.out.println(shipments.get(i).postOfficeId.name);
                Calendar c = Calendar.getInstance();
                Date date = c.getTime();
                shipments.get(i).dateCreated = date;
                Ebean.update(shipments.get(i));
                if (i != shipments.size() - 2) {
                    shipments.get(i + 1).status = StatusHelper.READY_FOR_SHIPPING;
                    Ebean.update(shipments.get(i + 1));
                } else {
                    for (int j = 0; j < shipments.size(); j++) {
                        shipments.get(j).status = StatusHelper.DELIVERED;
                        Calendar c1 = Calendar.getInstance();
                        Date date1 = c1.getTime();
                        shipments.get(i).dateCreated = date1;
                        Ebean.update(shipments.get(j));
                    }
                }
                break;
            }
        }
        List<Package> packages = new ArrayList<>();
        List<Shipment> shipments1 = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", u1.postOffice).findList();

        for (int i = 0; i < shipments1.size(); i++) {

            packages.add(shipments1.get(i).packageId);
        }
        return ok(deliveryworkerpanel.render(packages));
    }
    @Security.Authenticated(Authenticators.AdminDeliveryWorkerFilter.class)
    public Result changePackageStatus(Long id) {

        return ok(packagestatus.render(Package.findPackageById(id)));
    }
}
