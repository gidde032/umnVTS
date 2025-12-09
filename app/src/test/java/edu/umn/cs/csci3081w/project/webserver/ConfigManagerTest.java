package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umn.cs.csci3081w.project.model.Counter;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.PassengerFactory;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Route;
import edu.umn.cs.csci3081w.project.model.Stop;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigManagerTest {

  private ConfigManager testCM;

  /**
   * Setup deterministic operations before each test runs.
   */
  @BeforeEach
  public void setUp() throws UnsupportedEncodingException {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;

    String configPath =
        URLDecoder.decode(
            getClass().getClassLoader().getResource("config.txt").getFile(), "UTF-8");

    Counter testCounter = new Counter();

    testCM = new ConfigManager();
    testCM.readConfig(testCounter, configPath);
  }

  /**
   * Tests if storage information is parsed correctly.
   */
  @Test
  public void testStorageInfoParsed() {
    assertEquals(4, testCM.getStorageFacility().getSmallBusesNum());
    assertEquals(2, testCM.getStorageFacility().getLargeBusesNum());
    assertEquals(1, testCM.getStorageFacility().getElectricTrainsNum());
    assertEquals(5, testCM.getStorageFacility().getDieselTrainsNum());
  }

  /**
   * Tests if line information is parsed correctly.
   */
  @Test
  public void testLineInfoParsed() {
    assertEquals(2, testCM.getLines().size());
    Line testBusLine = testCM.getLines().get(0);
    assertEquals(Line.BUS_LINE, testBusLine.getType());
    assertEquals("Campus Connector", testBusLine.getName());
    Route testRoute = testBusLine.getOutboundRoute();
    assertEquals(8, testRoute.getStops().size());
    assertEquals("East Bound", testRoute.getName());
    Stop testStop = testRoute.getNextStop();
    assertEquals("Blegen Hall", testStop.getName());
    assertEquals(44.972392, testStop.getPosition().getLatitude());
    assertEquals(-93.243774, testStop.getPosition().getLongitude());

    testRoute = testBusLine.getInboundRoute();
    assertEquals(9, testRoute.getStops().size());
    assertEquals("West Bound", testRoute.getName());
    testStop = testRoute.getNextStop();
    assertEquals("St. Paul Student Center", testStop.getName());
    assertEquals(44.984630, testStop.getPosition().getLatitude());
    assertEquals(-93.186352, testStop.getPosition().getLongitude());

    Line testTrainLine = testCM.getLines().get(1);
    assertEquals(Line.TRAIN_LINE, testTrainLine.getType());
    assertEquals("Express Train", testTrainLine.getName());
    testRoute = testTrainLine.getOutboundRoute();
    assertEquals(3, testRoute.getStops().size());
    assertEquals("East Bound Train", testRoute.getName());
    testStop = testRoute.getNextStop();
    assertEquals("Stadium Village", testStop.getName());
    assertEquals(44.974769, testStop.getPosition().getLatitude());
    assertEquals(-93.222770, testStop.getPosition().getLongitude());

    testRoute = testTrainLine.getInboundRoute();
    assertEquals(3, testRoute.getStops().size());
    assertEquals("West Bound Train", testRoute.getName());
    testStop = testRoute.getNextStop();
    assertEquals("Raymond", testStop.getName());
    assertEquals(44.963552, testStop.getPosition().getLatitude());
    assertEquals(-93.195403, testStop.getPosition().getLongitude());
  }

  /**
   * Cleanup after each test.
   */
  @AfterEach
  public void cleanUp() {
    testCM = null;
  }

}