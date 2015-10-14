package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mladen.teofilovic on 14/10/15.
 */
@Entity
public class Country extends Model {

    @Id
    public Long id;
    public String countryCode;
    public String countryName;
    @OneToMany(mappedBy = "country")
    public List<PostOffice> countryPostOffices = new ArrayList<>();

    public Country(){

    }

    public static Finder<Long, Country> findCountry = new Finder<>(Country.class);

    public static Country findCountryById(Long id){
        return findCountry.where().eq("id", id).findUnique();
    }

    public static Country findCountryByCode(String code){
        return findCountry.where().eq("countryCode", code).findUnique();
    }
}
