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
import java.io.FileWriter;
import java.io.IOException;
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
 * Looked up some of the Java API
 * Looked up convenient ways of making a checkbox list element; ended up using a table - http://stackoverflow.com/a/19796/513038
 * @author erhannis
 */
public class PlethoraQuoteProducer {
  
  // I added this late, so it's not used in all the places it could be.
  public static final boolean DEBUGGING = false;

  public static final double PADDING = 0.1; // inches
  public static final double MATERIAL_COST = 0.75; // $/(in^2)
  public static final double MAX_SPEED = 0.5; // in/s
  public static final double TIME_COST = 0.07; // $/s
  
  public static final double SNAP_THRESHOLD = 0.000001; // The distance under which two points are (sometimes) considered equal
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    //DEBUGGING
//    args = new String[]{"ExtrudeCircularArc.json", "ui"};
    
    if (args.length < 1) {
      System.out.println("Usage: java -jar PlethoraQuoteProducer.java FILENAME.JSON [ui]");
      System.out.println("Including `ui` as the second parameter will open the gui.");
      return;
    } else if (args.length >= 2) {
      if ("ui".equals(args[1])) {
        MainScreen ms = new MainScreen(args[0]);
        ms.setVisible(true);
        return;
      }
    }
    String filename = args[0];
    
