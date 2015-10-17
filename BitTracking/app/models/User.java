package models;


import javax.persistence.*;

import com.avaje.ebean.Ebean;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.*;
import play.db.ebean.Model;

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

    @ManyToOne
    public Country country;
    @Column(length = 50)
    public String phoneNumber = null;
    @Column
    public Boolean numberValidated = false;
    @Column
    public String validationCode;

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

    @ManyToMany(mappedBy = "users")
    public List<Package> packages = new ArrayList<>();

    @Column(unique = true)
    public String token;

    @Column
    public Boolean validated = false;

    @Column
    public String drivingOffice;




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


    public static Finder<String, User> find = new Finder<String, User>(String.class, User.class);


    public static List<User> findUsersByPostOffice(PostOffice office) {
        return find.where().eq("postOffice", office).findList();
    }

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

    /**
     * This method checks if entered character are only letters from A to  Z
     * @param name
     * @return
     */
    public static boolean checkName(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) < 31 || (name.charAt(i) > 32 && name.charAt(i) < 65) || (name.charAt(i) > 90 &&
                    name.charAt(i) < 97) || (name.charAt(i) > 122 && name.charAt(i) < 262) ||
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
     * This method checks if entered character are only numbers, or spaces,
     * and if number of entered digits is valid (should be min 8).
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 8) {
            return false;
        }
        int count = 0;
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (phoneNumber.charAt(i) < 31 || (phoneNumber.charAt(i) > 32 && phoneNumber.charAt(i) < 48) ||
                    phoneNumber.charAt(i) > 57) {
                return false;
            }
            if (phoneNumber.charAt(i) > 47 && phoneNumber.charAt(i) < 58) {
                count++;
            }
        }
        if (count < 8) {
            return false;
        }
        return true;
    }


    /**
     * Checks if password has more then 6 letters
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
        String id = longId.toString();
        User user = find.byId(id);
        if (user != null) {
            return user;
        }
        return null;
    }

    public static List<User> findOfficeWorkers() {
        return find.where().eq("typeOfUser", UserType.OFFICE_WORKER).findList();
    }

    public static User findByToken(String token) {
        return find.where().eq("token", token).findUnique();
    }

    public static User findByValidationCode(String validationCode) {
        return find.where().eq("validationCode", validationCode).findUnique();
    }


    @Override
    public String toString() {
        return "id=" + id +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", password=" + password +
                ", email=" + email;
    }

    public static class UserFormHelper{

        public String firstName;
        public String lastName;
        public String password;
        public String email;
    }
}