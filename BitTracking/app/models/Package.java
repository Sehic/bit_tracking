package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by USER on 9.9.2015.
 */
@Entity
public class Package extends Model {

    @Id
    public Long id;

    @ManyToOne
    public PostOffice postOffice;

    @Column
    public String trackingNum;

    @Column(length = 255)
    @Constraints.Required
    public String destination;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('READY_FOR_SHIPING', 'ON_ROUTE', 'OUT_FOR_DELIVERY', 'DELIVERED')")
    public Status status = Status.READY_FOR_SHIPING;

    public static Finder<Long, Package> finder = new Finder<Long, Package>(Package.class);

    public static List<Package> findByPostOffice(PostOffice office) {
        return finder.where().eq("postOffice", office).findList();
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



    public enum Status {
        READY_FOR_SHIPING,
        ON_ROUTE,
        OUT_FOR_DELIVERY,
        DELIVERED
    }




}
