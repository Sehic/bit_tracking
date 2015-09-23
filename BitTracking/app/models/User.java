package models;


import javax.persistence.*;

import com.avaje.ebean.Model;
import controllers.UserController;
import play.data.validation.Constraints;
import play.db.*;

import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Column(length = 50)
    public String firstName;

    @Constraints.Required
    @Column(length = 50)
    public String lastName;

    @Constraints.Required
    @Column(length = 50)
    @Constraints.MinLength(6)
    @Constraints.MaxLength(28)
    public String password;

    @Constraints.Required
    @Column(length = 50)
    public String email;

    @Column
    @Enumerated(EnumType.STRING)
    public UserType typeOfUser;

    @ManyToOne
    public PostOffice postOffice;

    @OneToMany(mappedBy = "profilePhoto", cascade = CascadeType.ALL)
    public ImagePath imagePath;

    @ManyToMany(mappedBy = "deliveryWorkers")
    public List<Package> packages = new ArrayList<>();

    public String repassword;

    public static Finder<String, User> finder = new Finder<String, User>(User.class);


    public User() {
    }

    /**
     * Constructor for registered user
     *
     * @param firstName - represents his first name
     * @param lastName  - last name
     * @param password  - password
     * @param email     - email address
     */
    public User(String firstName, String lastName, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.typeOfUser = UserType.REGISTERED_USER;
    }

    /**
     * Constructor that creates office worker
     *
     * @param firstName
     * @param lastName
     * @param password
     * @param email
     * @param postOffice - office where he works
     */
    public User(String firstName, String lastName, String password, String email, PostOffice postOffice) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.typeOfUser = UserType.OFFICE_WORKER;
        this.postOffice = postOffice;

    }


    public static Finder<String, User> find = new Finder<>(User.class);


    /**
     * Method that checks if user exists in database
     *
     * @param email    - email address from inserted user
     * @param password - inserted user password
     * @return - user if it finds him, otherwise null
     */
    public static User findEmailAndPassword(String email, String password) {
        return find.where().eq("email", email).eq("password", password).findUnique();
    }


    /**
     * This method checks if the entered email exists in the database
     *
     * @param email email from user input
     * @return null if email doesnt exist in database, otherwise 1
     */
    public static User checkEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    /**
     * This method checks if entered character are only letters from A to  Z
     *
     * @param name
     * @return
     */
    public static boolean checkName(String name) {
        for (int i = 0; i < name.length(); i++) {
            if ((name.charAt(i) < 65) ||
                    (name.charAt(i) > 90 && name.charAt(i) < 97) ||
                    (name.charAt(i) > 122 && name.charAt(i) < 262) ||
                    (name.charAt(i) > 263 && name.charAt(i) < 268) ||
                    (name.charAt(i) > 269 && name.charAt(i) < 272) ||
                    (name.charAt(i) > 273 && name.charAt(i) < 352) ||
                    (name.charAt(i) > 353 && name.charAt(i) < 381) ||
                    name.charAt(i) > 382) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if password has more then 6 letters
     *
     * @param password
     * @return
     */
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

    public static User findById(Long longId) {
        User user = find.byId(longId.toString());
        if (user != null) {
            return user;
        }
        return null;
    }

    public static List<User> findOfficeWorkers() {
        return find.where().eq("typeOfUser", UserType.OFFICE_WORKER).findList();
    }

    public static List<User> findDeliveryWorkers() {
        return find.where().eq("typeOfUser", UserType.DELIVERY_WORKER).findList();
    }

    public static List<User> findRegisteredUsers() {
        return find.where().eq("typeOfUser", UserType.REGISTERED_USER).findList();
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