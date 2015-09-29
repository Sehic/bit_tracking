package helpers;

import com.avaje.ebean.annotation.EnumValue;

/**
 * Created by USER on 29.9.2015.
 */
public enum PackageType {
    @EnumValue("1")
    BOX,
    @EnumValue("2")
    ENVELOPE,
    @EnumValue("3")
    FLYER,
    @EnumValue("4")
    TUBE
}
