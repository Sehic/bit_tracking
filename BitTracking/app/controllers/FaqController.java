package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import helpers.Authenticators;
import helpers.SessionHelper;
import models.Faq;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.i18n.Messages;

import play.mvc.Security;
import views.html.*;

import java.util.List;

import play.Logger;

/**
 * Created by Emina on 17.10.2015.
 */
public class FaqController extends Controller {

    public static final Form faqForm = Form.form(Faq.class);


    /**
     * Method renders admin panel where all FAQs are listed
     *
     * @return faq list page on adminpanel
     */
    public Result adminFaqView() {
        List<Faq> faqList = Faq.faqFinder.findList();
        return ok(faqview.render(faqList));
    }

    /**
     * Adding a new faq to the list (To database)
     *
     * @return
     */

    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result addNewFaq() {
        return ok(faqnew.render());
    }

    /**
     * Method used to post a new FAQ.
     *
     * @return admin panel with list of all FAQs, otherwise warning message
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result saveFaq() {
        DynamicForm saveForm = Form.form().bindFromRequest();
        if (saveForm.hasErrors()) {
            return badRequest(faqnew.render());
        } else {
            Faq faq  = new Faq();
            faq.question = saveForm.get("question");
            faq.answer = saveForm.get("answer");
            faq.save();
        }
        List<Faq> faqList = Faq.faqFinder.findList();
        return ok(faqview.render(faqList));
    }


    /**
     * This method will get user to faqedit page
     *
     * @param id
     * @return
     */
    public Result editFaq(Long id) {
        Faq faq = Faq.findById(id);
        Form<Faq> fillForm = faqForm.fill(faq);
        return ok(faqedit.render(faq, fillForm));
    }

    /**
     * Method to update edited FAQ
     * @param id - FAQ's id that will be edited
     * @return admin panel view with all FAQ's
     */
    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result updateFaq(Long id) {
        DynamicForm updateForm = Form.form().bindFromRequest();
        Faq faq = Faq.faqFinder.byId(id);
        faq.question = updateForm.get("question");
        faq.answer = updateForm.get("answer");
        faq.update();
        List<Faq> faqList = Faq.faqFinder.findList();
        return ok(faqview.render(faqList));
    }

    /**
     * Method deletes FAQ from database
     * @param id - FAQ's id that will be deleted
     * @return admin panel page with all FAQs
     */
    public Result deleteFaq(Long id) {
        Faq faq = Faq.findById(id);
        if(faq == null){
            return badRequest(faqview.render(Faq.allFaqs()));
        }
        faq.delete();
        return redirect(routes.FaqController.adminFaqView());

    }

    /**
     * Method will get user to userfaq view with list of all FAQs
     * @return FAQ list page
     */
    public Result userFaqView(){
        List<Faq> faqList = Faq.faqFinder.findList();
        return ok(faqindex.render(faqList));
    }
}