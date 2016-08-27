package com.cyxoud.robots;

import com.cyxoud.robots.entities.*;
import com.cyxoud.robots.exceptions.IllegalArgumentsNumberException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents modelling of the robot fight against each other to get the charger
 */
public class RobotChargeModelling {
    /** list of robot threads(strategies and discharges */
    private List<Thread> robotsThreads;
    /** list of robots that participate in modelling */
    private List<Robot> robots = new ArrayList<>(6);

    public RobotChargeModelling(String[] arguments) {
        checkInput(arguments);
        init(arguments);
    }

    /**
     * check input of the class and throws exceptions if input is not correct
     * @param args arguments of the modelling
     */
    public void checkInput(String[] args) {
        if (args.length != 6) {
            throw new IllegalArgumentsNumberException("Illegal number of arguments:" +
                    " you need to pass 6 numbers(1/2/3) according to chosen strategy");
        }
        for (int i = 0; i < 6; i++) {
            try {
                if (Integer.parseInt(args[i]) < 1 || Integer.parseInt(args[i]) > 3) {
                    throw new IllegalArgumentException("Illegal argument: you need to pass a number(1/2/3)" +
                            " according to chosen strategy. Error in argument number: " + (i + 1));
                }
            }
            catch (NumberFormatException numberFormatException) {
                throw (IllegalArgumentException)
                        new IllegalArgumentException("Illegal argument: you need to pass a number(1/2/3)" +
                                " according to chosen strategy. Error in argument number: " + (i + 1)).initCause(numberFormatException);
            }
        }
    }

    /**
     *  Construct robots due to the strategies and continue modelling while all robot are not discharged
     *  and all alive robots are not full charged
     */
    private void init(String[] args) {
        java.util.List<Robot> robots = initRobots(args);
        setGentlemanRobotNeighbours(robots);

        initThreads(robots);

        while (!isAllRobotsDischarged() && !isAllAliveRobotsFullCharged()) {}
        stopThreads();
    }

    /**
     * Constructs robots and put them in list
     * @param args input modelling arguments
     * @return constructed list of robots
     */
    private java.util.List<Robot> initRobots(String[] args) {
        java.util.List<ChargerPart> chargerParts = new ArrayList(6) {
            {
                add(new Fork("Fork1"));
                add(new Cable("Cable1"));
                add(new Fork("Fork2"));
                add(new Cable("Cable2"));
                add(new Fork("Fork3"));
                add(new Cable("Cable3"));
            }
        };

        for (int i = 0; i < 6; i++) {
            Strategy strategy = Strategy.values()[Integer.parseInt(args[i]) - 1];

            if (i == 0) {
                if (strategy.equals(Strategy.GENTLEMANLY)) {
                    robots.add(new GentlemanlyRobot(chargerParts.get(i), chargerParts.get(5)));
                }
                else if (strategy.equals(Strategy.RANDOM)){
                    robots.add(new RandomRobot(chargerParts.get(i), chargerParts.get(5)));
                }
                else {
                    robots.add(new GreedyRobot(chargerParts.get(i), chargerParts.get(5)));
                }
            }
            else {
                if (strategy.equals(Strategy.GENTLEMANLY)) {
                    robots.add(new GentlemanlyRobot(chargerParts.get(i), chargerParts.get(i - 1)));
                }
                else if (strategy.equals(Strategy.RANDOM)){
                    robots.add(new RandomRobot(chargerParts.get(i), chargerParts.get(i - 1)));
                }
                else {
                    robots.add(new GreedyRobot(chargerParts.get(i), chargerParts.get(i - 1)));
                }
            }
        }

        return robots;
    }

    /**
     * Sets neighbours of robots with gentleman strategy
     * @param robots list of constructed robots
     */
    private void setGentlemanRobotNeighbours(List<Robot> robots) {
        for (int i = 0; i < 6; i ++) {
            Robot robot = robots.get(i);
            if (robot instanceof GentlemanlyRobot) {
                GentlemanlyRobot gentlemanlyRobot = (GentlemanlyRobot) robot;
                if (i == 0) {
                    gentlemanlyRobot.setLeftNeighbour(robots.get(i + 1));
                    gentlemanlyRobot.setRightNeighbour(robots.get(5));
                }
                else if (i == 5) {
                    gentlemanlyRobot.setLeftNeighbour(robots.get(0));
                    gentlemanlyRobot.setRightNeighbour(robots.get(i - 1));
                }
                else {
                    gentlemanlyRobot.setLeftNeighbour(robots.get(i + 1));
                    gentlemanlyRobot.setRightNeighbour(robots.get(i - 1));
                }
            }
        }
    }

    /**
     * Starts discharge and strategy threads and put them in thread list
     * @param robots list of constructed robots
     */
    private void initThreads(List<Robot> robots) {
        robotsThreads = new ArrayList<>(12);
        for (Robot robot : robots) {
            Thread robotThread;
            robotThread = new Thread(robot.strategy());
            robotsThreads.add(robotThread);
            robotsThreads.add(new Thread(robot.discharge(robotThread)));
        }

        for (Thread thread : robotsThreads) {
            thread.start();
        }
    }

    /**
     * Check if all robots are discharged
     * @return true if all robots are discharged and false otherwise
     */
    private boolean isAllRobotsDischarged() {
        for (Robot robot : robots) {
            if (robot.isActive()) return false;
        }
        return true;
    }

    /**
     * Check if all alive robots are fullchargedd
     * @return true if all alive robots are full charged and false otherwise
     */
    private boolean isAllAliveRobotsFullCharged() {
        for (Robot robot : robots) {
            if (robot.isActive()) {
                if (!robot.isFullCharged()) return false;
            }
        }
        return true;
    }

    /**
     * Interrupts all the threads due to modelling terminate condition
     */
    public void stopThreads() {
        for (Thread thread : robotsThreads) {
            thread.interrupt();
        }
    }

    public static void main(String[] args) {
        new RobotChargeModelling(args);
    }


}
