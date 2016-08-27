import com.cyxoud.robots.entities.*;
import com.cyxoud.robots.exceptions.NeighbourIsNotSetException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Admin on 07.08.16.
 */
public class RobotTest {
    private Robot robot;
    private Fork fork = new Fork();
    private Cable cable = new Cable();

    @Before
    public void initRobot() {
        robot = new RandomRobot(fork, cable);
    }

    @Test
    public void testThatRobotHasDefaultCharge50() {
        assertEquals(robot.getCharge(), 50);
    }

    @Test(expected = NeighbourIsNotSetException.class)
    public void testThatExceptionHappensWhenNeighboursInGentlemanlyRobotAreNotSet() {
        robot = new GentlemanlyRobot(fork, cable);
        ((GentlemanlyRobot) robot).tryFreeChargerPartInFavourOfNeighbour();
    }
    @Test
    public void testThatRobotCanNotBeChargedMoreThan100() {
        robot.tryTakeLeftChargerPart();
        robot.tryTakeRightChargerPart();
        for (int i = 0; i < 6; i++) {
            robot.beCharged();
        }

        assertEquals(robot.getCharge(), 100);
    }

    @Test
    public void testThatRobotIsDisconnectedWhenDischargedTo0() {
        for (int i = 0; i < 5; i++) {
            robot.beDischarged();
        }

        assertEquals(robot.isActive(), false);
        assertEquals(robot.tryTakeRightChargerPart(), false);
        assertEquals(robot.tryTakeLeftChargerPart(), false);
    }

    @Test
    public void testThatRobotNeedsTwoChargerPartsToBeCharged() {
        Cable anotherCable = new Cable();
        Robot anotherRobot = new RandomRobot(anotherCable, fork);
        anotherRobot.tryTakeRightChargerPart();
//        fork.setAvailable(false);
        robot.tryTakeRightChargerPart();
        robot.tryTakeLeftChargerPart();
        robot.beCharged();
        assertEquals(robot.getCharge(), 50);
        anotherRobot.tryFreeRightChargerPart();
//        fork.setAvailable(true);
        robot.tryTakeLeftChargerPart();
        robot.beCharged();
        assertEquals(robot.gatheredCharging(), true);
        assertEquals(robot.getCharge(), 60);
    }

    @Test
    public void testThatRobotFreeChargePartsBeforeExiting() throws InterruptedException {
        Cable anotherCable = new Cable();
        Robot anotherRobot = new RandomRobot(anotherCable, fork);
        anotherRobot.tryTakeRightChargerPart();
//        fork.setAvailable(false);
        assertEquals(robot.tryTakeRightChargerPart(), true);

        while (robot.isActive()) {
            robot.beDischarged();
        }
        assertTrue(!robot.tryFreeRightChargerPart());
    }

    @Test
    public void testThatRobotWithGentelmanlyStrategyGiveHisChargePart() {
        Fork fork1 = new Fork();
        Cable cable1 = new Cable();
        Fork fork2 = new Fork();
        Cable cable2 = new Cable();

        Robot randomRobot = new RandomRobot(fork1, cable1);
        Robot gentlemanlyRobot = new GentlemanlyRobot(cable1, fork2);
        Robot greedyRobot = new GreedyRobot(fork2, cable2);
        ((GentlemanlyRobot) gentlemanlyRobot).setLeftNeighbour(randomRobot);
        ((GentlemanlyRobot) gentlemanlyRobot).setRightNeighbour(greedyRobot);


        gentlemanlyRobot.tryTakeLeftChargerPart();
        gentlemanlyRobot.tryTakeRightChargerPart();
        assertEquals(gentlemanlyRobot.gatheredCharging(), true);

        randomRobot.beDischarged();
        ((GentlemanlyRobot) gentlemanlyRobot).tryFreeChargerPartInFavourOfNeighbour();
        assertEquals(gentlemanlyRobot.gatheredCharging(), false);
        assertEquals(gentlemanlyRobot.tryFreeLeftChargerPart(), false);
        assertEquals(randomRobot.tryTakeRightChargerPart(), true);
        assertEquals(greedyRobot.tryTakeLeftChargerPart(), false);

    }
}
