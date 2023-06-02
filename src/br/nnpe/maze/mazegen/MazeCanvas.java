/*
 * Created on 16/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze.mazegen;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

/******** MAZE CANVAS ********
   draws the maze using double buffer
*/   
public final class MazeCanvas extends Canvas {
   static final int BORDER=0, MAX_CELL_SIZE=20 ;
   public int width, height, mzWidth, mzHeight, cellSize,
               wOffset, hOffset, rows, cols, pathSize,
               nwPathOffset, sePathOffset ;
   Image bufferImage ;
   Graphics buffer ;
   MazeGen mg ;
   
   MazeCanvas(int mcw,int mch) { 
      width = mcw ; height = mch ;
   }
   private void drawInit(Maze mz) {
      cols = mz.getCols() ; rows = mz.getRows() ;
      cellSize = (int)Math.min(MAX_CELL_SIZE,
                  Math.min((width-BORDER)/cols,(height-BORDER)/rows)) ;
      mzWidth = cellSize * cols ;
      mzHeight = cellSize * rows ;
      wOffset = (int)((width-mzWidth)/2) ;
      hOffset = (int)((height-mzHeight)/2) ;
      pathSize = (int)(cellSize/2)-1 ;
      nwPathOffset = (int)((cellSize-pathSize)/2) ;
      sePathOffset = cellSize - pathSize - nwPathOffset ;

      if (buffer==null) {
         bufferImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB) ;
         buffer = bufferImage.getGraphics() ;
      }
      // draw background
      buffer.setColor(MazeGen.appletColor);
      buffer.fillRect(0,0,width,height) ;
      buffer.setColor(MazeGen.bkgrndColor);
      buffer.fillRect(wOffset,hOffset,mzWidth,mzHeight) ;
      // draw outer maze frame
      buffer.setColor(MazeGen.frameColor);
      buffer.draw3DRect(wOffset-2, hOffset-2, mzWidth+4, mzHeight+4, true) ;
      buffer.drawRect(wOffset-1, hOffset-1, mzWidth+2, mzHeight+2) ;
   }
   // draw full generated maze with start and end points
   // called if Show Gen off
   void drawMaze(Maze mz) {
      drawInit(mz) ;
      buffer.setColor(MazeGen.wallColor);
      for (int i=0; i<cols; i++) {
         for (int j=0; j<rows; j++) {
            // only drawing North and West walls
            if ( (j!=0)&&((mz.getCell(i,j)&0x01)!=0) ) {
               buffer.drawLine((wOffset+(i*cellSize)), 
                               (hOffset+(j*cellSize)),
                               (wOffset+((i+1)*cellSize)),
                               (hOffset+(j*cellSize))) ;
            }
            if ( (i!=0)&&((mz.getCell(i,j)&0x08)!=0) ) {
               buffer.drawLine((wOffset+(i*cellSize)), 
                               (hOffset+(j*cellSize)),
                               (wOffset+(i*cellSize)),
                               (hOffset+((j+1)*cellSize))) ;
            }
         }
      }
      // draw inner frame
      buffer.setColor(MazeGen.frameColor);
      buffer.draw3DRect(wOffset, hOffset, mzWidth, mzHeight, false) ;
      // draw start and end points
      drawStartEnd(mz.getStart(),mz.getEnd()) ;
   }
   // draw grid for generating maze
   // called if Show Gen on
   void drawGrid(Maze mz) {
      drawInit(mz) ;
      buffer.setColor(MazeGen.wallColor);
      for (int i=0; i<cols-1; i++) {
         buffer.drawLine((wOffset+((i+1)*cellSize)),hOffset,
                         (wOffset+((i+1)*cellSize)),
                         (hOffset+(rows*cellSize))) ;
      }
      for (int j=0; j<rows-1; j++) {
         buffer.drawLine(wOffset,(hOffset+((j+1)*cellSize)),
                        (wOffset+(cols*cellSize)),
                        (hOffset+((j+1)*cellSize))) ;
      }
      // draw inner frame
      buffer.setColor(MazeGen.frameColor);
      buffer.draw3DRect(wOffset, hOffset, mzWidth, mzHeight, false) ;
      repaint() ;
   }
   // knock down a single wall in our maze drawing
   // called if Show Gen on
   void drawGen(int x,int y,int direction,Maze mz) {
      buffer.setColor(MazeGen.bkgrndColor);
      switch(direction) {
         case Maze.NORTH: buffer.drawLine(
                        wOffset+((x*cellSize)+1),hOffset+(y*cellSize),
                        wOffset+((x+1)*cellSize)-1,hOffset+(y*cellSize) ) ;
                        break ;
         case Maze.EAST:  buffer.drawLine(
                        wOffset+((x+1)*cellSize),hOffset+(y*cellSize)+1,
                        wOffset+((x+1)*cellSize),hOffset+((y+1)*cellSize)-1 ) ;
                        break ;
         case Maze.SOUTH: buffer.drawLine(
                        wOffset+(x*cellSize)+1,hOffset+((y+1)*cellSize),
                        wOffset+((x+1)*cellSize)-1,hOffset+((y+1)*cellSize) ) ;
                        break ;
         case Maze.WEST:  buffer.drawLine(
                        wOffset+(x*cellSize),hOffset+(y*cellSize)+1,
                        wOffset+(x*cellSize),hOffset+((y+1)*cellSize)-1 ) ;
                        break ;
      }
      repaint() ;
   }
   // draw the colorful start and end squares
   void drawStartEnd(Point startPt,Point endPt) {
      buffer.setColor(MazeGen.startColor) ;
      buffer.fillRect((wOffset+(startPt.x*cellSize)+1),
                      (hOffset+(startPt.y*cellSize)+1),
                      (cellSize-2),(cellSize-2)) ;
      buffer.setColor(MazeGen.endColor) ;
      buffer.fillRect((wOffset+(endPt.x*cellSize)+1),
                      (hOffset+(endPt.y*cellSize)+1),
                      (cellSize-2),(cellSize-2)) ;
      repaint() ;
   }
   // draw full solution path with or without backtracks
   // called if Show Solve off
   void drawPath(boolean showBack,Maze mz) {
      int cell = 0 ;
      
      for (int i=0; i<cols; i++) {
         for (int j=0; j<rows; j++) {
            cell = mz.getCell(i,j) ;
            // check for path first, only Center, N and W needed
            // since we'll draw N and W paths into the next cell
            buffer.setColor(mg.pathColor) ;
            if ((cell&mz.C_PATH)!=0) {
               buffer.fillRect(
                  (wOffset+(i*cellSize)+nwPathOffset),
                  (hOffset+(j*cellSize)+nwPathOffset),
                  pathSize,pathSize) ;
            }
            if ((cell&mz.N_PATH)!=0) {
               buffer.fillRect(
                  (wOffset+(i*cellSize)+nwPathOffset),
                  (hOffset+(j*cellSize)-sePathOffset),
                  pathSize,sePathOffset+nwPathOffset) ;
            }
            if ((cell&mz.W_PATH)!=0) {
               buffer.fillRect(
                  (wOffset+(i*cellSize)-sePathOffset),
                  (hOffset+(j*cellSize)+nwPathOffset),
                  sePathOffset+nwPathOffset,pathSize) ;
            }
            if (showBack) { 
               // same deal for backtracks
               buffer.setColor(mg.backColor) ;
               if ((cell&mz.C_BACK)!=0) {
                  buffer.fillRect(
                     (wOffset+(i*cellSize)+nwPathOffset),
                     (hOffset+(j*cellSize)+nwPathOffset),
                     pathSize,pathSize) ;
               }
               if ((cell&mz.N_BACK)!=0) {
                  buffer.fillRect(
                     (wOffset+(i*cellSize)+nwPathOffset),
                     (hOffset+(j*cellSize)-sePathOffset),
                     pathSize,sePathOffset+nwPathOffset) ;
               }
               if ((cell&mz.W_BACK)!=0) {
                  buffer.fillRect(
                     (wOffset+(i*cellSize)-sePathOffset),
                     (hOffset+(j*cellSize)+nwPathOffset),
                     sePathOffset+nwPathOffset,pathSize) ;
               }
            }
         }
      }
      // redraw start and end points
      drawStartEnd(mz.getStart(),mz.getEnd()) ;
   }
   // draw next cell (or backtrack cell) of solution path
   // called if Show Solve on
   void drawSolve(int x,int y,int direction,Color c,Maze mz) {
      buffer.setColor(c) ;
      // draw center 
      buffer.fillRect((wOffset+(x*cellSize)+nwPathOffset),
                     (hOffset+(y*cellSize)+nwPathOffset),
                     pathSize,pathSize) ;
      switch(direction) {
         case Maze.NORTH: buffer.fillRect(
                        (wOffset+(x*cellSize)+nwPathOffset),
                        (hOffset+(y*cellSize)+nwPathOffset+pathSize),
                        pathSize,sePathOffset+nwPathOffset) ;
                        break ;
         case Maze.EAST:  buffer.fillRect(
                        (wOffset+(x*cellSize)-sePathOffset),
                        (hOffset+(y*cellSize)+nwPathOffset),
                        sePathOffset+nwPathOffset,pathSize) ;
                        break ;
         case Maze.SOUTH: buffer.fillRect(
                        (wOffset+(x*cellSize)+nwPathOffset),
                        (hOffset+(y*cellSize)-sePathOffset),
                        pathSize,sePathOffset+nwPathOffset) ;
                        break ;
         case Maze.WEST:  buffer.fillRect(
                        (wOffset+(x*cellSize)+nwPathOffset+pathSize),
                        (hOffset+(y*cellSize)+nwPathOffset),
                        sePathOffset+nwPathOffset,pathSize) ;
                        break ;
      }
      repaint() ;
   }
   public void paint(Graphics g) { update(g) ; }
   public void update(Graphics g) { 
      if (bufferImage!=null) g.drawImage(bufferImage,0,0,this) ;
   }
/**
 * @return Returns the bufferImage.
 */
public Image getBufferImage() {
	return bufferImage;
}

/**
 * @param bufferImage The bufferImage to set.
 */
public void setBufferImage(Image bufferImage) {
	this.bufferImage = bufferImage;
}

/**
 * @return Returns the buffer.
 */
public Graphics getBuffer() {
	return buffer;
}

/**
 * @return Returns the cellSize.
 */
public final int getCellSize() {
	return cellSize;
}

}


