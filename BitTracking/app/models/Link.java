package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by USER on 20.10.2015.
 */
@Entity
public class Link extends Model {

    @Id
    public Long id;

    @Column
    public String target;

    @ManyToOne
    public Office startOffice;

    @Column
    public double weight;

    public Link(Office argStart, String argTarget, double argWeight) {
        startOffice = argStart;
        target = argTarget;
        weight = argWeight;
    }

    public static Finder<String, Link> finder = new Finder<String, Link>(Link.class);

    public static Link findById(Long id) {
        return finder.where().eq("id", id).findUnique();
    }

    public static List<Link> findByStartOffice(String startOffice) {
        return finder.where().eq("startOffice", startOffice).findList();
    }
}
