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
import views.html.adminlinkoffices;
import views.html.adminpostofficedetails;
import views.html.owmakeroute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mladen.teofilovic on 09/09/15.
 */
public class PostOfficeController extends Controller {

    private static final Form<PostOffice> officeForm =
            Form.form(PostOffice.class);

    /**
     * Method that deletes office from database
     *
     * @param id - represents office id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result deleteOffice(Long id) {
        PostOffice office = PostOffice.findPostOffice(id);
        for (int i = 0; i<office.shipmentOffices.size(); i++){
            office.shipmentOffices.remove(i);
            office.update();
        }
        Ebean.delete(office);
        return redirect(routes.Application.adminPostOffice());
    }
    /**
     * Method that adds new office to database using (adminpostoffice.scala.html)
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result addNewOffice() {

        Form<PostOffice> boundForm = officeForm.bindFromRequest();

        String name = boundForm.get().name;
        String address = boundForm.get().address;
        String lon = boundForm.field("longitude").value();
        String lat = boundForm.field("latitude").value();

        Double x = null;
        Double y = null;

        try {
            x = Double.parseDouble(lon);
            y = Double.parseDouble(lat);
        } catch (NumberFormatException e) {
            flash("wrongAddress", "Entered place does not exists!");
            return redirect(routes.Application.addPostOffice());
        }
        Location place = new Location(x, y);
        Ebean.save(place);
        PostOffice p = new PostOffice(name, address, place);

        Ebean.save(p);
        return redirect(routes.Application.adminPostOffice());
    }

    /**
     * Method that enables post office editing
     *
     * @param id - post office id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result postOfficeDetails(Long id) {

        return ok(adminpostofficedetails.render(PostOffice.findPostOffice(id)));
    }

    /**
     * Method that updates post office information (postofficedetails.scala.html)
     *
     * @param Id
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result updateOffice(Long Id) {

        PostOffice office = PostOffice.findPostOffice(Id);
        Location place = Location.findLocationById(office.place.id);

        if (place == null) {
            System.out.println("Place je null");
            return redirect(routes.Application.adminPostOffice());
        }

        if (office == null) {
            System.out.println("Office je null");
            return redirect(routes.Application.adminPanel());
        }
       // Form<PostOffice> newOfficeForm = officeForm.fill(office);
        Form<PostOffice> forma = officeForm.bindFromRequest();
        String lon = forma.field("longitude").value();
        String lat = forma.field("latitude").value();

        if (lon == null || lat == null) {
            System.out.println("lat ili lon je null");
            return redirect(routes.Application.addPostOffice());
        }

        Double x = null;
        Double y = null;

        try {
            x = Double.parseDouble(lon);
            y = Double.parseDouble(lat);
        } catch (NumberFormatException e) {
            System.out.println("Parse je null");
            return redirect(routes.Application.addPostOffice());
        }

        place.x = x;
        place.y = y;
        Ebean.update(place);
        office.place = place;


        office.name = forma.field("name").value();
        office.address = forma.field("address").value();
        Ebean.update(office);

        return redirect(routes.Application.adminPostOffice());

    }

    /**
     * Method that opens up window for making links to offices
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result linkPostOffices(Long id) {

        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        PostOffice office = PostOffice.findOffice.byId(id);
        List<PostOffice> mainOfficeRelationList = office.postOfficesA;

        for (int i = 0; i < mainOfficeRelationList.size(); i++) {
            for (int j = 0; j < postOffices.size(); j++) {
                if (mainOfficeRelationList.get(i).id == postOffices.get(j).id) {
                    postOffices.remove(j);
                }
            }
        }
        return ok(adminlinkoffices.render(postOffices, office));
    }

    /**
     * Method that saves link of offices to database
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result savePostOffices() {

        Form<PostOffice> boundForm = officeForm.bindFromRequest();

        List<PostOffice> postOffices = PostOffice.findOffice.findList();
        //Getting values from checkboxes
        List<String> checkBoxValues = new ArrayList<>();
        for (int i = 0; i < postOffices.size(); i++) {
            String office = boundForm.bindFromRequest().field(postOffices.get(i).name).value();
            checkBoxValues.add(office);

        }
        //Removing null elements from list
        checkBoxValues.removeAll(Collections.singleton(null));
        postOffices.clear();
        //Making postoffices with names from checkBoxValues list
        for (int i = 0; i < checkBoxValues.size(); i++) {
            PostOffice postOffice = PostOffice.findOffice.where().eq("name", checkBoxValues.get(i)).findUnique();
            postOffices.add(postOffice);
        }

        String officeName = boundForm.bindFromRequest().field("mainOfficeName").value();

        PostOffice mainPostOffice = PostOffice.findOffice.where().eq("name", officeName).findUnique();
        //Saving offices and their relationship to database
        for (int i = 0; i < postOffices.size(); i++) {
            PostOffice linkedPostOffice = postOffices.get(i);
            mainPostOffice.postOfficesA.add(linkedPostOffice);
            linkedPostOffice.postOfficesA.add(mainPostOffice);
            Ebean.save(mainPostOffice);
            Ebean.save(linkedPostOffice);

        }
        return redirect("/adminpanel/postoffice");
    }

    @Security.Authenticated(Authenticators.AdminOfficeWorkerFilter.class)
    public Result listRoutes(Long id) {

        Package officePackage = Package.findPackageById(id);
        PostOffice office = officePackage.shipmentPackages.get(0).postOfficeId;

        return ok(owmakeroute.render(office.postOfficesA, officePackage));
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

        Shipment initialOfficeShip = Shipment.shipmentFinder.where().eq("packageId", packageWithRoute).findUnique();
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
            return redirect(routes.UserController.officeWorkerPanel());
    }
}
