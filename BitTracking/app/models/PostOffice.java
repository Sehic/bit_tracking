package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

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
    @Constraints.Required
    @Column
    public String address;
    @OneToMany(mappedBy = "postOffice")
    public List<User> officeWorkers = new ArrayList<>();

    @OneToMany(mappedBy = "postOfficeId")
    public List<Shipment> shipmentOffices = new ArrayList<>();

    @ManyToMany
    @JoinTable(name="linked_offices",
            joinColumns=@JoinColumn(name="post_officeA_id"),
            inverseJoinColumns=@JoinColumn(name="post_officeB_id")
    )
    public List<PostOffice> postOfficesA = new ArrayList<>();

    @OneToOne
    public Location place;

    public Statistic postOfficeStatistic;

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

    public static Finder<Long, PostOffice> findOffice = new Finder<Long, PostOffice>(Long.class, PostOffice.class);

    public static PostOffice findPostOffice(Long id) {

        PostOffice office = findOffice.byId(id);
        if (office != null) {
            return office;
        }
        return null;
    }

    public static PostOffice findPostOfficeByName(String name){
        return findOffice.where().eq("name", name).findUnique();
    }

    public static PostOffice findPostOfficeByAddress(String address){
        return findOffice.where().eq("address", address).findUnique();
    }


    /**
     * Method used to get Statistics model of this Post Office
     * @return
     */
    public Statistic getStatisticPostOffice(){
        return postOfficeStatistic;
    }

    /**
     * Mehod used to set Statistic of this Post Office
     * @param statisticPostOffice
     */
    public void setStatisticPostOffice(Statistic statisticPostOffice){
        this.postOfficeStatistic = statisticPostOffice;
    }

}
