/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plethoraquoteproducer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erhannis
 */
public class MainScreen extends javax.swing.JFrame {

  private ImagePanel ip;
  private ArrayList<ArrayList<Pair<Profile, Color>>> states = new ArrayList<ArrayList<Pair<Profile, Color>>>();
  private int selectedState = -1;
  private DefaultTableModel layersModel;
  
  private ProfileBuilder builder;
  
  /**
   * Creates new form MainScreen
   */
  public MainScreen(String filename) {
    initComponents();
    layersModel = (DefaultTableModel)tableLayers.getModel();
    ip = new ImagePanel(layersModel);
    ip.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
      }

      private Point2D startedAt = null;
      
      @Override
      public void mousePressed(MouseEvent e) {
        if (builder != null) {
          Point2D m = ip.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
          startedAt = m;
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (builder != null && startedAt != null) {
          if (e.isShiftDown()) {
            // Close path
            builder.lineClose();
          } else {
            // Add edge
            Point2D m = ip.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
            if (e.isControlDown()) {
              // Make arc
              boolean ccw = e.isAltDown();
              builder.arc(startedAt, m, ccw);
            } else {
              // Revert to line
              builder.lineTo(m);
            }
          }

          addBuilderState();
          startedAt = null;
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    });
    ip.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        Point2D m = ip.ati.transform(new Point2D.Double(e.getX(), e.getY()), null);
        labelX.setText(m.getX() + "");
        labelY.setText(m.getY() + "");
      }
    });
    jSplitPane1.setLeftComponent(ip);
    
    
    try {
      Profile profile = PlethoraQuoteProducer.parseFile(filename);

      //DEBUGGING remove
//      profile = PlethoraQuoteProducer.genTestProfile();
//      profile = profile.rotate(Math.PI);

      Profile hull = profile.constructConvexHull(this);
      
//      ip.addProfile(profile, Color.BLACK);
//      ip.addProfile(hull, Color.CYAN);
      
      double quote = PlethoraQuoteProducer.calcQuote(profile);
      DecimalFormat df = new DecimalFormat("0.00");
      labelQuote.setText(df.format(quote) + " dollars");
//      System.out.println(df.format(quote) + " dollars");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(PlethoraQuoteProducer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void addState(ArrayList<Pair<Profile, Color>> state) {
    states.add(state);
    if (selectedState < 0) {
      setProfileState(0);
    } else {
      labelCurState.setText((selectedState + 1) + "/" + states.size());
    }
  }
  
  private void setProfileState(int index) {
    if (index >= 0 && index < states.size()) {
      selectedState = index;
      ip.setProfiles(states.get(index));
      labelCurState.setText((selectedState + 1) + "/" + states.size());
      updateLayersTable();
    }
  }
  
  private void updateLayersTable() {
    layersModel.setNumRows(0);
    if (selectedState >= 0) {
      ArrayList<Pair<Profile, Color>> state = states.get(selectedState);
      for (int i = 0; i < state.size(); i++) {
        Profile profile = state.get(i).getKey();
        layersModel.addRow(new Object[]{true, i, profile.lines.size(), profile.arcs.size()});
      }
    }
  }
  
  /**
   * For debugging purposes, mostly.
   * @param profile
   * @param hull 
   */
  public void constructAndAddHullState(Profile profile, Profile hull, Point2D curPoint, ArrayList<Pair<Point2D, Arc>> candidates, ArrayList<Point2D> sourceCandidates, Point2D selectedSource, Line2D bestLine) {
    double ptSize = 0.1;
    ArrayList<Pair<Profile, Color>> state = new ArrayList<Pair<Profile, Color>>();
    state.add(new Pair(profile, Color.BLACK));
    state.add(new Pair(hull.rotate(0), Color.CYAN));
    state.add(new Pair(Profile.getProfileWPoints(new ArrayList<Point2D>(Arrays.asList(new Point2D[]{curPoint})), ptSize), Color.ORANGE));
    if (candidates != null) {
      ArrayList<Point2D> points = new ArrayList<Point2D>();
      for (Pair<Point2D, Arc> pair : candidates) {
        points.add(pair.getKey());
      }
      state.add(new Pair(Profile.getProfileWPoints(points, ptSize), Color.MAGENTA.darker()));
    }
    if (sourceCandidates != null) {
      state.add(new Pair(Profile.getProfileWPoints(sourceCandidates, ptSize), Color.RED));
    }
    if (selectedSource != null) {
      state.add(new Pair(Profile.getProfileWPoints(new ArrayList<Point2D>(Arrays.asList(new Point2D[]{selectedSource})), ptSize), Color.GREEN.darker()));
    }
    if (bestLine != null) {
      Profile p = new Profile();
      p.lines.add(new Line2D.Double(bestLine.getP1(), bestLine.getP2()));
      state.add(new Pair(p, Color.PINK));
    }
    addState(state);
    setProfileState(states.size() - 1);
    if (states.size() == 8) {
      states = states;
    }
  }
  
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jSplitPane1 = new javax.swing.JSplitPane();
    jPanel1 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    btnPrev = new javax.swing.JButton();
    btnNext = new javax.swing.JButton();
    labelCurState = new javax.swing.JLabel();
    labelQuote = new javax.swing.JLabel();
    btnBuilder = new javax.swing.JButton();
    btnClear = new javax.swing.JButton();
    jScrollPane2 = new javax.swing.JScrollPane();
    tableLayers = new javax.swing.JTable();
    labelY = new javax.swing.JLabel();
    labelX = new javax.swing.JLabel();
    jMenuBar1 = new javax.swing.JMenuBar();
    jMenu1 = new javax.swing.JMenu();
    jMenuItem2 = new javax.swing.JMenuItem();
    jMenuItem1 = new javax.swing.JMenuItem();
    jMenuItem3 = new javax.swing.JMenuItem();
    jMenu2 = new javax.swing.JMenu();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    jSplitPane1.setDividerLocation(500);
    jSplitPane1.setResizeWeight(1.0);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 500, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 379, Short.MAX_VALUE)
    );

    jSplitPane1.setLeftComponent(jPanel1);

    btnPrev.setText("<");
    btnPrev.setToolTipText("CTRL for first");
    btnPrev.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnPrevActionPerformed(evt);
      }
    });

    btnNext.setText(">");
    btnNext.setToolTipText("CTRL for last");
    btnNext.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnNextActionPerformed(evt);
      }
    });

    labelCurState.setText("0/0");

    labelQuote.setText("0 dollars");

    btnBuilder.setText("Start builder");
    btnBuilder.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnBuilderActionPerformed(evt);
      }
    });

    btnClear.setText("Clear");
    btnClear.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnClearActionPerformed(evt);
      }
    });

    tableLayers.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {

      },
      new String [] {
        "Show", "Layer", "Lines", "Arcs"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.Boolean.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
      };
      boolean[] canEdit = new boolean [] {
        true, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    tableLayers.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        tableLayersPropertyChange(evt);
      }
    });
    tableLayers.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
      public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
        tableLayersVetoableChange(evt);
      }
    });
    jScrollPane2.setViewportView(tableLayers);

    labelY.setText("0");

    labelX.setText("0");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(btnPrev)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNext))
              .addComponent(labelCurState)
              .addComponent(labelQuote))
            .addContainerGap(172, Short.MAX_VALUE))
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(btnBuilder)
                  .addComponent(btnClear))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(labelX, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(labelY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
              .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(0, 0, Short.MAX_VALUE))))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(btnPrev)
          .addComponent(btnNext))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(labelCurState)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(labelQuote)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
        .addGap(29, 29, 29)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(labelX)
          .addComponent(btnClear))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnBuilder)
          .addComponent(labelY))
        .addContainerGap())
    );

    jSplitPane1.setRightComponent(jPanel2);

    jMenu1.setText("File");

    jMenuItem2.setText("New");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem2ActionPerformed(evt);
      }
    });
    jMenu1.add(jMenuItem2);

    jMenuItem1.setText("Parse...");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem1ActionPerformed(evt);
      }
    });
    jMenu1.add(jMenuItem1);

    jMenuItem3.setText("Export...");
    jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem3ActionPerformed(evt);
      }
    });
    jMenu1.add(jMenuItem3);

    jMenuBar1.add(jMenu1);

    jMenu2.setText("Edit");
    jMenuBar1.add(jMenu2);

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSplitPane1)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jSplitPane1)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private JFileChooser jChooser = new JFileChooser(new File("./"));
  
  private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    if (jChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      String filename = jChooser.getSelectedFile().getAbsolutePath();

      clear();
      
      try {
        Profile profile = PlethoraQuoteProducer.parseFile(filename);

//        profile = PlethoraQuoteProducer.genTestProfile();
//        profile = profile.rotate(Math.PI);

        Profile hull = profile.constructConvexHull(this);

//        ip.addProfile(profile, Color.BLACK);
//        ip.addProfile(hull, Color.CYAN);

        double quote = PlethoraQuoteProducer.calcQuote(profile);
        DecimalFormat df = new DecimalFormat("0.00");
        labelQuote.setText(df.format(quote) + " dollars");
      } catch (FileNotFoundException ex) {
        Logger.getLogger(PlethoraQuoteProducer.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }//GEN-LAST:event_jMenuItem1ActionPerformed

  private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
    if ((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
      setProfileState(0);
    } else {
      if (selectedState > 0) {
        setProfileState(selectedState - 1);
      }
    }
  }//GEN-LAST:event_btnPrevActionPerformed

  private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
    if ((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
      setProfileState(states.size() - 1);
    } else {
      if (selectedState < states.size() - 1) {
        setProfileState(selectedState + 1);
      }
    }
  }//GEN-LAST:event_btnNextActionPerformed

  private void clear() {
    selectedState = -1;
    labelQuote.setText("? dollars");
    labelCurState.setText("0/0");
    states.clear();
    ip.clearProfiles();
    updateLayersTable();
  }
  
  private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
    if (builder != null) {
      builder = null;
      clear();
      btnBuilderActionPerformed(evt);
    } else {
      clear();
    }
  }//GEN-LAST:event_btnClearActionPerformed

  private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    clear();
  }//GEN-LAST:event_jMenuItem2ActionPerformed

  private void addBuilderState() {
    if (builder != null) {
      ArrayList<Pair<Profile, Color>> state = new ArrayList<Pair<Profile, Color>>();
      state.add(new Pair<Profile, Color>(builder.getProfile(), Color.BLACK));
      addState(state);
      setProfileState(states.size() - 1);
    }
  }
  
  private void btnBuilderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuilderActionPerformed
    if (builder == null) {
      // Start builder
      Profile startProfile = null;
      if (selectedState >= 0 && selectedState < states.size() && states.get(selectedState).size() > 0) {
        startProfile = states.get(selectedState).get(0).getKey();
      }
      clear();
      builder = new ProfileBuilder(startProfile, null);
      addBuilderState();
      
      btnBuilder.setText("Close");
    } else {
      // Close and finalize
      clear();
      
      builder.lineClose();
      //addBuilderState();
      
      Profile profile = builder.getProfile();
      if (profile.lines.size() > 0 || profile.arcs.size() > 0) {
        Profile hull = profile.constructConvexHull(this);
        
        double quote = PlethoraQuoteProducer.calcQuote(profile);
        DecimalFormat df = new DecimalFormat("0.00");
        labelQuote.setText(df.format(quote) + " dollars");
      } else {
        labelQuote.setText("? dollars");
      }
      
      builder = null;
      btnBuilder.setText("Start builder");
    }
  }//GEN-LAST:event_btnBuilderActionPerformed

  private void tableLayersPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tableLayersPropertyChange
    if (ip != null) {
      ip.repaint();
    }
  }//GEN-LAST:event_tableLayersPropertyChange

  private void tableLayersVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_tableLayersVetoableChange
    
  }//GEN-LAST:event_tableLayersVetoableChange

  private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
    if (selectedState >= 0 && selectedState < states.size() && states.get(selectedState).size() > 0) {
      if (jChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
          PlethoraQuoteProducer.saveProfile(states.get(selectedState).get(0).getKey(), jChooser.getSelectedFile().getAbsolutePath());
        } catch (IOException ex) {
          Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
  }//GEN-LAST:event_jMenuItem3ActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
        //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new MainScreen(null).setVisible(true);
      }
    });
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnBuilder;
  private javax.swing.JButton btnClear;
  private javax.swing.JButton btnNext;
  private javax.swing.JButton btnPrev;
  private javax.swing.JMenu jMenu1;
  private javax.swing.JMenu jMenu2;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JMenuItem jMenuItem1;
  private javax.swing.JMenuItem jMenuItem2;
  private javax.swing.JMenuItem jMenuItem3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JSplitPane jSplitPane1;
  private javax.swing.JLabel labelCurState;
  private javax.swing.JLabel labelQuote;
  private javax.swing.JLabel labelX;
  private javax.swing.JLabel labelY;
  private javax.swing.JTable tableLayers;
  // End of variables declaration//GEN-END:variables
}
