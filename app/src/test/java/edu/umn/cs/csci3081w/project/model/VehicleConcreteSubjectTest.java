package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.webserver.WebServerSession;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class VehicleConcreteSubjectTest {

  private Vehicle testVehicle;
  private Route testRouteIn;
  private Route testRouteOut;
  private VehicleConcreteSubject testSubject;
  private WebServerSession testSession;

  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;

    testSession = mock(WebServerSession.class);

    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<Double>();
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
    List<Double> distancesOut = new ArrayList<Double>();
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

    testVehicle.setVehicleSubject(testSubject);
  }

  @Test
  public void testConstructor() {
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(testSession);
    assertEquals(0, vehicleConcreteSubject.getObservers().size());
  }

  @Test
  public void testAttachObserver() {
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(testSession);
    vehicleConcreteSubject.attachObserver(testVehicle);
    assertEquals(1, vehicleConcreteSubject.getObservers().size());
  }

  @Test
  public void testDetachObserver() {
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(testSession);
    vehicleConcreteSubject.attachObserver(testVehicle);
    vehicleConcreteSubject.detachObserver(testVehicle);
    assertEquals(0, vehicleConcreteSubject.getObservers().size());
  }

  @Test
  public void testNotifyObservers() {
    VehicleConcreteSubject vehicleConcreteSubject =
        new VehicleConcreteSubject(testSession);
    testVehicle.setVehicleSubject(vehicleConcreteSubject);
    vehicleConcreteSubject.attachObserver(testVehicle);
    testVehicle.update();
    vehicleConcreteSubject.notifyObservers();

    ArgumentCaptor<JsonObject> argCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(testSession).sendJson(argCaptor.capture());
    JsonObject testOutput = argCaptor.getValue();

    String command = testOutput.get("command").getAsString();
    String expectedCommand = "observedVehicle";
    assertEquals(expectedCommand, command);
    String observedText = testOutput.get("text").getAsString();
    String expectedText = "1" + System.lineSeparator()
        + "-----------------------------" + System.lineSeparator()
        + "* Type: " + System.lineSeparator()
        + "* Position: (-93.235071,44.973580)" + System.lineSeparator()
        + "* Passengers: 0" + System.lineSeparator()
        + "* CO2: 0" + System.lineSeparator();
    assertEquals(expectedText, observedText);
  }

  @Test
  public void testNotifyObserversRemovesCompletedObserver() {
    WebServerSession sessionMock = mock(WebServerSession.class);
    VehicleConcreteSubject subject = new VehicleConcreteSubject(sessionMock);

    VehicleObserver observerMock = mock(VehicleObserver.class);
    when(observerMock.provideInfo()).thenReturn(true);

    subject.attachObserver(observerMock);
    assertEquals(1, subject.getObservers().size());

    subject.notifyObservers();

    assertEquals(0, subject.getObservers().size());
    verify(observerMock).setVehicleSubject(subject);
    verify(observerMock).provideInfo();
  }
}
