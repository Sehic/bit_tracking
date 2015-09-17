package models;

import helpers.StatusHelper;
import play.data.format.Formats;
import play.db.ebean.Model;

import javax.persistence.*;

import java.util.Date;

/**
 * Created by Mladen13 on 16.9.2015.
 */
@Entity
public class Shipment extends Model {

    @Id
    public Long id;
    @ManyToOne
    public PostOffice postOfficeId;
    @ManyToOne
    public Package packageId;
    @Column
    @Enumerated(EnumType.STRING)
    public StatusHelper status;
    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date dateCreated;

    public Shipment(){

    }

    public static Finder<Long, Shipment> shipmentFinder = new Finder<Long, Shipment>(Shipment.class);
}
