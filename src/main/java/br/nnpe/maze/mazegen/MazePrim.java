/*
 * Created on 16/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze.mazegen;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Vector;

/******** MAZE PRIM ********
   creates maze using Prim's algorithm
*/   
public final  class MazePrim extends Maze {
   
   // constructor
   MazePrim(Dimension d,MazeCanvas mc,ControlPanel cp,MazeGen mg) {
      super(d,mc,cp,mg) ;
      // run the algorithm, set start/end
      setWalls() ;
      setStartEnd() ;
      // draw the new maze if we haven't yet
      if (!cp.isShowGen()) mc.drawMaze(this) ;
      // or just the start/end squares
      else mc.drawStartEnd(startPt,endPt) ;
   }
   public void setWalls() {
      Vector out=new Vector(totalCells) ;
      Vector frontier=new Vector(totalCells) ;
      Vector in=new Vector(totalCells) ;
      Point cell=null, nCell=new Point(0,0), eCell=new Point(0,0), 
                       sCell=new Point(0,0), wCell=new Point(0,0) ;
      int[] direction=new int[4] ;
      int index=0, candidates=0, choice=0 ;

      // initialize - all cells in Out
      for (int i=0; i<cols; i++)
         for (int j=0; j<rows; j++) out.addElement(new Point(i,j)) ;
      // choose starting cell at random, move to In
      index = randomInt(totalCells) ;
      cell = (Point)out.elementAt(index) ;
      moveCell(out,in,index) ;
      // move starting cell's neighbors to Frontier
      if (cell.x>0) 
         moveCell(out,frontier,out.indexOf(new Point(cell.x-1,cell.y))) ;
      if (cell.y>0) 
         moveCell(out,frontier,out.indexOf(new Point(cell.x,cell.y-1))) ;
      if (cell.x<(cols-1))
         moveCell(out,frontier,out.indexOf(new Point(cell.x+1,cell.y))) ;
      if (cell.y<(rows-1))
         moveCell(out,frontier,out.indexOf(new Point(cell.x,cell.y+1))) ;
         
      // the loop
      while (!frontier.isEmpty()) {
         // move random cell from Frontier to In
         index = randomInt(frontier.size()) ;
         cell = (Point)frontier.elementAt(index) ;
         moveCell(frontier,in,index) ;
         // move cell's neighbors from Out to Frontier                  
         if (cell.x>0) {
            wCell.x = cell.x - 1 ; wCell.y = cell.y ;
            moveCell(out,frontier,out.indexOf(wCell)) ;
         }
         if (cell.y>0) {
            nCell.x = cell.x ; nCell.y = cell.y - 1 ;
            moveCell(out,frontier,out.indexOf(nCell)) ;
         }
         if (cell.x<(cols-1)) {
            eCell.x = cell.x + 1 ; eCell.y = cell.y ;
            moveCell(out,frontier,out.indexOf(eCell)) ;
         }
         if (cell.y<(rows-1)) {
            sCell.x = cell.x ; sCell.y = cell.y + 1 ;
            moveCell(out,frontier,out.indexOf(sCell)) ;
         }
         // find all neighbors of cell in In
         candidates = 0 ;
         if (cell.x>0)
            if (in.indexOf(wCell)>=0) direction[candidates++] = WEST ;
         if (cell.y>0)
            if (in.indexOf(nCell)>=0) direction[candidates++] = NORTH ;
         if (cell.x<(cols-1))
            if (in.indexOf(eCell)>=0) direction[candidates++] = EAST ;
         if (cell.y<(rows-1))
            if (in.indexOf(sCell)>=0) direction[candidates++] = SOUTH ;
         // choose one at random
         choice = direction[randomInt(candidates)] ;
         // erase wall between the cells from maze drawing
         if (cp.isShowGen()) {
            mc.drawGen(cell.x,cell.y,choice,this) ;
            mg.sleep(cp.getGenDelay()) ;
         }         
         switch (choice) {
            // knock down the wall
            case NORTH: m[cell.x][cell.y] &= ~N_WALL ;
                        m[cell.x][cell.y-1] &= ~S_WALL ;
                        break ;
            case EAST:  m[cell.x][cell.y] &= ~E_WALL ;
                        m[cell.x+1][cell.y] &= ~W_WALL ;
                        break ;
            case SOUTH: m[cell.x][cell.y] &= ~S_WALL ;
                        m[cell.x][cell.y+1] &= ~N_WALL ;
                        break ;
            case WEST:  m[cell.x][cell.y] &= ~W_WALL ;
                        m[cell.x-1][cell.y] &= ~E_WALL ;
                        break ;
         }         
      }
   }
   void moveCell(Vector from, Vector to, int index) {
      if (index!=-1) {
         to.addElement((Point)from.elementAt(index)) ;
         from.removeElementAt(index) ;
      }
   }
}

