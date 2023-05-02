package br.nnpe.maze.server;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import br.nnpe.maze.MazeGameObject;
import br.nnpe.maze.mazegen.MazeGen;

/**
 *
 * @author  Paulo Sobreira
 * Created on 9 de Outubro de 2003, 20:39
 */
public class LinhaServidor extends Thread {
    
    private javax.swing.JTextArea saidaTxt;
    private java.net.ServerSocket srv;
    private  static MazeGameObject mazeGameObject;
    private static MazeGen mg;
    private static java.util.Hashtable listaJog;
    private static Map mapaJogadores;
    private boolean jogoIniciado;
    private boolean fimdoJogo;
    private boolean ativo;
    private static int mazeALGORTHIM;
    
    /** Creates a new instance of LinhaServidor */
    public LinhaServidor(int porta,javax.swing.JTextArea ta) {
        saidaTxt=ta;
        ativo=true;
        listaJog=new java.util.Hashtable();
          try {
             srv = new java.net.ServerSocket( porta );
             saidaTxt.append("Ok eu sou "+InetAddress.getLocalHost() +
             				 " ouvindo na porta "+srv.getLocalPort()+"\n");
          }
          catch( Exception e ) {
              e.printStackTrace();
          }
    }

    public void run(){ 
       try {
           sleep(20);
           while (ativo && !jogoIniciado && !interrupted()){
               LinhaJogadorSrv jogador=new LinhaJogadorSrv(srv.accept());
               jogador.setIdent(String.valueOf(listaJog.size()));
               listaJog.put(jogador.getIdent(),jogador);
               jogador.start();
               //System.out.println(listaJog);
           }
       }
        catch ( Exception e ) { e.printStackTrace(); }
 
   }
    
    /** Getter for property ativo.
     * @return Value of property ativo.
     *
     */
    public boolean isAtivo() {
        return ativo;
    }
    
    /** Setter for property ativo.
     * @param ativo New value of property ativo.
     *
     */
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    /** Getter for property fimdoJogo.
     * @return Value of property fimdoJogo.
     *
     */
    public boolean isFimdoJogo() {
        return fimdoJogo;
    }
    
    /** Setter for property fimdoJogo.
     * @param fimdoJogo New value of property fimdoJogo.
     *
     */
    public void setFimdoJogo(boolean fimdoJogo) {
        this.fimdoJogo = fimdoJogo;
    }
    
    /** Getter for property jogoIniciado.
     * @return Value of property jogoIniciado.
     *
     */
    public boolean isJogoIniciado() {
        return jogoIniciado;
    }
    
    /** Setter for property jogoIniciado.
     * @param jogoIniciado New value of property jogoIniciado.
     *
     */
    public void setJogoIniciado(boolean jogoIniciado) {
        try{
            if (jogoIniciado==true){
            	enviaCmdGeral(ProtocoloDeMenssagens.FIM_DO_JOGO);
            	gerarLabirinto();
                enviarPtOrigemGeral();
                enviaListaJogadoresGeral();
                enviaLabirintoGeral();                
                Thread.sleep(500);
                enviaCmdGeral(ProtocoloDeMenssagens.INICIAR_LABIRINTO);
                enviaMsgGeral("Jogo Iniciado");
            }
       }
        catch ( Exception e ) { e.printStackTrace(); }            
        this.jogoIniciado = jogoIniciado;
    }
    public synchronized static void mostraPtsGeral() {
        java.util.Enumeration e = listaJog.elements();
        while (e.hasMoreElements()){
            LinhaJogadorSrv jog=(LinhaJogadorSrv)e.nextElement();
            enviaMsgGeral(jog.getNome()+" "+jog.getPontos()+"pts");
        }
    }
    public synchronized static void enviaMsgGeral(java.lang.String msg) {
        java.util.Enumeration e = listaJog.elements();
        while (e.hasMoreElements()){
            LinhaJogadorSrv jog=(LinhaJogadorSrv)e.nextElement();
            jog.enviaMsg(msg);
        }
    }

