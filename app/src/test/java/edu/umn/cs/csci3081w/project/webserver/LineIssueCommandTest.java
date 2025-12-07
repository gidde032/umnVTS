package edu.umn.cs.csci3081w.project.webserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.Line;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for the LineIssueCommand class.
 */
public class LineIssueCommandTest {

  /**
   * Test that execute calls createIssue on the line with the given id.
   */
  @Test
  public void testExecuteCreatesIssueOnMatchingLine() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    Line line1 = mock(Line.class);
    Line line2 = mock(Line.class);

    when(line1.getId()).thenReturn(1);
    when(line2.getId()).thenReturn(2);

    List<Line> lines = new ArrayList<Line>();
    lines.add(line1);
    lines.add(line2);
    when(simulatorMock.getLines()).thenReturn(lines);

    LineIssueCommand command = new LineIssueCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "lineIssue");
    fromClient.addProperty("id", 2);

    command.execute(sessionMock, fromClient);

    verify(line2).createIssue();
    verify(line1, never()).createIssue();
  }

  /**
   * Test that execute does not call createIssue when no ids match.
   */
  @Test
  public void testExecuteDoesNothingWhenNoLineMatches() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    Line line1 = mock(Line.class);
    Line line2 = mock(Line.class);

    when(line1.getId()).thenReturn(1);
    when(line2.getId()).thenReturn(2);

    List<Line> lines = new ArrayList<Line>();
    lines.add(line1);
    lines.add(line2);
    when(simulatorMock.getLines()).thenReturn(lines);

    LineIssueCommand command = new LineIssueCommand(simulatorMock);
    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "lineIssue");
    fromClient.addProperty("id", 99);

    command.execute(sessionMock, fromClient);

    verify(line1, never()).createIssue();
    verify(line2, never()).createIssue();
  }
}
