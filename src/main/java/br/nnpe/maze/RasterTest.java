/*
 * Created on 17/03/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.nnpe.maze;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import br.nnpe.maze.mazegen.MazeGen;
/**
 * @author sobreira
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RasterTest {
	
	public static void main(String[] args) {
		MazeGen mg= new MazeGen(515,530,40,40);
		mg.generate(MazeGen.MazeDFS_ALGORTHIM);
		ImageIcon img=new ImageIcon(mg.getMazeCanvas().getBufferImage());
		BufferedImage srcBufferedImage = new BufferedImage(img.getIconWidth(),img.getIconHeight(),BufferedImage.TYPE_INT_ARGB);  
		BufferedImage bufferedImage = new BufferedImage(img.getIconWidth(),img.getIconHeight(),BufferedImage.TYPE_INT_ARGB);
		srcBufferedImage.getGraphics().drawImage(img.getImage(),0,0,null);
		Raster srcRaster = srcBufferedImage.getData();
		WritableRaster destRaster = bufferedImage.getRaster();
		int[] argbArray= new int[4];
		for (int i = 0; i < img.getIconWidth(); i++) {
			for (int j = 0; j < img.getIconHeight(); j++) {
				argbArray= new int[4];
				argbArray = srcRaster.getPixel(i,j,argbArray);
				Color c = new Color(argbArray[0],argbArray[1],argbArray[2],argbArray[3]);
				if (c.equals(MazeGen.bkgrndColor) || c.equals(MazeGen.startColor) || c.equals(MazeGen.endColor))
					destRaster.setPixel(i,j,argbArray);

			}
		}
		JFrame frame = new JFrame();
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(srcBufferedImage));
		System.out.println(srcBufferedImage.getWidth());
		System.out.println(srcBufferedImage.getHeight());
		//frame.getContentPane().setLayout(new AbsoluteLayout());
//		frame.getContentPane().add(labelt, new AbsoluteConstraints(0,0,70,20));
//		frame.getContentPane().add(label, new AbsoluteConstraints(0,0,100,100));
		
		frame.getContentPane().add(label);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(img.getIconWidth(),img.getIconHeight());
		frame.show();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
		
}
