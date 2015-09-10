package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by USER on 9.9.2015.
 */

@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @Column
    public String name;
    @Column
    public Double x;
    @Column
    public Double y;
    @OneToOne(mappedBy="place", cascade=CascadeType.ALL)
    public PostOffice postOffice;

    public Location(String name, Double x, Double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public static Model.Finder<Long, Location> findLocation = new Model.Finder<Long, Location>(Long.class, Location.class);

    public static Location findLocationById(Long id) {

        Location loc = findLocation.byId(id);
        if (loc != null) {
            return loc;
        }
        return null;
    }
}
