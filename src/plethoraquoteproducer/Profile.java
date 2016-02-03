/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plethoraquoteproducer;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javafx.util.Pair;

/**
 *
 * @author erhannis
 */
public class Profile {
  public ArrayList<Line2D.Double> lines;
  public ArrayList<Arc> arcs; // Made my own, rather than java.awt.geom.Arc2D, because theirs stores the information in an inconveniently different manner.
  
  public Profile() {
    lines = new ArrayList<Line2D.Double>();
    arcs = new ArrayList<Arc>();
  }
  
  public Point2D findLeftmostPointOfLines() {
    Point2D leftPoint = null;
    for (Line2D.Double line : lines) {
      if (leftPoint == null || line.getX1() < leftPoint.getX()) {
        leftPoint = line.getP1();
      }
      if (line.getX2() < leftPoint.getX()) {
        leftPoint = line.getP2();
      }
    }
    return leftPoint;
  }
  
  /**
   * 
   * @return The point on an arc which is leftmost of the points on arcs in the profile, and the arc itself.
   */
  public Pair<Point2D, Arc> findLeftmostPointOfArcs() {
    Point2D leftPoint = null;
    Arc leftArc = null;
    for (Arc arc : arcs) {
      Point2D pt = null;
      if (arc.angleInArc(Math.PI)) {
        pt = new Point2D.Double(arc.center.getX() - arc.radius, arc.center.getY());
      } else {
        if (arc.startPoint.getX() <= arc.endPoint.getX()) {
          pt = arc.startPoint;
        } else {
          pt = arc.endPoint;
        }
      }
      if (leftPoint == null || pt.getX() < leftPoint.getX()) {
        leftPoint = pt;
        leftArc = arc;
      }
    }
    if (leftPoint != null) {
      return new Pair<Point2D, Arc>(leftPoint, leftArc);
    } else {
      return null;
    }
  }

  // It's a bit cluttered having multiple versions of these for different directions, but I'm not sure I'll need a totally general one, and I think that one'd be more complicated.
  // Having 4 versions of both is REALLY unpleasant, though.
  public Point2D findBottommostPointOfLines() {
    Point2D bottomPoint = null;
    for (Line2D.Double line : lines) {
      if (bottomPoint == null || line.getY1() < bottomPoint.getY()) {
        bottomPoint = line.getP1();
      }
      if (line.getY2() < bottomPoint.getY()) {
        bottomPoint = line.getP2();
      }
    }
    return bottomPoint;
  }
  
  /**
   * 
   * @return The point on an arc which is bottommost of the points on arcs in the profile, and the arc itself.
   */
  public Pair<Point2D, Arc> findBottommostPointOfArcs() {
    Point2D bottomPoint = null;
    Arc bottomArc = null;
    for (Arc arc : arcs) {
      Point2D pt = null;
      if (arc.angleInArc(-Math.PI / 2.0)) {
        pt = new Point2D.Double(arc.center.getX(), arc.center.getY() - arc.radius);
      } else {
        if (arc.startPoint.getY() <= arc.endPoint.getY()) {
          pt = arc.startPoint;
        } else {
          pt = arc.endPoint;
        }
      }
      if (bottomPoint == null || pt.getY() < bottomPoint.getY()) {
        bottomPoint = pt;
        bottomArc = arc;
      }
    }
    if (bottomPoint != null) {
      return new Pair<Point2D, Arc>(bottomPoint, bottomArc);
    } else {
      return null;
    }
  }

  // Still gross having four*2 versions of this.
  public Point2D findRightmostPointOfLines() {
    Point2D rightPoint = null;
    for (Line2D.Double line : lines) {
      if (rightPoint == null || line.getX1() > rightPoint.getX()) {
        rightPoint = line.getP1();
      }
      if (line.getX2() > rightPoint.getX()) {
        rightPoint = line.getP2();
      }
    }
    return rightPoint;
  }
  
  /**
   * 
   * @return The point on an arc which is bottommost of the points on arcs in the profile, and the arc itself.
   */
  public Pair<Point2D, Arc> findRightmostPointOfArcs() {
    Point2D rightPoint = null;
    Arc rightArc = null;
    for (Arc arc : arcs) {
      Point2D pt = null;
      if (arc.angleInArc(0.0)) {
        pt = new Point2D.Double(arc.center.getX() + arc.radius, arc.center.getY());
      } else {
        if (arc.startPoint.getX() >= arc.endPoint.getX()) {
          pt = arc.startPoint;
        } else {
          pt = arc.endPoint;
        }
      }
      if (rightPoint == null || pt.getX() > rightPoint.getX()) {
        rightPoint = pt;
        rightArc = arc;
      }
    }
    if (rightPoint != null) {
      return new Pair<Point2D, Arc>(rightPoint, rightArc);
    } else {
      return null;
    }
  }

