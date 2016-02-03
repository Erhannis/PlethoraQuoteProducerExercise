/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plethoraquoteproducer;

import com.google.gson.Gson;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Things I looked up:
 * Parsing JSON in Java - used GSON
 * Algorithms for finding convex hulls - wikipedia.  Modifying the gift-wrapping algorithm.
 * Looked up the rotation matrix formula on wikipedia.
 * Looked up how to format dollar amounts properly - http://stackoverflow.com/a/13791420/513038
 * @author erhannis
 */
public class PlethoraQuoteProducer {

  public static final double PADDING = 0.1; // inches
  public static final double MATERIAL_COST = 0.75; // $/(in^2)
  public static final double MAX_SPEED = 0.5; // in/s
  public static final double TIME_COST = 0.07; // $/s
  
  public static final double SNAP_THRESHOLD = 0.000001; // The distance under which two points are (sometimes) considered equal
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Please include profile .json file as first parameter.");
      return;
//      // Debugging
//      //args = new String[]{"CutCircularArc.json"};
//      //args = new String[]{"ExtrudeCircularArc.json"};
//      args = new String[]{"Rectangle.json"};
    }
    String filename = args[0];
    
    PlethoraQuoteProducer pqp = new PlethoraQuoteProducer();
    try {
      Profile profile = pqp.parseFile(filename);
      double quote = pqp.calcQuote(profile);
      DecimalFormat df = new DecimalFormat("0.00");
//      // Debugging; test many rotations
//      for (double a = -Math.PI - 1; a < Math.PI + 1; a += 0.01) {
//        Profile p = profile.rotate(a);
//        quote = pqp.calcQuote(p);
//        // Formatting from http://stackoverflow.com/a/13791420/513038
//        if (!"14.10".equals(df.format(quote))) {
//          System.out.println("err " + df.format(quote) + " != 4.06 on angle " + a);
//        }
//      }
      System.out.println(df.format(quote) + " dollars");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(PlethoraQuoteProducer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  /**
   * Parses a JSON profile file.
   * @param filename
   * @return Profile parsed
   */
  public Profile parseFile(String filename) throws FileNotFoundException {
    Gson gson = new Gson();
    FileReader fr = new FileReader(filename);
    Map m = gson.fromJson(fr, Map.class);
    
    HashMap<String, Point2D.Double> vertexLookup = new HashMap<String, Point2D.Double>();
    Map vertices = ((Map)m.get("Vertices"));
    for (Object entry : vertices.entrySet()) {
      Entry e = (Entry)entry;
      String id = (String)e.getKey();
      Map vertex = (Map)e.getValue();
      Map position = (Map)vertex.get("Position");
      double x = (Double)position.get("X");
      double y = (Double)position.get("Y");
      vertexLookup.put(id, new Point2D.Double(x, y));
    }
    
    Profile profile = new Profile();
    
    Map edges = ((Map)m.get("Edges"));
    for (Object entry : edges.entrySet()) {
      // These could probably be done as a couple long one-liners, but, eh
      Entry e = (Entry)entry;
      String id = (String)e.getKey();
      Map edge = (Map)e.getValue();
      String type = (String)edge.get("Type");
      List edgeVertices = (List)edge.get("Vertices"); //TODO Eh...I feel like the JSON objects should be named jThing, to distinguish between them and the actual objects.  Maybe I'll do that later.
      ArrayList<Point2D.Double> edgeVertexPoints = new ArrayList<Point2D.Double>();
      for (Object ev : edgeVertices) {
        String vid = String.valueOf(((Double)ev).intValue()); // It gets parsed as a double
        edgeVertexPoints.add(vertexLookup.get(vid));
      }
      if ("LineSegment".equals(type)) {
        // Assumes that there are exactly 2 vertices listed for this edge segment
        profile.lines.add(new Line2D.Double(edgeVertexPoints.get(0), edgeVertexPoints.get(1)));
      } else if ("CircularArc".equals(type)) {
        Map center = (Map)edge.get("Center");
        double cx = (Double)center.get("X");
        double cy = (Double)center.get("Y");
        String vid = String.valueOf(((Double)edge.get("ClockwiseFrom")).intValue()); // It gets parsed as a double
        Point2D.Double startVertex = vertexLookup.get(vid);
        Point2D.Double endVertex;
        // Assumes that there are exactly 2 vertices listed for this edge segment, and they're both the same distance from the center
        if (edgeVertexPoints.get(0) == startVertex) {
          endVertex = edgeVertexPoints.get(1);
        } else {
          endVertex = edgeVertexPoints.get(0);
        }
        
        Arc arc = new Arc();
        arc.center = new Point2D.Double(cx, cy);
        arc.radius = arc.center.distance(startVertex);
        arc.startAngle = Math.atan2(startVertex.y - arc.center.y, startVertex.x - arc.center.x);
        arc.endAngle = Math.atan2(endVertex.y - arc.center.y, endVertex.x - arc.center.x);
        arc.startPoint = startVertex;
        arc.endPoint = endVertex;
        
        profile.arcs.add(arc);
      } else {
        throw new IllegalArgumentException("Illegal edge type " + type);
      }
    }

    return profile;
  }
  
  /**
   * Takes a profile and calculates a quote for its cost.
   * @param profile
   * @return Quote in dollars.
   */
  public double calcQuote(Profile profile) {
    // Calculate time cost
    double timeCost = 0;
    for (Line2D.Double line : profile.lines) {
      timeCost += (line.getP1().distance(line.getP2()) / MAX_SPEED) * TIME_COST;
    }
    for (Arc arc : profile.arcs) {
      double dist = arc.arcLength();
      // Assumes non-zero radius.
      double speed = MAX_SPEED * Math.exp(-1 / arc.radius);
      timeCost += (dist / speed) * TIME_COST;
    }
    
    // Calculate materials cost
    // I assert the following to be probably true, but have not proven it.  Given more time, I'd likely try to prove it more rigorously.
    // First, I assert that the optimal orientation of the profile will have a
    // flat segment of its convex hull snug up against a side of the rectangular
    // boundary.  Furthermore, it does not matter WHICH side, as it would result
    // in the same rectangle, in any direction, so we shall assume the lower
    // side.  Therefore, we shall find the convex hull of the profile, and for
    // each flat segment of it, rotate the object so that the segment in
    // question lies on the bottom edge, and find the bounding rectangle in that
    // orientation.  We shall pick the orientation with the smallest bounding
    // rectangle as being optimal.  Then we shall increase its width and height
    // each by the padding.  We shall assume that doing so does not change which
    // orientation is optimal, or at least, not account for it.
    
    // To find the convex hull, we shall use a form of the gift-wrapping
    // algorithm, as described on wikipedia, but shall modify it to accommodate
    // the arcs.
    // Given more time, we could use a more efficient algorithm, but this one
    // was noted for its simplicity.
    double matCost = 0;
    Profile hull = profile.constructConvexHull();
    
    //TODO I could/should maybe move this into a function on Profile or something
    double minAreaCost = Double.POSITIVE_INFINITY;
    double minAreaAngle = Double.POSITIVE_INFINITY;
    for (Line2D.Double line : hull.lines) {
      double angle = -Math.atan2(line.getY2() - line.getY1(), line.getX2() - line.getX1());
      Profile rotProfile = profile.rotate(angle);
      // It may be "upside down", but that shouldn't matter.
      Rectangle2D bounds = rotProfile.calcBounds();
      double area = (bounds.getWidth() + PADDING) * (bounds.getHeight() + PADDING);
      double areaCost = area * MATERIAL_COST;
      if (areaCost < minAreaCost) {
        minAreaCost = areaCost;
        minAreaAngle = angle; // Just in case later we want to know.
      }
    }
    matCost += minAreaCost;
    
    return timeCost + matCost;
  }
}