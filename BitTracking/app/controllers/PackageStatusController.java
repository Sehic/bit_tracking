package controllers;

import com.avaje.ebean.Ebean;
import helpers.MailHelper;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.deliveryworkerpanel;
import views.html.officeworkerpanel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Mladen13 on 7.10.2015.
 */
public class PackageStatusController extends Controller {

    /**
     * This method is used for changing status as delivery worker. When package is ready for shipping, delivery worker takes package
     * and replaces it to another office, where package gets status recieved until office worker checks it.
     *
     * @param u1   - current logged in delivery worker
     * @param pack - package which status will be updated
     * @param c    - Calendar
     * @param date - Date which is used for timestamp
     * @return - delivery worker panel
     */
    public Result updateStatusAsDeliveryWorker(User u1, models.Package pack, Calendar c, Date date) {

        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("packageId", pack).findList();

        for (int i = 0; i < shipments.size(); i++) {
            if (shipments.get(i).status == StatusHelper.READY_FOR_SHIPPING) {
                shipments.get(i).status = StatusHelper.OUT_FOR_DELIVERY;
                c = Calendar.getInstance();
                date = c.getTime();
                shipments.get(i).dateCreated = date;
                Ebean.update(shipments.get(i));
                shipments.get(i + 1).status = StatusHelper.RECEIVED;
                Ebean.update(shipments.get(i + 1));
                break;
            }
        }
        List<Package> packages = new ArrayList<>();
        List<Shipment> shipmentByOfficeAndStatus = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", u1.postOffice).findList();

        for (int i = 0; i < shipmentByOfficeAndStatus.size(); i++) {

            packages.add(shipmentByOfficeAndStatus.get(i).packageId);
        }
        List<Package> userPackages = u1.packages;

        return redirect(routes.WorkerController.deliveryWorkerPanel());
    }

    /**
     * Method that is called when office worker has packages with status recieved. After he clicks it, package will change status to
     * ready for shipping, or delivered if office workers office is package final destination.
     *
     * @param u1   - current logged in office worker
     * @param pack - package on which worker changes status
     * @param c    - Calendar
     * @param date - Date
     * @return - office worker panel
     */
    public Result updateStatusAsOfficeWorker(User u1, Package pack, Calendar c, Date date) {

        List<Shipment> shipmentsByPostOffice = Shipment.shipmentFinder.where().eq("postOfficeId", u1.postOffice).findList();
        List<Package> packages = new ArrayList<>();
        for (int i = 0; i < shipmentsByPostOffice.size(); i++) {
            packages.add(shipmentsByPostOffice.get(i).packageId);
            if (shipmentsByPostOffice.get(i).packageId.id == pack.id) {
                if (pack.destination.equals(u1.postOffice.name)) {
                    packages.remove(i);
                    List<Shipment> shipmentByPackage = Shipment.shipmentFinder.where().eq("packageId", pack).findList();
                    for (int j = 0; j < shipmentByPackage.size(); j++) {
                        shipmentByPackage.get(j).status = StatusHelper.DELIVERED;
                        Ebean.update(shipmentByPackage.get(j));
                    }
                    MailHelper.packageDeliveredNotification(pack.users.get(0).lastName, pack.trackingNum, pack.users.get(0).email);
                    int last = shipmentByPackage.size() - 1;
                    c = Calendar.getInstance();
                    date = c.getTime();
                    shipmentByPackage.get(last).dateCreated = date;
                    Ebean.update(shipmentByPackage.get(last));
                    packages.add(shipmentByPackage.get(last).packageId);
                    break;
                }
                shipmentsByPostOffice.get(i).status = StatusHelper.READY_FOR_SHIPPING;
                Ebean.update(shipmentsByPostOffice.get(i));
            }
        }
        List<Package> packagesWaiting = Package.findPackagesWaitingForApproval();
        List<Package> packagesForOfficeWorker = packagesForOfficeWorkerWaitingForApproval(u1.postOffice, packagesWaiting);

        return ok(officeworkerpanel.render(packages, u1.postOffice, packagesForOfficeWorker, PostOffice.findOffice.findList()));
    }

    /**
     * Method that is used for calling methods for changing package status
     *
     * @param id - package id
     * @return - delivery or office worker panel
     */
    public Result updateStatus(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        Package pack = Package.finder.byId(id);
        Calendar c = null;
        Date date = null;

        if (u1 != null && (u1.typeOfUser == UserType.ADMIN || u1.typeOfUser == UserType.DELIVERY_WORKER)) {
            updateStatusAsDeliveryWorker(u1, pack, c, date);
            return redirect(routes.WorkerController.deliveryWorkerPanel());
        }

        if (u1 != null && u1.typeOfUser == UserType.OFFICE_WORKER) {
            updateStatusAsOfficeWorker(u1, pack, c, date);
            return redirect(routes.WorkerController.officeWorkerPanel());
        }

        return redirect(routes.Application.index());
    }

    public static List<Package> packagesForOfficeWorkerWaitingForApproval(PostOffice userOffice, List<Package> packagesWaiting) {
        List<Package> packagesForOfficeWorker = new ArrayList<>();

        for (int i = 0; i < packagesWaiting.size(); i++) {

            PostOffice officeFromShipment = packagesWaiting.get(i).shipmentPackages.get(0).postOfficeId;

            if (officeFromShipment.id == userOffice.id) {
                packagesForOfficeWorker.add(packagesWaiting.get(i));
            }
        }
        return packagesForOfficeWorker;
    }

}
