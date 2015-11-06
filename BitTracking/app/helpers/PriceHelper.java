package helpers;

/**
 * Created by Mladen13 on 24.10.2015.
 */
public class PriceHelper {
    /**
     * Method that calculates price for package shipping, it can be modified, and it is part of business logic of company
     * @param weight
     * @param distance
     * @return
     */
    public static double calculatePrice(double weight, double distance) {
        double index = 0.20;
        if (distance > 10 && distance <= 50) {
            index = 0.15;
        } else if (distance > 50 && distance <= 150) {
            index = 0.12;
        } else if (distance > 150 && distance <= 300) {
            index = 0.10;
        } else if (distance > 300 && distance <= 500) {
            index = 0.08;
        } else if (distance > 500 && distance <= 1000) {
            index = 0.06;
        } else if (distance > 1000){
            index = 0.04;
        }
        return weight * distance * index;
    }
}
