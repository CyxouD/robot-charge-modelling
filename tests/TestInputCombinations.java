import com.cyxoud.robots.RobotChargeModelling;
import com.cyxoud.robots.entities.Robot;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Admin on 07.08.16.
 */
public class TestInputCombinations {

    /**
     * Randomly create 200 modelling states with different inputs. Takes an infinity time with some input
     */
    @Ignore
    @Test
    public void testThatModellingExitsWithRightRobotCharges(){
        ArrayList<String[]> list = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 200; i++) {
            list.add(new String[]{Integer.toString(r.nextInt(3) + 1), Integer.toString(r.nextInt(3) + 1),
                    Integer.toString(r.nextInt(3) + 1), Integer.toString(r.nextInt(3) + 1),
                    Integer.toString(r.nextInt(3) + 1), Integer.toString(r.nextInt(3) + 1)});
        }
        for (String[] input : list) {
            try {
                System.out.println(Arrays.toString(input));
                RobotChargeModelling robotCharge = new RobotChargeModelling(input);
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
}
