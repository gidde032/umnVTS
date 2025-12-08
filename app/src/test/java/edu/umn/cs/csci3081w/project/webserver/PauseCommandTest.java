package edu.umn.cs.csci3081w.project.webserver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

/**
 * Tests for the PauseCommand class.
 */
public class PauseCommandTest {

  /**
   * Test that execute calls togglePause on the simulator.
   */
  @Test
  public void testExecuteCallsTogglePause() {
    VisualTransitSimulator simulatorMock = mock(VisualTransitSimulator.class);
    WebServerSession sessionMock = mock(WebServerSession.class);

    PauseCommand command = new PauseCommand(simulatorMock);

    JsonObject fromClient = new JsonObject();
    fromClient.addProperty("command", "pause");

    command.execute(sessionMock, fromClient);

    verify(simulatorMock).togglePause();
  }
}
