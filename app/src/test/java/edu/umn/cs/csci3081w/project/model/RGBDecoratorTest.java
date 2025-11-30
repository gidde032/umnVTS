package edu.umn.cs.csci3081w.project.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RGBDecoratorTest {

  private RGBDecorator testRGBDecorator;
  private Vehicle testVehicle;
  private Route testRouteIn;
  private Route testRouteOut;


  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    Vehicle.TESTING = true;
    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

  }

  /**
   * Tests constructor having SmallBus as argument.
   */
  @Test
  public void testConstructorSmallBus() {
    testVehicle = new SmallBus(1, new Line(10000, "testLine", "BUS", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0);
    testRGBDecorator = new RGBDecorator(testVehicle);
    assertEquals(122, testRGBDecorator.getColor().getRed());
    assertEquals(0, testRGBDecorator.getColor().getGreen());
    assertEquals(25, testRGBDecorator.getColor().getBlue());
    assertEquals(testVehicle.getColor().getAlpha(), testRGBDecorator.getColor().getAlpha());
    assertEquals(testVehicle, testRGBDecorator.v);
  }

  /**
   * Tests constructor having LargeBus as argument.
   */
  @Test
  public void testConstructorLargeBus() {
    testVehicle = new LargeBus(1, new Line(10000, "testLine", "BUS", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0);
    testRGBDecorator = new RGBDecorator(testVehicle);
    assertEquals(239, testRGBDecorator.getColor().getRed());
    assertEquals(130, testRGBDecorator.getColor().getGreen());
    assertEquals(238, testRGBDecorator.getColor().getBlue());
    assertEquals(testVehicle.getColor().getAlpha(), testRGBDecorator.getColor().getAlpha());
  }

  /**
   * Tests constructor having ElectricTrain as argument.
   */
  @Test
  public void testConstructorElectricTrain() {
    testVehicle = new ElectricTrain(1, new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0);
    testRGBDecorator = new RGBDecorator(testVehicle);
    assertEquals(60, testRGBDecorator.getColor().getRed());
    assertEquals(179, testRGBDecorator.getColor().getGreen());
    assertEquals(113, testRGBDecorator.getColor().getBlue());
    assertEquals(testVehicle.getColor().getAlpha(), testRGBDecorator.getColor().getAlpha());
  }

  /**
   * Tests constructor having DieselTrain as argument.
   */
  @Test
  public void testConstructorDieselTrain() {
    testVehicle = new DieselTrain(1, new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0);
    testRGBDecorator = new RGBDecorator(testVehicle);
    assertEquals(255, testRGBDecorator.getColor().getRed());
    assertEquals(204, testRGBDecorator.getColor().getGreen());
    assertEquals(51, testRGBDecorator.getColor().getBlue());
    assertEquals(testVehicle.getColor().getAlpha(), testRGBDecorator.getColor().getAlpha());
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testRGBDecorator = null;
    testVehicle = null;
  }

}
