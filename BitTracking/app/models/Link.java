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

    @Column
    public String startOffice;

    @Column
    public double distance;

    public Link(String argStart, String argTarget, double argDistance) {
        startOffice = argStart;
        target = argTarget;
        distance = argDistance;
    }

    public static Finder<String, Link> finder = new Finder<String, Link>(Link.class);

    public static List<Link> findByStartOffice(String startOffice) {
        return finder.where().eq("startOffice", startOffice).findList();
    }

    public static List<Link> findByTargetOffice(String target) {
        return finder.where().eq("target", target).findList();
    }

    public static Link findByStartAndTargetOffice(String start, String target) {
        return finder.where().eq("startOffice", start).eq("target", target).findUnique();
    }
}
