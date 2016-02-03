/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plethoraquoteproducer;

import com.google.gson.Gson;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
 * @author erhannis
 */
public class PlethoraQuoteProducer {

  // Stuff to do now, later, or maybe never
  //TODO Improve comments
  
  public static final double PADDING = 0.1; // inches
  public static final double MATERIAL_COST = 0.75; // $/(in^2)
  public static final double MAX_SPEED = 0.5; // in/s
  public static final double TIME_COST = 0.07; // $/s
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (args.length < 1) {
//      System.out.println("Please include profile .json file as first parameter.");
//      return;
      //TODO This is for debugging.  Remove it.
      args = new String[]{"Rectangle.json"};
    }
    String filename = args[0];
    
    PlethoraQuoteProducer pqp = new PlethoraQuoteProducer();
    try {
      Profile profile = pqp.parseFile(filename);
      double quote = pqp.calcQuote(profile);
      //TODO Might not quite be formatted right for dollars.
      System.out.println((Math.round(quote * 100) / 100.0) + " dollars");
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
    
    //TODO Debugging.  Remove.
//    for (Entry<String, Point2D.Double> e : vertexLookup.entrySet()) {
//      System.out.println(e.getKey());
//      System.out.println(e.getValue().x + ", " + e.getValue().y);
//    }
    
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
        String vid = String.valueOf(((Double)edgeVertices.get(0)).intValue()); // It gets parsed as a double
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
        
        profile.arcs.add(arc);
      } else {
        throw new IllegalArgumentException("Illegal edge type " + type);
      }
    }

    //TODO Debugging.  Remove.
//    System.out.println("lines " + profile.lines.size());
//    System.out.println("arcs " + profile.arcs.size());
    
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
      //double dist = arc.;
    }
    
    // Calculate materials cost
    
    return 0;
  }
}