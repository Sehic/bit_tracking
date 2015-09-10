package controllers;


import models.User;
import models.PostOffice;
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
        final PostOffice office = PostOffice.findPostOffice(id);
//        if (office == null) {
//            return notFound(String.format("Office %s does not exists.", id));
//        }
        office.linkOffice = null;
        Ebean.update(office);
        Ebean.delete(office);
        return redirect(routes.Application.adminPostOffice());
    }



    public Result addNewOffice(){
        Form<PostOffice> boundForm = officeForm.bindFromRequest();
        String name = boundForm.bindFromRequest().field("name").value();
        String address = boundForm.bindFromRequest().field("address").value();
        PostOffice p = new PostOffice(name, address);
        Ebean.save(p);
        return redirect(routes.Application.adminPostOffice());
    }


    public Result postOfficeDetails(Long id){
        PostOffice office = PostOffice.findPostOffice(id);
        return ok(postofficedetails.render(office, PostOffice.findOffice.findSet(), PostOffice.linkedOffices(office.name)));
    }

    public Result updateOffice(Long Id){
        PostOffice office = PostOffice.findPostOffice(Id);
        if (office == null) {
            return TODO;
        }
        Form<PostOffice> newOfficeForm = officeForm.fill(office);
        if(office.linkOffice == null || office.linkOffice.equals(PostOffice.findPostOfficeByName(newOfficeForm.bindFromRequest().field("officePost").value()))) {
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
        }
        return redirect(routes.PostOfficeController.postOfficeDetails(office.id));

    }


}
