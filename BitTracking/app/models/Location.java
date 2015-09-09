package models;

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



}
