package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import edu.umn.cs.csci3081w.project.model.Bus;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.Train;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import edu.umn.cs.csci3081w.project.model.VehicleFactory;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for the VisualTransitSimulator class.
 *
 * <p>These tests exercise different update paths, including normal updates,
 * paused updates, line issues, logging branches, and the branch where
 * the simulation time exceeds the configured number of time steps.
 */
public class VisualTransitSimulatorTest {

  private VisualTransitSimulator simulator;

  /**
   * Sets up a VisualTransitSimulator instance using the same config file
   * that the WebServerSession uses.
   */
  @BeforeEach
  public void setUp() throws UnsupportedEncodingException {
    String configPath =
        URLDecoder.decode(
            getClass().getClassLoader().getResource("config.txt").getFile(), "UTF-8");
    WebServerSession dummySession = mock(WebServerSession.class);
    simulator = new VisualTransitSimulator(configPath, dummySession);
  }

  /**
   * Tests that calling update without any pause runs the normal update path.
   *
   * <p>If an exception is thrown, the test will fail automatically.
   */
  @Test
  public void testUpdateRunsNormally() {
    simulator.setVehicleFactories(0);

    List<Integer> vehicleStartTimings = new ArrayList<>();
    for (int i = 0; i < simulator.getLines().size(); i++) {
      vehicleStartTimings.add(1);
    }

    simulator.start(vehicleStartTimings, 3);
    simulator.update();
    simulator.update();
    simulator.update();
  }

  /**
   * Tests that when the simulator is paused, the body of update does not run.
   *
   * <p>This mainly exercises the {@code if (!paused)} branch.
   */
  @Test
  public void testUpdateWhilePaused() {
    simulator.setVehicleFactories(0);

    List<Integer> vehicleStartTimings = new ArrayList<>();
    for (int i = 0; i < simulator.getLines().size(); i++) {
      vehicleStartTimings.add(1);
    }

    simulator.start(vehicleStartTimings, 5);
    simulator.togglePause();
    simulator.update();  // paused update should effectively be a no-op
  }

  /**
   * Tests that calling update more times than the configured number of
   * time steps exercises the branch where {@code simulationTimeElapsed > numTimeSteps}.
   */
  @Test
  public void testUpdateStopsAfterNumTimeSteps() {
    simulator.setVehicleFactories(0);

    List<Integer> vehicleStartTimings = new ArrayList<>();
    for (int i = 0; i < simulator.getLines().size(); i++) {
      vehicleStartTimings.add(1);
    }

    simulator.start(vehicleStartTimings, 1);

    simulator.update(); // time step 1
    simulator.update(); // simulationTimeElapsed > numTimeSteps, early return
    simulator.update(); // calling again should still be safe
  }

  /**
   * Tests that when all lines have an issue, no vehicles are generated
   * even if the timing would otherwise allow generation.
   */
  @Test
  public void testLineIssuePreventsVehicleGeneration() {
    simulator.setVehicleFactories(0);

    List<Integer> vehicleStartTimings = new ArrayList<>();
    for (int i = 0; i < simulator.getLines().size(); i++) {
      vehicleStartTimings.add(1);
      Line line = simulator.getLines().get(i);
      line.createIssue();  // mark each line as having an issue
    }

    simulator.start(vehicleStartTimings, 2);
    simulator.update();

    assertEquals(
        0,
        simulator.getActiveVehicles().size(),
        "no vehicles should be generated when all lines have issues");
  }

  /**
   * Tests the branch in update where timeSinceLastVehicle is greater than zero
   * and gets decremented in the else part of the generation loop.
   */
  @Test
  public void testUpdateDecrementsTimeSinceLastVehicle() {
    simulator.setVehicleFactories(0);

    List<Integer> vehicleStartTimings = new ArrayList<>();
    for (int i = 0; i < simulator.getLines().size(); i++) {
      // Use a value greater than 1 to force the else branch on the second update.
      vehicleStartTimings.add(3);
    }

    simulator.start(vehicleStartTimings, 4);

    simulator.update();
    simulator.update();
  }

