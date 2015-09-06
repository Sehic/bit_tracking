package models;

import com.avaje.ebean.Model.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mladen13 on 6.9.2015.
 */
@Entity
public class PostOffice {
    @Id
    public Long id;
    @Column
    public String name;
    @Column
    public String address;
    @OneToMany(mappedBy = "postOffice", cascade = CascadeType.ALL)
    public List<User> officeWorkers = new ArrayList<>();

    public PostOffice(){

    }

    public PostOffice(String name, String address) {

        this.name = name;
        this.address = address;
    }

    public static Finder<String, PostOffice> findOffice = new Finder<String, PostOffice>(String.class, PostOffice.class);

    public static PostOffice findPostOffice(String id) {

        PostOffice office = findOffice.byId(id);
        if (office != null) {
            return office;
        }
        return null;
    }

}
