package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public abstract class ColorDecorator extends Vehicle {

  protected final Vehicle vehicle;

  /**
   * ColorDecorator's constructor.
   *
   * @param v Vehicle
   */
  public ColorDecorator(Vehicle v) {
    super(
        v.getId(),
        v.getLine(),
        v.getCapacity(),
        v.getSpeed(),
        v.getPassengerLoader(),
        v.getPassengerUnloader()
    );
    this.vehicle = v;
  }

  /**
   * Report statistics for the inner vehicle.
   *
   * @param out stream for printing
   */
  @Override
  public void report(PrintStream out) {
    vehicle.report(out);
  }

  @Override
  public int getCurrentCO2Emission() {
    return vehicle.getCurrentCO2Emission();
  }

  public abstract Color getColor();

}
