package controllers;

import com.avaje.ebean.Ebean;
import helpers.Authenticators;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

import javax.persistence.PersistenceException;
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
        Location place = Location.findLocationById(office.place.id);

        List<User> users = User.findUsersByPostOffice(office);
        for (int i = 0; i < users.size(); i++) {
            users.get(i).postOffice = null;
            users.get(i).typeOfUser = UserType.REGISTERED_USER;
            users.get(i).update();
        }

        Ebean.delete(place);

        return redirect(routes.Application.adminPostOffice());
    }

    /**
     * Method that adds new office to database using (adminpostoffice.scala.html)
     *
     * @return
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result addNewOffice() {

        DynamicForm form = Form.form().bindFromRequest();

        String name = form.get("name");
        String address = form.get("address");
        String countryCode = form.get("country");

        PostOffice officeByName = PostOffice.findPostOfficeByName(name);
        PostOffice officeByAddress = PostOffice.findPostOfficeByAddress(address);

        if(officeByName != null || officeByAddress != null){
            return redirect(routes.Application.addPostOffice());
        }

        String lon = form.get("longitude");
        String lat = form.get("latitude");

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
        Country officeCountry = Country.findCountryByCode(countryCode);
        PostOffice p = new PostOffice(name, address, place, officeCountry);

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
            return redirect(routes.Application.adminPostOffice());
        }

        if (office == null) {
            return redirect(routes.Application.adminPanel());
        }

        Form<PostOffice> newOfficeForm = officeForm.bindFromRequest();
        String lon = newOfficeForm.field("longitude").value();
        String lat = newOfficeForm.field("latitude").value();

        if (lon == null || lat == null) {
            return redirect(routes.Application.addPostOffice());
        }

        Double x = null;
        Double y = null;

        try {
            x = Double.parseDouble(lon);
            y = Double.parseDouble(lat);
        } catch (NumberFormatException e) {

            return redirect(routes.Application.addPostOffice());
        }

        place.x = x;
        place.y = y;
        Ebean.update(place);
        office.place = place;

        office.name = newOfficeForm.field("name").value();
        office.address = newOfficeForm.field("address").value();
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
        List<Location> locations = Location.findLocation.findList();

        for (int i = 0; i < mainOfficeRelationList.size(); i++) {
            for (int j = 0; j < postOffices.size(); j++) {
                if (mainOfficeRelationList.get(i).id == postOffices.get(j).id) {
                    postOffices.remove(j);
                }
            }
        }
        return ok(adminlinkoffices.render(postOffices, mainOfficeRelationList, office, locations));
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
        List<PostOffice> relationOffices = mainPostOffice.postOfficesA;

        if (relationOffices.size() != 0) {
            for (int i = 0; i < relationOffices.size(); i++) {
                relationOffices.get(i).postOfficesA.remove(mainPostOffice);
                Ebean.update(relationOffices.get(i));
            }
            mainPostOffice.postOfficesA.clear();
            Ebean.update(mainPostOffice);
        }
        //    Saving offices and their relationship to database
        for (int i = 0; i < postOffices.size(); i++) {
            PostOffice linkedPostOffice = postOffices.get(i);
            mainPostOffice.postOfficesA.add(linkedPostOffice);
            linkedPostOffice.postOfficesA.add(mainPostOffice);
            Ebean.save(mainPostOffice);
            Ebean.save(linkedPostOffice);
        }
        return redirect("/adminpanel/postoffice");
    }

    public Result checkOfficeName() {
        DynamicForm form = Form.form().bindFromRequest();
        String officeName = form.data().get("name");
        String officeAddress = form.data().get("address");
        PostOffice officeByName = PostOffice.findPostOfficeByName(officeName);
        PostOffice officeByAddress = PostOffice.findPostOfficeByAddress(officeAddress);
        if (officeByName != null & officeByAddress!=null) {
            return badRequest("1");
        }
        if(officeByName!=null){
            return badRequest("2");
        }
        if(officeByAddress!=null){
            return badRequest("X");
        }
        return ok();
    }
}
