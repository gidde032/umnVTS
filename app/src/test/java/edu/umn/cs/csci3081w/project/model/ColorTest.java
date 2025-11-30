package edu.umn.cs.csci3081w.project.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColorTest {

  private Color testColor;

  /**
   * Setup deterministic operations before each test run.
   */
  @BeforeEach
  public void setUp() {
    PassengerFactory.DETERMINISTIC = true;
    PassengerFactory.DETERMINISTIC_NAMES_COUNT = 0;
    PassengerFactory.DETERMINISTIC_DESTINATION_COUNT = 0;
    RandomPassengerGenerator.DETERMINISTIC = true;
  }

  /**
   * Test constructor.
   */
  @Test
  public void testConstructor() {
    testColor = new Color(100, 110, 120, 200);
    assertEquals(100, testColor.getRed());
    assertEquals(110, testColor.getGreen());
    assertEquals(120, testColor.getBlue());
    assertEquals(200, testColor.getAlpha());
  }

}
