package models;


import javax.persistence.*;

import com.avaje.ebean.Ebean;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.Model;

import java.lang.Override;
import java.lang.String;
import java.util.List;

@Entity
public class User extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    public User(String firstName, String lastName, String password, String email) {
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

        List<User> list = find.where().eq("email", email).eq("password", password).findList();
        if (list.size()==0){
            return null;
        }
        return (User)(list.get(0));
    }


    /**
     * This method checks if the entered email exists in the database
     * @param email email from user input
     * @return null if email doesnt exist in database, otherwise 1
     */
    public static User checkEmail(String email) {
        List<User> listEmail = find.where().eq("email", email).findList();
        if (listEmail.size()==0){
            return null;
        }
        return (User)(listEmail.get(0));
    }


    @Override
    public String toString() {
        return "id=" + id +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", password=" + password +
                ", email=" + email;
    }
}