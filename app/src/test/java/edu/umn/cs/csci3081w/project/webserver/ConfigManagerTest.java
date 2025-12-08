package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import edu.umn.cs.csci3081w.project.model.Counter;
import edu.umn.cs.csci3081w.project.model.PassengerFactory;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManagerTest {

  private ConfigManager testCM;
  private Path testFile;
  private String testContent;
  private Counter testCounter;

  /**
   * Setup deterministic operations before each test runs.
   */
  @BeforeEach
  public void setUp() throws IOException {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    Vehicle.TESTING = true;
    testFile = Files.createTempFile("test_config", ".txt");

    testContent = """
        LINE_START, BUS_LINE, Campus Connector
        
        ROUTE_START, East Bound
        
        STOP, Blegen Hall, 44.972392, -93.243774, .15

        ROUTE_END
        
        ROUTE_START, West Bound
        
        STOP, St. Paul Student Center, 44.984630, -93.186352, .35
        
        ROUTE_END
        
        LINE_END
        
        LINE_START, TRAIN_LINE, Express Train
        
        ROUTE_START, East Bound Train
        
        STOP, Stadium Village, 44.974769, -93.222770, .15
        STOP, Water Tower, 44.969139, -93.210371, .3
        
        ROUTE_END
        
        ROUTE_START, West Bound Train
        
        STOP, Raymond, 44.963552, -93.195403, .35
        
        ROUTE_END
        
        LINE_END
        
        STORAGE_FACILITY_START
        
        SMALL_BUSES, 4
        LARGE_BUSES, 2
        ELECTRIC_TRAINS, 1
        DIESEL_TRAINS, 5
        
        STORAGE_FACILITY_END
        """;

    Files.writeString(testFile, testContent);
    testCounter = new Counter();
    testCM = new ConfigManager();
    testCM.readConfig(testCounter, testFile.toString());
  }

  /**
   *
   */
  @Test
  public void testValidStorageFacility() {
    assertEquals(5, testCM.getStorageFacility().getDieselTrainsNum());
    assertEquals(1, testCM.getStorageFacility().getElectricTrainsNum());
    assertEquals(2, testCM.getStorageFacility().getLargeBusesNum());
    assertEquals(4, testCM.getStorageFacility().getSmallBusesNum());
  }

  /**
   *
   */
  @Test
  public void testValidBusLine() {
    try {
      final Charset charset = StandardCharsets.UTF_8;
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream testStream = new PrintStream(outputStream, true, charset.name());
      testCM.getLines().get(0).report(testStream);
      outputStream.flush();
      String data = new String(outputStream.toByteArray(), charset);
      testStream.close();
      outputStream.close();
      String strToCompare =
          "====Line Info Start====" + System.lineSeparator()
          + "ID: 10000" + System.lineSeparator()
          + "Name: Campus Connector" + System.lineSeparator()
          + "Type: BUS_LINE" + System.lineSeparator()

          // outbound route report
          + "####Route Info Start####" + System.lineSeparator()
          + "ID: 10" + System.lineSeparator()
          + "Name: East Bound" + System.lineSeparator()
          + "Num stops: 1" + System.lineSeparator()
          + "****Stops Info Start****" + System.lineSeparator()
          + "++++Next Stop Info Start++++" + System.lineSeparator()
          + "####Stop Info Start####" + System.lineSeparator()
          + "ID: 100" + System.lineSeparator()
          + "Name: Blegen Hall" + System.lineSeparator()
          + "Position: 44.972392,-93.243774" + System.lineSeparator()
          + "****Passengers Info Start****" + System.lineSeparator()
          + "Num passengers waiting: 0" + System.lineSeparator()
          + "****Passengers Info End****" + System.lineSeparator()
          + "####Stop Info End####" + System.lineSeparator()
          + "++++Next Stop Info End++++" + System.lineSeparator()
          + "****Stops Info End****" + System.lineSeparator()
          + "####Route Info End####" + System.lineSeparator()

          // inbound route report
          + "####Route Info Start####" + System.lineSeparator()
          + "ID: 11" + System.lineSeparator()
          + "Name: West Bound" + System.lineSeparator()
          + "Num stops: 1" + System.lineSeparator()
          + "****Stops Info Start****" + System.lineSeparator()
          + "++++Next Stop Info Start++++" + System.lineSeparator()
          + "####Stop Info Start####" + System.lineSeparator()
          + "ID: 101" + System.lineSeparator()
          + "Name: St. Paul Student Center" + System.lineSeparator()
          + "Position: 44.98463,-93.186352" + System.lineSeparator()
          + "****Passengers Info Start****" + System.lineSeparator()
          + "Num passengers waiting: 0" + System.lineSeparator()
          + "****Passengers Info End****" + System.lineSeparator()
          + "####Stop Info End####" + System.lineSeparator()
          + "++++Next Stop Info End++++" + System.lineSeparator()
          + "****Stops Info End****" + System.lineSeparator()
          + "####Route Info End####" + System.lineSeparator()


          + "====Line Info End====" + System.lineSeparator();
      assertEquals(data, strToCompare);
    } catch (IOException ioe) {
      fail();
    }
  }

  /**
   *
   */
  @Test
  public void testValidTrainLine() {
    try {
      final Charset charset = StandardCharsets.UTF_8;
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream testStream = new PrintStream(outputStream, true, charset.name());
      testCM.getLines().get(1).report(testStream);
      outputStream.flush();
      String data = new String(outputStream.toByteArray(), charset);
      testStream.close();
      outputStream.close();
      String strToCompare =
          "====Line Info Start====" + System.lineSeparator()
          + "ID: 10001" + System.lineSeparator()
          + "Name: Express Train" + System.lineSeparator()
          + "Type: TRAIN_LINE" + System.lineSeparator()

          // outbound route report
          + "####Route Info Start####" + System.lineSeparator()
          + "ID: 12" + System.lineSeparator()
          + "Name: East Bound Train" + System.lineSeparator()
          + "Num stops: 2" + System.lineSeparator()
          + "****Stops Info Start****" + System.lineSeparator()
          + "++++Next Stop Info Start++++" + System.lineSeparator()
          + "####Stop Info Start####" + System.lineSeparator()
          + "ID: 102" + System.lineSeparator()
          + "Name: Stadium Village" + System.lineSeparator()
          + "Position: 44.974769,-93.22277" + System.lineSeparator()
          + "****Passengers Info Start****" + System.lineSeparator()
          + "Num passengers waiting: 0" + System.lineSeparator()
          + "****Passengers Info End****" + System.lineSeparator()
          + "####Stop Info End####" + System.lineSeparator()
          + "++++Next Stop Info End++++" + System.lineSeparator()
          + "####Stop Info Start####" + System.lineSeparator()
          + "ID: 103" + System.lineSeparator()
          + "Name: Water Tower" + System.lineSeparator()
          + "Position: 44.969139,-93.210371" + System.lineSeparator()
          + "****Passengers Info Start****" + System.lineSeparator()
          + "Num passengers waiting: 0" + System.lineSeparator()
          + "****Passengers Info End****" + System.lineSeparator()
          + "####Stop Info End####" + System.lineSeparator()
          + "****Stops Info End****" + System.lineSeparator()
          + "####Route Info End####" + System.lineSeparator()

          // inbound route report
          + "####Route Info Start####" + System.lineSeparator()
          + "ID: 13" + System.lineSeparator()
          + "Name: West Bound Train" + System.lineSeparator()
          + "Num stops: 1" + System.lineSeparator()
          + "****Stops Info Start****" + System.lineSeparator()
          + "++++Next Stop Info Start++++" + System.lineSeparator()
          + "####Stop Info Start####" + System.lineSeparator()
          + "ID: 104" + System.lineSeparator()
          + "Name: Raymond" + System.lineSeparator()
          + "Position: 44.963552,-93.195403" + System.lineSeparator()
          + "****Passengers Info Start****" + System.lineSeparator()
          + "Num passengers waiting: 0" + System.lineSeparator()
          + "****Passengers Info End****" + System.lineSeparator()
          + "####Stop Info End####" + System.lineSeparator()
          + "++++Next Stop Info End++++" + System.lineSeparator()
          + "****Stops Info End****" + System.lineSeparator()
          + "####Route Info End####" + System.lineSeparator()


          + "====Line Info End====" + System.lineSeparator();
      assertEquals(data, strToCompare);
    } catch (IOException ioe) {
      fail();
    }
  }

  /**
   * Clean up our variables after each test.
   */
  @AfterEach
  public void cleanUpEach() {
    testCM = null;
  }


}
