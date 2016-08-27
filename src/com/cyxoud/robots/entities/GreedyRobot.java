package com.cyxoud.robots.entities;

import java.util.logging.Logger;

/**
 * Represents robot with greedy strategy
 */
public class GreedyRobot extends Robot {
    public GreedyRobot(ChargerPart leftChargerPart, ChargerPart rightChargerPart) {
        super(Strategy.GREEDY, leftChargerPart, rightChargerPart);
    }

    /**
     * Actions of the robot with greedy strategy. If robot's strategy thread is not interrupted, if
     * robot's charge is less than 60 percents, it tries to take both right and left charger part one after another.
     * Then if robot collected the charger while it is not full charged, robot is being charged each amount of time;
     * otherwise sleeps for delay time
     * @return greedy strategy of the robot
     */
    @Override
    public Runnable strategy() {
        return new Runnable() {
            @Override
            public void run() {
                final int delayTime = 500;
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        if (getCharge() < 60) {
                            tryTakeLeftChargerPart();
                            tryTakeRightChargerPart();

                            if (gatheredCharging()) {
                                while (!isFullCharged()) {
                                    Thread.sleep(Charger.CHARGE_DELAY_TIME);
                                    beCharged();
                                }
                            }
                        } else {
                            Thread.sleep(delayTime);
                        }
                    }
                } catch(InterruptedException ex) {
                }
                finally {
                    Logger.getGlobal().info("Robot" + getCurI() + " was disconnected");
                    tryFreeLeftChargerPart();
                    tryFreeRightChargerPart();
                }

            }
        };
    }
}
