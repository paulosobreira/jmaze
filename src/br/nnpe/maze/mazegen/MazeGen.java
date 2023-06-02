/*
 * Created on 16/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze.mazegen;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class MazeGen implements Runnable {
   public static final Color wallColor=Color.BLACK, 
                      pathColor=Color.yellow,
                      backColor=Color.LIGHT_GRAY, 
                      startColor=Color.green,
                      endColor=Color.red,
                      frameColor=Color.gray,
                      bkgrndColor=Color.LIGHT_GRAY,
                      panelColor=Color.lightGray,
                      appletColor=Color.GRAY ;
   public static final int MazeDFS_ALGORTHIM = 0;
   public static final int MazePrim_ALGORTHIM = 1;
   public static final int MazeKruskal_ALGORTHIM = 2;
   static int mcWidth=800, mcHeight=600 ;
   private boolean isCycle, isGen, isSolve ;
   private Maze mz ;
   private MazeCanvas mc ;
   private ControlPanel cp ;
   private Thread mazeThread ;
   private Point startPt, endPt ;
   public MazeGen(){
   }   
   
   public MazeGen(int Width,int Height,int rows,int cols){
   	mcWidth=Width;
   	mcHeight=Height;
   	mc = new MazeCanvas(mcWidth,mcHeight) ;
   	mc.resize(mcWidth,mcHeight) ;
   	cp = new ControlPanel(this,mc,rows,cols) ;
   }
   
   // start thread
   public void start() {
      if (mazeThread == null) {
         mazeThread = new Thread(this) ;
         mazeThread.start() ;
      }
   }
   // kill thread
   public void stop() {
      if (mazeThread != null) {
         mazeThread.stop() ;
         mazeThread = null ;
      }
   }
   // run thread
   public void run() { 
      if (isGen) { 
         generate() ;
         cp.setGenEnable(true) ;
         cp.setSolveEnable(true) ;
         isGen = false ;
      } else if (isSolve) { 
         mz.solveMaze() ;
         isSolve = false ;
      } else
         while(isCycle) {
            generate() ;
            sleep(2000) ;
            mz.solveMaze() ;
            sleep(3000) ;
         }
      System.gc() ;
   }
   public void generate() {
      switch(cp.getAlgorithm()) {
         case 0: mz = new MazeDFS(cp.getDim(),mc,cp,this) ; break ;
         case 1: mz = new MazePrim(cp.getDim(),mc,cp,this) ; break ;
         case 2: mz = new MazeKruskal(cp.getDim(),mc,cp,this) ; break ;
      } 
   }
   public void generate(int mazeAlgorithm) {
   	switch(mazeAlgorithm) {
   		case 0: mz = new MazeDFS(cp.getDim(),mc,cp,this) ; break ;
   		case 1: mz = new MazePrim(cp.getDim(),mc,cp,this) ; break ;
   		case 2: mz = new MazeKruskal(cp.getDim(),mc,cp,this) ; break ;
   	}
   	startPt=new Point();
   	startPt.x=(mc.wOffset+(mz.startPt.x*mc.cellSize)+5);
   	startPt.y=(mc.hOffset+(mz.startPt.y*mc.cellSize)+5);
   	
   	endPt = new Point();
   	endPt.x=(mc.wOffset+(mz.endPt.x*mc.cellSize)+5);
   	endPt.y=(mc.wOffset+(mz.endPt.y*mc.cellSize)+5);
   }
   // UI event handlers
   void startGen() {
      stop() ;
      isGen = true ;
      cp.setGenEnable(false) ;
      cp.setSolveEnable(false) ;
      start() ;
   }
   void startSolve() {
      stop() ;
      isSolve = true ;
      cp.setSolveEnable(false) ;
      start() ;
   }
   void startCycle() {
      stop() ;
      isCycle = true ;
      cp.setCycleLabel("STOP CYCLE") ;
      cp.setGenEnable(false) ;
      cp.setSolveEnable(false) ;
      start() ;
   }
   void stopCycle() { 
      isCycle = false ;
      cp.setCycleLabel("CYCLE") ;
      cp.setGenEnable(true) ;
      stop() ;
   }
   void sleep(int ms) {
      try { Thread.currentThread().sleep(ms) ; } 
      catch (InterruptedException e) {}
   }
	/**
	 * @return Returns the mz.
	 */
	public Maze getMaze() {
		return mz;
	}
	
	/**
	 * @param mz The mz to set.
	 */
	public void setMaze(Maze mz,int Width,int Height,int rows,int cols) {
		mcWidth=Width;
		mcHeight=Height;
		mc = new MazeCanvas(mcWidth,mcHeight) ;
		mc.resize(mcWidth,mcHeight) ;
		this.mz = mz;
		mc.drawMaze(mz);
//		cp = new ControlPanel(this,mc,rows,cols) ;		
//		mz.setMc(mc);
//		mz.setMg(this);
//		mz.setCp(cp);
	}
	
	/**
	 * @return Returns the mc.
	 */
	public MazeCanvas getMazeCanvas() {
		return mc;
	}
	public static void main(String[] args) {
		final MazeGen mg= new MazeGen(800,600,60,70);
		mg.generate(MazeDFS_ALGORTHIM);
		JFrame frame = new JFrame();
		frame.getContentPane().add(mg.getMazeCanvas());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(950,600);
		frame.setTitle("MazeGen 1.0 - Copyright (c) 2002 Robert Kirkland");
		frame.show();
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				mg.stop();
				System.exit(0);
			}
		});
	}
	/**
	 * @return Returns the startPt.
	 */
	public final Point getStartPt() {
		return startPt;
	}
	
	/**
	 * @return Returns the endPt.
	 */
	public final Point getEndPt() {
		return endPt;
	}
}


