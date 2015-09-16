package controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import helpers.SessionHelper;
import helpers.StatusHelper;
import models.*;
import models.Package;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import play.mvc.Result;

import views.html.*;

import com.avaje.ebean.Ebean;
import play.Logger;

import java.io.IOException;
import java.lang.System;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    public Result deleteOffice(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        PostOffice office = PostOffice.findPostOffice(id);
        Location place = Location.findLocationById(office.place.id);
        Ebean.delete(place);
        return redirect(routes.Application.adminPostOffice());
    }

    /**
     * Method that adds new office to database using (adminpostoffice.scala.html)
     *
     * @return
     */
    public Result addNewOffice() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        Form<PostOffice> boundForm = officeForm.bindFromRequest();
        String name = boundForm.bindFromRequest().field("name").value();
        String address = boundForm.bindFromRequest().field("address").value();
        String lon = boundForm.bindFromRequest().field("longitude").value();
        String lat = boundForm.bindFromRequest().field("latitude").value();
        System.out.println("lon "+lon);
        if (lon == null || lat == null) {
            return redirect(routes.Application.adminPostOffice());
        }

        Double x = Double.parseDouble(lon);
        Double y = Double.parseDouble(lat);
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
    public Result postOfficeDetails(Long id) {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }
        return ok(postofficedetails.render(PostOffice.findPostOffice(id)));
    }

    /**
     * Method that updates post office information (postofficedetails.scala.html)
     *
     * @param Id
     * @return
     */
    public Result updateOffice(Long Id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        PostOffice office = PostOffice.findPostOffice(Id);
        Location place = Location.findLocationById(office.place.id);

        if (place == null) {
            return redirect(routes.Application.adminPostOffice());
        }

        if (office == null) {
            return redirect(routes.Application.adminPanel());
        }
        Form<PostOffice> newOfficeForm = officeForm.fill(office);
        String lon = newOfficeForm.bindFromRequest().field("longitude").value();
        String lat = newOfficeForm.bindFromRequest().field("latitude").value();

        if (lon == null || lat == null) {
            return redirect(routes.Application.adminPostOffice());
        }

        if (lon != "") {
            Double x = Double.parseDouble(lon);
            Double y = Double.parseDouble(lat);
            place.x = x;
            place.y = y;
            Ebean.update(place);
            office.place = place;
        }

        office.name = newOfficeForm.bindFromRequest().field("name").value();
        office.address = newOfficeForm.bindFromRequest().field("address").value();
        Ebean.update(office);


        return redirect(routes.Application.adminPostOffice());

    }

    /**
     * Method that opens up window for making links to offices
     *
     * @return
     */
    public Result linkPostOffices(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        List<PostOffice> postOffices = PostOffice.findOffice.findList();

        return ok(linkoffices.render(postOffices, PostOffice.findOffice.byId(id)));
    }

    /**
     * Method that saves link of offices to database
     *
     * @return
     */
    public Result savePostOffices() {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

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

        return redirect("/adminpanel");

    }

    public Result listRoutes(Long id) {
        List<PostOffice> offices = PostOffice.findOffice.findList();
        Package officePackage = Package.findPackageById(id);
        PostOffice office= new PostOffice();
        for (int i = 0; i < officePackage.packageRoutes.size(); i++) {
            if (offices.get(i).id == officePackage.packageRoutes.get(i).id) {
                office = offices.get(i);
            }
        }

        return ok(makearoute.render(office, officePackage));
    }

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

    public Result saveRoute(Long id) {
        DynamicForm form = Form.form().bindFromRequest();
        String route = form.data().get("route");
        Package packageWithRoute = Package.findPackageById(id);

        packageWithRoute.route=route;
        packageWithRoute.status = StatusHelper.READY_FOR_SHIPPING;

        String []arr = route.split(" ");
//        for(int i = 1; i<arr.length; i++){
//            PostOffice p = PostOffice.findPostOfficeByName(arr[i]);
//            Package tmp = new Package();
//            System.out.println("Office name "+p.name);
//            tmp.postOffice = p;
//            tmp.status = "On route";
//            tmp.destination = packageWithRoute.destination;
//            tmp.route = route;
//            tmp.trackingNum = packageWithRoute.trackingNum;
//            Ebean.save(tmp);
//        }

        //Eh ovdje pazi, napravio sam dva statusa, jedan je u postofficeu status paketa, a drugi je u paketu
        //Koristio sam tvoj string, izvrtio ga za sad, samo da nam fino sprema u bazu vezu paket office
        //ne znam sta ti radi ova change route pa vidi, ima to bruku bagova. Vjerovatno mi ta logika ne valja za
        //office status paketa, al nekako sam zamislio da npr paket ima listu ruta(postofficea) u sebi packet.list..
        //eh sad da tom paketu stavimo status ready for shipment kad se obradi, outfor delivery kad krene, a post officeima,
        //koji imaju u ovom paketu dodijelimo status on route.. eh sad mozda opet treba napravit tabelu status i povezat je sa officeom vise na vise
        //da da jedan office za razlicite pakete moze imat razlicite statuse, jer mislim da ce se na moj nacin pravit lupanje podataka, kad vise paketa saljes preko istog centra, ali na razlicite destinacije
        //vidi ti to jos malo, ako sta skontas javi al ovo dole je okej, nisam htio sad ajax da diram, pa da odmah vadim u rutu mozda cu vcrs to

   //    for (int i=0;i<packageWithRoute.packageRoutes.size();i++){
            for(int j = 1; j<arr.length; j++) {
                PostOffice p = PostOffice.findPostOfficeByName(arr[j]);
                System.out.println(p.name);

                    //dodaje u rutu ako ime officea iz naseg stringa nije jednako imenu od paketa kojeg saljemo i elementa liste
                    packageWithRoute.packageRoutes.add(p);

                    //znaci paket koji se salje svim officeima osim prvom, dodijeli status on route
                //Ovo treba skontat kako implementirat ispod.. ali kad ovu petlju arr preklopis ovom packagewithroute.packageroutes.size
                //beskonacna bude i nikad se ne izvrsi
                //ugl ovako radi da se u medju tabelu upisuju officei i paketi koji su u vezi
        //            packageWithRoute.packageRoutes.get(i).packageStatus= StatusHelper.ON_ROUTE;
                Ebean.save(packageWithRoute);
            }
    //    }



        return redirect(routes.PackageController.adminPackage());
    }

    public Result changeRoute(Long id){
        Package p = Package.findPackageById(id);
        p.status = StatusHelper.OUT_FOR_DELIVERY;
        Ebean.update(p);
        return redirect(routes.UserController.officeWorkerPanel());
    }


}
