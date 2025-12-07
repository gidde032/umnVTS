package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Tests for the GetVehiclesCommand class.
 */
public class GetVehiclesCommandTest {

  /**
   * Test that execute sends a JSON payload with the correct command
   * and a vehicles array, even when there are no active vehicles.
   */
  @Test
  public void testExecuteSendsJsonWithVehiclesArray() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    // No active vehicles in this scenario.
    List<Vehicle> vehicles = new ArrayList<Vehicle>();
    when(simulatorMock.getActiveVehicles()).thenReturn(vehicles);

    GetVehiclesCommand command = new GetVehiclesCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();

    command.execute(sessionMock, fromClient);

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    verify(sessionMock).sendJson(captor.capture());
    JsonObject data = captor.getValue();

    assertEquals("updateVehicles", data.get("command").getAsString());

    JsonArray vehiclesArray = data.getAsJsonArray("vehicles");
    assertEquals(0, vehiclesArray.size());
  }
}
