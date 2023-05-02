/*
 * Created on 16/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze.mazegen;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;

import br.nnpe.maze.mazegen.MazeCanvas;
import br.nnpe.maze.mazegen.MazeGen;

/******** CONTROL PANEL ********
   the UI components
*/   
public final class ControlPanel extends Panel {
   static final int MIN_CELLS=10, MAX_W_CELLS=20, MAX_H_CELLS=20, 
                    MAX_DELAY=500, MIN_DELAY=5 ;
   private int rows, cols, genDelay=0, solveDelay=50 ;
   private Button bGen, bSolve, bCycle ;
   private Button bRowsPlus, bRowsMinus, bColsPlus, bColsMinus ;
   private TextField tfRows, tfCols ;
   private Checkbox cbShowGen, cbShowSolve, cbShowBack ;
   private Choice cAlgorithm ;
   private Scrollbar sbGenSpeed, sbSolveSpeed ;
   private Font monoFont = new Font("Courier", Font.BOLD, 12) ;
   private Font textFont = new Font("Helvetica", Font.PLAIN, 12) ;
   private Font boldFont = new Font("Helvetica", Font.BOLD, 12) ;
   private Panel rowsPanel, colsPanel, rowsBtnPanel, colsBtnPanel ;
   private MazeGen mg ;
   private MazeCanvas mc ;

   private Panel p = new Panel() ;

   ControlPanel(MazeGen mg,MazeCanvas mc,int rows,int cols) {
      this.mg = mg ; this.mc = mc ;
      this.rows =rows; this.cols= cols;
      setLayout(new GridLayout(11,1,0,3)) ;
      setFont(textFont) ;
      // first set up the dimension controls
      rowsBtnPanel = new Panel() ;
      rowsBtnPanel.setForeground(MazeGen.appletColor) ;
      colsBtnPanel = new Panel() ;
      colsBtnPanel.setForeground(MazeGen.appletColor) ;
      rowsBtnPanel.setLayout(new GridLayout(1,2)) ;
      colsBtnPanel.setLayout(new GridLayout(1,2)) ;
      rowsBtnPanel.add(bRowsMinus = new Button("-")) ;
      rowsBtnPanel.add(bRowsPlus = new Button("+")) ;
      rowsBtnPanel.validate() ;
      colsBtnPanel.add(bColsMinus = new Button("-")) ;
      colsBtnPanel.add(bColsPlus = new Button("+")) ;
      colsBtnPanel.validate() ;
      rowsPanel = new Panel() ;
      rowsPanel.setFont(monoFont) ;
      rowsPanel.setLayout(new BorderLayout(2,0)) ;
      rowsPanel.add("West",new Label("H")) ;
      rowsPanel.add("Center",tfRows = new TextField(3)) ;
      rowsPanel.add("East",rowsBtnPanel) ;
      rowsPanel.validate() ;
      colsPanel = new Panel() ;
      colsPanel.setFont(monoFont) ;
      colsPanel.setLayout(new BorderLayout(2,0)) ;
      colsPanel.add("West",new Label("W")) ;
      colsPanel.add("Center",tfCols = new TextField(3)) ;
      colsPanel.add("East",colsBtnPanel) ;
      colsPanel.validate() ;
      tfRows.setEditable(false) ;
      tfCols.setEditable(false) ;
      // then the scrollbars
      sbGenSpeed = new Scrollbar(Scrollbar.HORIZONTAL,
                  (MAX_DELAY-genDelay),0,MIN_DELAY,MAX_DELAY) ;
      sbGenSpeed.setBackground(mg.panelColor) ;
      sbGenSpeed.setPageIncrement((int)(MAX_DELAY/10)) ;
      sbGenSpeed.setLineIncrement((int)(MAX_DELAY/100)) ;
      sbSolveSpeed = new Scrollbar(Scrollbar.HORIZONTAL,
                    (MAX_DELAY-solveDelay),0,MIN_DELAY,MAX_DELAY) ;
      sbSolveSpeed.setBackground(mg.panelColor) ;
      sbSolveSpeed.setPageIncrement((int)(MAX_DELAY/10)) ;
      sbSolveSpeed.setLineIncrement((int)(MAX_DELAY/100)) ;
      // now add everything
      add(bGen = new Button("GENERATE")) ;
      bGen.setFont(boldFont) ;
      add(rowsPanel) ;
      add(colsPanel) ;
      add(cAlgorithm = new Choice()) ;
      cAlgorithm.addItem("DFS");
      cAlgorithm.addItem("Prim");
      cAlgorithm.addItem("Kruskal");
      add(cbShowGen = new Checkbox("Show Gen")) ;
      add(sbGenSpeed) ;
      add(bSolve = new Button("SOLVE")) ;
      bSolve.setFont(boldFont) ;
      add(cbShowBack = new Checkbox("Backtracks")) ;
      add(cbShowSolve = new Checkbox("Show Solve")) ;
      add(sbSolveSpeed) ;
      add(bCycle = new Button("STOP CYCLE")) ;
      bCycle.setFont(boldFont) ;
      validate() ;
      // finally set controls to defaults
      tfRows.setText(Integer.toString(rows)) ;
      tfCols.setText(Integer.toString(cols)) ;
      cbShowGen.setState(true) ;
      cbShowBack.setState(true) ;
      cbShowSolve.setState(true) ;
      bGen.enable(false) ;
      bSolve.enable(false) ;
   }
   boolean isShowGen() { return cbShowGen.getState() ; }
   boolean isShowBack() { return cbShowBack.getState() ; }
   boolean isShowSolve() { return cbShowSolve.getState() ; }
   Dimension getDim() {return new Dimension(cols,rows);}
   int getAlgorithm() { return cAlgorithm.getSelectedIndex() ; }
   int getGenDelay() { return genDelay ; } 
   int getSolveDelay() { return solveDelay ; } 

