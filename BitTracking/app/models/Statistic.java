package models;

import com.avaje.ebean.Model;
import helpers.PackageType;
import helpers.StatusHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 10.10.2015.
 */
@Entity
public class Statistic  extends Model{

    @Id
    public Long id;

    public int numOfPostOffices;
    public int numOfAdmins;
    public int numOfOfficeWorkers;
    public int numOfDeliveryWorkers;
    public int numOnRoutePackages;
    public int numReadyForShippingPackages;
    public int numOutForDeliveryPackages;
    public int numDeliveredPackages;


    public PostOffice postOfficeStatistics;

    public List<PostOffice> postOfficeList = new ArrayList<>();
    public List<User> userList = new ArrayList<>();
    public List<Package> packageList = new ArrayList<>();


    public static Finder<Long, Statistic> statisticFinder = new Finder<>(Statistic.class);
    public static List<Statistic> statisticList = new ArrayList<>();

    /**
     * An empty Statistic constructor
     */
    public Statistic(){

    }

    public Statistic(int numOfPostOffices) {
        super();
        this.numOfPostOffices = 0;
    }

    public Statistic(PostOffice postOfficeStatistics) {
        super();
        this.numOfPostOffices = 0;
        this.postOfficeStatistics = postOfficeStatistics;
    }

    /**
     * Statistic constructor
     * @param postOfficeList - List of all Post Offices
     * @param userList - List of all Users
     * @param packageList - List of all Packages
     */
    public Statistic(List<PostOffice> postOfficeList, List<User> userList, List<Package> packageList) {
        this.postOfficeList = postOfficeList;
        this.userList = userList;
        this.packageList = packageList;
    }

    /**
     * This method will create a new Statistic for PostOffice, User and Package lists
     * @param  list of postOffices, users and packages
     * @return new statistic
     */
    public static Statistic createStatistic(List<PostOffice> postOfficeList, List<User> userList, List<Package> packageList){
        Statistic statistic = new Statistic(postOfficeList, userList, packageList);
        statistic.save();
        return statistic;
    }

    /*
    * This method will reset Statistic for a single PostOffice
    * @param postOfficeStatistic
    */
    public static Statistic createPostOfficeStatistic(PostOffice postOffice){

        Statistic statistic = new Statistic(postOffice);
        statistic.save();
        return statistic;
    }

    public static List<Statistic> createUserStatistic(){
        List<Statistic> statisticAllUsers = new ArrayList<>();
        List<User> userList = User.find.findList();
        for(int i = 0; i < userList.size(); i++){
            statisticAllUsers.add(userList.get(i).userStatistic);
        }
        return statisticAllUsers;
    }
    /**
     * This method is used to ge statistic list of office workers
     * @return office worker list
     */
    public static List<Statistic> createOfficeWorkerStatistic(){
        List<Statistic> statisticOfficeWorker = new ArrayList<>();
        List<User> userList = User.find.where().eq("typeOfUser", UserType.OFFICE_WORKER).findList();
        for(int i = 0; i < userList.size();i++){
            statisticOfficeWorker.add(userList.get(i).userStatistic);
        }
        return statisticOfficeWorker;
    }

    /**
     * This method is used to ge statistic list of delivery workers
     * @return delivery worker list
     */
    public static List<Statistic> createDeliveryWorkerStatistic(){
        List<Statistic> statisticDeliveryWorker = new ArrayList<>();
        List<User> userList = User.find.where().eq("typeOfUser", UserType.DELIVERY_WORKER).findList();
        for(int i = 0; i < userList.size();i++){
            statisticDeliveryWorker.add(userList.get(i).userStatistic);
        }
        return statisticDeliveryWorker;
    }

    /**
     * This method is used to ge statistic list of users
     * @return office statistics users list
     */
    public static List<Statistic> createAdminStatistic(){
        List<Statistic> statisticAdmin = new ArrayList<>();
        List<User> userList = User.find.where().eq("typeOfUser", UserType.ADMIN).findList();
        for(int i = 0; i < userList.size();i++){
            statisticAdmin.add(userList.get(i).userStatistic);
        }
        return statisticAdmin;
    }

    /**
     * This method is used to ge statistic list of registered users
     * @return registered users list
     */
    public static List<Statistic> createRegistersUsersStatistic(){
        List<Statistic> statisticUser = new ArrayList<>();
        List<User> userList = User.find.where().eq("typeOfUser", UserType.REGISTERED_USER).findList();
        for(int i = 0; i < userList.size();i++){
            statisticUser.add(userList.get(i).userStatistic);
        }
        return statisticUser;
    }

    /**
     * This method is used to get statistic list for all post offices
     * @return list of all post offices
     */
    public static List<Statistic> createPostOfficeStatistic(){
        List<Statistic> statisticPostOffice = new ArrayList<>();
        List<PostOffice> postOfficeList = PostOffice.findOffice.findList();
        for(int i = 0; i < postOfficeList.size();i++){
            statisticPostOffice.add(postOfficeList.get(i).postOfficeStatistic);
        }
        return statisticPostOffice;
    }

