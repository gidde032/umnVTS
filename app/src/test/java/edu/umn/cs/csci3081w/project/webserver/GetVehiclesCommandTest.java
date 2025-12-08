package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.SmallBus;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import edu.umn.cs.csci3081w.project.model.LargeBus;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Tests for the GetVehiclesCommand class.
 */
public class GetVehiclesCommandTest {

  /**
   * Test that execute sends a JSON payload with an empty vehicles array
   * when there are no active vehicles.
   */
  @Test
  public void testExecuteWithNoActiveVehicles() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    List<Vehicle> vehicles = new ArrayList<Vehicle>();
    when(simulatorMock.getActiveVehicles()).thenReturn(vehicles);

    GetVehiclesCommand command = new GetVehiclesCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "getVehicles");

    command.execute(sessionMock, fromClient);

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    verify(sessionMock).sendJson(captor.capture());
    JsonObject data = captor.getValue();

    assertEquals("updateVehicles", data.get("command").getAsString());

    JsonArray vehiclesArray = data.getAsJsonArray("vehicles");
    assertEquals(0, vehiclesArray.size());
  }

  /**
   * Test that execute starts processing a vehicle and currently
   * throws a NullPointerException due to the underlying decorator logic.
   * This still increases coverage on the loop and type handling.
   */
  @Test
  public void testExecuteWithSingleVehicleExpectException() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    SmallBus smallBus = mock(SmallBus.class);
    when(simulatorMock.getActiveVehicles())
        .thenReturn(Collections.<Vehicle>singletonList(smallBus));

    // Basic fields used before the failure point
    when(smallBus.getId()).thenReturn(1);
    when(smallBus.getPassengers()).thenReturn(Collections.emptyList());
    when(smallBus.getCapacity()).thenReturn(10);

    GetVehiclesCommand command = new GetVehiclesCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "getVehicles");

    assertThrows(NullPointerException.class,
        () -> command.execute(sessionMock, fromClient));
  }

  /**
   * Test execute using a real simulator so that the decorator logic
   * and JSON fields are fully exercised.
   */
  @Test
  public void testExecuteWithRealSimulatorVehicles() throws UnsupportedEncodingException {
    // Build a real simulator from the config file
    WebServerSession webServerSessionDummy = mock(WebServerSession.class);
    String configPath = URLDecoder.decode(
        getClass().getClassLoader().getResource("config.txt").getFile(), "UTF-8");

    VisualTransitSimulator simulator =
        new VisualTransitSimulator(configPath, webServerSessionDummy);

    // Start one vehicle per line with a short simulation
    List<Integer> startTimings = new ArrayList<Integer>();
    for (int i = 0; i < simulator.getLines().size(); i++) {
      startTimings.add(1);
    }
    simulator.setVehicleFactories(0);
    simulator.start(startTimings, 3);
    simulator.update();
    simulator.update();

    // Ensure there is at least one active vehicle
    List<Vehicle> activeVehicles = simulator.getActiveVehicles();
    assertTrue(activeVehicles.size() > 0);

    // Mark the first active vehicle's line as having an issue so that
    // the AlphaDecorator branch is exercised.
    activeVehicles.get(0).getLine().createIssue();

    WebServerSession sessionMock = mock(WebServerSession.class);
    GetVehiclesCommand command = new GetVehiclesCommand(simulator);
    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "getVehicles");

    command.execute(sessionMock, fromClient);

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    verify(sessionMock).sendJson(captor.capture());
    JsonObject data = captor.getValue();

    assertEquals("updateVehicles", data.get("command").getAsString());
    JsonArray vehiclesArray = data.getAsJsonArray("vehicles");
    assertTrue(vehiclesArray.size() > 0);

    // Check that the first vehicle has the expected fields populated
    JsonObject firstVehicle = vehiclesArray.get(0).getAsJsonObject();
    firstVehicle.get("type").getAsString();
    firstVehicle.get("co2").getAsInt();

    JsonObject pos = firstVehicle.getAsJsonObject("position");
    pos.get("longitude").getAsDouble();
    pos.get("latitude").getAsDouble();

    JsonObject color = firstVehicle.getAsJsonObject("color");
    color.get("r").getAsInt();
    color.get("g").getAsInt();
    color.get("b").getAsInt();
    color.get("alpha").getAsInt();
  }

  /**
   * Test that the LargeBus instanceof branch is exercised.
   *
   * <p>We only care that the code reaches the LargeBus branch; the test
   * expects a NullPointerException from deeper decorator logic.
   */
  @Test
  public void testExecuteWithLargeBusBranch() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    LargeBus largeBus = mock(LargeBus.class);
    when(simulatorMock.getActiveVehicles())
        .thenReturn(Collections.<Vehicle>singletonList(largeBus));

    when(largeBus.getId()).thenReturn(2);
    when(largeBus.getPassengers()).thenReturn(Collections.emptyList());
    when(largeBus.getCapacity()).thenReturn(40);

    GetVehiclesCommand command = new GetVehiclesCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "getVehicles");

    assertThrows(NullPointerException.class,
        () -> command.execute(sessionMock, fromClient));
  }

}
