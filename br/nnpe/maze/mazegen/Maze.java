/*
 * Created on 16/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze.mazegen;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

/******** MAZE ********
   base class; initializes maze cells
*/   
public abstract class Maze implements Serializable {
   static final int 
      NORTH=0, N_WALL=0x01, N_BORDER=0x10, N_PATH=0x100, CN_PATH=0x1100, 
      EAST =1, E_WALL=0x02, E_BORDER=0x20, E_PATH=0x200, CE_PATH=0x1200, 
      SOUTH=2, S_WALL=0x04, S_BORDER=0x40, S_PATH=0x400, CS_PATH=0x1400, 
      WEST =3, W_WALL=0x08, W_BORDER=0x80, W_PATH=0x800, CW_PATH=0x1800,
               ALL_WALLS=0x0F,             C_PATH=0x1000,
      N_BACK=0x10000, CN_BACK=0x110000,
      E_BACK=0x20000, CE_BACK=0x120000,
      S_BACK=0x40000, CS_BACK=0x140000,
      W_BACK=0x80000, CW_BACK=0x180000,
      C_BACK=0x100000 ;
      
   protected Point startPt, endPt ;
   protected int m[][], stack[], stackPtr, cols, rows, totalCells ;
   MazeCanvas mc ;
   ControlPanel cp ;
   MazeGen mg ;
   
   public MazeTO generateTO(){
   	MazeTO mazeTO = new MazeTO();
   	mazeTO.setM(m);
   	mazeTO.setStack(stack);
   	mazeTO.setStackPtr(totalCells);
   	mazeTO.setCols(cols);
   	mazeTO.setRows(rows);
   	mazeTO.setTotalCells(totalCells);
   	mazeTO.setStartPt(startPt);
   	mazeTO.setEndPt(endPt);
   	return mazeTO;
   }