  // Yep.
  public Point2D findTopmostPointOfLines() {
    Point2D topPoint = null;
    for (Line2D.Double line : lines) {
      if (topPoint == null || line.getY1() > topPoint.getY()) {
        topPoint = line.getP1();
      }
      if (line.getY2() > topPoint.getY()) {
        topPoint = line.getP2();
      }
    }
    return topPoint;
  }
  
  /**
   * 
   * @return The point on an arc which is bottommost of the points on arcs in the profile, and the arc itself.
   */
  public Pair<Point2D, Arc> findTopmostPointOfArcs() {
    Point2D topPoint = null;
    Arc topArc = null;
    for (Arc arc : arcs) {
      Point2D pt = null;
      if (arc.angleInArc(Math.PI / 2.0)) {
        pt = new Point2D.Double(arc.center.getX(), arc.center.getY() + arc.radius);
      } else {
        if (arc.startPoint.getY() >= arc.endPoint.getY()) {
          pt = arc.startPoint;
        } else {
          pt = arc.endPoint;
        }
      }
      if (topPoint == null || pt.getY() > topPoint.getY()) {
        topPoint = pt;
        topArc = arc;
      }
    }
    if (topPoint != null) {
      return new Pair<Point2D, Arc>(topPoint, topArc);
    } else {
      return null;
    }
  }
  
  public Profile constructConvexHull() {
    Profile hull = new Profile();
    //TODO Finish
    return null;
  }
  
  /**
   * Returns this profile, rotated CCW around the origin by the number of radians specified.
   * It's CCW to match with Math.atan2().
   * @param angle
   * @return 
   */
  public Profile rotate(double angle) {
    Profile result = new Profile();
    for (Line2D.Double line : lines) {
      Point2D p1r = Utils.rotatePoint(line.getP1(), angle);
      Point2D p2r = Utils.rotatePoint(line.getP2(), angle);
      result.lines.add(new Line2D.Double(p1r, p2r));
    }
    for (Arc arc : arcs) {
      Arc newArc = new Arc();
      newArc.center = Utils.rotatePoint(arc.center, angle);
      newArc.startAngle = Utils.mod(arc.startAngle + Math.PI, 2 * Math.PI) - Math.PI; //TODO Technically results in PI -> -PI, but it should still work, I think.  Might consider fixing it.
      newArc.endAngle = Utils.mod(arc.endAngle + Math.PI, 2 * Math.PI) - Math.PI; //TODO Same
      newArc.startPoint = Utils.rotatePoint(arc.startPoint, angle);
      newArc.endPoint = Utils.rotatePoint(arc.endPoint, angle);
      result.arcs.add(arc);
    }
    return result;
  }
  
  /**
   * Fits a vertical/horizontal (i.e., not angled) rectangle around the profile as it is currently oriented.
   * @return 
   */
  public Rectangle2D calcBounds() {
    Point2D topL = findTopmostPointOfLines();
    Pair<Point2D, Arc> topA = findTopmostPointOfArcs();
    Point2D rightL = findRightmostPointOfLines();
    Pair<Point2D, Arc> rightA = findRightmostPointOfArcs();
    Point2D bottomL = findBottommostPointOfLines();
    Pair<Point2D, Arc> bottomA = findBottommostPointOfArcs();
    Point2D leftL = findLeftmostPointOfLines();
    Pair<Point2D, Arc> leftA = findLeftmostPointOfArcs();
    
    Point2D top, right, bottom, left;
    if (topA == null || (topL != null && topL.getY() > topA.getKey().getY())) {
      top = topL;
    } else {
      top = topA.getKey();
    }
    if (rightA == null || (rightL != null && rightL.getX() > rightA.getKey().getX())) {
      right = rightL;
    } else {
      right = rightA.getKey();
    }
    if (bottomA == null || (bottomL != null && bottomL.getY() < bottomA.getKey().getY())) {
      bottom = bottomL;
    } else {
      bottom = bottomA.getKey();
    }
    if (leftA == null || (leftL != null && leftL.getX() < leftA.getKey().getX())) {
      left = leftL;
    } else {
      left = leftA.getKey();
    }
    return new Rectangle2D.Double(left.getX(), bottom.getY(), right.getX() - left.getX(), top.getY() - bottom.getY());
  }
}