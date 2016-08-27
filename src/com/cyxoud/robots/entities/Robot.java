package com.cyxoud.robots.entities;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents robot
 */
abstract public class Robot implements Strategic {
    protected static final Logger logger = Logger.getLogger("com.cyxoud.robots.entities");
    /** initialization of logger*/
    static {
        logger.setLevel(Level.FINER);
        ConsoleHandler handler= new ConsoleHandler();
        handler.setLevel(Level.FINER);
        logger.addHandler(handler);
    }
    /** amount of full charge in percents */
    private static final int FULL_CHARGE = 100;
    /** default percent of charge. May be changed concurrently */
    private int charge = 50;
    /** strategy of the robot */
    private Strategy strategy;
    /** charger part located at the left side of the robot */
    protected ChargerPart leftChargerPart;
    /** charger part located at the right side of the robot */
    protected ChargerPart rightChargerPart;
    /** shows if robot got left charger part */
    protected boolean hasLeftChargerPart;
    /** shows if robot got right charger part */
    protected boolean hasRightChargerPart;

    /** index of the last robot created. Useful for logging/testing */
    private static int i;
    /** index of the current robot. Useful for logging/testing */
    private int curI;

    /** Constructs robot
     *  @param strategy strategy of the robot
     *  @param leftChargerPart charger part at the left of the robot
     *  @param rightChargerPart charger part at the right of the robot
     * */
    public Robot(Strategy strategy, ChargerPart leftChargerPart, ChargerPart rightChargerPart) {
        this.strategy = strategy;
        this.leftChargerPart = leftChargerPart;
        this.rightChargerPart = rightChargerPart;
        curI = i++;
    }

    /**
     * Add amount of the charge if the robot is active, constructed charging and is not yet full charged
     * */
    public void beCharged() {
        synchronized (this) {
            if (isActive() && gatheredCharging() && !isFullCharged()) {
                charge += Charger.charge();
                logger.log(Level.FINE, "Robot" + curI + " charged by 10%. Current charge = " + charge);
            }
        }
    }

    /**
     *  Subtract amount of the discharge charge if the robot is active
     *  if charge percent is equal to zero then left and right charge parts if robot had them are freed
     */
    public void beDischarged() {
        synchronized (this) {
            if (isActive()) {
                charge -= Discharge.ONE_STEP_DISCHARGE;
                if (charge == 0) {
                    tryFreeLeftChargerPart();
                    tryFreeRightChargerPart();
                }
                logger.log(Level.FINE, "Robot" + curI + " was discharged by 10%. Current charge = " + charge);
            }
        }
    }

    /**
     * return true if robot is active and false otherwise
     */
    public synchronized boolean isActive() {
        return charge != 0;
    }

    /**
     * return true if robot is full charged and false otherwise
     */
    public synchronized boolean isFullCharged() {
        return charge == FULL_CHARGE;
    }

    /**
     * @return  true if left charger part was successfully taken, if left charger part is available and robot is active
     */
    public boolean tryTakeLeftChargerPart() {
        boolean successfullyTaken;
        if (isActive()) {
            successfullyTaken = leftChargerPart.tryBeTaken(this);
            if (successfullyTaken) {
                hasLeftChargerPart = true;
                logger.log(Level.FINE, "Robot" + curI + " got left charger part: " + leftChargerPart +
                        ". Now it has " + (hasRightChargerPart ? rightChargerPart + " and " : "") + leftChargerPart);
            }
        } else return false;

        return successfullyTaken;
    }

    /**
     * @return  true if right charger part was successfully taken, if right charger part was available and robot was active
     * and false otherwise
     */
    public boolean tryTakeRightChargerPart() {
        boolean successfullyTaken;
        if (isActive()) {
            successfullyTaken = rightChargerPart.tryBeTaken(this);
            if (successfullyTaken) {
                hasRightChargerPart = true;
                logger.log(Level.FINE, "Robot" + curI + " got right charger part: " + rightChargerPart +
                        ". Now it has " + (hasLeftChargerPart ? leftChargerPart + " and " : "") + rightChargerPart);
            }
        } else return false;

        return successfullyTaken;
    }

    /**
     * @return  true if left charger part was successfully freed, if robot had left charger part
     * and false otherwise
     */
    public boolean tryFreeLeftChargerPart() {
        boolean successfullyFreed = leftChargerPart.tryBeFreed(this);
        if (successfullyFreed) {
            hasLeftChargerPart = false;
            logger.log(Level.FINE, "Robot" + curI + " freed left charger part: " + leftChargerPart);
        }

        return successfullyFreed;
    }

    /**
     * @return  true if right charger part was successfully freed, if robot had right charger part
     * and false otherwise
     */
    public boolean tryFreeRightChargerPart() {
        boolean successfullyFreed = rightChargerPart.tryBeFreed(this);
        if (successfullyFreed) {
            hasRightChargerPart = false;
            logger.log(Level.FINE, "Robot" + curI + " freed right charger part: " + rightChargerPart);
        }

        return successfullyFreed;
    }

    public synchronized int getCharge() {
        return charge;
    }

    /**
     * @return true if robot has both right and left charger parts
     */
    public boolean gatheredCharging() {
        return hasLeftChargerPart && hasRightChargerPart;
    }

    public int getCurI() {
        return curI;
    }

    @Override
    public String toString() {
        return "Robot{" +
                ", charge=" + charge +
                ", strategy=" + strategy +
                ", leftChargerPart=" + leftChargerPart +
                ", rightChargerPart=" + rightChargerPart +
                ", hasLeftChargerPart=" + hasLeftChargerPart +
                ", hasRightChargerPart=" + hasRightChargerPart +
                ", curI=" + curI +
                '}';
    }

    /**
     * Get access to discharge connected with current robot
     * @param robotThread thread of robot strategy
     * @return robot discharge
     */
    public Discharge discharge(Thread robotThread) {
        return new Discharge(robotThread);
    }

    /**
     * Represents actions of discharge
     */
    private class Discharge implements Runnable {
        /**
         * amount of beDischarged each period of time
         */
        public static final int ONE_STEP_DISCHARGE = 10;
        /**
         * delay time in milliseconds between robot is discharged
         */
        private final int dischargeDelayTime = 1000;
        private Thread robotThread;

        public Discharge(Thread robotThread) {
            this.robotThread = robotThread;
        }

        /**
         * Decrease amount of robot charge each delay time if it is active and the modelling is not finished(thread interrupted)
         * otherwise interrupts strategy of robot thread
         */
        @Override
        public void run() {
            try {
                while (isActive() && !Thread.currentThread().isInterrupted()) {
                    Thread.sleep(dischargeDelayTime);
                    beDischarged();
                }
                robotThread.interrupt();
            } catch (InterruptedException e) {
            }
        }

    }
}
