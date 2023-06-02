/*
 * Created on 16/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze.mazegen;

import java.awt.Dimension;
import java.io.Serializable;

/******** MAZE DFS ********
   creates maze using Depth-First Search algorithm
*/   
public final class MazeDFS extends Maze implements Serializable {
   
   // constructor
   MazeDFS(Dimension d,MazeCanvas mc,ControlPanel cp,MazeGen mg) {
      super(d,mc,cp,mg) ;
      // run the algorithm, set start/end
      setWalls() ;
      setStartEnd() ;
      // draw the new maze if we haven't yet
      if (!cp.isShowGen()) mc.drawMaze(this) ;
      // or just the start/end squares
      else mc.drawStartEnd(startPt,endPt) ;
   }
   /**
 * @param to
 */
public MazeDFS(MazeTO to) {
	super(to);
	// TODO Auto-generated constructor stub
}
public void setWalls() {
      int direction[]=new int[4], visited=0, candidates=0, choice=0 ;
      // random starting point
      int x=randomInt(cols), y=randomInt(rows) ;

      stackPtr = 0 ;
      visited = 1 ;
      while(visited < totalCells) {
         candidates = 0;
         // find all possible directions for next cell
         // first look for a border, then see if all walls intact
         if (((m[x][y]&N_BORDER)==0)&&((m[x][y-1]&ALL_WALLS)==ALL_WALLS))
            direction[candidates++] = NORTH ;
         if (((m[x][y]&E_BORDER)==0)&&((m[x+1][y]&ALL_WALLS)==ALL_WALLS))
            direction[candidates++] = EAST ;
         if (((m[x][y]&S_BORDER)==0)&&((m[x][y+1]&ALL_WALLS)==ALL_WALLS))
            direction[candidates++] = SOUTH ;
         if (((m[x][y]&W_BORDER)==0)&&((m[x-1][y]&ALL_WALLS)==ALL_WALLS))
            direction[candidates++] = WEST ;
      
         if (candidates != 0) {
            // save current cell, choose a direction at random
            choice = direction[randomInt(candidates)] ;
            stack[stackPtr++] = choice ;
            // erase wall in selected direction from maze drawing
            if (cp.isShowGen()) {
               mc.drawGen(x,y,choice,this) ;
               mg.sleep(cp.getGenDelay()) ;
            }         
            switch (choice) {
               // knock down walls and make new cell the current cell
               case NORTH: m[x][y] &= ~N_WALL ;
                           m[x][--y] &= ~S_WALL ;
                           break ;
               case EAST:  m[x][y] &= ~E_WALL ;
                           m[++x][y] &= ~W_WALL ;
                           break ;
               case SOUTH: m[x][y] &= ~S_WALL ;
                           m[x][++y] &= ~N_WALL ;
                           break ;
               case WEST:  m[x][y] &= ~W_WALL ;
                           m[--x][y] &= ~E_WALL ;
                           break ;
            }         
            visited++ ;
         }
         else {
            // nowhere to go, back up to previous cell
            switch (stack[--stackPtr]) {
               // change current cell
               case NORTH: y++ ; break ;
               case EAST:  x-- ; break ;
               case SOUTH: y-- ; break ;
               case WEST:  x++ ; break ;
            }         
         }
      }               
   }
}


