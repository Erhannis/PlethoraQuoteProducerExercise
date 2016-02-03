/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plethoraquoteproducer;

import java.awt.geom.Point2D;

/**
 *
 * @author erhannis
 */
public class Arc {
  public Point2D.Double center;
  public double radius; // inches
  public double startAngle; // Radians, -pi to pi, counterclockwise starting from the direction of (-1,0).  Blame Math.atan2().
  public double endAngle;  // Clockwise to here
  
  public double arcLength() {
    return mod(startAngle - endAngle, 2 * Math.PI) * radius; // 2pi*r*(arcAngle / 2pi) -> r*arcAngle
  }
  
  /**
   * Because java's mod is different from mathematic's mod.
   * @param x
   * @param n
   * @return 
   */
  public static double mod(double x, double n) {
    return ((x % n) + n) % n;
  }
}
