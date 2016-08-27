package com.cyxoud.robots.entities;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Represents robot with random strategy
 */
public class RandomRobot extends Robot {

    public RandomRobot(ChargerPart leftChargerPart, ChargerPart rightChargerPart) {
        super(Strategy.RANDOM, leftChargerPart, rightChargerPart);
    }

    /**
     * return true if both charge part are available and false otherwise
     */
    public boolean checkChargePartsAvailability() {
        return rightChargerPart.isFree() &&
                leftChargerPart.isFree();
    }

    /**
     * Actions of the robot with random strategy. If robot's strategy thread is not interrupted, if
     * it is not full charged, then it waits while both charger parts will not be available or
     * robot's thread is not interrupted, then tries to take both right and left charger part one after another.
     * and if robot collected the charger, while current thread is not interrupted it is being charged each delay of
     * time and is being slept between 100 and 300 seconds
     *
     * @return random strategy of the robot
     */
    @Override
    public Runnable strategy() {
        return new Runnable() {
            @Override
            public void run() {
                final int minSleep = 100;
                final int maxSleep = 300;
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        if (!isFullCharged()) {

                            while (!checkChargePartsAvailability() && !Thread.currentThread().isInterrupted()) {
                            }
                            if (Thread.currentThread().isInterrupted()) return;

                            tryTakeLeftChargerPart();
                            tryTakeRightChargerPart();

                            if (gatheredCharging()) {
                                while (!Thread.currentThread().isInterrupted()) {
                                    Thread.sleep(Charger.CHARGE_DELAY_TIME);
                                    beCharged();
                                    Thread.sleep(ThreadLocalRandom.current().nextInt(maxSleep - minSleep + 1) + minSleep);
                                }
                            }
                        }
                    }
                } catch (InterruptedException ex) {
                } finally {
                    Logger.getGlobal().info("Robot" + getCurI() + " was disconnected");
                    tryFreeLeftChargerPart();
                    tryFreeRightChargerPart();
                }
            }
        };
    }
}

