package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Vehicle;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Tests for the RegisterVehicleCommand class.
 */
public class RegisterVehicleCommandTest {

  /**
   * Test that execute registers the vehicle that matches the given id.
   */
  @Test
  public void testExecuteRegistersMatchingVehicle() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    Vehicle vehicle1 = mock(Vehicle.class);
    Vehicle vehicle2 = mock(Vehicle.class);

    when(vehicle1.getId()).thenReturn(1);
    when(vehicle2.getId()).thenReturn(2);

    List<Vehicle> activeVehicles = new ArrayList<Vehicle>();
    activeVehicles.add(vehicle1);
    activeVehicles.add(vehicle2);
    when(simulatorMock.getActiveVehicles()).thenReturn(activeVehicles);

    RegisterVehicleCommand command = new RegisterVehicleCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "registerVehicle");
    fromClient.addProperty("id", 2);

    command.execute(sessionMock, fromClient);

    verify(simulatorMock).addObserver(vehicle2);
  }

  /**
   * Test that execute passes null to addObserver when no vehicle matches.
   */
  @Test
  public void testExecuteRegistersNullWhenNoMatch() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    Vehicle vehicle1 = mock(Vehicle.class);
    Vehicle vehicle2 = mock(Vehicle.class);

    when(vehicle1.getId()).thenReturn(1);
    when(vehicle2.getId()).thenReturn(2);

    List<Vehicle> activeVehicles = new ArrayList<Vehicle>();
    activeVehicles.add(vehicle1);
    activeVehicles.add(vehicle2);
    when(simulatorMock.getActiveVehicles()).thenReturn(activeVehicles);

    RegisterVehicleCommand command = new RegisterVehicleCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "registerVehicle");
    fromClient.addProperty("id", 99);

    command.execute(sessionMock, fromClient);

    ArgumentCaptor<Vehicle> captor = ArgumentCaptor.forClass(Vehicle.class);
    verify(simulatorMock).addObserver(captor.capture());
    assertNull(captor.getValue());
  }
}
