package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public class RGBDecorator extends ColorDecorator{

  private Color rgba;

  /**
   * Constructor for a RGBDecorator
   *
   * @param v Vehicle
   */
  public RGBDecorator(Vehicle v) {
    super(v);
    this.rgba = v.getColor();
    if (v instanceof SmallBus) {
      this.rgba = new Color(122, 0, 25, v.getColor().getAlpha());
    }
    if (v instanceof LargeBus) {
      this.rgba = new Color(239, 130, 238, v.getColor().getAlpha());
    }
    if (v instanceof ElectricTrain) {
      this.rgba = new Color(60, 179, 113, v.getColor().getAlpha());
    }
    if (v instanceof DieselTrain) {
      this.rgba = new Color(255, 204, 51, v.getColor().getAlpha());
    }
  }

  @Override
  public Color getColor() {
    return this.rgba;
  }

}
