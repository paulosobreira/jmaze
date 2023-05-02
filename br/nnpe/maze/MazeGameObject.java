/*
 * Created on 16/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.nnpe.maze.mazegen.MazeGen;
/**
 * @author sobreira
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MazeGameObject implements Serializable {
	private Raster raster;
	private int[] argbArray; 
	private BufferedImage image;
	
	public MazeGameObject(){
	}
	
	public MazeGameObject(BufferedImage image) {
        this.image = image;
		raster = image.getData();
    }
	public boolean moveAt(int x,int y)
	{
		if (x<0 || y<0 || x>=image.getWidth() || y>=image.getHeight()){
			//System.out.println("Saiu no primero if");
			return false;
		}

		argbArray = new int[4];
		argbArray = raster.getPixel(x,y,argbArray);
		Color c = new Color(argbArray[0],argbArray[1],argbArray[2],argbArray[3]);
		if (c.equals(MazeGen.bkgrndColor) || c.equals(MazeGen.startColor) || c.equals(MazeGen.endColor)){
			//System.out.println("true");
			return true;
			
		}
		return false;
	}

	public boolean endPoint(int x,int y){
		if (x<0 || y<0 || x>=image.getWidth() || y>=image.getHeight()){
			//System.out.println("Saiu no primero if");
			return false;
		}
		argbArray = new int[4];
		argbArray = raster.getPixel(x,y,argbArray);
		Color c = new Color(argbArray[0],argbArray[1],argbArray[2],argbArray[3]);
		if (c.equals(MazeGen.endColor)){
			//System.out.println("true");
			return true;
			
		}
		return false;	
	} 
	
	/**
	 * @return Returns the image.
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * @param image The image to set.
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
		raster = image.getData();
	}
	
	public void rebuild(List argbList,int x,int y){
		image=new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
		WritableRaster wRaster= image.getRaster();
		int[] argbArray;
		int listcont=0;
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				argbArray= (int[])argbList.get(listcont);
				wRaster.setPixel(i,j,argbArray);
				listcont++;
			}
		}
		raster = wRaster;
	}
	
	public List generateList(){
		int[] argbArray;
		List list= new ArrayList();
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				argbArray= new int[4];
				argbArray = raster.getPixel(i,j,argbArray);
				list.add(argbArray);
			}
		}
		return list;
	}	
}
