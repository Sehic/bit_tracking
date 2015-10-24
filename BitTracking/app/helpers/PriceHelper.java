package helpers;

/**
 * Created by Mladen13 on 24.10.2015.
 */
public class PriceHelper {

    public static double calculatePrice(double weight, double distance){
        if(distance<500) {
           return getPrice(weight, distance);
        }
        if(distance>=500){
            return getPrice(weight, distance);
        }
        return -1;
    }

    private static double getPrice(double weight, double distance){
        if (weight < 2) {
            return distance * 1.10;
        }
        if(weight>=2 && weight<=5){
            return distance * 1.20;
        }
        if(weight>5 &&weight<10){
            return distance*1.30;
        }
        if (weight>=10){
            return distance*1.50;
        }
        return -1;
    }
}
