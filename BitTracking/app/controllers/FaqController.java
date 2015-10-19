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

    public static Form faqForm = new Form<Faq>(Faq.class);


    /**
     * Method renders admin panel where all FAQs are listed
     *
     * @return
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
        try {
            DynamicForm form = Form.form().bindFromRequest();
            if (form.hasErrors() || form.hasGlobalErrors()) {
                flash("error", Messages.get("error"));
                return redirect(routes.FaqController.addNewFaq());
            }
            String question = form.data().get("question");
            String answer = form.data().get("answer");

            Faq.createFaq(question, answer);
            flash("success", Messages.get("New FAQ added successfully"));
            return ok(faqnew.render(form));

        } catch (IllegalStateException e) {
            flash("error", Messages.get("Please fill out all fields in a form"));
            return redirect(routes.FaqController.addNewFaq());
        }
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
            return badRequest(faqnew.render(saveForm));
        } else {
            String question = saveForm.field("question").value();
            String answer = saveForm.field("answer").value();
            Faq.createFaq(question, answer);
        }
        return redirect(routes.FaqController.adminFaqView());
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


    @Security.Authenticated(Authenticators.AdminFilter.class)
    public Result updateFaq(Long id) {
        DynamicForm updateForm = Form.form().bindFromRequest();
        if(updateForm.hasErrors()){
            return badRequest(faqnew.render(updateForm));
        }else{
            Faq faq = Faq.findById(id);

            faq.question = updateForm.field("question").value();
            faq.answer = updateForm.field("answer").value();
            faq.update();
        }
        Logger.debug(id + "");
        Logger.debug("SSSS" + updateForm.field("question").value());
        Logger.debug("SSSS" + updateForm.field("answer").value());
        return redirect(routes.FaqController.adminFaqView());
    }

    /**
     * Method deletes FAQ from database
     * @param id - FAQ's id that will be deleted
     * @return admin panel page with all FAQs
     */
    public Result deleteFaq(Long id) {
        try {
            Faq.deleteFaq(id);
            flash("success", Messages.get("successfully deleted"));
            return ok(faqview.render(Faq.allFaqs()));
        } catch (Exception ex) {
            flash("error", Messages.get("Error"));
            return redirect(routes.FaqController.adminFaqView());
        }
    }
}

