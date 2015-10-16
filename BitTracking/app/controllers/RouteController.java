package controllers;

import com.avaje.ebean.Ebean;
import helpers.Authenticators;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.owmakeautoroute;
import views.html.owmakeroute;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mladen.teofilovic on 15/10/15.
 */
public class RouteController extends Controller {

    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result listRoutes(Long id) {

        models.Package officePackage = Package.findPackageById(id);
        PostOffice office = officePackage.shipmentPackages.get(0).postOfficeId;
        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> allOffices = PostOffice.findOffice.findList();

        return ok(owmakeroute.render(office.postOfficesA, officePackage, locations, allOffices));
    }

    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result createRoute() {

        DynamicForm form = Form.form().bindFromRequest();

        String nextOffice = form.data().get("name");

        PostOffice mainOffice = PostOffice.findOffice.where().eq("name", nextOffice).findUnique();

        List<PostOffice> linkedOffices = mainOffice.postOfficesA;

        String officesString = "";
        for (int i = 0; i < linkedOffices.size(); i++) {
            officesString += linkedOffices.get(i).name;
            if (i != linkedOffices.size() - 1) {
                officesString += ",";
            }
        }
        return ok(officesString);
    }

    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result saveRoute(Long id) {

        DynamicForm form = Form.form().bindFromRequest();

        String route = form.get("route");

        Package packageWithRoute = Package.findPackageById(id);
        Shipment initialOfficeShip = new Shipment();
        try {
            initialOfficeShip = Shipment.shipmentFinder.where().eq("packageId", packageWithRoute).findUnique();
        }catch(PersistenceException e){
            return redirect(routes.RouteController.listRoutes(id));
        }
        initialOfficeShip.status = StatusHelper.READY_FOR_SHIPPING;
        initialOfficeShip.update();

        String[] arr = route.split(",");

        for (int j = 0; j < arr.length; j++) {
            PostOffice p = PostOffice.findPostOfficeByName(arr[j]);

            if (p == null) {
                return redirect(routes.PackageController.adminPackage());
            }
            Shipment ship = new Shipment();
            ship.postOfficeId = p;
            ship.packageId = packageWithRoute;
            ship.status = StatusHelper.ON_ROUTE;

            Ebean.save(ship);
        }
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1.typeOfUser == UserType.ADMIN)
            return redirect(routes.PackageController.adminPackage());
        else
            return redirect(routes.WorkerController.officeWorkerPanel());
    }

    public Result showAutoRouting(Long id){

        Package officePackage = Package.findPackageById(id);
        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> offices = PostOffice.findOffice.findList();
        return ok(owmakeautoroute.render(offices, locations, officePackage));
    }

    public Result saveAutoRoute(Long id){

        Package routePackage = Package.findPackageById(id);
        if(routePackage == null){
            return redirect(routes.Application.index());
        }

        DynamicForm form = Form.form().bindFromRequest();
        String route = form.get("route");

        Shipment initialOfficeShip = new Shipment();
        try {
            initialOfficeShip = Shipment.shipmentFinder.where().eq("packageId", routePackage).findUnique();
        }catch(PersistenceException e){
            return redirect(routes.RouteController.showAutoRouting(id));
        }
        initialOfficeShip.status = StatusHelper.READY_FOR_SHIPPING;
        initialOfficeShip.update();

        List<PostOffice> officesFromRoute = officesFromAutoRoute(route);

        for (int i = 1; i < officesFromRoute.size(); i++) {
            Shipment ship = new Shipment();
            ship.packageId = routePackage;
            ship.postOfficeId = officesFromRoute.get(i);
            ship.status = StatusHelper.ON_ROUTE;
            ship.save();
        }
        return redirect(routes.WorkerController.officeWorkerPanel());
    }

    /**
     * Method that is used for splitting offices string and returning them for creating shipment.
     *
     * @param route - string route offices
     * @return - list of post offices
     */
    public static List<PostOffice> officesFromAutoRoute(String route) {
        List<PostOffice> routeOffices = new ArrayList<>();
        if (route == null) {
            return routeOffices;
        }
        String[] offices = route.split("\\|");

        for (int i = 0; i < offices.length; i++) {
            PostOffice officeFromRoute = PostOffice.findPostOfficeByName(offices[i]);
            routeOffices.add(officeFromRoute);
        }
        return routeOffices;
    }
}
