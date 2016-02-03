/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plethoraquoteproducer;

import java.awt.geom.Point2D;

/**
 * Holds some utility methods.
 * @author erhannis
 */
public class Utils {
  /**
   * Because java's mod is different from mathematic's mod.
   * @param x
   * @param n
   * @return 
   */
  public static double mod(double x, double n) {
    return ((x % n) + n) % n;
  }
  
  /**
   * Returns a copy of the input point, rotated CCW around the origin by `angle` radians.
   * @param pt
   * @param angle
   * @return 
   */
  public static Point2D.Double rotatePoint(Point2D pt, double angle) {
    return new Point2D.Double((Math.cos(angle) * pt.getX()) - (Math.sin(angle) * pt.getY()), (Math.sin(angle) * pt.getX()) + (Math.cos(angle) * pt.getY()));
  }
}
