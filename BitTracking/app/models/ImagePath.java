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

    public static ImagePath create(String public_id, String image_url, String secret_image_url){
        ImagePath i = new ImagePath();
        i.public_id = public_id;
        i.image_url = image_url;
        i.secret_image_url = secret_image_url;
        i.save();
        return i;
    }

    public static ImagePath create(Map uploadResult){
        ImagePath i = new ImagePath();
        i.public_id = (String) uploadResult.get("public_id");
        i.image_url = (String) uploadResult.get("url");
        i.image_url = (String) uploadResult.get("secure_url");

        i.save();
        return i;
    }

    public static ImagePath create(File image){
        Map result;
        try {
            result = cloudinary.uploader().upload(image,null);
            return create(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public String getImage(){
        String url = cloudinary.url().format("jpg").transformation(new Transformation().width(100).height(100).crop("fit").generate(public_id));
        return url;
    }*/

    public ImagePath(String image_url, User profilePhoto){
        this.image_url = image_url;
        this.profilePhoto = profilePhoto;
    }

    public static Model.Finder<Long, ImagePath> findImage = new Model.Finder<Long, ImagePath>(Long.class, ImagePath.class);

    public static ImagePath findByUser(User user){
        ImagePath path = findImage.where().eq("profilePhoto", user).findUnique();
        return path;
    }

}
