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
import java.util.HashSet;
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
  
  /**
   * Construct a profile of the convex hull of THIS profile.
   * Uses a modified version of the gift-wrapping algorithm.
   * //TODO This code makes me suspicious.  I'd like to write a bunch of verification code.
   * I've included MainScreen so I can call debugging functions on it.
   * @param ms
   * @param bgProfile Profile to show in the background of the states
   * @return 
   */
  public Profile constructConvexHull(MainScreen ms, Profile bgProfile) {
    Profile hull = new Profile();
    
    Point2D fpl = this.findLeftmostPointOfLines();
    Pair<Point2D, Arc> fpa = this.findLeftmostPointOfArcs();

    Pair<Point2D, Arc> firstArc, curArc; // Not necessarily arcs - only arcs if the Arc is not null.
    if (fpa == null || (fpl != null && fpl.getX() < fpa.getKey().getX())) {
      firstArc = new Pair<Point2D, Arc>(fpl, null);
    } else {
      firstArc = new Pair<Point2D, Arc>(fpa.getKey(), fpa.getValue());
    }
    curArc = firstArc;
    
    boolean leftFirstEdgeOnce = false;
    boolean leftFirstEdgeTwice = false;
    Arc lastArc = null;
    Arc lastLastArc = null; // Only non-null if equal to lastArc.  These make sure we don't get stuck on a single arc.
    int count = 0;
    // I feel like these are just kinda patching leaks and missing something bigger...
    HashSet<Point2D> spentTargets = new HashSet<Point2D>();
    while (true) {
      //DEBUGGING
      if (count++ > 30 && PlethoraQuoteProducer.DEBUGGING) {
        break;
      }
if (ms != null) ms.constructAndAddHullState(this, bgProfile, hull, curArc.getKey(), null, null, null, null);
      if (curArc.getValue() == null) {
        // Currently on a single point
        ArrayList<Pair<Point2D, Arc>> candidates = getCandidateWrappingTargets(curArc.getKey(), spentTargets);
if (ms != null) ms.constructAndAddHullState(this, bgProfile, hull, curArc.getKey(), candidates, null, null, null);
        Pair<Point2D, Arc> bestCandidate = null;
        for (Pair<Point2D, Arc> nextCandidate : candidates) {
          if (isPointArcEqual(nextCandidate, curArc) || nextCandidate.getKey().distance(curArc.getKey()) < PlethoraQuoteProducer.SNAP_THRESHOLD) {
            continue;
          }
          if (bestCandidate == null || new Line2D.Double(curArc.getKey(), bestCandidate.getKey()).relativeCCW(nextCandidate.getKey()) == -1) {
            bestCandidate = nextCandidate;
if (ms != null) ms.constructAndAddHullState(this, bgProfile, hull, curArc.getKey(), candidates, null, null, new Line2D.Double(curArc.getKey(), bestCandidate.getKey()));
          }
        }
        if (bestCandidate == null) {
          // Couldn't find a spot to go to - may be done.  Otherwise, just broken.
          break;
        }
        hull.lines.add(new Line2D.Double(curArc.getKey(), bestCandidate.getKey()));
        curArc = bestCandidate;
        spentTargets.add(curArc.getKey());
      } else {
        // Currently on an arc - no longer used (preempted by getAllLines), because it had weird edge cases
        //TODO Add arc parts to hull

//        if (curArc.getKey().distance(curArc.getValue().endPoint) < PlethoraQuoteProducer.SNAP_THRESHOLD) {
//          // We're starting on the end point, which means there's a hole to our right as we go forward
//        } else {
//          
//        }
        
        ArrayList<Pair<Point2D, Arc>> candidates = getCandidateWrappingTargets(curArc.getValue(), spentTargets);
if (ms != null) ms.constructAndAddHullState(this, bgProfile, hull, curArc.getKey(), candidates, null, null, null);
        Pair<Point2D, Arc> startArc = curArc;
        Pair<Point2D, Arc> bestCandidate = null;
        for (Pair<Point2D, Arc> nextCandidate : candidates) {
          // Don't land on the same point you're at, and don't land on the same arc you've done for the past two times.
          if (isPointArcEqual(nextCandidate, curArc) || (nextCandidate.getValue() == curArc.getValue() && lastLastArc == nextCandidate.getValue())) {
            continue;
          }
          // Find starting point corresponding to the target point
          Pair<Point2D, Arc> tempArc = new Pair<Point2D, Arc>(getCandidateWrappingSource(curArc.getValue(), nextCandidate), curArc.getValue());
          
if (ms != null) {ArrayList<Pair<Point2D, Arc>> nc = new ArrayList<Pair<Point2D, Arc>>(); nc.add(nextCandidate); ms.constructAndAddHullState(this, bgProfile, hull, curArc.getKey(), nc, getCandidateWrappingSources(curArc.getValue(), nextCandidate), tempArc.getKey(), null);}

          // Not allowed to cut across an arc from start to finish
          if ((tempArc.getValue().startPoint.distance(tempArc.getKey()) < PlethoraQuoteProducer.SNAP_THRESHOLD
               && tempArc.getValue().endPoint.distance(nextCandidate.getKey()) < PlethoraQuoteProducer.SNAP_THRESHOLD)
            || nextCandidate.getKey().distance(tempArc.getKey()) < PlethoraQuoteProducer.SNAP_THRESHOLD) { // and not allowed to jump to the selfsame point
            continue;
          }

          if (bestCandidate == null || new Line2D.Double(curArc.getKey(), bestCandidate.getKey()).relativeCCW(nextCandidate.getKey()) == -1) {
            bestCandidate = nextCandidate;
            curArc = tempArc;
if (ms != null) ms.constructAndAddHullState(this, bgProfile, hull, curArc.getKey(), candidates, null, null, new Line2D.Double(curArc.getKey(), bestCandidate.getKey()));
          }
        }
        if (startArc.getKey().distance(startArc.getValue().endPoint) < PlethoraQuoteProducer.SNAP_THRESHOLD
         && curArc.getKey().distance(curArc.getValue().startPoint) < PlethoraQuoteProducer.SNAP_THRESHOLD) {
          // We've cut across a hole and are leaving from the other side; put down a line before we go
          //TODO This is related to a bug in self-intersecting profiles.  Two bugs, actually.
          hull.lines.add(new Line2D.Double(startArc.getKey(), curArc.getKey()));
        }
        if (bestCandidate == null) {
          // Couldn't find a spot to go to - may be done.  Otherwise, just broken.
          break;
        }
        // Doing this bit to avoid weirdness with starting on arcs; make sure if we've wrapped around and a little past the first edge, we stop
        // Also threw in a line for if there's just a single arc, which'd be weird.
        if ((curArc.getValue() == firstArc.getValue() && bestCandidate.getValue() != firstArc.getValue()) || (arcs.size() == 1 && lines.size() == 0)) {
          if (leftFirstEdgeOnce) {
            leftFirstEdgeTwice = true;
            break;
          }
          leftFirstEdgeOnce = true;
        }
        hull.lines.add(new Line2D.Double(curArc.getKey(), bestCandidate.getKey()));
        curArc = bestCandidate;
        spentTargets.add(curArc.getKey());
      }
      if (lastArc == curArc.getValue()) {
        lastLastArc = lastArc;
        lastArc = curArc.getValue();
      } else {
        lastLastArc = null;
        lastArc = curArc.getValue();
      }
      if (isPointArcEqual(curArc, firstArc) || leftFirstEdgeTwice) {
        break;
      }
    }

if (ms != null) ms.constructAndAddHullState(this, bgProfile, hull, curArc.getKey(), null, null, null, null);
    
    return hull;
  }
  
  /**
   * Constructs a list of points for the convex hull algorithm, for a given point
   * to connect to.  The trick is turning the arcs into the appropriate points.
   * @param pt
   * @return 
   */
  public ArrayList<Pair<Point2D, Arc>> getCandidateWrappingTargets(Point2D pt, HashSet<Point2D> spentTargets) {
    ArrayList<Pair<Point2D, Arc>> result = new ArrayList<Pair<Point2D, Arc>>();
    for (Arc arc : arcs) {
      // Find the two points on the edges of the circle tangent to which you can draw lines through pt
      double angleDiff = Math.acos(arc.radius / arc.center.distance(pt));
      double anglePtAbs = Utils.angleFromTo(arc.center, pt);
      double angle1 = Utils.wrapAngle(anglePtAbs + angleDiff);
      double angle2 = Utils.wrapAngle(anglePtAbs - angleDiff);
      if (arc.angleInArc(angle1)) {
        Point2D p = arc.getPointAtAngle(angle1);
        if (!spentTargets.contains(p)) {
          result.add(new Pair<Point2D, Arc>(p, arc));
        }
      }
      if (arc.angleInArc(angle2)) {
        Point2D p = arc.getPointAtAngle(angle2);
        if (!spentTargets.contains(p)) {
          result.add(new Pair<Point2D, Arc>(p, arc));
        }
      }
      // Also include start/end points, 'cause they're on the arc, too, and may be important
      if (!spentTargets.contains(arc.startPoint)) {
        result.add(new Pair<Point2D, Arc>(arc.startPoint, arc));
      }
      if (!spentTargets.contains(arc.endPoint)) {
        result.add(new Pair<Point2D, Arc>(arc.endPoint, arc));
      }
    }
    for (Line2D.Double line : lines) {
      Point2D p1 = line.getP1();
      Point2D p2 = line.getP2();
      if (p1 != pt && !spentTargets.contains(p1)) {
        result.add(new Pair<Point2D, Arc>(p1, null));
      }
      if (p2 != pt && !spentTargets.contains(p2)) {
        result.add(new Pair<Point2D, Arc>(p2, null));
      }
    }
    return result;
  }

  /**
   * Constructs a list of points for the convex hull algorithm, for a given arc
   * to connect to.
   * @param arc
   * @return 
   */
  public ArrayList<Pair<Point2D, Arc>> getCandidateWrappingTargets(Arc arc, HashSet<Point2D> spentTargets) {
    ArrayList<Pair<Point2D, Arc>> result = new ArrayList<Pair<Point2D, Arc>>();
    for (Arc arc2 : arcs) {
      if (arc == arc2) {
        if (!spentTargets.contains(arc.startPoint)) {
          result.add(new Pair<Point2D, Arc>(arc.startPoint, arc));
        }
        if (!spentTargets.contains(arc.endPoint)) {
          result.add(new Pair<Point2D, Arc>(arc.endPoint, arc));
        }
      } else {
        // Find the points on the edges of arc2 tangent to which you can draw lines through points on arc

        {
          // Starting from startPoint
          double angleDiff = Math.acos(arc2.radius / arc2.center.distance(arc.startPoint));
          double anglePtAbs = Utils.angleFromTo(arc2.center, arc.startPoint);
          double angle1 = Utils.wrapAngle(anglePtAbs + angleDiff);
          double angle2 = Utils.wrapAngle(anglePtAbs - angleDiff);
          if (arc2.angleInArc(angle1)) {
            Point2D p = arc2.getPointAtAngle(angle1);
            if (!spentTargets.contains(p)) {
              result.add(new Pair<Point2D, Arc>(p, arc2));
            }
          }
          if (arc2.angleInArc(angle2)) {
            Point2D p = arc2.getPointAtAngle(angle2);
            if (!spentTargets.contains(p)) {
              result.add(new Pair<Point2D, Arc>(p, arc2));
            }
          }
        }
        
        {
          // Starting from endPoint
          double angleDiff = Math.acos(arc2.radius / arc2.center.distance(arc.endPoint));
          double anglePtAbs = Utils.angleFromTo(arc2.center, arc.endPoint);
          double angle1 = Utils.wrapAngle(anglePtAbs + angleDiff);
          double angle2 = Utils.wrapAngle(anglePtAbs - angleDiff);
          if (arc2.angleInArc(angle1)) {
            Point2D p = arc2.getPointAtAngle(angle1);
            if (!spentTargets.contains(p)) {
              result.add(new Pair<Point2D, Arc>(p, arc2));
            }
          }
          if (arc2.angleInArc(angle2)) {
            Point2D p = arc2.getPointAtAngle(angle2);
            if (!spentTargets.contains(p)) {
              result.add(new Pair<Point2D, Arc>(p, arc2));
            }
          }
        }

        double angleDiff;
        //TODO I'm not completely sure these angles can't get out of the appropriate range.
        if (arc2.radius > arc.radius) {
          angleDiff = Math.acos(Math.abs(arc.radius - arc2.radius) / arc.center.distance(arc2.center));
        } else if (arc2.radius < arc.radius) {
          angleDiff = (Math.PI / 2.0) + Math.asin(Math.abs(arc.radius - arc2.radius) / arc.center.distance(arc2.center));
        } else {
          angleDiff = Math.PI / 2.0;
        }
        double anglePtAbs = Utils.angleFromTo(arc2.center, arc.center);
        double angle1 = Utils.wrapAngle(anglePtAbs + angleDiff);
        double angle2 = Utils.wrapAngle(anglePtAbs - angleDiff);
        // I could probably check whether the corresponding point on arc is in angle, too, and it'd be more efficient, but this is still accurate.
        if (arc2.angleInArc(angle1)) {
          Point2D p = arc2.getPointAtAngle(angle1);
          if (!spentTargets.contains(p)) {
            result.add(new Pair<Point2D, Arc>(p, arc2));
          }
        }
        if (arc2.angleInArc(angle2)) {
          Point2D p = arc2.getPointAtAngle(angle2);
          if (!spentTargets.contains(p)) {
            result.add(new Pair<Point2D, Arc>(p, arc2));
          }
        }
        // Also include start/end points, 'cause they're on the arc, too, and may be important
        if (!spentTargets.contains(arc2.startPoint)) {
          result.add(new Pair<Point2D, Arc>(arc2.startPoint, arc2));
        }
        if (!spentTargets.contains(arc2.endPoint)) {
          result.add(new Pair<Point2D, Arc>(arc2.endPoint, arc2));
        }
      }
    }
    for (Line2D.Double line : lines) {
      Point2D p1 = line.getP1();
      Point2D p2 = line.getP2();
      if (!spentTargets.contains(p1)) {
        result.add(new Pair<Point2D, Arc>(p1, null));
      }
      if (!spentTargets.contains(p2)) {
        result.add(new Pair<Point2D, Arc>(p2, null));
      }
    }
    return result;
  }

  /**
   * Factored out mainly for debugging purposes
   * @param source
   * @param target
   * @return 
   */
  public ArrayList<Point2D> getCandidateWrappingSources(Arc source, Pair<Point2D, Arc> target) {
    ArrayList<Point2D> points = new ArrayList<Point2D>();
    // Find the points on the edges of the source arc tangent to which you can draw lines through the target
    double angleDiff = Math.acos(source.radius / source.center.distance(target.getKey()));
    double anglePtAbs = Utils.angleFromTo(source.center, target.getKey());
    double angle1 = Utils.wrapAngle(anglePtAbs + angleDiff);
    double angle2 = Utils.wrapAngle(anglePtAbs - angleDiff);
    if (source.angleInArc(angle1)) {
      points.add(source.getPointAtAngle(angle1));
    }
    if (source.angleInArc(angle2)) {
      points.add(source.getPointAtAngle(angle2));
    }
    // Also include start/end points, 'cause they're on the arc, too, and may be important
    points.add(source.startPoint);
    points.add(source.endPoint);
    
    return points;
  }
  
  public Point2D getCandidateWrappingSource(Arc source, Pair<Point2D, Arc> target) {
    ArrayList<Point2D> points = getCandidateWrappingSources(source, target);
    
    Point2D bestPoint = null;
    double bestAngle = Double.POSITIVE_INFINITY;
    // Looking at the line from target to source center, find which source point is most clockwise.
    double anglePtAbs = Utils.angleFromTo(target.getKey(), source.center);
    for (Point2D pt : points) {
      double angleAbs = Utils.angleFromTo(target.getKey(), pt);
      double angleRel = Utils.wrapAngle(angleAbs - anglePtAbs);
      //TODO I don't know what it'd mean if angleRel were less than -PI/2.  Makes me suspicious.
      if (bestPoint == null || angleRel < bestAngle) {
        bestPoint = pt;
        bestAngle = angleRel;
      }
    }
    return bestPoint;
  }
  
  /**
   * The Pair<Point2D, Arc> is a bit fuzzy, because in some cases (the hull algorithm)
   * I use it both for single points and for points on arcs - in the former case,
   * the Arc is null, and in the latter, the "same" points on the arc may not be
   * strictly equal.
   * ...Hmm, you could have different points on the same arc....  I'll make this once I need it.
   * @param a
   * @param b
   * @return 
   */
  public static boolean isPointArcEqual(Pair<Point2D, Arc> a, Pair<Point2D, Arc> b) {
    if (a.getValue() == null) {
      if (b.getValue() != null) {
        return false;
      } else {
        // point and point
        if (a.getKey() == b.getKey() || ((a.getKey().getX() == b.getKey().getX()) && (a.getKey().getY() == b.getKey().getY()))) {
          return true;
        } else {
          return false;
        }
      }
    } else {
      if (b.getValue() == null) {
        return false;
      } else {
        // arc and arc
        if (a.getValue() != b.getValue()) {
          return false;
        }
        //TODO For now, I'm just checking if the coords are equal.  This may not be correct.
        if (a.getKey() == b.getKey() || ((a.getKey().getX() == b.getKey().getX()) && (a.getKey().getY() == b.getKey().getY()))) {
          return true;
        } else {
          return false;
        }
      }
    }
  }
  
  /**
   * The hull algorithm is having trouble wrapping around curves properly, so
   * if we give it a mess of lines (or rather, all possible contact points as
   * lines), it should be able to find the hull properly
   * @return 
   */
  public Profile getAllLines() {
    HashSet<Point2D> vertices = new HashSet<Point2D>();
    for (Line2D line : this.lines) {
      vertices.add(line.getP1());
      vertices.add(line.getP2());
    }
    for (Arc arc : this.arcs) {
      vertices.add(arc.startPoint);
      vertices.add(arc.endPoint);
    }
    
    HashSet<Point2D> dummySpentPoints = new HashSet<Point2D>();
    for (Arc arc : this.arcs) {
       ArrayList<Pair<Point2D, Arc>> targets = getCandidateWrappingTargets(arc, dummySpentPoints);
       for (Pair<Point2D, Arc> pair : targets) {
         vertices.add(pair.getKey());
         ArrayList<Point2D> sources = getCandidateWrappingSources(arc, pair);
         vertices.addAll(sources);
       }
    }
    
    Profile allLines = new Profile();
    for (Point2D v : vertices) {
      allLines.lines.add(new Line2D.Double(v, v));
    }
    
    return allLines;
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
      newArc.radius = arc.radius;
      newArc.center = Utils.rotatePoint(arc.center, angle);
      newArc.startAngle = Utils.wrapAngle(arc.startAngle + angle);
      newArc.endAngle = Utils.wrapAngle(arc.endAngle + angle);
      newArc.startPoint = Utils.rotatePoint(arc.startPoint, angle);
      newArc.endPoint = Utils.rotatePoint(arc.endPoint, angle);
      result.arcs.add(newArc);
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
  
  /**
   * For debugging.  Creates a profile with a bunch of arcs circling the specified points.
   * @param points
   * @return 
   */
  public static Profile getProfileWPoints(ArrayList<Point2D> points, double radius) {
    Profile profile = new Profile();
    for (Point2D pt : points) {
      Arc newArc = new Arc();
      newArc.radius = radius;
      newArc.center = new Point2D.Double(pt.getX(), pt.getY());
      newArc.startAngle = Math.PI;
      newArc.endAngle = 0.01 - Math.PI;
      newArc.startPoint = (Point2D.Double)newArc.getPointAtAngle(newArc.startAngle);
      newArc.endPoint = (Point2D.Double)newArc.getPointAtAngle(newArc.endAngle);
      profile.arcs.add(newArc);
    }
    return profile;
  }
}