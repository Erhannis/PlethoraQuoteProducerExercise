/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plethoraquoteproducer;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Things I looked up:
 * Parsing JSON in Java - used GSON
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
      pqp.parseFile(filename);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(PlethoraQuoteProducer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  /**
   * Parses a JSON profile file.
   * @param filename 
   */
  public void parseFile(String filename) throws FileNotFoundException {
    Gson gson = new Gson();
    FileReader fr = new FileReader(filename);
    Map m = gson.fromJson(fr, Map.class);
    System.out.println("m.size() " + m.size());
  }
}
