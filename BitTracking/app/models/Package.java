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
    public String trackingNum;

    @Column(length = 255)
    @Constraints.Required
    public String destination;

    @Enumerated(EnumType.STRING)
    public StatusHelper status;

    @ManyToMany
    public List<User> deliveryWorkers = new ArrayList<>();

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
        return trackingNum + " " + destination;
    }







}
