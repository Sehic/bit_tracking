package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Rasta  on 29.10.2015.
 */
@Entity
public class ApplicationLog extends Model {

    @Id
    public Long id;

    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date date;

    @Column
    public String comment;

    public ApplicationLog(){

    }

    public ApplicationLog (String comment){
        this.comment = comment;
        this.date = new Date();
    }

    public static Finder<Long, ApplicationLog> logFinder = new Finder<Long, ApplicationLog>(ApplicationLog.class);
}
