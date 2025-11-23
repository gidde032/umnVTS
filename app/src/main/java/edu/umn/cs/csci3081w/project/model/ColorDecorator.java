package edu.umn.cs.csci3081w.project.model;

import java.io.PrintStream;

public abstract class ColorDecorator extends Vehicle {

  protected final Vehicle v;

  public ColorDecorator(Vehicle v) {
    super(
        v.getId(),
        v.getLine(),
        v.getCapacity(),
        v.getSpeed(),
        v.getPassengerLoader(),
        v.getPassengerUnloader()
    );
    this.v = v;
  }

  @Override
  public void report(PrintStream out) {
    v.report(out);
  }

  @Override
  public int getCurrentCO2Emission() {
    return v.getCurrentCO2Emission();
  }

  public abstract Color getColor();

}
