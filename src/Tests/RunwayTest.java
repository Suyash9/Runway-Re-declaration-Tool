package Tests;

import Controller.MainController;
import Exceptions.InvalidArgumentException;
import Model.Airport;
import Model.Obstacle;
import Model.ObstacleManager;
import Model.Runway;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RunwayTest  {
    private Airport testAirport = new Airport("Heathrow airport");
    private Runway testRunway = new Runway("09L", 3902,3902,3902,3595,306);
    private Obstacle testObstacle = new Obstacle("test1",12,1,1,0,-50,3646);
    private Obstacle testObstacle2 = new Obstacle("test1",12,11,41,10,-50,3646);
    private MainController testController = new MainController();
    private ObstacleManager testObstacleManager = ObstacleManager.getInstance();
    private List<Obstacle> testList = new ArrayList<>();

    @Test
    public final void testIfRunwayIsAdded(){
        testAirport.addRunway(testRunway);

        Assert.assertTrue(testAirport.getRunways().contains(testRunway));
    }

    @Test
    public final void testIfRunwayIsRemoved(){
        testAirport.clearRunwayList();
        testAirport.addRunway(testRunway);
        testAirport.removeRunway(testRunway);

        Assert.assertTrue(!testAirport.getRunways().contains(testRunway));
    }

    @Test
    public final void testIfRunwayListIsCleared(){
        this.testIfRunwayIsAdded();
        testAirport.clearRunwayList();

        Assert.assertEquals(0, testAirport.getRunways().size());
    }

    @Test(expected = InvalidArgumentException.class)
    public final void whenRunwayWithNegativeValuesIsCreatedThrowException(){
        new Runway("09L", -3902,3902,3902,3595,306);
        new Runway("09L", 3902,-3902,3902,3595,306);
        new Runway("09L", 3902,3902,-3902,3595,306);
        new Runway("09L", 3902,3902,3902,-3595,306);
        new Runway("09L", 3902,3902,3902,3595,-306);
        new Runway("09L", 3902,3902,3902,3595,306);
    }

    @Test
    public final void noExceptionThrownWhenRunwayHasPositiveParameters(){
        new Runway("09L", 3902,3902,3902,3595,306);
        Assert.assertTrue(true);
    }

    @Test
    public final void checkIfObstacleIsAdded(){
        testController.setRunway(null);
        testController.setRunway(testRunway);
        Assert.assertEquals(testRunway,testController.getRunway());
    }

    @Test(expected = NullPointerException.class)
    public final void checkIfObstacleIsRemoved(){
        testController.setRunway(testRunway);
        testRunway.setObstacle(testObstacle);
        testController.removeObstacle();
        testRunway.getObstacle();

    }

    @Test(expected = InvalidArgumentException.class)
    public final void throwExceptionWhenObstacleWithInvalidParametersIsAdded(){
        new Obstacle("test1",-12,1,1,0,-50,3646);
        new Obstacle("test1",12,-1,1,0,-50,3646);
        new Obstacle("test1",12,1,-1,0,-50,3646);
        new Obstacle("test1",12,1,1,-0,-50,3646);
    }

    @Test
    public final void noExceptionThrownWhenValidObstaclePParametersArePassed(){
        new Obstacle("test1",12,1,1,0,-50,3646);
        Assert.assertTrue(true);
    }

    @Test
    public final void checkIfObstacleIsAddedToThePredefinedList(){
        testObstacleManager.addObstacle(testObstacle);
        Assert.assertTrue(testObstacleManager.getPredefinedObstacles().contains(testObstacle));
    }

    @Test
    public final void checkIfObstacleListIsAddedToThePredefinedList(){
        testList.add(testObstacle);
        testList.add(testObstacle2);
        testObstacleManager.addObstacles(testList);

        Assert.assertTrue(testObstacleManager.getPredefinedObstacles().contains(testObstacle) && testObstacleManager.getPredefinedObstacles().contains(testObstacle2));
    }

    @Test
    public final void testIfPredefinedObstacleListIsTheSame(){
        testObstacleManager.clearPredefinedList();
        testList.add(testObstacle);
        testList.add(testObstacle2);
        testObstacleManager.addObstacles(testList);

        Assert.assertEquals(testList,testObstacleManager.getPredefinedObstacles());
    }

    @Test
    public final void testIfPredefinedObstacleListIsCleared(){
        this.checkIfObstacleListIsAddedToThePredefinedList();
        testObstacleManager.clearPredefinedList();

        Assert.assertEquals(0, testObstacleManager.getPredefinedObstacles().size());
    }


    @Test
    public final void testCalculate1() {
        Runway runway = new Runway("09L", 3902,3902,3902,3595,306);
        Obstacle obstacle = new Obstacle("test1",12,1,1,0,-50,3646);
        runway.setObstacle(obstacle);
        runway.calculate();

        int updatedTORA = runway.getUpdatedTORA();
        int updatedASDA = runway.getUpdatedASDA();
        int updatedTODA = runway.getUpdatedTODA();
        int updatedLDA = runway.getUpdatedLDA();

        Assert.assertEquals(updatedTORA,3346);
        Assert.assertEquals(updatedTODA,3346);
        Assert.assertEquals(updatedASDA,3346);
        Assert.assertEquals(updatedLDA,2985);
    }

    @Test
    public final void testCalculate2() {

        Runway runway = new Runway("27R", 3884, 3962,3884,3884,0);
        Obstacle obstacle = new Obstacle("test1",12,1,1,0,-50,3646);
        runway.setObstacle(obstacle);
        runway.setDirection(1);
        runway.calculate();

        int updatedTORA = runway.getUpdatedTORA();
        int updatedASDA = runway.getUpdatedASDA();
        int updatedTODA = runway.getUpdatedTODA();
        int updatedLDA = runway.getUpdatedLDA();

        Assert.assertEquals(updatedTORA,2986);
        Assert.assertEquals(updatedTODA,2986);
        Assert.assertEquals(updatedASDA,2986);
        Assert.assertEquals(updatedLDA,3346);

    }

    @Test
    public final void testCalculate3() {

        Runway runway = new Runway("09R", 3660, 3660,3660,3353,307);
        Obstacle obstacle = new Obstacle("test1",25,1,1,20,500,2853);
        runway.setObstacle(obstacle);
        runway.setDirection(1);
        runway.calculate();

        int updatedTORA = runway.getUpdatedTORA();
        int updatedASDA = runway.getUpdatedASDA();
        int updatedTODA = runway.getUpdatedTODA();
        int updatedLDA = runway.getUpdatedLDA();

        Assert.assertEquals(updatedTORA,1850);
        Assert.assertEquals(updatedTODA,1850);
        Assert.assertEquals(updatedASDA,1850);
        Assert.assertEquals(updatedLDA,2553);

    }

    @Test
    public final void testCalculate4() {

        Runway runway = new Runway("27L", 3660, 3660,3660,3660,0);
        Obstacle obstacle = new Obstacle("test1",25,1,1,20,500,2853);
        runway.setObstacle(obstacle);
        runway.setDirection(0);
        runway.calculate();

        int updatedTORA = runway.getUpdatedTORA();
        int updatedASDA = runway.getUpdatedASDA();
        int updatedTODA = runway.getUpdatedTODA();
        int updatedLDA = runway.getUpdatedLDA();

        Assert.assertEquals(updatedTORA, 2860);
        Assert.assertEquals(updatedTODA,2860);
        Assert.assertEquals(updatedASDA,2860);
        Assert.assertEquals(updatedLDA,1850);

    }
}