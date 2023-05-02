/*
 * Created on 22/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze.mazegen;

import java.awt.Point;
import java.io.Serializable;

/**
 * @author sobreira
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MazeTO implements Serializable{
	private Point startPt, endPt ;
	private int m[][], stack[], stackPtr, cols, rows, totalCells ;

	/**
	 * @return Returns the cols.
	 */
	public final int getCols() {
		return cols;
	}

	/**
	 * @param cols The cols to set.
	 */
	public final void setCols(int cols) {
		this.cols = cols;
	}

	/**
	 * @return Returns the endPt.
	 */
	public final Point getEndPt() {
		return endPt;
	}

	/**
	 * @param endPt The endPt to set.
	 */
	public final void setEndPt(Point endPt) {
		this.endPt = endPt;
	}

	/**
	 * @return Returns the m.
	 */
	public final int[][] getM() {
		return m;
	}

	/**
	 * @param m The m to set.
	 */
	public final void setM(int[][] m) {
		this.m = m;
	}

	/**
	 * @return Returns the rows.
	 */
	public final int getRows() {
		return rows;
	}

	/**
	 * @param rows The rows to set.
	 */
	public final void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * @return Returns the stack.
	 */
	public final int[] getStack() {
		return stack;
	}

	/**
	 * @param stack The stack to set.
	 */
	public final void setStack(int[] stack) {
		this.stack = stack;
	}

	/**
	 * @return Returns the stackPtr.
	 */
	public final int getStackPtr() {
		return stackPtr;
	}

	/**
	 * @param stackPtr The stackPtr to set.
	 */
	public final void setStackPtr(int stackPtr) {
		this.stackPtr = stackPtr;
	}

	/**
	 * @return Returns the startPt.
	 */
	public final Point getStartPt() {
		return startPt;
	}

	/**
	 * @param startPt The startPt to set.
	 */
	public final void setStartPt(Point startPt) {
		this.startPt = startPt;
	}

	/**
	 * @return Returns the totalCells.
	 */
	public final int getTotalCells() {
		return totalCells;
	}

	/**
	 * @param totalCells The totalCells to set.
	 */
	public final void setTotalCells(int totalCells) {
		this.totalCells = totalCells;
	}

}
