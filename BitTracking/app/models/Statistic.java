package models;

import com.avaje.ebean.Model;
import helpers.PackageType;
import helpers.StatusHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Emina on 10.10.2015.
 */
public class Statistic  extends Model{

    public List<PostOffice> postOfficeList = new ArrayList<>();
    public List<User> userList = new ArrayList<>();
    public List<Package> packageList = new ArrayList<>();


    public static Finder<Long, Statistic> statisticFinder = new Finder<>(Statistic.class);

    /**
     * An empty Statistic constructor
     */
    public Statistic(){
    }

    /**
     * Statistic constructor
     * @param postOfficeList - List of Post Offices
     * @param userList - List of  Users
     * @param packageList - List of Packages
     */
    public Statistic(List<PostOffice> postOfficeList, List<User> userList, List<Package> packageList) {
        this.postOfficeList = postOfficeList;
        this.userList = userList;
        this.packageList = packageList;
    }


    /**
     * Method is used to get list of all users.
     * @return list of users
     */
    public static List<User> createUserStatistic(){
        List<User> userList = User.find.findList();
        return userList;
    }
    /**
     * This method is used to ge statistic list of office workers
     * @return office worker list
     */
    public static List<User> createOfficeWorkerStatistic(){
        List<User> userList = User.find.where().eq("typeOfUser", UserType.OFFICE_WORKER).findList();
        return userList;
    }

    /**
     * This method is used to get statistic list of delivery workers
     * @return delivery worker list
     */
    public static List<User> createDeliveryWorkerStatistic(){
        List<User> userList = User.find.where().eq("typeOfUser", UserType.DELIVERY_WORKER).findList();
        return userList;
    }

    /**
     * This method is used to get statistic list of users
     * @return office statistics users list
     */
    public static List<User> createAdminStatistic(){
        List<User> userList = User.find.where().eq("typeOfUser", UserType.ADMIN).findList();
        return userList;
    }

    /**
     * This method is used to get statistic list of registered users
     * @return registered users list
     */
    public static List<User> createRegistersUsersStatistic(){
        List<User> userList = User.find.where().eq("typeOfUser", UserType.REGISTERED_USER).findList();
        return userList;
    }

    /**
     * This method is used to get total number of delivery workers, admins and office workers
     * @return list of delivery workers, admins and office workers
     */
    public static List<User> createEmployeesStatistic(){

        List<User> employeesList = new ArrayList<>();
        for (int i = 0; i < User.find.findList().size(); i++) {
            if(User.find.findList().get(i).typeOfUser != UserType.REGISTERED_USER) {
                employeesList.add(User.find.findList().get(i));
            }
        }
        return employeesList;
    }
    /**
     * This method is used to get statistic list for all post offices
     * @return list of all post offices
     */
    public static List<PostOffice> createPostOfficeStatistic(){
        List<PostOffice> postOfficeList = PostOffice.findOffice.findList();
        return postOfficeList;
    }


    /**
     * This method will calculate number of packages in each office
     * @param office
     * @return number of packages in an office
     */
    public static int packageByPostOffice(PostOffice office){
        List<Shipment> shipmentList = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", office).findList();
        List<Package> packages = new ArrayList<>();

        for(int i =0;i<shipmentList.size();i++){
            packages.add(shipmentList.get(i).packageId);
        }

        return packages.size();
    }

    /**
     * This method is used to get total number of office workers
     * @return list office workers
     */
    public static int officeWorkerByPostOffice(PostOffice office){
        return User.find.where().eq("typeOfUser", UserType.OFFICE_WORKER).eq("postOffice", office).findRowCount();
    }

    /**
     * This method is used to get total number of delivery workers
     * @return list of delivery workers
     */
    public static int deliveryWorkerByPostOffice(PostOffice office){
        List<User> officeWorkerList = User.find.where().eq("typeOfUser", UserType.DELIVERY_WORKER).eq("postOffice", office).findList();
        return officeWorkerList.size();
    }

    /**
     * This method is used to get number of routes
     * @param office
     * @return number of routes
     */
    public static int getNumOfRoutes(PostOffice office) {
        List<Shipment> shipmentList = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).eq("postOfficeId", office).findList();
        return shipmentList.size();
    }


    /**
     *This method is used to get statistic list for all Packages
     * @return list of all packages
     */
    public static List<Package> createPackageStatistic(){
        List<Package> packageList = Package.finder.findList();
        return packageList;
    }

    /**
     *This method is used to get statistic list for boxes
     * @return list of box type packages
     */
    public static List<Package> createPackageBoxStatistic(){
        List<Package> packageList = Package.finder.where().eq("packageType", PackageType.BOX).findList();
        return packageList;
    }

    /**
     *This method is used to get statistic list for envelopes
     * @return list of envelope type packages
     */
    public static List<Package> createPackageEnvelopeStatistic(){
        List<Package> packageList = Package.finder.where().eq("packageType", PackageType.ENVELOPE).findList();
        return packageList;
    }

    /**
     *This method is used to get statistic list for flyers
     * @return list of flyer type of packages
     */
    public static List<Package> createPackageFlyerStatistic(){
        List<Package> packageList = Package.finder.where().eq("packageType", PackageType.FLYER).findList();
        return packageList;
    }

    /**
     *This method is used to get statistic list for tubes
     * @return list of tubes
     */
    public static List<Package> createPackageTubeStatistic(){
        List<Package> packageList = Package.finder.where().eq("packageType", PackageType.TUBE).findList();
        return packageList;
    }

    /**
     *This method is used to get statistic list for packages with status "delivered"
     * @return list of delivered packages
     */
    public static List<Shipment> createPackageDeliveredStatistics(){
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("status", StatusHelper.DELIVERED).findList();
        return shipments;
    }

    /**
     *This method is used to get statistic list for packages with status "On Route"
     * @return list of "on route" packages
     */
    public static List<Shipment> createPackageOnRouteStatistics(){
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("status", StatusHelper.ON_ROUTE).findList();
        return shipments;
    }

    /**
     *This method is used to get statistic list for packages with status "Out For Delivery"
     * @return list of "Out For Delivery" packages
     */
    public static List<Shipment> createPackageOutForDeliveryStatistics(){
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("status", StatusHelper.OUT_FOR_DELIVERY).findList();
        return shipments;
    }

    /**
     *This method is used to get statistic list for packages with status "Ready For Shipping"
     * @return list of "Ready For Shipping" packages
     */
    public static List<Shipment> createPackageReadyForShippStatistics(){
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("status", StatusHelper.READY_FOR_SHIPPING).findList();
        return shipments;
    }

    /**
     *This method is used to get statistic list for packages with status "received"
     * @return list of received packages
     */
    public static List<Shipment> createPackageReceivedStatistics(){
        List<Shipment> shipments = Shipment.shipmentFinder.where().eq("status", StatusHelper.RECEIVED).findList();
        return shipments;
    }



}
