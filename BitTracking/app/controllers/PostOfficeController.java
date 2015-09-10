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
import java.util.ArrayList;
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
        return ok(postofficedetails.render(office, PostOffice.findOffice.findList()));
    }

    public Result updateOffice(Long Id){
        PostOffice office = PostOffice.findPostOffice(Id);

        if (office == null) {
            return TODO;
        }
        Form<PostOffice> newOfficeForm = officeForm.fill(office);
        office.name = newOfficeForm.bindFromRequest().field("name").value();
        office.address = newOfficeForm.bindFromRequest().field("address").value();

        String postOffice = newOfficeForm.bindFromRequest().field("postOffice").value();


        PostOffice linkedPostOffice = PostOffice.findOffice.where().eq("name", postOffice).findUnique();
        office.childOffices.add(linkedPostOffice);

        System.out.println(office.name+" "+postOffice+" "+linkedPostOffice);
        Ebean.update(office);
        return redirect(routes.PostOfficeController.postOfficeDetails(office.id));

    }


}
