package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 20.10.2015.
 */
@Entity
public class Office extends Model implements Comparable<Office> {

    @Id
    public Long id;

    @Column
    public String name;

    @OneToMany(mappedBy = "startOffice")
    public List<Link> links = new ArrayList<>();

    @Transient
    public double minDistance = Double.POSITIVE_INFINITY;

    @Transient
    public Office previous;

    public Office(String argName) {
        name = argName;
    }

    public String toString() {
        return name;
    }

    public static Finder<String, Office> finder = new Finder<String, Office>(Office.class);

    public int compareTo(Office other) {
        return Double.compare(minDistance, other.minDistance);
    }

    public static Office findById(Long id) {
        return finder.where().eq("id", id).findUnique();
    }

    public static Office findByName(String name) {
        return finder.where().eq("name", name).findUnique();
    }

}