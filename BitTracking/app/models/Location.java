package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by USER on 9.9.2015.
 */

@Entity
public class Location extends Model{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @Column
    public Double x;
    @Column
    public Double y;
    @OneToOne(mappedBy="place", cascade=CascadeType.ALL)
    public PostOffice postOffice;

    public Location(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public static Finder<Long, Location> findLocation = new Finder<Long, Location>( Location.class);

    public static Location findLocationById(Long id) {

        Location loc = findLocation.byId(id);
        if (loc != null) {
            return loc;
        }
        return null;
    }
}
