package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import edu.umn.cs.csci3081w.project.model.PassengerFactory;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WebServerSessionStateTest {
  /**
   * Setup deterministic operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    Vehicle.TESTING = true;
  }

  /**
   * Tests if the commands can be modified and stored by accessing them through getCommands().
   */
  @Test
  public void testAddCommandsThroughGetCommands() {
    WebServerSessionState testState = new WebServerSessionState();
    SimulatorCommand commandDummy = mock(SimulatorCommand.class);
    testState.getCommands().put("testCommand", commandDummy);
    assertSame(commandDummy, testState.getCommands().get("testCommand"));
  }
}
