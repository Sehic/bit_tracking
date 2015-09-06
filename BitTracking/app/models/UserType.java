package models;

import com.avaje.ebean.annotation.EnumValue;

/**
 * Created by Mladen13 on 6.9.2015.
 */
public enum UserType {
    @EnumValue("1")
    ADMIN,
    @EnumValue("2")
    OFFICE_WORKER,
    @EnumValue("3")
    REGISTERED_USER
}
