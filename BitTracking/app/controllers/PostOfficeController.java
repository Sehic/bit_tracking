package controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import helpers.SessionHelper;
import models.User;
import models.PostOffice;
import models.UserType;
import models.Location;
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

    public Result listRoutes(){
        List<PostOffice> offices = PostOffice.findOffice.findList();

        return ok(makearoute.render(offices));
    }

    public Result createRoute(){
        DynamicForm form = Form.form().bindFromRequest();
        System.out.println(form.data().toString());
        String nextOffice = form.data().get("name");
        System.out.println("next office = "+nextOffice);
        PostOffice mainOffice = PostOffice.findOffice.where().eq("name", nextOffice).findUnique();
        List<PostOffice> linkedOffices = mainOffice.postOfficesA;

        String officesString="";
        for (int i=0;i<linkedOffices.size();i++){
            officesString += linkedOffices.get(i).name;
            if(i != linkedOffices.size()-1){
                officesString += ",";
            }
        }

        System.out.println("lista= "+officesString);


        return ok(officesString);
    }

    public Result saveRoute(){
        return TODO;
    }


}
