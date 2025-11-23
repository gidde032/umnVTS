package edu.umn.cs.csci3081w.project.model;

public class Color {
  private final int red;
  private final int green;
  private final int blue;
  private final int alpha;

  public Color(int r, int g, int b, int alpha) {
    this.red = r;
    this.green = g;
    this.blue = b;
    this.alpha = alpha;
  }

  public int getRed() {
    return red;
  }

  public int getGreen() {
    return green;
  }

  public int getBlue() {
    return blue;
  }

  public int getAlpha() {
    return alpha;
  }
}
