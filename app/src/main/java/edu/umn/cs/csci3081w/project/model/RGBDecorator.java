package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public class RGBDecorator extends ColorDecorator{

  private Color rgba;

  /**
   * Constructor for a RGBDecorator
   *
   * @param v Vehicle
   * @param rgba Color to decorate a vehicle with
   */
  public RGBDecorator(Vehicle v, Color rgba) {
    super(v);
    this.rgba = rgba;
  }

  @Override
  public Color getColor() {
    return this.rgba;
  }

}
