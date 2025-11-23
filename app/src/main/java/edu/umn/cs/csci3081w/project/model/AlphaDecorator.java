package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public class AlphaDecorator extends ColorDecorator {

  public AlphaDecorator(Vehicle v) {
    super(v);
  }

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
