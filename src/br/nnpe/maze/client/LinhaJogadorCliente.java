package br.nnpe.maze.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

import br.nnpe.maze.MazeGame;
import br.nnpe.maze.MazeGameObject;
import br.nnpe.maze.mazegen.MazeDFS;
import br.nnpe.maze.mazegen.MazeGen;
import br.nnpe.maze.mazegen.MazeTO;
import br.nnpe.maze.server.ProtocoloDeMenssagens;
/**
 *
 * @author  Paulo Sobreira
 * Created on 9 de Outubro de 2003, 21:25
 */
public class LinhaJogadorCliente extends LinhaJogador{
    private javax.swing.JPanel painel;
    private MazeGame mainFrame;
    private MazeGameObject mazeGameObject= new MazeGameObject();
    private Map jogadores;
    private String ip;
    private String jogar_Direcao="";
    private Point ptOrigem,ptAtual;
    private int descontoX,descontoY;
    private JLabel mazeLabel = new JLabel();
    
    public LinhaJogadorCliente(java.net.Socket con,javax.swing.JTextArea ta) {
        super(con);
        saidaTxt=ta;
    }
    public void run(){
        try {
           while (!interrupted() && getCon().isConnected()){           
           String cmd=getIn().readUTF();
           if (cmd!=null)
              precessaCmd(cmd);
           }
           getOut().close();
           getIn().close();           
       }
        catch ( Exception e ) { e.printStackTrace(); }
    }
    public void precessaCmd(java.lang.String cmd) {
        //System.out.println("Client :> "+cmd);
    	java.util.StringTokenizer tokens=new java.util.StringTokenizer(
    			cmd,ProtocoloDeMenssagens.SEPARADOR);
        String t1=tokens.nextToken();
        if (t1==null)
            return;
        if (ProtocoloDeMenssagens.MSG.equals(t1))
            adicionarTxt(tokens.nextToken());
        else if(ProtocoloDeMenssagens.ENVIAR_PONTO_ORIGEM.equals(t1)){
        	ptOrigem= (Point) receiveObject();
			ptAtual = new Point(ptOrigem.x,ptOrigem.y);
        }
        else if(ProtocoloDeMenssagens.ENVIAR_LISTA_JOGADORES.equals(t1)){
        	jogadores=(Map) receiveObject();
        	for (Iterator iter = jogadores.keySet().iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				JLabel jogadorLabel = (JLabel) jogadores.get(element);
				BufferedImage srcBufferedImage = new BufferedImage(
										14,14,BufferedImage.TYPE_INT_ARGB);
				Graphics g= srcBufferedImage.getGraphics();
				Color c=new Color(0.0f,0.0f,0.0f,0.0f);
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION ,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.setColor(c);
				g2.fillRect(0,0,14,14);
				g2.setColor(jogadorLabel.getForeground());
				g2.fillRoundRect(0,0,10,10,15,15);
				g2.dispose();
				jogadorLabel.setIcon(new ImageIcon(srcBufferedImage));
				descontoY=20;
				descontoX=5;
				painel.add(jogadorLabel, new AbsoluteConstraints(
										ptOrigem.x-descontoX,
										ptOrigem.y-descontoY,70,45));
			}
        }
        else if(ProtocoloDeMenssagens.ENVIAR_LABIRINTO.equals(t1)){
        	MazeTO to = (MazeTO) receiveObject();
        	StringTokenizer xy;
			try {
				xy = new java.util.StringTokenizer(
						getIn().readUTF(),ProtocoloDeMenssagens.SEPARADOR);
				MazeGen mg= new MazeGen();
				mg.setMaze(new MazeDFS(to),Integer.parseInt(
						xy.nextToken()),Integer.parseInt(xy.nextToken()),40,40);
				ImageIcon img=new ImageIcon(
						mg.getMazeCanvas().getBufferImage());
				BufferedImage srcBufferedImage = 
				new BufferedImage(
						img.getIconWidth(),img.getIconHeight()
						,BufferedImage.TYPE_INT_ARGB);  
				srcBufferedImage.getGraphics().drawImage(
						img.getImage(),0,0,null);
				mazeGameObject.setImage(srcBufferedImage);				
				gerarPainel();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        else if(ProtocoloDeMenssagens.INICIAR_LABIRINTO.equals(t1)){
        	mainFrame.setFimJogo(false);
        	mainFrame.iniciarJogo();
        }
		else if(ProtocoloDeMenssagens.FIM_DO_JOGO.equals(t1)){
			mainFrame.setFimJogo(true);
        	painel.removeAll();
		}
        else if (ProtocoloDeMenssagens.MOVER.equals(t1)){
        	JLabel jogadorLabel = (JLabel) jogadores.get(tokens.nextToken());
        	int x = Integer.parseInt(tokens.nextToken());
        	int y = Integer.parseInt(tokens.nextToken());
        	jogadorLabel.setLocation(x-descontoX,y-descontoY);
        }
        else if (ProtocoloDeMenssagens.BEEP.equals(t1)){
            java.awt.Toolkit tk = getMainFrame().getToolkit();
            tk.beep();
        }
            
    }
    
	public Object receiveObject(){
    	try {
    		ObjectInputStream out=new ObjectInputStream(getIn());
    		return out.readObject();
    	} catch ( Exception ex ) { ex.printStackTrace(); }
    	return null;
    }
    
    public void setNome(java.lang.String nome) {    
        super.setNome(nome);
        try{        
        getOut().writeUTF(ProtocoloDeMenssagens.NOME+
        		ProtocoloDeMenssagens.SEPARADOR+nome);
        }
        catch ( Exception e ) { e.printStackTrace(); }       
    }
    
    public void deconectar(){
    	try{        
    		getOut().writeUTF(ProtocoloDeMenssagens.DESCONECTAR);
    	}
    	catch ( Exception e ) { e.printStackTrace(); }       
    }
    /** Getter for property painel.
     * @return Value of property painel.
     *
     */
    public javax.swing.JPanel getPainel() {
        return painel;
    }
    
    /** Setter for property painel.
     * @param painel New value of property painel.
     *
     */
    public void setPainel(javax.swing.JPanel painel) {
        this.painel = painel;
    }
    
   
    public void gerarPainel() {
    	mazeLabel.setIcon(new ImageIcon(mazeGameObject.getImage()));
        boolean contains = false;
    	for (int i = 0; i < painel.getComponents().length; i++) {
			if (painel.getComponents()[i]==mazeLabel)
				contains=true;
		} 
    	if (!contains)
    		painel.add(mazeLabel, new AbsoluteConstraints(
    				0,0,mazeGameObject.getImage().getWidth()
					,mazeGameObject.getImage().getHeight()));
        mazeLabel.invalidate();
    	painel.invalidate();
        mainFrame.pack();
    }
    public void jogadaCliente(){
        try {
            getOut().writeUTF(ProtocoloDeMenssagens.MOVER+
            				  ProtocoloDeMenssagens.SEPARADOR+
            		          String.valueOf(ptAtual.x)+
                        	  ProtocoloDeMenssagens.SEPARADOR+
		            		  String.valueOf(ptAtual.y));
        } catch ( Exception ex ) { ex.printStackTrace(); }  
    }
    
    /** Getter for property mainFrame.
     * @return Value of property mainFrame.
     *
     */
    public javax.swing.JFrame getMainFrame() {
        return mainFrame;
    }    

    /** Setter for property mainFrame.
     * @param mainFrame New value of property mainFrame.
     *
     */
    public void setMainFrame(MazeGame mainFrame) {
        this.mainFrame = mainFrame;
    }    
    
    /** Getter for property ip.
     * @return Value of property ip.
     *
     */
    public java.lang.String getIp() {
        return ip;
    }
    
    /** Setter for property ip.
     * @param ip New value of property ip.
     *
     */
    public void setIp(java.lang.String ip) {
        this.ip = ip;
    }
    
	/**
	 * @return Returns the mazeGameObject.
	 */
	public MazeGameObject getMazeGameObject() {
		return mazeGameObject;
	}

	/**
	 * @param mazeGameObject The mazeGameObject to set.
	 */
	public void setMazeGameObject(MazeGameObject mazeGameObject) {
		this.mazeGameObject = mazeGameObject;
	}

	/**
	 * @return Returns the ptOrigem.
	 */
	public final Point getPtOrigem() {
		return ptOrigem;
	}

	/**
	 * @return Returns the jogar_Direcao.
	 */
	public final String getJogar_Direcao() {
		return jogar_Direcao;
	}

	/**
	 * @param jogar_Direcao The jogar_Direcao to set.
	 */
	public final void setJogar_Direcao(String jogar_Direcao) {
		this.jogar_Direcao = jogar_Direcao;
	}

	/**
	 * @return Returns the ptAtual.
	 */
	public final Point getPtAtual() {
		return ptAtual;
	}

	/**
	 * @param ptAtual The ptAtual to set.
	 */
	public final void setPtAtual(Point ptAtual) {
		this.ptAtual = ptAtual;
	}

}
