import com.cyxoud.robots.RobotChargeModelling;
import com.cyxoud.robots.entities.Robot;
import com.cyxoud.robots.exceptions.IllegalArgumentsNumberException;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Admin on 06.08.16.
 */
public class RobotChargeTest {


    @Test(expected = IllegalArgumentsNumberException.class)
    public void testThatExceptionThrownWhenInccorrectArgumentsNumber() {
        RobotChargeModelling.main(new String[] {"1", "2", "3", "2", "3"});
    }
    @Test(expected = IllegalArgumentException.class)
    public void testThatExceptionThrownWhenIntegerNotPassed() {
        RobotChargeModelling.main(new String[] {"1", "2", "3", "ok", "3", "4"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThatExceptionThrownWhenIntegersBetweenRangeNotPassed() {
        RobotChargeModelling.main(new String[] {"1", "2", "3", "2", "3", "4"});
    }

    @Test
    public void testThatModellingExitsWithRightRobotCharges(){
        try {
            RobotChargeModelling robotCharge = new RobotChargeModelling(new String[] {"1", "2" , "3", "3", "2", "1"});
            Field robotsField = RobotChargeModelling.class.getDeclaredField("robots");
            robotsField.setAccessible(true);
            List<Robot> robots = (List<Robot>) robotsField.get(robotCharge);
            for (Robot robot : robots) {
                assertTrue(robot.getCharge() == 0 || robot.getCharge() == 100);
            }
        } catch (NoSuchFieldException e) {
            System.out.println("Error in test");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
