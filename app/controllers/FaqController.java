package controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import models.Faq;
import models.Location;
import models.Meal;
import models.Restaurant;
import models.User;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.*;
import views.html.*;
import Utilites.AdminFilter;
import Utilites.Session;
import play.data.DynamicForm;
import play.db.ebean.Model.Finder;
import Utilites.*;

/**
 * 
 * @author neldindzekovic
 *
 */
public class FaqController extends Controller {
	
	
	
	/**
	 * This method creates new FAQ.
	 * @return Redirects to FAQ page.
	 */
	@Security.Authenticated(AdminFilter.class)
	public static Result create() {
		DynamicForm inputForm = Form.form().bindFromRequest();
		
		String question = inputForm.data().get("question");
		String answer = inputForm.data().get("answer");
		
		boolean success = Faq.create(question, answer);
		
		if (success == true) {
			flash("successFaq", "Succesfuly created FAQ!");

			return redirect("/admin/" + Session.getCurrentUser(ctx()).email);
		}
		
		flash("failFaq", "Creating FAQ failed!");
		return redirect("/admin/" + Session.getCurrentUser(ctx()).email);

	}
	
	/**
	 * This method deletes FAQ.
	 * @param id Id of FAQ to delete.
	 * @return Redirects to FAQ page.
	 */
	@Security.Authenticated(AdminFilter.class)
	public static Result delete(int id) {
		Faq.delete(id);
		flash("deleteFaq", "Succesfuly deleted FAQ!");

		return redirect("/admin/" + Session.getCurrentUser(ctx()).email);
	}

	@Security.Authenticated(AdminFilter.class)
	public static Result editFaq(int id) {
		DynamicForm inputForm = Form.form().bindFromRequest();
		Faq f = Faq.findById(id);
		String question = inputForm.data().get("question");
		String answer = inputForm.data().get("answer");
		
		f.question = question;
		f.answer = answer;
		f.save();
		
//		boolean success = Faq.edit(f, question, answer);
//		
//		if (success == true) {
			flash("successEditFaq", "Succesfuly edited FAQ!");
			return redirect("/faqEdit/" + f.id);
//		}
//		
//		flash("failEditFaq", "Editing FAQ failed!");		
//
//		return redirect("/admin/" + Session.getCurrentUser(ctx()).email);

	}
	
	public static Result showEditFaq(int id) {		
		String userEmail= Session.getCurrentUser(ctx()).email;
        Faq faqList = Faq.findById(id);
		return ok(faqEdit.render(userEmail, faqList));
	}
}

