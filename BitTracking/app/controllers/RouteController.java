package controllers;

import com.avaje.ebean.Ebean;
import helpers.Authenticators;
import helpers.DijkstraHelper;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mladen.teofilovic on 15/10/15.
 */
public class RouteController extends Controller {
    /**
     * This method opens up view for making route manually
     * @param id - package that needs route
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result listRoutes(Long id) {

        models.Package officePackage = Package.findPackageById(id);
        PostOffice office = officePackage.shipmentPackages.get(0).postOfficeId;
        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> allOffices = PostOffice.findOffice.findList();

        return ok(owmakeroute.render(office.postOfficesA, officePackage, locations, allOffices));
    }

    /**
     * Method that is used for updating select box with new values
     * @return - list of offices that are linked with selected office
     */
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

    /**
     * Method that is used for saving package route
     * @param id - package id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result saveRoute(Long id) {

        DynamicForm form = Form.form().bindFromRequest();
        String packages = form.get("packagesForRoute");
        List<Package> packagesForRoute = new ArrayList<>();
        //Getting values from form and checking if we need to route one or more packages
        if (packages != null) {
            packagesForRoute = packagesForRoute(packages);
        } else {
            Package onePackage = Package.findPackageById(id);
            packagesForRoute.add(onePackage);
        }
        //Getting route field
        String route = form.get("route");
        //Creating route for list of packages
        for (int i = 0; i < packagesForRoute.size(); i++) {
            Package packageWithRoute = packagesForRoute.get(i);
            Shipment initialOfficeShip = new Shipment();
            try {
                initialOfficeShip = Shipment.shipmentFinder.where().eq("packageId", packageWithRoute).findUnique();
            } catch (PersistenceException e) {
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

                ship.save();
            }
        }
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1.typeOfUser == UserType.ADMIN) {
            return redirect(routes.PackageController.adminPackage());
        }
            return redirect(routes.WorkerController.officeWorkerPanel());
    }

    /**
     * Method that is used for showing up auto route view
     * @param id - package id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result showAutoRouting(Long id) {

        Package officePackage = Package.findPackageById(id);
        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> offices = PostOffice.findOffice.findList();
        return ok(owmakeautoroute.render(offices, locations, officePackage));
    }

    /**
     * This method is used for saving auto route for one or more packages that goes to same destination
     * @param id - package id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result saveAutoRoute(Long id) {

        DynamicForm form = Form.form().bindFromRequest();
        String packages = form.get("packagesForRoute");
        List<Package> packagesForRoute = new ArrayList<>();
        //Getting values from form and checking if we need to route one or more packages
        if (packages != null) {
            packagesForRoute = packagesForRoute(packages);
        } else {
            Package onePackage = Package.findPackageById(id);
            packagesForRoute.add(onePackage);
        }
        String route = form.get("route");
        for (int i = 0; i < packagesForRoute.size(); i++) {
            Package routePackage = packagesForRoute.get(i);
            if (routePackage == null) {
                return redirect(routes.Application.index());
            }

            Shipment initialOfficeShip = new Shipment();
            try {
                initialOfficeShip = Shipment.shipmentFinder.where().eq("packageId", routePackage).findUnique();
            } catch (PersistenceException e) {
                return redirect(routes.RouteController.showAutoRouting(id));
            }
            initialOfficeShip.status = StatusHelper.READY_FOR_SHIPPING;
            initialOfficeShip.update();

            List<PostOffice> officesFromRoute = officesFromAutoRoute(route);

            for (int j = 1; j < officesFromRoute.size(); j++) {
                Shipment ship = new Shipment();
                ship.packageId = routePackage;
                ship.postOfficeId = officesFromRoute.get(j);
                ship.status = StatusHelper.ON_ROUTE;
                ship.save();
            }
        }

        return redirect(routes.WorkerController.officeWorkerPanel());
    }

    /**
     * This method is used when more than one package is selected using checkboxes, and when button is clicked, new view shows up.
     * That view is used for routing all selected packages.
     * Packages must have same destination post office.
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result listMultiRoute() {
        User u = SessionHelper.getCurrentUser(ctx());
        DynamicForm form = Form.form().bindFromRequest();
        String routePackages = form.get("packagesForRoute");
        String packagesId = "";
        Package officePackage = new Package();
        PostOffice office = new PostOffice();
        if (routePackages != null) {
            packagesId = routePackages;
            List<Package> packagesForMultiRoute = packagesForRoute(packagesId);
            officePackage = Package.findPackageById(packagesForMultiRoute.get(0).id);
            office = officePackage.shipmentPackages.get(0).postOfficeId;
        } else {
            List<Package> packages = Package.finder.findList();
            //Getting values from checkboxes
            List<Package> packagesToTake = new ArrayList<>();
            Package newPack = new Package();

            for (int i = 0; i < packages.size(); i++) {
                String pack = form.field("" + packages.get(i).id).value();
                if (pack != null) {
                    newPack = Package.findPackageById(Long.parseLong(pack));
                    packagesToTake.add(newPack);
                    packagesId += newPack.id + "|";
                }
            }
            for (int i = 0; i < packagesToTake.size() - 1; i++) {
                //Checking if every selected package goes to same destination post office
                if (!newPack.destination.equals(packagesToTake.get(i).destination)) {
                    flash("differentDestinationOffices", "You must select Packages which have same Destination Post Office!");
                    ApplicationLog newLog = new ApplicationLog(u.email+": Error choosing Multi Route. Packages must have same Destination Post Office.");
                    newLog.save();
                    return redirect(routes.WorkerController.officeWorkerPanel());
                }
            }
            officePackage = Package.findPackageById(newPack.id);
            //Validation on button click without selecting any packages
            if (officePackage == null) {
                ApplicationLog newLog = new ApplicationLog(u.email+": Error choosing Multi Route. Select at least one Package.");
                newLog.save();
                flash("noOffices", "You must select at least one Package!");
                return redirect(routes.WorkerController.officeWorkerPanel());
            }
            office = officePackage.shipmentPackages.get(0).postOfficeId;
        }

        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> allOffices = PostOffice.findOffice.findList();
        return ok(owmakemultiroute.render(office.postOfficesA, officePackage, locations, allOffices, packagesId));
    }

    /**
     * This method is used for creating route for more than one package.
     * This view is called clicking on button, and auto routing is available then.
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result showMultiAutoRouting() {

        DynamicForm form = Form.form().bindFromRequest();
        String packagesId = form.get("packagesForRoute");
        List<Package> packages = packagesForRoute(packagesId);
        Package officePackage = Package.findPackageById(packages.get(0).id);
        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> offices = PostOffice.findOffice.findList();
        return ok(owmakemultiautoroute.render(offices, locations, officePackage, packagesId));
    }

    /**
     * This method is used for showing up dijkstra routing for one package
     * @param id - package id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result showDijkstraRouting(Long id) {

        Package officePackage = Package.findPackageById(id);
        String initialOffice = officePackage.shipmentPackages.get(0).postOfficeId.name;
        String destinationOffice = officePackage.destination;
        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> finalOffices = officesInRoute(initialOffice, destinationOffice);

        return ok(owmakedijkstraroute.render(finalOffices, locations, officePackage));
    }

    /**
     * This method is used for showing up view that is used for routing more than one package
     * @return
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result showMultiDijkstraRouting() {

        DynamicForm form = Form.form().bindFromRequest();
        String packagesId = form.get("packagesForRoute");
        List<Package> packages = packagesForRoute(packagesId);
        Package officePackage = Package.findPackageById(packages.get(0).id);
        String initialOffice = officePackage.shipmentPackages.get(0).postOfficeId.name;
        String destinationOffice = officePackage.destination;
        List<PostOffice> finalOffices = officesInRoute(initialOffice, destinationOffice);
        List<Location> locations = Location.findLocation.findList();

        return ok(owmakemultidijkstraroute.render(finalOffices, locations, officePackage, packagesId));
    }

    /**
     * This method will give us Dijkstra shortest path
     * @return - list of post offices as string
     */
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result getDijkstraPath(){
        DynamicForm form = Form.form().bindFromRequest();

        String initialOffice = form.data().get("initial");
        String destinationOffice = form.data().get("destination");
        DijkstraHelper dijkstra = new DijkstraHelper();
        List<String> offices = dijkstra.getStringPath(initialOffice, destinationOffice);
        return ok(offices.toString());
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

    /**
     * This method returns packages that should be routed
     * @param packages - list of packages
     * @return
     */
    public static List<Package> packagesForRoute(String packages) {
        List<Package> routePackages = new ArrayList<>();
        if (packages == null) {
            return routePackages;
        }
        String[] packagesForRoute = packages.split("\\|");

        for (int i = 0; i < packagesForRoute.length; i++) {
            Package packageForRoute = Package.findPackageById(Long.parseLong(packagesForRoute[i]));
            routePackages.add(packageForRoute);
        }
        return routePackages;
    }

    /**
     * This method is used for getting list of post offices that are part of route
     * @param initialOffice - initial post office
     * @param destinationOffice - destination post office
     * @return
     */
    public static List<PostOffice> officesInRoute(String initialOffice, String destinationOffice){
        List<PostOffice> offices = PostOffice.findOffice.findList();
        DijkstraHelper dijkstra = new DijkstraHelper();
        List<String> routeOffices = dijkstra.getStringPath(initialOffice, destinationOffice);
        List<PostOffice> finalOffices = new ArrayList<>();

        for (String routeOfficeName : routeOffices) {
            for (PostOffice office : offices) {
                if (office.name.equals(routeOfficeName)) {
                    finalOffices.add(office);
                }
            }
        }
        return finalOffices;
    }

}
