package com.cyxoud.robots.entities;

/**
 * Represents behaviour that depends on charger
 */
public class Charger {
    /** amount of percent on which charge increased each period of time */
    private static final int CHARGE_AMOUNT = 10;
    /** period of time in milliseconds between each charge on CHARGE_AMOUNT */
    public static final int CHARGE_DELAY_TIME = 500;

    /** @return amount of charge increase by charger */
    public static int charge() {
        return CHARGE_AMOUNT;
    }
}