   void setPlusMinusEnable() { 
      bRowsPlus.enable(rows<MAX_H_CELLS) ;
      bRowsMinus.enable(rows>MIN_CELLS) ;
      bColsPlus.enable(cols<MAX_W_CELLS) ;
      bColsMinus.enable(cols>MIN_CELLS) ;
   }   
   void setGenEnable(boolean b) { bGen.enable(b) ; }
   void setSolveEnable(boolean b) { bSolve.enable(b) ; }
   void setCycleEnable(boolean b) { bCycle.enable(b) ; }
   void setCycleLabel(String s) { bCycle.setLabel(s) ; }
   void setRows(int i) { 
      String s = Integer.toString(i) ;
      if (s.length()==1) s = " " + s ;
      tfRows.setText(s) ;
   } 
   void setCols(int i) { 
      String s = Integer.toString(i) ;
      if (s.length()==1) s = " " + s ;
      tfCols.setText(s) ;
   } 
   public boolean action(Event e,Object o) { 
      if ("GENERATE".equals(o)) { mg.startGen() ; }
      else if ("SOLVE".equals(o)) { mg.startSolve() ; }
      else if ("CYCLE".equals(o)) { mg.startCycle() ; } 
      else if ("STOP CYCLE".equals(o)) { mg.stopCycle() ; }
      else {
         if (e.target==cbShowGen) sbGenSpeed.enable(cbShowGen.getState()) ; 
         else if (e.target==cbShowSolve) sbSolveSpeed.enable(cbShowSolve.getState()) ; 
         if (e.target==bRowsPlus) setRows(++rows) ; 
         else if (e.target==bRowsMinus) setRows(--rows) ;
         else if (e.target==bColsPlus) setCols(++cols) ;
         else if (e.target==bColsMinus) setCols(--cols) ;
         setPlusMinusEnable() ;
      }
      mc.requestFocus() ;
      return true ;
   }
   public boolean handleEvent(Event e) {
      if (e.target==sbGenSpeed) {
         genDelay = MAX_DELAY - sbGenSpeed.getValue() ;
         return true ;
      }
      else if (e.target==sbSolveSpeed) {
         solveDelay = MAX_DELAY - sbSolveSpeed.getValue() ;
         return true ;
      }
      else return super.handleEvent(e) ;
   }
}

