package models;

import helpers.PackageType;
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
    public String recipientAddress;

    @Column(length = 255)
    @Constraints.Required
    public String destination;

    @ManyToMany
    public List<User> users = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    public Double weight;

    @Column(precision = 10, scale = 2)
    public Double price;

    @Column
    @Enumerated(EnumType.STRING)
    public PackageType packageType;

    @Column(length = 255)
    public String senderName;

    @Column(length = 255)
    public String recipientName;

    public Boolean approved = null;
    public Boolean seen = null;

    public static Finder<Long, Package> finder = new Finder<Long, Package>(Package.class);

    public static List<Package> findByPostOffice(Long id) {
        return finder.where().eq("postOffice", id).findList();
    }

    public static Package findPackageById(Long id) {
        return finder.where().eq("id", id).findUnique();
    }

    public static Package findPackageByTrackingNumber(String num) {
        return finder.where().eq("trackingNum", num).findUnique();
    }

    public static List<Package> findPackageByPackageType(String type) {
        return finder.where().eq("packageType", type).findList();
    }

    public static List<Package> findPackagesByUser(User user) {
        return finder.where().eq("users", user).findList();
    }

    public static List<Package> findPackagesWaitingForApproval(){
        return finder.where().eq("approved", null).findList();
    }

    @Override
    public String toString() {
        if (shipmentPackages.get(shipmentPackages.size() - 1).status == StatusHelper.DELIVERED) {
            return trackingNum + "," + weight + "," + price + "," + shipmentPackages.get(0).postOfficeId.name + "," + destination + "," + shipmentPackages.get(0).dateCreated + "," + StatusHelper.DELIVERED.toString();
        } else {
            return trackingNum + "," + weight + "," + price + "," + shipmentPackages.get(0).postOfficeId.name + "," + destination + "," + shipmentPackages.get(0).dateCreated + "," + StatusHelper.OUT_FOR_DELIVERY.toString();
        }
    }


}
