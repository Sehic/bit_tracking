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
     *
     * @param email    - email address from inserted user
     * @param password - inserted user password
     * @return - user if it finds him, otherwise null
     */
    public static User findEmailAndPassword(String email, String password) {

        List<User> list = find.where().eq("email", email).eq("password", password).findList();
        if (list.size() == 0) {
            return null;
        }
        return (User) (list.get(0));
    }


    /**
     * This method checks if the entered email exists in the database
     *
     * @param email email from user input
     * @return null if email doesnt exist in database, otherwise 1
     */
    public static User checkEmail(String email) {
        List<User> listEmail = find.where().eq("email", email).findList();
        if (listEmail.size() == 0) {
            return null;
        }
        return (User) (listEmail.get(0));
    }

    public static boolean checkName(String name) {


    for (int i = 0; i < name.length(); i++) {
        if ((name.charAt(i) > 64 && name.charAt(i) < 91) || (name.charAt(i) > 96 && name.charAt(i) < 124)){
            return true;
        } else if (name.charAt(i)==262 || name.charAt(i)==263 || name.charAt(i)==268 || name.charAt(i)==269 || name.charAt(i)==272 || name.charAt(i)==273 || name.charAt(i)==352 || name.charAt(i)==353 || name.charAt(i)==381 || name.charAt(i)==382) {
            return true;
        }
    }
    return false;
}

    public static boolean checkPassword(String password) {
        int letters = 0;
        int numbers = 0;
        if (password.length() < 6) {
            return false;
        }
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) > 47 && password.charAt(i) < 58) {
                numbers++;
            } else if ((password.charAt(i) > 64 && password.charAt(i) < 91) || (password.charAt(i) > 96 && password.charAt(i) < 123)) {
                letters++;
            }
        }
        if (letters == 0 || numbers == 0) {
            return false;
        }
        return true;
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