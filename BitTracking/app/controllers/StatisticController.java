package controllers;

import models.Package;
import models.PostOffice;
import models.Statistic;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.statistic;

import java.util.List;

/**
 * Created by Emina on 10.10.2015.
 */
public class StatisticController extends Controller {

    public static List<Statistic> allPostOfficesStatistic;

    private static final Form<Statistic> statisticForm = Form.form(Statistic.class);

    /**
     * Method for Post Offices Statistics
     * @return all post offices and number of linked post offices
     */
    public Result showPostOfficeStatistic(){
        return ok(statistic.render(PostOffice.findOffice.findList(), User.find.findList(), Package.finder.findList()));
    }

}
