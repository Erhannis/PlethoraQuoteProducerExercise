/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plethoraquoteproducer;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Builds a profile as a path
 * @author erhannis
 */
public class ProfileBuilder {
  private Profile profile = new Profile();
  private Point2D.Double firstPoint;
  private Point2D.Double curPoint;
  
  public ProfileBuilder() {
    this(new Point2D.Double(0, 0));
  }
  
  public ProfileBuilder(Point2D start) {
    firstPoint = new Point2D.Double(start.getX(), start.getY());
    curPoint = firstPoint;
  }
  
  public Profile getProfile() {
    return profile.rotate(0);
  }
  
  public void lineInDir(double angle, double dist) {
    lineTo(Arc.getPointAtAngle(curPoint, dist, angle));
  }
  
  public void lineTo(Point2D pt) {
    Line2D.Double line = new Line2D.Double(curPoint, pt);
    profile.lines.add(line);
    curPoint = new Point2D.Double(pt.getX(), pt.getY());
  }
  
  public void arc(Point2D center, double radius, double radians) {
    Arc arc = new Arc();
    arc.center = new Point2D.Double(center.getX(), center.getY());
    arc.radius = radius;
    arc.startAngle = Utils.angleFromTo(center, curPoint);
    arc.startPoint = curPoint;
    arc.endAngle = Utils.wrapAngle(arc.startAngle + radians);
    Point2D ep = arc.getPointAtAngle(arc.endAngle);
    arc.endPoint = new Point2D.Double(ep.getX(), ep.getY());
    
    profile.arcs.add(arc);
    
    curPoint = new Point2D.Double(arc.endPoint.getX(), arc.endPoint.getY());
  }
  
  /**
   * Close the path.  Maybe be careful not to cut across the design, though
   * maybe that's a good thing to test.
   */
  public void lineClose() {
    lineTo(firstPoint);
  }
}
