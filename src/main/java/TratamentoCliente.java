
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TratamentoCliente implements Runnable {

    private Servidor servidor;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public String nome;

    /**
     * Construtor com parâmetros.
     *
     * @param servidor
     * @param socket
     */
    public TratamentoCliente(Servidor servidor, Socket socket) {
        this.servidor = servidor;

        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ioe) {
            servidor.getTxtMensagens().append("\nProblemas de IO");
        }
    }

    @Override
    public void run() {
        try {
            //Leitura da primeira mensagem com o nome
            nome = leituraMensagem();
            enviaMensagem("\nBem vindo," + nome + " ao servidor " + servidor.getNome());

            //Mensagens para o servidor
            servidor.getTxtMensagens().append("\n-----------------------------------------");
            servidor.getTxtMensagens().append("\nConectando o usuário:" + servidor.getConexoes() + " no servidor " + servidor.getNome());
            servidor.getTxtMensagens().append("\nUsuario:" + nome + " conectado!");
            servidor.getTxtMensagens().append("\n-----------------------------------------");

            //Aguarda mensagens do servidor    
            while (true) {
                
                //Leitura da mensagem do fluxo
                String mensagem = leituraMensagem();
                  
                //Se mensagem diferente de nulo    
                if (mensagem != null) {

                    if (mensagem.startsWith("#fim")) {
                        //Mensagem de desconexão
                        servidor.getTxtMensagens().append("\nDesconectando " + nome + " do servidor " + servidor.getNome());
                        
                        //Descrementa a quantidade de conexoes
                        if (servidor.getConexoes() >= 0) {
                            servidor.setConexoes(servidor.getConexoes() - 1);
                        }
                        break;
                    } else {
                        //Mostra no log a mensagem recebida
                        servidor.getTxtMensagens().append("\nMensagem de " + nome + " > " + mensagem);
                        
                        //Replica a mensagem para os outros clientes
                        servidor.replicarMensagem(nome + " > " + mensagem);
                    }                   
                }
            }

        } catch (UnknownHostException uhe) {
            servidor.getTxtMensagens().append("\nConexao Terminada!");
        } catch (IOException ioe) {
            if (servidor.getConexoes() >= 0) {
                servidor.setConexoes(servidor.getConexoes() - 1);
            }
            servidor.getTxtMensagens().append("\nProblemas de IO");
        }
    }

    /**
     * Envia mensagem pelo fluxo
     * @param mensagem 
     */
    public void enviaMensagem(String mensagem) {
        out.println(mensagem);
    }

    /**
     * Recupera mensagem do fluxo
     * @return
     * @throws IOException 
     */    
    public String leituraMensagem() throws IOException {
        String mensagem = in.readLine();
        return mensagem;
    }
}