   public Maze(MazeTO to){
   	m=to.getM();
   	stack=to.getStack();
   	stackPtr=to.getStackPtr();
   	cols=to.getCols();
   	rows=to.getRows();
   	totalCells=to.getTotalCells();
   	startPt=to.getStartPt();
   	endPt=to.getEndPt();
   }
   // constructor
   public Maze(Dimension d,MazeCanvas mc,ControlPanel cp,MazeGen mg) {
      this.mc = mc ; this.cp = cp ; this.mg = mg ;
      cols = d.width ; rows = d.height ;
      totalCells = cols * rows ;
      stack = new int[totalCells] ;
      // initialize maze array - all walls up, set borders
      m = new int[cols][rows] ;
      for (int i=0; i<cols; i++) {
         for (int j=0; j<rows; j++) {
            m[i][j] |= ALL_WALLS ;
            if (j==0) m[i][0] |= N_BORDER ;
            if (i==(cols-1)) m[i][j] |= E_BORDER ;
            if (j==(rows-1)) m[i][j] |= S_BORDER ;
            if (i==0) m[0][j] |= W_BORDER ;
         }
      }
      // draw the grid
      if (cp.isShowGen()) {
         mc.drawGrid(this) ;
         //mg.sleep(500) ;
      }
   }
   void setStartEnd() {
      // start and end points in opposite corners
      switch (randomInt(4)) {
         case 0: startPt = new Point(0,0) ; 
                 endPt = new Point(cols-1,rows-1) ; 
                 break ;
         case 1: startPt = new Point(cols-1,0) ;
                 endPt = new Point(0,rows-1) ; 
                 break ;
         case 2: startPt = new Point(cols-1,rows-1) ;
                 endPt = new Point(0,0) ; 
                 break ;
         case 3: startPt = new Point(0,rows-1) ;
                 endPt = new Point(cols-1,0) ; 
                 break ;
      }
   }
   public void solveMaze() {
      int direction[]=new int[4], candidates=0, choice=0 ;
      int x=startPt.x, y=startPt.y ;

      stackPtr = 0 ;
      m[x][y] |= C_PATH ;
      while ((x!=endPt.x)||(y!=endPt.y)) {
         candidates = 0 ;
         // first check for walls, then see if we've been here before
         if ( ((m[x][y]&N_WALL)==0)&&((m[x][y-1]&C_PATH)==0)&&
            ((m[x][y-1]&C_BACK)==0) ) direction[candidates++] = NORTH ;
         if ( ((m[x][y]&E_WALL)==0)&&((m[x+1][y]&C_PATH)==0)&&
            ((m[x+1][y]&C_BACK)==0) ) direction[candidates++] = EAST ;
         if ( ((m[x][y]&S_WALL)==0)&&((m[x][y+1]&C_PATH)==0)&&
            ((m[x][y+1]&C_BACK)==0) ) direction[candidates++] = SOUTH ;
         if ( ((m[x][y]&W_WALL)==0)&&((m[x-1][y]&C_PATH)==0)&&
            ((m[x-1][y]&C_BACK)==0) ) direction[candidates++] = WEST ;

         if (candidates != 0) {
            // choose a direction at random
            choice = direction[randomInt(candidates)] ;
            stack[stackPtr++] = choice ;
            switch (choice) {
               // set path, change current cell
               case NORTH: m[x][y--] |= N_PATH ;
                           m[x][y] |= CS_PATH ;
                           break ;
               case EAST:  m[x++][y] |= E_PATH ;
                           m[x][y] |= CW_PATH ;
                           break ;
               case SOUTH: m[x][y++] |= S_PATH ;
                           m[x][y] |= CN_PATH ;
                           break ;
               case WEST:  m[x--][y] |= W_PATH ;
                           m[x][y] |= CE_PATH ;
                           break ;
            }         
            // draw path into new cell
            if (cp.isShowSolve()) {
               mc.drawSolve(x,y,choice,mg.pathColor,this) ;
              // mg.sleep(cp.getSolveDelay()) ;
            }         
         }
         else {
            // nowhere to go, back up and draw backtrack
            choice = stack[--stackPtr] ;
            if (cp.isShowSolve()) {
               mc.drawSolve(x,y,choice,
                  cp.isShowBack()?mg.backColor:mg.bkgrndColor,this) ;
              // mg.sleep(cp.getSolveDelay()) ;
            }         
            switch (choice) {
               // switch off path, set backpath, change current cell
               case NORTH: m[x][y] &= ~CS_PATH ;
                           m[x][y] |= CS_BACK ;
                           m[x][++y] &= ~N_PATH ; 
                           m[x][y] |= N_BACK ;
                           break ;
               case EAST:  m[x][y] &= ~CW_PATH ;
                           m[x][y] |= CW_BACK ;
                           m[--x][y] &= ~E_PATH ;
                           m[x][y] |= E_BACK ;
                           break ;
               case SOUTH: m[x][y] &= ~CN_PATH ;
                           m[x][y] |= CN_BACK ;
                           m[x][--y] &= ~S_PATH ;
                           m[x][y] |= S_BACK ;
                           break ;
               case WEST:  m[x][y] &= ~CE_PATH ;
                           m[x][y] |= CE_BACK ;
                           m[++x][y] &= ~W_PATH ;
                           m[x][y] |= W_BACK ;
                           break ;
            }
         }
         // redraw squares if we're near the start
         if ((stackPtr==1)&&cp.isShowSolve()) mc.drawStartEnd(startPt,endPt) ;
      }
      // if we've been updating the screen, just redraw the squares
      // again to make sure they look nice. Otherwise it's time to
      // draw the solution path we just calculated
      if (cp.isShowSolve()) mc.drawStartEnd(startPt,endPt) ;
      else mc.drawPath(cp.isShowBack(),this) ;
      
   }
   int getCols() { return cols ; }
   int getRows() { return rows ; }
   int getCell(int i,int j) { return m[i][j] ; }
   Point getStart() { return startPt ; }
   Point getEnd() { return endPt ; }
   int randomInt(int n) { return (int)(n*Math.random()) ; }

   public abstract void setWalls() ;
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

	/**
	 * @return Returns the mc.
	 */
	public final MazeCanvas getMc() {
		return mc;
	}
	
	/**
	 * @param mc The mc to set.
	 */
	public final void setMc(MazeCanvas mc) {
		this.mc = mc;
	}
	
	/**
	 * @return Returns the mg.
	 */
	public final MazeGen getMg() {
		return mg;
	}
	
	/**
	 * @param mg The mg to set.
	 */
	public final void setMg(MazeGen mg) {
		this.mg = mg;
	}

	/**
	 * @return Returns the cp.
	 */
	public final ControlPanel getCp() {
		return cp;
	}
	
	/**
	 * @param cp The cp to set.
	 */
	public final void setCp(ControlPanel cp) {
		this.cp = cp;
	}

}

