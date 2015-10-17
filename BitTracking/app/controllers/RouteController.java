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
import views.html.*;

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
        String packages = form.get("packagesForRoute");
        List<Package> packagesForRoute = new ArrayList<>();
        if(packages != null) {
            packagesForRoute = packagesForRoute(packages);
        }else{
            Package onePackage = Package.findPackageById(id);
            packagesForRoute.add(onePackage);
        }
        String route = form.get("route");
        for(int i=0;i<packagesForRoute.size();i++) {
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

                Ebean.save(ship);
            }
        }
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1.typeOfUser == UserType.ADMIN)
            return redirect(routes.PackageController.adminPackage());
        else
            return redirect(routes.WorkerController.officeWorkerPanel());
    }
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result showAutoRouting(Long id){

        Package officePackage = Package.findPackageById(id);
        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> offices = PostOffice.findOffice.findList();
        return ok(owmakeautoroute.render(offices, locations, officePackage));
    }
    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result saveAutoRoute(Long id){

        DynamicForm form = Form.form().bindFromRequest();
        String packages = form.get("packagesForRoute");
        List<Package> packagesForRoute = new ArrayList<>();
        if(packages != null) {
            packagesForRoute = packagesForRoute(packages);
        }else{
            Package onePackage = Package.findPackageById(id);
            packagesForRoute.add(onePackage);
        }
        String route = form.get("route");
        for(int i =0;i<packagesForRoute.size();i++){
            Package routePackage = packagesForRoute.get(i);
            if(routePackage == null){
                return redirect(routes.Application.index());
            }

            Shipment initialOfficeShip = new Shipment();
            try {
                initialOfficeShip = Shipment.shipmentFinder.where().eq("packageId", routePackage).findUnique();
            }catch(PersistenceException e){
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

    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result listMultiRoute() {
        DynamicForm form = Form.form().bindFromRequest();
        String routePackages = form.get("packagesForRoute");
        String packagesId = "";
        Package officePackage = new Package();
        PostOffice office = new PostOffice();
        if(routePackages!=null){
            packagesId=routePackages;
            List<Package> packagesForMultiRoute = packagesForRoute(packagesId);
            officePackage = Package.findPackageById(packagesForMultiRoute.get(0).id);
            office = officePackage.shipmentPackages.get(0).postOfficeId;
        }else {
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
            for (int i =0;i<packagesToTake.size()-1;i++){
                System.out.println(packagesToTake.get(i).id);
                if(!newPack.destination.equals(packagesToTake.get(i).destination)){
                    flash("differentDestinationOffices", "You must select Packages which have same Destination Post Office!");
                    return redirect(routes.WorkerController.officeWorkerPanel());
                }
            }
            officePackage = Package.findPackageById(newPack.id);
            if(officePackage==null){
                flash("noOffices", "You must select at least one Post Office!");
                return redirect(routes.WorkerController.officeWorkerPanel());
            }
            office = officePackage.shipmentPackages.get(0).postOfficeId;
        }

        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> allOffices = PostOffice.findOffice.findList();
        return ok(owmakemultiroute.render(office.postOfficesA, officePackage, locations, allOffices, packagesId));
    }

    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result showMultiAutoRouting(){

        DynamicForm form = Form.form().bindFromRequest();
        String packagesId= form.get("packagesForRoute");
        List<Package> packages = packagesForRoute(packagesId);
        System.out.println(packagesId);
        Package officePackage = Package.findPackageById(packages.get(0).id);
        List<Location> locations = Location.findLocation.findList();
        List<PostOffice> offices = PostOffice.findOffice.findList();
        return ok(owmakemultiautoroute.render(offices, locations, officePackage, packagesId));
    }

}
