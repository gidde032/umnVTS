package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Tests for the StartCommand class.
 */
public class StartCommandTest {

  /**
   * Test that execute reads the JSON payload correctly and forwards
   * the values to the simulator.
   */
  @Test
  public void testExecuteStartsSimulationWithCorrectParameters() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    StartCommand command = new StartCommand(simulatorMock);

    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "start");
    fromClient.addProperty("numTimeSteps", 5);

    JsonArray timeBetweenVehicles = new JsonArray();
    timeBetweenVehicles.add(3);
    timeBetweenVehicles.add(4);
    fromClient.add("timeBetweenVehicles", timeBetweenVehicles);

    command.execute(sessionMock, fromClient);

    verify(simulatorMock).setVehicleFactories(Mockito.anyInt());

    ArgumentCaptor<List> timingsCaptor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<Integer> stepsCaptor = ArgumentCaptor.forClass(Integer.class);
    verify(simulatorMock).start(timingsCaptor.capture(), stepsCaptor.capture());

    List timings = timingsCaptor.getValue();
    Integer numTimeSteps = stepsCaptor.getValue();

    assertEquals(5, numTimeSteps.intValue());
    assertEquals(2, timings.size());
    assertEquals(3, ((Number) timings.get(0)).intValue());
    assertEquals(4, ((Number) timings.get(1)).intValue());
  }
}
