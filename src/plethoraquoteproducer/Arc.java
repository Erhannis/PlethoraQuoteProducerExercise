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
  public double startAngle; // Radians, -pi to pi, counterclockwise starting from the direction of (-1,0).  Matches Math.atan2().
  public double endAngle;  // Clockwise to here
  public Point2D.Double startPoint; // for convenience
  public Point2D.Double endPoint; // for convenience
  
  public double arcLength() {
    return Utils.mod(startAngle - endAngle, 2 * Math.PI) * radius; // 2pi*r*(arcAngle / 2pi) -> r*arcAngle
  }
  
  public boolean angleInArc(double angle) {
    double arcSize = Utils.mod(startAngle - endAngle, 2 * Math.PI);
    double sizeA = Utils.mod(startAngle - angle, 2 * Math.PI);
    //double sizeB = mod(angle - endAngle, 2 * Math.PI);
    if (sizeA < arcSize) {
      return true;
    } else {
      return false;
    }
  }
  
  public Point2D getPointAtAngle(double angle) {
    return new Point2D.Double(center.getX() + (radius * Math.cos(angle)), center.getY() + (radius * Math.sin(angle)));
  }

  public static Point2D getPointAtAngle(Point2D center, double radius, double angle) {
    return new Point2D.Double(center.getX() + (radius * Math.cos(angle)), center.getY() + (radius * Math.sin(angle)));
  }
}