  /**
   * Tests the branch where a Bus in the active list completes its trip
   * and is removed, exercising the completedTripVehicles and factory return path.
   */
  @Test
  public void testUpdateHandlesCompletedTripBus() throws Exception {
    Bus mockBus = mock(Bus.class);
    Mockito.when(mockBus.isTripComplete()).thenReturn(true);

    Field activeField = VisualTransitSimulator.class.getDeclaredField("activeVehicles");
    activeField.setAccessible(true);
    List<Vehicle> activeList = new ArrayList<>();
    activeList.add(mockBus);
    activeField.set(simulator, activeList);

    Field completedField =
        VisualTransitSimulator.class.getDeclaredField("completedTripVehicles");
    completedField.setAccessible(true);
    completedField.set(simulator, new ArrayList<Vehicle>());

    Field busFactoryField =
        VisualTransitSimulator.class.getDeclaredField("busFactory");
    busFactoryField.setAccessible(true);
    busFactoryField.set(simulator, mock(VehicleFactory.class));

    Field numStepsField =
        VisualTransitSimulator.class.getDeclaredField("numTimeSteps");
    numStepsField.setAccessible(true);
    numStepsField.setInt(simulator, 1);

    Field elapsedField =
        VisualTransitSimulator.class.getDeclaredField("simulationTimeElapsed");
    elapsedField.setAccessible(true);
    elapsedField.setInt(simulator, 0);

    simulator.update();

    @SuppressWarnings("unchecked")
    List<Vehicle> remaining =
        (List<Vehicle>) activeField.get(simulator);

    assertEquals(
        0,
        remaining.size(),
        "activeVehicles should be empty after the completed trip vehicle is removed");
  }

  /**
   * Tests the branch where a Train in the active list completes its trip
   * and is removed, covering the trainFactory return path.
   */
  @Test
  public void testUpdateHandlesCompletedTripTrain() throws Exception {
    Train mockTrain = mock(Train.class);
    Mockito.when(mockTrain.isTripComplete()).thenReturn(true);

    Field activeField = VisualTransitSimulator.class.getDeclaredField("activeVehicles");
    activeField.setAccessible(true);
    List<Vehicle> activeList = new ArrayList<>();
    activeList.add(mockTrain);
    activeField.set(simulator, activeList);

    Field completedField =
        VisualTransitSimulator.class.getDeclaredField("completedTripVehicles");
    completedField.setAccessible(true);
    completedField.set(simulator, new ArrayList<Vehicle>());

    Field trainFactoryField =
        VisualTransitSimulator.class.getDeclaredField("trainFactory");
    trainFactoryField.setAccessible(true);
    trainFactoryField.set(simulator, mock(VehicleFactory.class));

    Field numStepsField =
        VisualTransitSimulator.class.getDeclaredField("numTimeSteps");
    numStepsField.setAccessible(true);
    numStepsField.setInt(simulator, 1);

    Field elapsedField =
        VisualTransitSimulator.class.getDeclaredField("simulationTimeElapsed");
    elapsedField.setAccessible(true);
    elapsedField.setInt(simulator, 0);

    simulator.update();

    @SuppressWarnings("unchecked")
    List<Vehicle> remaining =
        (List<Vehicle>) activeField.get(simulator);

    assertEquals(
        0,
        remaining.size(),
        "activeVehicles should be empty after the completed trip train is removed");
  }

  /**
   * Tests that addObserver forwards the vehicle to the subject without throwing.
   */
  @Test
  public void testAddObserverDoesNotThrow() {
    Vehicle dummyVehicle = mock(Vehicle.class);
    simulator.addObserver(dummyVehicle);
  }

  /**
   * Tests the constructor logging branch where LOGGING is true and
   * each line reports itself.
   */
  @Test
  public void testConstructorLoggingBranch() throws Exception {
    Field loggingField = VisualTransitSimulator.class.getDeclaredField("LOGGING");
    loggingField.setAccessible(true);
    boolean original = loggingField.getBoolean(null);
    loggingField.setBoolean(null, true);
    try {
      String configPath =
          URLDecoder.decode(
              getClass().getClassLoader().getResource("config.txt").getFile(), "UTF-8");
      WebServerSession dummySession = mock(WebServerSession.class);
      new VisualTransitSimulator(configPath, dummySession);
    } finally {
      loggingField.setBoolean(null, original);
    }
  }

  /**
   * Tests the logging branches inside update for both vehicles and lines
   * by temporarily setting LOGGING to true.
   */
  @Test
  public void testUpdateLoggingBranches() throws Exception {
    Field loggingField = VisualTransitSimulator.class.getDeclaredField("LOGGING");
    loggingField.setAccessible(true);
    boolean original = loggingField.getBoolean(null);
    loggingField.setBoolean(null, true);
    try {
      simulator.setVehicleFactories(0);

      List<Integer> vehicleStartTimings = new ArrayList<>();
      for (int i = 0; i < simulator.getLines().size(); i++) {
        vehicleStartTimings.add(1);
      }

      simulator.start(vehicleStartTimings, 2);
      simulator.update();
      simulator.update();
    } finally {
      loggingField.setBoolean(null, original);
    }
  }
}
