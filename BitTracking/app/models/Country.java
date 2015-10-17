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
    @Column(length = 10)
    public String iso2;
    public String countryName;
    public String shortName;
    @Column(length = 10)
    public String iso3;
    @Column(length = 15)
    public String numcode;
    @Column(length = 20)
    public String unmember;
    @Column(length = 20)
    public String callingCode;
    @Column(length = 10)
    public String cctId;
    @OneToMany(mappedBy = "country")
    public List<PostOffice> countryPostOffices = new ArrayList<>();
    @OneToMany(mappedBy = "country")
    public List<User> countryUsers = new ArrayList<>();

    public Country(){

    }

    public static Finder<Long, Country> findCountry = new Finder<>(Country.class);

    public static Country findCountryById(Long id){
        return findCountry.where().eq("id", id).findUnique();
    }

    public static Country findCountryByCode(String code){
        return findCountry.where().eq("countryCode", code).findUnique();
    }

    public static Country findCountryByCallingCode(String code){
        return findCountry.where().eq("callingCode", code).findUnique();
    }
}
