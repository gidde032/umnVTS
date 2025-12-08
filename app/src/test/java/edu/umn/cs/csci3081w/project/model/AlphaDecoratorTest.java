package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlphaDecoratorTest {

  private AlphaDecorator testAlphaDecorator;
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

    testVehicle = new VehicleTestImpl(1, new Line(10000, "testLine",
        "VEHICLE_LINE", testRouteOut, testRouteIn,
        new Issue()), 3, 1.0, new PassengerLoader(), new PassengerUnloader());

    testAlphaDecorator = new AlphaDecorator(testVehicle);
  }

  /**
   * Tests constructor.
   */
  @Test
  public void testConstructor() {
    assertEquals(1, testAlphaDecorator.getId());
    assertEquals("testRouteOut1", testAlphaDecorator.getName());
    assertEquals(3, testAlphaDecorator.getCapacity());
    assertEquals(1, testAlphaDecorator.getSpeed());
    assertEquals(testRouteOut, testAlphaDecorator.getLine().getOutboundRoute());
    assertEquals(testRouteIn, testAlphaDecorator.getLine().getInboundRoute());
    assertEquals(testVehicle, testAlphaDecorator.vehicle);
  }

  /**
   * Tests if AlphaDecorator properly decorates the wrapped vehicle's Color.
   */
  @Test
  public void testGetColor() {
    assertEquals(testVehicle.getColor().getRed(), testAlphaDecorator.getColor().getRed());
    assertEquals(testVehicle.getColor().getGreen(), testAlphaDecorator.getColor().getGreen());
    assertEquals(testVehicle.getColor().getBlue(), testAlphaDecorator.getColor().getBlue());
    assertEquals(155, testAlphaDecorator.getColor().getAlpha());
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testAlphaDecorator = null;
  }

}
