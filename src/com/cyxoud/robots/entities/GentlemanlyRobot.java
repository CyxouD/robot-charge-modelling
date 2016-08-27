package com.cyxoud.robots.entities;

import com.cyxoud.robots.exceptions.NeighbourIsNotSetException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents robot with gentleman strategy
 */
public class GentlemanlyRobot extends Robot{
    /** left neighbour of the robot */
    private Robot leftNeighbour;
    /** right neighbour of the robot */
    private Robot rightNeighbour;

    public GentlemanlyRobot(ChargerPart leftChargerPart, ChargerPart rightChargerPart) {
        super(Strategy.GENTLEMANLY, leftChargerPart, rightChargerPart);
    }

    /**
     * Free charge part to oneof the neighbours if robot has part, it is active and his charge is higher than neighbour's charge
     * @return true if left or right part was freed or false otherwise
     */
    public boolean tryFreeChargerPartInFavourOfNeighbour() {
        if (leftNeighbour == null || rightNeighbour == null) {
            throw new NeighbourIsNotSetException("One or both neighbours are not set");
        }

        if (hasLeftChargerPart && leftNeighbour.isActive() && leftNeighbour.getCharge() < getCharge()) {
            tryFreeLeftChargerPart();
            logger.log(Level.FINE, "in favour of left Robot" + leftNeighbour.getCurI());
            return true;
        }
        else if (hasRightChargerPart && rightNeighbour.isActive() && rightNeighbour.getCharge() < getCharge()) {
            tryFreeRightChargerPart();
            logger.log(Level.FINE, "in favour of right Robot" + rightNeighbour.getCurI());
            return true;
        }

        return false;
    }

    public void setLeftNeighbour(Robot leftNeighbour) {
        this.leftNeighbour = leftNeighbour;
    }

    public void setRightNeighbour(Robot rightNeighbour) {
        this.rightNeighbour = rightNeighbour;
    }

    /**
     * Actions of the robot with gentleman strategy. If robot's strategy thread is not interrupted
     * robot tries to take both right and left charger part one after another Then robot tries to free some of the parts
     * in favour of the neighbours, and if it was successful, then sleeps some time. Otherwise if charger was collected
     * robot is charged each amount of time
     * @return gentleman strategy of the robot
     */
    @Override
    public Runnable strategy() {
        if (leftNeighbour == null || rightNeighbour == null) {
            throw new NeighbourIsNotSetException("One or both neighbours are not set");
        }

        return new Runnable() {
            /** amount of sleeping time in milliseconds after giving neighbour one of the parts */
            private final int sleepTimeAfterGivingPart = 200;
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        tryTakeLeftChargerPart();
                        tryTakeRightChargerPart();

                        if (tryFreeChargerPartInFavourOfNeighbour()) {
                            Thread.sleep(sleepTimeAfterGivingPart);
                        }
                        else {
                            if (gatheredCharging()) {
                                Thread.sleep(Charger.CHARGE_DELAY_TIME);
                                beCharged();
                            }
                        }
                    }
                } catch (InterruptedException ex) {
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