    /**
     *This method is used to get statistic list for all Packages
     * @return list of all packages
     */
    public static List<Statistic> createPackageStatistic(){
        List<Statistic> statisticsPackage = new ArrayList<>();
        List<Package> packageList = Package.finder.findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsPackage.add(packageList.get(i).packageStatistics);
        }
        return statisticsPackage;
    }

    /**
     *This method is used to get statistic list for boxes
     * @return list of box type packages
     */
    public static List<Statistic> createPackageBoxStatistic(){
        List<Statistic> statisticsBox = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageType", PackageType.BOX).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsBox.add(packageList.get(i).packageStatistics);
        }
        return statisticsBox;
    }

    /**
     *This method is used to get statistic list for envelopes
     * @return list of envelope type packages
     */
    public static List<Statistic> createPackageEnvelopeStatistic(){
        List<Statistic> statisticsEnvelope = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageType", PackageType.ENVELOPE).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsEnvelope.add(packageList.get(i).packageStatistics);
        }
        return statisticsEnvelope;
    }

    /**
     *This method is used to get statistic list for flyers
     * @return list of flyer type of packages
     */
    public static List<Statistic> createPackageFlyerStatistic(){
        List<Statistic> statisticsFlyer = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageType", PackageType.FLYER).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsFlyer.add(packageList.get(i).packageStatistics);
        }
        return statisticsFlyer;
    }

    /**
     *This method is used to get statistic list for tubes
     * @return list of tube type of packages
     */
    public static List<Statistic> createPackageTubeStatistic(){
        List<Statistic> statisticsTube = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageType", PackageType.TUBE).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsTube.add(packageList.get(i).packageStatistics);
        }
        return statisticsTube;
    }

    public static List<Statistic> createPackageDeliveredStatistics(){
        List<Statistic> statisticsDelivered = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageStatus", StatusHelper.DELIVERED).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsDelivered.add(packageList.get(i).packageStatistics);
        }
        return statisticsDelivered;
    }

    public static List<Statistic> createPackageOnRouteStatistics(){
        List<Statistic> statisticsOnRoute = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageStatus", StatusHelper.ON_ROUTE).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsOnRoute.add(packageList.get(i).packageStatistics);
        }
        return statisticsOnRoute;
    }

    public static List<Statistic> createPackageOutForDeliveryStatistics(){
        List<Statistic> statisticsOutForDelivery = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageStatus", StatusHelper.OUT_FOR_DELIVERY).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsOutForDelivery.add(packageList.get(i).packageStatistics);
        }
        return statisticsOutForDelivery;
    }

    public static List<Statistic> createPackageReadyForShippStatistics(){
        List<Statistic> statisticsReadyForShipp = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageStatus", StatusHelper.READY_FOR_SHIPPING).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsReadyForShipp.add(packageList.get(i).packageStatistics);
        }
        return statisticsReadyForShipp;
    }

    public static List<Statistic> createPackageReceivedStatistics(){
        List<Statistic> statisticsReceived = new ArrayList<>();
        List<Package> packageList = Package.finder.where().eq("packageStatus", StatusHelper.RECEIVED).findList();
        for(int i = 0; i < packageList.size(); i++){
            statisticsReceived.add(packageList.get(i).packageStatistics);
        }
        return statisticsReceived;
    }




    //***********Getters $ setters***************

    public int getNumOfPostOffices() {
        return numOfPostOffices;
    }

    public void setNumOfPostOffices(int numOfPostOffices) {
        this.numOfPostOffices = numOfPostOffices;
    }

    public int getNumOfAdmins() {
        return numOfAdmins;
    }

    public void setNumOfAdmins(int numOfAdmins) {
        this.numOfAdmins = numOfAdmins;
    }

    public int getNumOfOfficeWorkers() {
        return numOfOfficeWorkers;
    }

    public void setNumOfOfficeWorkers(int numOfOfficeWorkers) {
        this.numOfOfficeWorkers = numOfOfficeWorkers;
    }

    public int getNumOfDeliveryWorkers() {
        return numOfDeliveryWorkers;
    }

    public void setNumOfDeliveryWorkers(int numOfDeliveryWorkers) {
        this.numOfDeliveryWorkers = numOfDeliveryWorkers;
    }

    public int getNumOnRoutePackages() {
        return numOnRoutePackages;
    }

    public void setNumOnRoutePackages(int numOnRoutePackages) {
        this.numOnRoutePackages = numOnRoutePackages;
    }

    public int getNumReadyForShippingPackages() {
        return numReadyForShippingPackages;
    }

    public void setNumReadyForShippingPackages(int numReadyForShippingPackages) {
        this.numReadyForShippingPackages = numReadyForShippingPackages;
    }

    public int getNumOutForDeliveryPackages() {
        return numOutForDeliveryPackages;
    }

    public void setNumOutForDeliveryPackages(int numOutForDeliveryPackages) {
        this.numOutForDeliveryPackages = numOutForDeliveryPackages;
    }

    public int getNumDeliveredPackages() {
        return numDeliveredPackages;
    }

    public void setNumDeliveredPackages(int numDeliveredPackages) {
        this.numDeliveredPackages = numDeliveredPackages;
    }

}
