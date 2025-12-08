package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.webserver.VisualTransitSimulator;
import edu.umn.cs.csci3081w.project.webserver.WebServerSession;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class VehicleTest {

  private Vehicle testVehicle;
  private Route testRouteIn;
  private Route testRouteOut;
  private VehicleConcreteSubject testSubject;
  private WebServerSession testSession;
  private Line baseLine;

  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;

    testSession = mock(WebServerSession.class);
    testSubject = mock(VehicleConcreteSubject.class);

    when(testSubject.getSession()).thenReturn(testSession);
    doNothing().when(testSession).sendJson(any(JsonObject.class));

    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<Double>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(0.025);
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
    probabilitiesOut.add(0.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    baseLine = new Line(10000, "testLine",
        "VEHICLE_LINE", testRouteOut, testRouteIn,
        new Issue());

    testVehicle = new VehicleTestImpl(1, baseLine, 3, 1.0,
        new PassengerLoader(), new PassengerUnloader());

    testVehicle.setVehicleSubject(testSubject);
  }

  @Test
  public void testConstructor() {
    assertEquals(1, testVehicle.getId());
    assertEquals("testRouteOut1", testVehicle.getName());
    assertEquals(3, testVehicle.getCapacity());
    assertEquals(1, testVehicle.getSpeed());
    assertEquals(testRouteOut, testVehicle.getLine().getOutboundRoute());
    assertEquals(testRouteIn, testVehicle.getLine().getInboundRoute());
  }

  @Test
  public void testIsTripComplete() {
    assertEquals(false, testVehicle.isTripComplete());
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    testVehicle.move();
    assertEquals(true, testVehicle.isTripComplete());
  }

  @Test
  public void testLoadPassenger() {
    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    Passenger testPassenger2 = new Passenger(2, "testPassenger2");
    Passenger testPassenger3 = new Passenger(1, "testPassenger3");
    Passenger testPassenger4 = new Passenger(1, "testPassenger4");

    assertEquals(1, testVehicle.loadPassenger(testPassenger1));
    assertEquals(1, testVehicle.loadPassenger(testPassenger2));
    assertEquals(1, testVehicle.loadPassenger(testPassenger3));
    assertEquals(0, testVehicle.loadPassenger(testPassenger4));
  }

  @Test
  public void testMove() {
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.move();

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.move();
    assertEquals(null, testVehicle.getNextStop());
  }

  @Test
  public void testUpdate() {
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());
    testVehicle.update();

    Passenger testPassenger1 = new Passenger(3, "testPassenger1");
    assertEquals(1, testVehicle.loadPassenger(testPassenger1));

    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 1", testVehicle.getNextStop().getName());
    assertEquals(0, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals("test stop 2", testVehicle.getNextStop().getName());
    assertEquals(1, testVehicle.getNextStop().getId());

    testVehicle.update();
    assertEquals(null, testVehicle.getNextStop());
  }

  @Test
  public void testProvideInfo() {
    testVehicle.update();
    testVehicle.provideInfo();

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
  public void testProvideInfoForConcreteVehicleTypes()
      throws UnsupportedEncodingException {

    String configPath =
        URLDecoder.decode(
            getClass().getClassLoader().getResource("config.txt").getFile(), "UTF-8");
    WebServerSession dummySession = mock(WebServerSession.class);
    VisualTransitSimulator simulator =
        new VisualTransitSimulator(configPath, dummySession);

    List<Integer> vehicleStartTimings = new ArrayList<Integer>();
    for (int i = 0; i < simulator.getLines().size(); i++) {
      vehicleStartTimings.add(1);
    }
    simulator.setVehicleFactories(0);
    simulator.start(vehicleStartTimings, 10);
    for (int t = 0; t < 10; t++) {
      simulator.update();
    }

    List<Vehicle> activeVehicles = simulator.getActiveVehicles();
    assertTrue(activeVehicles.size() > 0);

    WebServerSession sessionMock = mock(WebServerSession.class);
    VehicleConcreteSubject subject = new VehicleConcreteSubject(sessionMock);
    doNothing().when(sessionMock).sendJson(any(JsonObject.class));

    for (Vehicle v : activeVehicles) {
      v.setVehicleSubject(subject);
      v.update();
      v.provideInfo();
    }
  }

  @Test
  public void testMoveAfterTripCompleteUsesTripCompleteBranch() {
    while (!testVehicle.isTripComplete()) {
      testVehicle.move();
    }
    Position before = testVehicle.getPosition();
    testVehicle.move();
    Position after = testVehicle.getPosition();
    assertEquals(before.getLongitude(), after.getLongitude());
    assertEquals(before.getLatitude(), after.getLatitude());
  }

  private static class NegativeSpeedVehicle extends Vehicle {

    NegativeSpeedVehicle(int id, Line line, int capacity,
                         double speed, PassengerLoader loader,
                         PassengerUnloader unloader) {
      super(id, line, capacity, speed, loader, unloader);
    }

    @Override
    public void report(PrintStream out) {
    }

    @Override
    public int getCurrentCO2Emission() {
      return 0;
    }
  }

  @Test
  public void testMoveWithNegativeSpeedUsesNegativeSpeedBranch() {
    PassengerLoader loader = new PassengerLoader();
    PassengerUnloader unloader = new PassengerUnloader();
    Vehicle negative = new NegativeSpeedVehicle(
        2, baseLine, 3, -1.0, loader, unloader);

    Position before = negative.getPosition();
    negative.move();
    Position after = negative.getPosition();

    assertEquals(before.getLongitude(), after.getLongitude());
    assertEquals(before.getLatitude(), after.getLatitude());
  }

  @Test
  public void testProvideInfoForTrainTypes() throws Exception {
    WebServerSession sessionMock = mock(WebServerSession.class);
    VehicleConcreteSubject subject = new VehicleConcreteSubject(sessionMock);
    doNothing().when(sessionMock).sendJson(any(JsonObject.class));

    Vehicle electricTrain = createTrainViaReflection(ElectricTrain.class);
    Vehicle dieselTrain = createTrainViaReflection(DieselTrain.class);

    electricTrain.setVehicleSubject(subject);
    dieselTrain.setVehicleSubject(subject);

    electricTrain.update();
    dieselTrain.update();
    electricTrain.provideInfo();
    dieselTrain.provideInfo();
  }

  private Vehicle createTrainViaReflection(Class<?> clazz) throws Exception {
    PassengerLoader loader = new PassengerLoader();
    PassengerUnloader unloader = new PassengerUnloader();

    for (Constructor<?> ctor : clazz.getConstructors()) {
      Class<?>[] types = ctor.getParameterTypes();
      Object[] args = new Object[types.length];
      for (int i = 0; i < types.length; i++) {
        if (types[i] == int.class) {
          args[i] = 1;
        } else if (types[i] == double.class) {
          args[i] = 1.0;
        } else if (types[i] == Line.class) {
          args[i] = baseLine;
        } else if (types[i] == PassengerLoader.class) {
          args[i] = loader;
        } else if (types[i] == PassengerUnloader.class) {
          args[i] = unloader;
        } else {
          args[i] = null;
        }
      }
      Object instance = ctor.newInstance(args);
      if (instance instanceof Vehicle) {
        return (Vehicle) instance;
      }
    }
    throw new IllegalStateException("No suitable Vehicle constructor found for " + clazz);
  }

  @AfterEach
  public void cleanUpEach() {
    testVehicle = null;
  }
}
