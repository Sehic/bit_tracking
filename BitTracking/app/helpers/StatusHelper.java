package helpers;

import com.avaje.ebean.annotation.EnumValue;

/**
 * Created by Mladen13 on 15.9.2015.
 */
public enum StatusHelper {
    @EnumValue("1")
    READY_FOR_SHIPPING,
    @EnumValue("2")
    ON_ROUTE,
    @EnumValue("3")
    OUT_FOR_DELIVERY,
    @EnumValue("4")
    DELIVERED,
    @EnumValue("5")
    RECEIVED
}
