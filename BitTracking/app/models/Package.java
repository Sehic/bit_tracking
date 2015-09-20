package models;

import helpers.StatusHelper;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 9.9.2015.
 */
@Entity
public class Package extends Model {

    @Id
    public Long id;

    @OneToMany(mappedBy = "packageId")
    public List<Shipment> shipmentPackages = new ArrayList<>();

    @Column
    public String office;

    @Column
    public String trackingNum;

    @Column(length = 255)
    @Constraints.Required
    public String destination;

    @Enumerated(EnumType.STRING)
    public StatusHelper status;

    @ManyToMany
    public List<User> deliveryWorkers = new ArrayList<>();

    @Column
    public Double weight;

    @Column
    public Double price;

    public static Finder<Long, Package> finder = new Finder<Long, Package>(Package.class);

    public static List<Package> findByPostOffice(Long id) {
        return finder.where().eq("postOffice", id).findList();
    }

    public static Package findPackageById(Long id){
        return finder.where().eq("id", id).findUnique();
    }

    public static Package findPackageByTrackingNumber(String num){
        return finder.where().eq("trackingNum", num).findUnique();
    }

    @Override
    public String toString() {
        if (shipmentPackages.get(shipmentPackages.size()-1).status == StatusHelper.DELIVERED) {
            return trackingNum + " " + shipmentPackages.get(0).postOfficeId.name +" " +destination + " " + StatusHelper.DELIVERED.toString();
        } else {
            return trackingNum + " " + shipmentPackages.get(0).postOfficeId.name + " " + destination + " " + StatusHelper.OUT_FOR_DELIVERY.toString();
        }
    }







}
