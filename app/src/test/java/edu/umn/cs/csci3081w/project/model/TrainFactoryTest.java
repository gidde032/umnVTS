package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrainFactoryTest {
  private StorageFacility storageFacility;
  private TrainFactory trainFactory;
  private TrainFactory trainFactoryNight;

  /**
   * Setup operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
    storageFacility = new StorageFacility(0, 0, 3, 3);
    trainFactory = new TrainFactory(storageFacility, new Counter(), 9);
    trainFactoryNight = new TrainFactory(storageFacility, new Counter(), 20);
  }

  @Test
  public void testConstructor() {
    assertTrue(trainFactory.getGenerationStrategy() instanceof TrainStrategyDay);
  }

  @Test
  public void testGenerateVehicle() {
    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    Line line = new Line(10000, "testLine", "TRAIN", testRouteOut, testRouteIn,
        new Issue());

    Vehicle vehicle1 = trainFactory.generateVehicle(line);
    Vehicle vehicle2 = trainFactory.generateVehicle(line);
    Vehicle vehicle3 = trainFactory.generateVehicle(line);
    Vehicle vehicle4 = trainFactory.generateVehicle(line);
    assertTrue(vehicle1 instanceof ElectricTrain);
    assertTrue(vehicle2 instanceof ElectricTrain);
    assertTrue(vehicle3 instanceof ElectricTrain);
    assertTrue(vehicle4 instanceof DieselTrain);
    Vehicle vehicle5 = trainFactoryNight.generateVehicle(null);
    assertNull(vehicle5);
  }

  @Test
  public void testReturnVehicleElectricTrain() {
    List<Stop> stopsIn = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "test stop 1", new Position(-93.243774, 44.972392));
    Stop stop2 = new Stop(1, "test stop 2", new Position(-93.235071, 44.973580));
    stopsIn.add(stop1);
    stopsIn.add(stop2);
    List<Double> distancesIn = new ArrayList<>();
    distancesIn.add(0.843774422231134);
    List<Double> probabilitiesIn = new ArrayList<Double>();
    probabilitiesIn.add(.025);
    probabilitiesIn.add(0.3);
    PassengerGenerator generatorIn = new RandomPassengerGenerator(stopsIn, probabilitiesIn);

    Route testRouteIn = new Route(0, "testRouteIn",
        stopsIn, distancesIn, generatorIn);

    List<Stop> stopsOut = new ArrayList<Stop>();
    stopsOut.add(stop2);
    stopsOut.add(stop1);
    List<Double> distancesOut = new ArrayList<>();
    distancesOut.add(0.843774422231134);
    List<Double> probabilitiesOut = new ArrayList<Double>();
    probabilitiesOut.add(0.3);
    probabilitiesOut.add(.025);
    PassengerGenerator generatorOut = new RandomPassengerGenerator(stopsOut, probabilitiesOut);

    Route testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    Train testTrain = new ElectricTrain(1, new Line(10000, "testLine", "BUS",
        testRouteOut, testRouteIn, new Issue()), 3, 1.0);

    Train testTrain2 = new DieselTrain(1, new Line(10000, "testLine", "BUS",
        testRouteOut, testRouteIn, new Issue()), 3, 1.0);

    assertEquals(3, trainFactory.getStorageFacility().getElectricTrainsNum());
    assertEquals(3, trainFactory.getStorageFacility().getDieselTrainsNum());
    trainFactory.returnVehicle(testTrain);
    trainFactory.returnVehicle(testTrain2);
    assertEquals(4, trainFactory.getStorageFacility().getElectricTrainsNum());
    assertEquals(4, trainFactory.getStorageFacility().getDieselTrainsNum());
  }

  @Test
  public void testGenerateVehicleDieselBranch() throws Exception {
    StorageFacility sf = new StorageFacility(0, 0, 3, 3);
    Counter counter = new Counter();
    TrainFactory factory = new TrainFactory(sf, counter, 9);

    GenerationStrategy dieselStrategy = new GenerationStrategy() {
      @Override
      public String getTypeOfVehicle(StorageFacility storage) {
        return DieselTrain.DIESEL_TRAIN_VEHICLE;
      }
    };

    Field field = TrainFactory.class.getDeclaredField("generationStrategy");
    field.setAccessible(true);
    field.set(factory, dieselStrategy);

    List<Stop> stops = new ArrayList<Stop>();
    Stop stop1 = new Stop(0, "s1", new Position(-93.0, 44.0));
    Stop stop2 = new Stop(1, "s2", new Position(-93.1, 44.1));
    stops.add(stop1);
    stops.add(stop2);
    List<Double> distances = new ArrayList<Double>();
    distances.add(1.0);
    List<Double> probabilities = new ArrayList<Double>();
    probabilities.add(0.5);
    probabilities.add(0.5);
    PassengerGenerator generator =
        new RandomPassengerGenerator(stops, probabilities);

    Route in = new Route(0, "in", stops, distances, generator);
    Route out = new Route(1, "out", stops, distances, generator);
    Line line = new Line(1, "line", "TRAIN", out, in, new Issue());

    assertEquals(3, sf.getDieselTrainsNum());
    Vehicle v = factory.generateVehicle(line);
    assertTrue(v instanceof DieselTrain);
    assertEquals(2, sf.getDieselTrainsNum());
  }

  @Test
  public void testReturnVehicleDieselBranch() {
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

    Route testRouteIn = new Route(0, "testRouteIn",
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

    Route testRouteOut = new Route(1, "testRouteOut",
        stopsOut, distancesOut, generatorOut);

    Train diesel = new DieselTrain(2, new Line(10001, "testLine2", "TRAIN",
        testRouteOut, testRouteIn, new Issue()), 3, 1.0);

    int before = trainFactory.getStorageFacility().getDieselTrainsNum();
    trainFactory.returnVehicle(diesel);
    assertEquals(before + 1, trainFactory.getStorageFacility().getDieselTrainsNum());
  }
}
