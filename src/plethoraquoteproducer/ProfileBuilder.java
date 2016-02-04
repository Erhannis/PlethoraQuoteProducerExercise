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
    this(null, new Point2D.Double(0, 0));
  }
  
  public ProfileBuilder(Profile profile, Point2D start) {
    if (profile != null) {
      this.profile = profile;
    }
    if (start != null) {
      firstPoint = new Point2D.Double(start.getX(), start.getY());
      curPoint = firstPoint;
    }
  }
  
  public Profile getProfile() {
    return profile.rotate(0);
  }
  
  public void lineInDir(double angle, double dist) {
    lineTo(Arc.getPointAtAngle(curPoint, dist, angle));
  }
  
  private boolean checkNotStarted(Point2D pt) {
    if (curPoint == null) {
      firstPoint = new Point2D.Double(pt.getX(), pt.getY());
      curPoint = firstPoint;
      return true;
    }
    return false;
  }
  
  public void lineTo(Point2D pt) {
    if (checkNotStarted(pt)) return;
    Line2D.Double line = new Line2D.Double(curPoint, pt);
    profile.lines.add(line);
    curPoint = new Point2D.Double(pt.getX(), pt.getY());
  }

  /**
   * It is assumed that ||start - center|| defines the radius.
   * @param center
   * @param start
   * @param end
   * @param ccw 
   */
  public void arc(Point2D center, Point2D end, boolean ccw) {
    if (checkNotStarted(center)) return;
    double sa = Utils.angleFromTo(center, curPoint);
    double ea = Utils.angleFromTo(center, end);
    double rads = Utils.wrapAngle(ea - sa);
    arc(center, curPoint.distance(center), rads, ccw);
  }
  
  public void arc(Point2D center, double radius, double radians, boolean ccw) {
    if (checkNotStarted(center)) return;
    Arc arc = new Arc();
    arc.center = new Point2D.Double(center.getX(), center.getY());
    arc.radius = radius;
    arc.startAngle = Utils.angleFromTo(center, curPoint);
    arc.startPoint = curPoint;
    arc.endAngle = Utils.wrapAngle(arc.startAngle + radians);
    Point2D endPoint = arc.getPointAtAngle(arc.endAngle);
    arc.endPoint = new Point2D.Double(endPoint.getX(), endPoint.getY());
    
    if (ccw) {
      double angle = arc.startAngle;
      Point2D.Double point = arc.startPoint;
      arc.startAngle = arc.endAngle;
      arc.startPoint = arc.endPoint;
      arc.endAngle = angle;
      arc.endPoint = point;
    }
    
    profile.arcs.add(arc);
    curPoint = new Point2D.Double(endPoint.getX(), endPoint.getY());
  }
  
  /**
   * Close the path.  Maybe be careful not to cut across the design, though
   * maybe that's a good thing to test.
   */
  public void lineClose() {
    if (curPoint == null) return;
    if (curPoint.getX() == firstPoint.getX() && curPoint.getY() == firstPoint.getY()) return;
    lineTo(firstPoint);
  }
}
