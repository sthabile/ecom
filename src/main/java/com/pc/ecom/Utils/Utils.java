package com.pc.ecom.Utils;

public class Utils {

    /**
     * Calculates the final discounted price
     * @param price
     * @param discount
     * @return
     */
    public static double computeSpecialPrice(double price, double discount){
        if(discount <= 0){
            return price;
        }
        return price - ((discount * 0.01) * price);
    }
}
