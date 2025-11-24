package edu.umn.cs.csci3081w.project.model;

public class Color {
  private final int red;
  private final int green;
  private final int blue;
  private final int alpha;

  /**
   * Constructor for a Color.
   *
   * @param r red intensity. 0-255
   * @param g green intensity. 0-255
   * @param b blue intensity. 0-255
   * @param alpha transparency. 0-255
   */
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
