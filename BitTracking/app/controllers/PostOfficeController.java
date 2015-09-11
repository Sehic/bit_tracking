package controllers;


import helpers.SessionHelper;
import models.User;
import models.PostOffice;
import models.UserType;
import models.Location;
import play.*;
import play.data.Form;
import play.mvc.*;
import play.mvc.Result;

import views.html.*;

import com.avaje.ebean.Ebean;
import play.Logger;

import java.lang.System;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by mladen.teofilovic on 09/09/15.
 */
public class PostOfficeController extends Controller {

    private static final Form<PostOffice> officeForm =
            Form.form(PostOffice.class);

    public Result deleteOffice(Long id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        final PostOffice office = PostOffice.findPostOffice(id);
        Location place = Location.findLocationById(office.place.id);
        Ebean.delete(place);
        return redirect(routes.Application.adminPostOffice());
    }


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
        Double x = Double.parseDouble(lon);
        Double y = Double.parseDouble(lat);
        Location place = new Location(x, y);
        Ebean.save(place);
        PostOffice p = new PostOffice(name, address, place);

        Ebean.save(p);
        return redirect(routes.Application.adminPostOffice());
    }


    public Result postOfficeDetails(Long id) {
        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }
        return ok(postofficedetails.render(PostOffice.findPostOffice(id)));
    }

    public Result updateOffice(Long Id) {

        User u1 = SessionHelper.getCurrentUser(ctx());
        if (u1 == null || u1.typeOfUser != UserType.ADMIN) {
            return redirect(routes.Application.index());
        }

        PostOffice office = PostOffice.findPostOffice(Id);
        Location place = Location.findLocationById(office.place.id);

        if(place == null){
            return redirect(routes.Application.adminPostOffice());
        }

        if (office == null) {
            return redirect(routes.Application.adminPanel());
        }
        Form<PostOffice> newOfficeForm = officeForm.fill(office);
        String lon = newOfficeForm.bindFromRequest().field("longitude").value();
        String lat = newOfficeForm.bindFromRequest().field("latitude").value();
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

        /*if(office.linkOffice == null || office.linkOffice.equals(PostOffice.findPostOfficeByName(newOfficeForm.bindFromRequest().field("officePost").value()))) {
            office.name = newOfficeForm.bindFromRequest().field("name").value();
            office.address = newOfficeForm.bindFromRequest().field("address").value();
            office.linkOffice = PostOffice.findPostOfficeByName(newOfficeForm.bindFromRequest().field("officePost").value());
            Ebean.update(office);
        } else {
            PostOffice newOffice = new PostOffice();
            newOffice.name = newOfficeForm.bindFromRequest().field("name").value();
            newOffice.address = newOfficeForm.bindFromRequest().field("address").value();
            newOffice.linkOffice = PostOffice.findPostOfficeByName(newOfficeForm.bindFromRequest().field("officePost").value());
            Ebean.save(newOffice);
        }*/

        return redirect(routes.Application.adminPostOffice());

    }


}
