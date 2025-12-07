package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Line;
import edu.umn.cs.csci3081w.project.model.Position;
import edu.umn.cs.csci3081w.project.model.Route;
import edu.umn.cs.csci3081w.project.model.Stop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class GetRoutesCommandTest {

  /**
   * Test that execute builds the expected JSON structure for routes.
   */
  @Test
  public void testExecuteBuildsRoutesJson() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    // Set up a line with one outbound and one inbound route.
    Line lineMock = mock(Line.class);
    Route outboundRouteMock = mock(Route.class);
    Route inboundRouteMock = mock(Route.class);

    Stop stopMock = mock(Stop.class);
    Position positionMock = mock(Position.class);

    List<Stop> stops = new ArrayList<Stop>();
    stops.add(stopMock);

    when(outboundRouteMock.getId()).thenReturn(1);
    when(outboundRouteMock.getStops()).thenReturn(stops);

    when(inboundRouteMock.getId()).thenReturn(2);
    when(inboundRouteMock.getStops()).thenReturn(stops);

    when(stopMock.getId()).thenReturn(10);
    when(stopMock.getPassengers()).thenReturn(Collections.emptyList());
    when(stopMock.getPosition()).thenReturn(positionMock);

    when(positionMock.getLongitude()).thenReturn(-93.0);
    when(positionMock.getLatitude()).thenReturn(44.0);

    when(lineMock.getOutboundRoute()).thenReturn(outboundRouteMock);
    when(lineMock.getInboundRoute()).thenReturn(inboundRouteMock);

    List<Line> lines = new ArrayList<Line>();
    lines.add(lineMock);
    when(simulatorMock.getLines()).thenReturn(lines);

    GetRoutesCommand command = new GetRoutesCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();

    command.execute(sessionMock, fromClient);

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
    verify(sessionMock).sendJson(captor.capture());
    JsonObject data = captor.getValue();

    assertEquals("updateRoutes", data.get("command").getAsString());

    JsonArray routesArray = data.getAsJsonArray("routes");
    assertEquals(2, routesArray.size());

    JsonObject firstRoute = routesArray.get(0).getAsJsonObject();
    assertEquals(1, firstRoute.get("id").getAsInt());

    JsonArray firstRouteStops = firstRoute.getAsJsonArray("stops");
    assertEquals(1, firstRouteStops.size());

    JsonObject firstStop = firstRouteStops.get(0).getAsJsonObject();
    assertEquals(10, firstStop.get("id").getAsInt());
    assertEquals(0, firstStop.get("numPeople").getAsInt());
  }
}
