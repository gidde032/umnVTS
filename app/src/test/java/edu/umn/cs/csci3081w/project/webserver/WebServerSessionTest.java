package edu.umn.cs.csci3081w.project.webserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.JsonObject;
import edu.umn.cs.csci3081w.project.model.PassengerFactory;
import edu.umn.cs.csci3081w.project.model.RandomPassengerGenerator;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class WebServerSessionTest {

  /**
   * Setup deterministic operations before each test runs.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
  }

  /**
   * Test command for initializing the simulation.
   */
  @Test
  public void testSimulationInitialization() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);
    JsonObject commandFromClient = new JsonObject();
    commandFromClient.addProperty("command", "initLines");
    webServerSessionSpy.onMessage(commandFromClient.toString());
    ArgumentCaptor<JsonObject> messageCaptor = ArgumentCaptor.forClass(JsonObject.class);
    verify(webServerSessionSpy).sendJson(messageCaptor.capture());
    JsonObject commandToClient = messageCaptor.getValue();
    assertEquals("2", commandToClient.get("numLines").getAsString());
  }

  /**
   * Tests that an unknown command is handled without throwing an exception.
   *
   * <p>This exercises the branch where the command is not present in the
   * command map of {@link WebServerSessionState}.
   */
  @Test
  public void testUnknownCommandDoesNotThrow() {
    WebServerSession webServerSessionSpy = spy(WebServerSession.class);
    doNothing().when(webServerSessionSpy).sendJson(Mockito.isA(JsonObject.class));
    Session sessionDummy = mock(Session.class);
    webServerSessionSpy.onOpen(sessionDummy);

    JsonObject unknownCommand = new JsonObject();
    unknownCommand.addProperty("command", "thisIsNotARealCommand");

    webServerSessionSpy.onMessage(unknownCommand.toString());
  }

  /**
   * Tests that sendJson successfully sends a message when no IOException is thrown.
   */
  @Test
  public void testSendJsonSuccess() throws IOException {
    WebServerSession webServerSession = new WebServerSession();

    Session sessionMock = mock(Session.class);
    RemoteEndpoint.Basic basicMock = mock(RemoteEndpoint.Basic.class);

    when(sessionMock.getBasicRemote()).thenReturn(basicMock);

    webServerSession.onOpen(sessionMock);

    JsonObject data = new JsonObject();
    data.addProperty("key", "value");

    webServerSession.sendJson(data);

    verify(basicMock).sendText(data.toString());
  }

  /**
   * Tests that sendJson handles IOException from the underlying session.
   *
   * <p>This exercises the catch block in sendJson.
   */
  @Test
  public void testSendJsonHandlesIoException() throws IOException {
    WebServerSession webServerSession = new WebServerSession();

    Session sessionMock = mock(Session.class);
    RemoteEndpoint.Basic basicMock = mock(RemoteEndpoint.Basic.class);

    when(sessionMock.getBasicRemote()).thenReturn(basicMock);
    doThrow(new IOException("forced failure")).when(basicMock).sendText(anyString());

    webServerSession.onOpen(sessionMock);

    JsonObject data = new JsonObject();
    data.addProperty("key", "value");

    webServerSession.sendJson(data);
  }

  /**
   * Tests that onError can be called without throwing an exception.
   */
  @Test
  public void testOnErrorDoesNotThrow() {
    WebServerSession webServerSession = new WebServerSession();
    webServerSession.onError(new RuntimeException("test error"));
  }

  /**
   * Tests that onClose clears the session field.
   */
  @Test
  public void testOnCloseClearsSession() throws Exception {
    WebServerSession webServerSession = new WebServerSession();
    Session sessionMock = mock(Session.class);

    webServerSession.onOpen(sessionMock);
    webServerSession.onClose(sessionMock);

    Field sessionField = WebServerSession.class.getDeclaredField("session");
    sessionField.setAccessible(true);
    Object internalSession = sessionField.get(webServerSession);

    assertNull(internalSession);
  }
}
