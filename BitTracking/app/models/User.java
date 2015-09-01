package models;


import javax.persistence.*;

import com.avaje.ebean.Ebean;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.Model;

import java.util.List;

@Entity
public class User extends Model {
    @Id
    public Long id;
    @Column(length = 50)
    public String firstName;
    @Column(length = 50)
    public String lastName;
    @Column(length = 50)
    @Constraints.MinLength(6)
    @Constraints.MaxLength(28)
    public String password;
    @Column(length = 50)
    public String email;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String password, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
    }

    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);

    /**
     * Method that checks if user exists in database
     * @param email - email address from inserted user
     * @param password - inserted user password
     * @return - user if it finds him, otherwise null
     */
    public static User findEmailAndPassword(String email, String password) {

        List<User> listEmail = find.where().eq("email", email).findList();
        List<User> listPassword = find.where().eq("password", password).findList();
        if (listEmail.size()==0 || listPassword.size()==0){
            return null;
        }
        return (User)(listEmail.get(0));
    }





}