    try {
      Profile profile = PlethoraQuoteProducer.parseFile(filename);
      double quote = PlethoraQuoteProducer.calcQuote(profile);
      DecimalFormat df = new DecimalFormat("0.00");
      System.out.println(df.format(quote) + " dollars");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(PlethoraQuoteProducer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Generate testing profiles
   * @return 
   */
  public static Profile genTestProfile() {
    Profile profile = new Profile();
    profile.lines.add(new Line2D.Double(-1, -2, 1, -2));
    profile.lines.add(new Line2D.Double(-1, 2, 1, 2));
    profile.lines.add(new Line2D.Double(-2, -1, -2, 1));
    profile.lines.add(new Line2D.Double(2, -1, 2, 1));
    Arc arc = new Arc();
    arc.radius = 1;
    arc.center = new Point2D.Double(2, -2);
    arc.startAngle = Math.PI / 2;
    arc.startPoint = new Point2D.Double(2, -1);
    arc.endAngle = Math.PI;
    arc.endPoint = new Point2D.Double(1, -2);
    profile.arcs.add(arc);
    arc = new Arc();
    arc.radius = 1;
    arc.center = new Point2D.Double(2, 2);
    arc.startAngle = Math.PI;
    arc.startPoint = new Point2D.Double(1, 2);
    arc.endAngle = -Math.PI / 2;
    arc.endPoint = new Point2D.Double(2, 1);
    profile.arcs.add(arc);
    arc = new Arc();
    arc.radius = 1;
    arc.center = new Point2D.Double(-2, 2);
    arc.startAngle = -Math.PI / 2;
    arc.startPoint = new Point2D.Double(-2, 1);
    arc.endAngle = 0;
    arc.endPoint = new Point2D.Double(-1, 2);
    profile.arcs.add(arc);
    arc = new Arc();
    arc.radius = 1;
    arc.center = new Point2D.Double(-2, -2);
    arc.startAngle = 0;
    arc.startPoint = new Point2D.Double(-1, -2);
    arc.endAngle = Math.PI / 2;
    arc.endPoint = new Point2D.Double(-2, -1);
    profile.arcs.add(arc);
    
    return profile;
  }
  
  /**
   * Parses a JSON profile file.
   * @param filename
   * @return Profile parsed
   */
  public static Profile parseFile(String filename) throws FileNotFoundException {
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
        arc.startAngle = Utils.angleFromTo(arc.center, startVertex);
        arc.endAngle = Utils.angleFromTo(arc.center, endVertex);
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
   * Exports a profile as JSON to a file.
   * @param profile
   * @param filename 
   */
  public static void saveProfile(Profile profile, String filename) throws IOException {
    int count = 0; // Not the most random of ids, but hey
    HashMap<Point2D, Integer> vertices = new HashMap<Point2D, Integer>();
    for (Line2D line : profile.lines) {
      if (!vertices.containsKey(line.getP1())) {
        vertices.put(line.getP1(), count++);
      }
      if (!vertices.containsKey(line.getP2())) {
        vertices.put(line.getP2(), count++);
      }
    }
    for (Arc arc : profile.arcs) {
      if (!vertices.containsKey(arc.startPoint)) {
        vertices.put(arc.startPoint, count++);
      }
      if (!vertices.containsKey(arc.endPoint)) {
        vertices.put(arc.endPoint, count++);
      }
    }
    
    HashMap jRoot = new HashMap();
    HashMap jVertices = new HashMap();
    for (Entry<Point2D, Integer> e : vertices.entrySet()) {
      HashMap jVertex = new HashMap();
      HashMap jPos = new HashMap();
      jPos.put("X", e.getKey().getX());
      jPos.put("Y", e.getKey().getY());
      jVertex.put("Position", jPos);
      jVertices.put(e.getValue().toString(), jVertex);
    }
    jRoot.put("Vertices", jVertices);
    HashMap jEdges = new HashMap();
    for (Line2D line : profile.lines) {
      HashMap jEdge = new HashMap();
      jEdge.put("Type", "LineSegment");
      ArrayList jEdgeVertices = new ArrayList();
      jEdgeVertices.add(vertices.get(line.getP1()));
      jEdgeVertices.add(vertices.get(line.getP2()));
      jEdge.put("Vertices", jEdgeVertices);
      jEdges.put((count++) + "", jEdge);
    }
    for (Arc arc : profile.arcs) {
      HashMap jEdge = new HashMap();
      jEdge.put("Type", "CircularArc");
      ArrayList jEdgeVertices = new ArrayList();
      jEdgeVertices.add(vertices.get(arc.startPoint));
      jEdgeVertices.add(vertices.get(arc.endPoint));
      jEdge.put("Vertices", jEdgeVertices);
      jEdge.put("ClockwiseFrom", vertices.get(arc.startPoint));
      HashMap jCenter = new HashMap();
      jCenter.put("X", arc.center.getX());
      jCenter.put("Y", arc.center.getY());
      jEdge.put("Center", jCenter);
      jEdges.put((count++) + "", jEdge);
    }
    jRoot.put("Edges", jEdges);
    
    Gson gson = new Gson();
    FileWriter fw = new FileWriter(filename);
    gson.toJson(jRoot, fw);
    fw.flush();
    fw.close();
  }
  
  /**
   * Takes a profile and calculates a quote for its cost.
   * @param profile
   * @return Quote in dollars.
   */
  public static double calcQuote(Profile profile) {
    // Calculate time cost
    double timeCost = 0;
    for (Line2D.Double line : profile.lines) {
      timeCost += (line.getP1().distance(line.getP2()) / MAX_SPEED) * TIME_COST;
    }
    for (Arc arc : profile.arcs) {
      double dist = arc.arcLength();
      if (arc.radius > 0) {
        double speed = MAX_SPEED * Math.exp(-1 / arc.radius);
        timeCost += (dist / speed) * TIME_COST;
      }
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
    Profile allLines = profile.getAllLines();
    Profile hull = allLines.constructConvexHull(null, null);
    
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
  
  /**
   * Just so we can see it.  It's duplicating a lot of the work from calcQuote, but eh.
   * @param profile
   * @return 
   */
  public static Profile getMinBoundingBox(Profile profile, Profile hull) {
    double minAreaCost = Double.POSITIVE_INFINITY;
    double minAreaAngle = Double.POSITIVE_INFINITY;
    Rectangle2D minAreaBounds = null;
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
        minAreaBounds = bounds;
      }
    }
    Profile boundsProfile = new Profile();
    double l = minAreaBounds.getX() - (PADDING / 2);
    double b = minAreaBounds.getY() - (PADDING / 2);
    double t = b + minAreaBounds.getHeight() + PADDING;
    double r = l + minAreaBounds.getWidth() + PADDING;
    boundsProfile.lines.add(new Line2D.Double(l, b, l, t));
    boundsProfile.lines.add(new Line2D.Double(l, t, r, t));
    boundsProfile.lines.add(new Line2D.Double(r, t, r, b));
    boundsProfile.lines.add(new Line2D.Double(r, b, l, b));
    
    return boundsProfile.rotate(-minAreaAngle);
  }
}