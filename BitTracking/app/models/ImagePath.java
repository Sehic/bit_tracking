package models;


import play.data.validation.Constraints.*;
import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by Mladen13 on 6.9.2015.
 */
@Entity
public class ImagePath extends Model {
    @Id
    public Long id;
    @Required
    public String image_url;
    @ManyToOne
    public User profilePhoto;

    public ImagePath(){

    }

    public static Finder<Long, ImagePath> finder = new Finder<Long, ImagePath>(ImagePath.class);

    public static ImagePath findByUser(User user){
        ImagePath path = finder.where().eq("profilePhoto", user).findUnique();
        return path;
    }
}