    public synchronized static void enviaListaJogadoresGeral() {
    	java.util.Enumeration e = listaJog.elements();
    	gerarMapaJogadores();
    	try {
	    	while (e.hasMoreElements()){
	    		LinhaJogadorSrv jog=(LinhaJogadorSrv)e.nextElement();
				jog.getOut().writeUTF(
						ProtocoloDeMenssagens.ENVIAR_LISTA_JOGADORES);
				sendObject(mapaJogadores,jog.getOut());
		  	}
	    } catch (IOException e1) {
	    	e1.printStackTrace();
	    }
    }    
    public static void enviarPtOrigemGeral(){
    	java.util.Enumeration e = listaJog.elements();
    	try {
    		while (e.hasMoreElements()){
    			LinhaJogadorSrv jog=(LinhaJogadorSrv)e.nextElement();
    			jog.getOut().writeUTF(
    					ProtocoloDeMenssagens.ENVIAR_PONTO_ORIGEM);
    			sendObject(mg.getStartPt(),jog.getOut());
    		}
    	} catch (IOException e1) {
    		e1.printStackTrace();
    	}
    }        
    public static void gerarMapaJogadores(){
    	mapaJogadores = new HashMap();
    	java.util.Enumeration e = listaJog.elements();
    	while (e.hasMoreElements()){
    		LinhaJogadorSrv jog=(LinhaJogadorSrv)e.nextElement();
    		JLabel label=new JLabel(jog.getNome());
//    		label.setToolTipText(
//    					"/imgs/smaley"+(1+(int)(Math.random()*5))+".gif");
    		Color c=new Color((float)Math.random(),
    					(float)Math.random(),(float)Math.random(),1.0f);
    		label.setForeground(c);
    		mapaJogadores.put(jog.getNome(),label);
    	}    	
    }
    public synchronized static void enviaLabirintoGeral() {
    	java.util.Enumeration e = listaJog.elements();
    	enviaMsgGeral("Aguarde enquanto o labirinto é carregado");
    	try {
    		while (e.hasMoreElements()){
    			LinhaJogadorSrv jog=(LinhaJogadorSrv)e.nextElement();
    			jog.getOut().writeUTF(ProtocoloDeMenssagens.ENVIAR_LABIRINTO);
    			sendObject(mg.getMaze().generateTO(),jog.getOut());
    			jog.getOut().writeUTF(mazeGameObject.getImage().getWidth()
    								  +ProtocoloDeMenssagens.SEPARADOR+
    								  mazeGameObject.getImage().getHeight());	
    		}
    	} catch (IOException e1) {
    		e1.printStackTrace();
    	}
    }    
    public synchronized static void  sendObject(Object o,OutputStream os){
    	try {
	    	ObjectOutputStream out=new ObjectOutputStream(os);
	    	out.writeObject(o);
	    	out.flush();
    	} catch ( Exception ex ) { ex.printStackTrace(); }
    }
    public synchronized static void enviaCmdGeral(java.lang.String msg) {
        java.util.Enumeration e = listaJog.elements();
        try {
            while (e.hasMoreElements()){
                LinhaJogadorSrv jog=(LinhaJogadorSrv)e.nextElement();
                jog.getOut().writeUTF(msg);
            }
        } catch ( Exception ex ) { ex.printStackTrace(); }              
    }
    
    public void gerarLabirinto() {
    	mg= new MazeGen(550,550,45,45);
    	mg.generate(mazeALGORTHIM);
    	ImageIcon img=new ImageIcon(mg.getMazeCanvas().getBufferImage());
    	BufferedImage srcBufferedImage = new BufferedImage(
    			img.getIconWidth(),img.getIconHeight(),BufferedImage.TYPE_INT_ARGB);  
    	srcBufferedImage.getGraphics().drawImage(img.getImage(),0,0,null);
    	mazeGameObject = new MazeGameObject(srcBufferedImage);
    }
    
   
    
    /** Getter for property srv.
     * @return Value of property srv.
     *
     */
    public java.net.ServerSocket getSrv() {
        return srv;
    }
    
    /** Setter for property srv.
     * @param srv New value of property srv.
     *
     */
    public void setSrv(java.net.ServerSocket srv) {
        this.srv = srv;
    }
    
	/**
	 * @return Returns the mazeGameObject.
	 */
	public static final MazeGameObject getMazeGameObject() {
		return mazeGameObject;
	}

	/**
	 * @return Returns the listaJog.
	 */
	public static final java.util.Hashtable getListaJog() {
		return listaJog;
	}

	/**
	 * @param listaJog The listaJog to set.
	 */
	public static final void setListaJog(java.util.Hashtable listaJog) {
		LinhaServidor.listaJog = listaJog;
	}

	/**
	 * @return Returns the mazeALGORTHIM.
	 */
	public static final int getMazeALGORTHIM() {
		return mazeALGORTHIM;
	}

	/**
	 * @param mazeALGORTHIM The mazeALGORTHIM to set.
	 */
	public static final void setMazeALGORTHIM(int mazeALGORTHIM) {
		LinhaServidor.mazeALGORTHIM = mazeALGORTHIM;
	}

}
