package models;

import com.avaje.ebean.Model.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

    public static Finder<Long, PostOffice> findOffice = new Finder<Long, PostOffice>(Long.class, PostOffice.class);

    public static PostOffice findPostOffice(Long id) {

        PostOffice office = findOffice.byId(id);
        if (office != null) {
            return office;
        }
        return null;
    }

    public static List<PostOffice> findAllOffices(){
      return findOffice.all();
    }

}
