package br.nnpe.maze.client;

import br.nnpe.maze.server.ProtocoloDeMenssagens;
/*
 * LinhaJogador.java
 *
 * Created on 9 de Outubro de 2003, 19:25
 */

/**
 *
 * @author  Paulo Sobreira
 */
public abstract class LinhaJogador extends Thread {
    
    private String nome;
    private String id;
    private boolean vez;
    private int pontos;
    private java.net.Socket con;
    private java.io.DataInputStream in;
    private java.io.DataOutputStream out;

    /** Creates a new instance of LinhaJogadorCliente  */
    protected javax.swing.JTextArea saidaTxt;
    
    /** Creates a new instance of LinhaJogador */
    public LinhaJogador(java.net.Socket con) {
       try{
        this.setCon(con);
         in = new java.io.DataInputStream(new java.io.BufferedInputStream(
                                            con.getInputStream()));
         out = new java.io.DataOutputStream(con.getOutputStream());
       }
        catch ( Exception e ) {
            e.printStackTrace();
         }
    }
    
    /** Getter for property con.
     * @return Value of property con.
     *
     */
    public java.net.Socket getCon() {
        return con;
    }
    
    /** Setter for property con.
     * @param con New value of property con.
     *
     */
    public void setCon(java.net.Socket con) {
        this.con = con;
    }
    
    /** Getter for property in.
     * @return Value of property in.
     *
     */
    public java.io.DataInputStream getIn() {
        return in;
    }
    
    /** Setter for property in.
     * @param in New value of property in.
     *
     */
    public void setIn(java.io.DataInputStream in) {
        this.in = in;
    }
    
    /** Getter for property out.
     * @return Value of property out.
     *
     */
    public java.io.DataOutputStream getOut() {
        return out;
    }
    
    /** Setter for property out.
     * @param out New value of property out.
     *
     */
    public void setOut(java.io.DataOutputStream out) {
        this.out = out;
    }
    
    /** Getter for property pontos.
     * @return Value of property pontos.
     *
     */
    public int getPontos() {
        return pontos;
    }
    
    /** Setter for property pontos.
     * @param pontos New value of property pontos.
     *
     */
    public void setPontos(int pontos) {
        this.pontos = pontos;
    }
    
    /** Getter for property vez.
     * @return Value of property vez.
     *
     */
    public boolean isVez() {
        return vez;
    }
    
    /** Setter for property vez.
     * @param vez New value of property vez.
     *
     */
    public void setVez(boolean vez) {
        this.vez = vez;
    }
    
    public void enviaMsg(java.lang.String msg) {
        try {
            out.writeUTF(ProtocoloDeMenssagens.MSG+ProtocoloDeMenssagens.SEPARADOR+msg);
        }
        catch ( Exception e ) {
            e.printStackTrace();
         }
    }
    
    /** Getter for property id.
     * @return Value of property id.
     *
     */
    public String getIdent() {
        return this.id;
    }
    
    /** Setter for property id.
     * @param id New value of property id.
     *
     */
    public void setIdent(String id) {
        this.id = id;
    }
    
    public void precessaCmd(java.lang.String cmd) {
    }
    
    /** Getter for property nome.
     * @return Value of property nome.
     *
     */
    public java.lang.String getNome() {
        return nome;
    }
    
    /** Setter for property nome.
     * @param nome New value of property nome.
     *
     */
    public void setNome(java.lang.String nome) {
        this.nome = nome;
    }
    
    public void adicionarTxt(java.lang.String txt) {
        saidaTxt.append(txt+"\n");
        saidaTxt.setCaretPosition(saidaTxt.getText().length());
    }
    
    /** Getter for property saidaTxt.
     * @return Value of property saidaTxt.
     *
     */
    public javax.swing.JTextArea getSaidaTxt() {
        return saidaTxt;
    }
    
    /** Setter for property saidaTxt.
     * @param saidaTxt New value of property saidaTxt.
     *
     */
    public void setSaidaTxt(javax.swing.JTextArea saidaTxt) {
        this.saidaTxt = saidaTxt;
    }
    
}
