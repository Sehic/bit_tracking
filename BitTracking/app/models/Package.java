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

    public static Finder<Long, Package> finder = new Finder<Long, Package>(Package.class);

    public static List<Package> findByPostOffice(Long id) {
        return finder.where().eq("postOffice", id).findList();
    }

    public static Package findPackageById(Long id){
        return finder.where().eq("id", id).findUnique();
    }







}
