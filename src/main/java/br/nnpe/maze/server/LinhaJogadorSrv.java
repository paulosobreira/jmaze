package br.nnpe.maze.server;

import br.nnpe.maze.client.LinhaJogador;

/**
 * @author Paulo Sobreira
 * Created on 9 de Outubro de 2003, 20:36
 */
public class LinhaJogadorSrv extends LinhaJogador {

    /**
     * Creates a new instance of LinhaJogadorSrv
     */
    public LinhaJogadorSrv(java.net.Socket con) {
        super(con);

    }

    public void run() {
        try {
            sleep(20);
            while (!interrupted() && getCon().isConnected()) {
                String cmd = getIn().readUTF();
                precessaCmd(cmd);
            }
            getOut().close();
            getIn().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void precessaCmd(java.lang.String cmd) {
        //System.out.println("Server :> "+cmd);
        java.util.StringTokenizer tokens = new java.util.StringTokenizer(cmd,
                ProtocoloDeMenssagens.SEPARADOR);
        String t1 = tokens.nextToken();
        if (t1 == null) {
            return;
        }
        if (ProtocoloDeMenssagens.NOME.equals(t1)) {
            this.setNome(tokens.nextToken());
            LinhaServidor.enviaMsgGeral(getNome() + " Conectado");
        } else if (ProtocoloDeMenssagens.MSG.equals(t1)) {
            LinhaServidor.enviaMsgGeral(getNome() + " Diz " + tokens.nextToken());
        } else if (ProtocoloDeMenssagens.DESCONECTAR.equals(t1)) {
            LinhaServidor.getListaJog().remove(this);
            try {
                getOut().close();
                getIn().close();
                getCon().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ProtocoloDeMenssagens.MOVER.equals(t1)) {
            int x = Integer.parseInt(tokens.nextToken());
            int y = Integer.parseInt(tokens.nextToken());
            if (LinhaServidor.getMazeGameObject().endPoint(x, y)) {
                enviaMsg(getNome() + " ganhou!");
                LinhaServidor.enviaCmdGeral(ProtocoloDeMenssagens.FIM_DO_JOGO);
            }

            if (LinhaServidor.getMazeGameObject().moveAt(x, y)) {
                LinhaServidor.enviaCmdGeral(ProtocoloDeMenssagens.MOVER +
                        ProtocoloDeMenssagens.SEPARADOR +
                        getNome() +
                        ProtocoloDeMenssagens.SEPARADOR +
                        x +
                        ProtocoloDeMenssagens.SEPARADOR +
                        y);

            }
        }

    }

}
