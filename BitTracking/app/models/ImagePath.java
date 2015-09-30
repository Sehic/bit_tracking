package models;


import com.cloudinary.Cloudinary;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.File;
import java.io.IOException;
import java.util.Map;

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

    public String public_id;
    public String secret_image_url;

    public static Cloudinary cloudinary;

    public ImagePath(){

    }

    public static ImagePath create(File image, User u){
        Map result;
        try {
            result = cloudinary.uploader().upload(image,null);
            ImagePath i = new ImagePath();
            i.profilePhoto = u;
            i.public_id = (String) result.get("public_id");
            i.image_url = (String) result.get("url");
            i.secret_image_url = (String) result.get("secure_url");

            i.save();
            return i;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Model.Finder<Long, ImagePath> findImage = new Model.Finder<Long, ImagePath>(Long.class, ImagePath.class);

    public static ImagePath findByUser(User user){
        ImagePath path = findImage.where().eq("profilePhoto", user).findUnique();
        return path;
    }

}
