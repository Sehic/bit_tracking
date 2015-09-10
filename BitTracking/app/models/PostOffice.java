package models;

import com.avaje.ebean.Model.*;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mladen13 on 6.9.2015.
 */
@Entity
public class PostOffice extends Model {
    @Id
    public Long id;
    @Column
    public String name;
    @Column
    public String address;
    @OneToMany(mappedBy = "postOffice", cascade = CascadeType.ALL)
    public List<User> officeWorkers = new ArrayList<>();

    @OneToMany(mappedBy = "postOffice", cascade = CascadeType.ALL)
    public List<Package> packages = new ArrayList<>();

    @OneToOne
    public Location place;

    @ManyToOne
    public PostOffice parentOffice;
    @OneToMany(mappedBy = "parentOffice")
    public List<PostOffice> childOffices;





    public PostOffice(){

    }

    public PostOffice(String name, String address) {

        this.name = name;
        this.address = address;
    }

    public PostOffice(String name, String address, Location place) {
        this.name = name;
        this.address = address;
        this.place = place;
    }

    public PostOffice(String name, String address, Location place, List<PostOffice> offices) {
        this.name = name;
        this.address = address;
        this.place = place;
        this.childOffices = new ArrayList<>(offices);
    }


    public static Model.Finder<Long, PostOffice> findOffice = new Model.Finder<Long, PostOffice>(Long.class, PostOffice.class);

    public static PostOffice findPostOffice(Long id) {

        PostOffice office = findOffice.byId(id);
        if (office != null) {
            return office;
        }
        return null;
    }

}
