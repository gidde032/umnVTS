package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import edu.umn.cs.csci3081w.project.model.Line;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the VisualTransitSimulator class.
 *
 * <p>These tests exercise different update paths, including normal updates,
 * paused updates, line issues, and the branch where the simulation time
 * exceeds the configured number of time steps.
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
}
