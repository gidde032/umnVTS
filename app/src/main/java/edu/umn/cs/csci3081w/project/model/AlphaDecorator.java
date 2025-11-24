package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public class AlphaDecorator extends ColorDecorator {

  /**
   * Constructor for a AlphaDecorator.
   *
   * @param v Vehicle
   */
  public AlphaDecorator(Vehicle v) {
    super(v);
  }

  /**
   * Decorates a Vehicle with a new alpha value.
   *
   * @return a new vehicle color
   */
  @Override
  public Color getColor() {
    Color vehicleColor = v.getColor();
    return new Color(
        vehicleColor.getRed(),
        vehicleColor.getGreen(),
        vehicleColor.getBlue(),
        155
    );
  }

}
