
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author osmar
 */
public class Servidor implements Runnable {

    private String nome;
    private int conexoes;
    private int maximoConexoes;
    private boolean replicarMensagens;
    private JLabel lblConexoes;
    
    private TratamentoCliente[] clientes;
    private Thread[] threadsClientes;

    private int portaServidor;
    private JTextArea txtMensagens;
    

    public Servidor(String nome, int portaServidor, int maximoConexoes, boolean replicarMensagens, JTextArea txtMensagens, JLabel lblConexoes) {
        this.nome = nome;
        this.portaServidor = portaServidor;
        this.maximoConexoes = maximoConexoes;
        this.replicarMensagens = replicarMensagens;
        this.txtMensagens = txtMensagens;
        this.conexoes = 0;
        this.lblConexoes = lblConexoes;

        //Inicializa o vetor de conexoes
        clientes = new TratamentoCliente[getMaximoConexoes()];
        threadsClientes = new Thread[getMaximoConexoes()];
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getConexoes() {
        return conexoes;
    }

    public void setConexoes(int conexoes) {
        this.conexoes = conexoes;
        lblConexoes.setText("" + getConexoes());
    }

    public int getMaximoConexoes() {
        return maximoConexoes;
    }

    public void setMaximoConexoes(int maximoConexoes) {
        this.maximoConexoes = maximoConexoes;
    }

    public boolean isReplicarMensagens() {
        return replicarMensagens;
    }

    public void setReplicarMensagens(boolean replicarMensagens) {
        this.replicarMensagens = replicarMensagens;
    }

    public JTextArea getTxtMensagens() {
        return txtMensagens;
    }

    public void setTxtMensagens(JTextArea txtMensagens) {
        this.txtMensagens = txtMensagens;
    }

    public int getPortaServidor() {
        return portaServidor;
    }

    public void setPortaServidor(int portaServidor) {
        this.portaServidor = portaServidor;
    }
    
    @Override
    public void run() {
        try {
            //Abre um server socket na porta especificada    
            ServerSocket serverSocket = new ServerSocket(portaServidor);

            txtMensagens.append("\n>>> Servidor " + nome + " no ar! <<<");
            txtMensagens.append("\nMaximo de clientes: " + getMaximoConexoes());
            txtMensagens.append("\nEscutando a porta: " + serverSocket.getLocalPort());
            txtMensagens.append("\nAguardando clientes!");

            while (true) {

                Socket socket = serverSocket.accept(); // espera	

                if (conexoes < getMaximoConexoes()) {
                    //Recebeu um cliente                    
                    clientes[conexoes] = new TratamentoCliente(this, socket);
                    threadsClientes[conexoes] = new Thread(clientes[conexoes]);
                    threadsClientes[conexoes].start();

                    //Incrementa o contador de conexoes                        
                    setConexoes(getConexoes() + 1);
                } else {
                    txtMensagens.append("\nNumero maximo de conexoes atingidas");
                }
            }
        } catch (UnknownHostException uhe) {
            txtMensagens.append("\nConexao Terminada!");
        } catch (IOException ioe) {
            txtMensagens.append("\nProblemas de IO");
        }
    }

    //Envia a mensagem para todos os clientes
    public void replicarMensagem(String mensagem) {
        //Se configurado para replicar mensagens
        if (isReplicarMensagens()) {
            for (int i = 0; i < conexoes; i++) {
                clientes[i].enviaMensagem(mensagem);
            }
        }
    }
}
