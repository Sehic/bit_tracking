package controllers;

import models.User;
import models.PostOffice;
import play.*;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import com.avaje.ebean.Ebean;
import play.Logger;

import java.lang.System;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class Application extends Controller {


    static Form<PostOffice> newPostOffice = new Form<PostOffice>(PostOffice.class);
    private static final Form<PostOffice> officeForm =
            Form.form(PostOffice.class);

    public Result index() {
        return ok(index.render());
    }

    public Result login() {
        return ok(login.render(""));
    }

    public Result logout() {
        session().clear();
        return redirect(routes.Application.login());
    }

    public Result register() {
        return ok(register.render());
    }

    public Result adminPanel(){
        return ok(adminindex.render());
    }

    public Result adminMaps(){
        return ok(adminmaps.render());
    }

    public Result adminTables(){
        return ok(admintables.render());
    }

    public Result adminPreferences(){
        return ok(adminpreferences.render());
    }

    public Result adminPostOffice(){
        List<PostOffice> list = PostOffice.findAllOffices();
        return ok(adminpostoffice.render(list));
    }



    public Result deleteOffice(Long id) {
        final PostOffice office = PostOffice.findPostOffice(id);
        if (office == null) {
            return notFound(String.format("Office %s does not exists.", id));
        }
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
        return ok(postofficedetails.render(office));
    }

    public Result updateOffice(Long Id){
        PostOffice office = PostOffice.findPostOffice(Id);

        if (office == null) {
            return TODO;
        }
        Form<PostOffice> filledForm = newPostOffice.fill(office);
        office.name = filledForm.bindFromRequest().field("name").value();
        office.address = filledForm.bindFromRequest().field("address").value();
        System.out.println(office.name+" "+office.address);
        Ebean.update(office);
        return redirect(routes.Application.adminPostOffice());

    }

}
