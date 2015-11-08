package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

/**
 * Created by USER on 28.10.2015.
 */

@Entity
public class ApiToken extends Model {

    @Id
    public Long id;

    @Column(nullable = false)
    public String token;


    public static Finder<String, ApiToken> finder = new Finder<>(ApiToken.class);

    public static ApiToken findApiToken(String token){
        return finder.where().eq("token", token).findUnique();
    }
}
