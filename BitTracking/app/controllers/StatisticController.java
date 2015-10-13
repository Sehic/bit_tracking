package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import helpers.SessionHelper;
import models.*;
import models.Package;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.adminindex;
import views.html.statistic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 10.10.2015.
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
/*
    public Result showUserStatistic(){
        List<User> userList = User.find.findList();
        return ok(statistic.render(PostOffice.findOffice.findList(), User.find.findList(), Package.finder.findList(), Statistic.statisticFinder.findList()));
    }

    public Result showUserStatisticList(){
        return ok(statistic.render(PostOffice.findOffice.findList(), User.find.findList(), Package.finder.findList(),Statistic.statisticFinder.findList()));
    }

    public Result showDeliveryStatisticList(){
        return ok(statistic.render(PostOffice.findOffice.findList(), User.find.findList(), Package.finder.findList(),Statistic.statisticFinder.findList()));
    }
*/

}
