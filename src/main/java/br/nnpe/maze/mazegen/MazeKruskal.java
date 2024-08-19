/*
 * Created on 16/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze.mazegen;

import java.awt.Dimension;

/******** MAZE KRUSKAL ********
   creates maze using Kruskal's algorithm
*/   
public final class MazeKruskal extends Maze {
   // 1D array of cells' equivalence classes for union-find
   private int uf[]=new int[totalCells] ;   

   // constructor
   MazeKruskal(Dimension d,MazeCanvas mc,ControlPanel cp,MazeGen mg) {
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
      // for the walls, first two ints are the cell, third is direction
      int walls[][]=new int[(totalCells*2)-cols-rows][3], wallsPtr=0 ;
      int index=0, visited=1, direction=0, root1=0, root2=0 ;   
      int x1=0, y1=0, x2=0, y2=0 ;
   
      for (int i=0; i<cols; i++) {
         for (int j=0; j<rows; j++) {
            // all cells in their own equivalence class
            uf[(j*cols)+i] = -1 ;
            // add all interior walls (only need N and W for each cell)
            if (i!=0) {
               walls[wallsPtr][0] = i ;
               walls[wallsPtr][1] = j ;
               walls[wallsPtr++][2] = WEST ;
            }
            if (j!=0) {
               walls[wallsPtr][0] = i ;
               walls[wallsPtr][1] = j ;
               walls[wallsPtr++][2] = NORTH ;
            }
         }
      }
      while (visited < totalCells) {
         // choose cell wall at random
         index = randomInt(wallsPtr--) ;
         x1 = walls[index][0] ;
         y1 = walls[index][1] ;
         direction = walls[index][2] ;
         // remove wall from array
         if (index!=wallsPtr)
            for (int i=0; i<3; i++) walls[index][i] = walls[wallsPtr][i] ;
         // find cell on other side of the wall
         if (direction==NORTH) { x2 = x1 ; y2 = y1 - 1 ; }
         else { x2 = x1 - 1 ; y2 = y1 ; }
         // find roots, converting to 1D array
         root1 = find(x1+(y1*cols)) ;
         root2 = find(x2+(y2*cols)) ;
         // are the cells in the same class?
         if (root1 != root2) {
            // no - connect the two trees, bump the counter
            union(root1,root2) ;
            visited++ ;
            // and knock down the wall
            if (direction == NORTH) {
               m[x1][y1] &= ~N_WALL ;
               m[x2][y2] &= ~S_WALL ;
            }
            else {
               m[x1][y1] &= ~W_WALL ;
               m[x2][y2] &= ~E_WALL ;
            }
            // erase wall from maze drawing
            if (cp.isShowGen()) {
               mc.drawGen(x1,y1,direction,this) ;
               mg.sleep(cp.getGenDelay()) ;
            }         
         }
      }
   }
   int find(int n) {
      // recursive find with path compression
      if (uf[n]<0) return n ; // at root
      return uf[n]=find(uf[n]) ;
   }
   void union(int r1,int r2) {
      // union-by-size
      if (uf[r1]<=uf[r2]) {
         uf[r1] += uf[r2] ;   // add size of r2 to r1
         uf[r2] = r1 ;         // r2 points to r1
      }
      else {
         uf[r2] += uf[r1] ;
         uf[r1] = r2 ;
      }
   }
}

