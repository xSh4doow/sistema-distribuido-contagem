import java.net.*;
import java.io.*;

public class ThreadClienteR extends Thread
{
    private String host;
    private int porta;
    private byte[] dadosParciais;
    private byte procurado;
    private int contagemParcial;
    private int threadId;
    private Socket socket;
    private ObjectOutputStream transmissor;
    private ObjectInputStream receptor;
    private boolean sucesso;

    public ThreadClienteR(String host, int porta, byte[] dadosParciais, byte procurado, int threadId)
    {
        this.host = host;
        this.porta = porta;
        this.dadosParciais = dadosParciais;
        this.procurado = procurado;
        this.threadId = threadId;
        this.contagemParcial = 0;
        this.sucesso = false;
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("[D-Thread " + threadId + "] Conectando ao Receptor " +
                               host + ":" + porta + "...");

            socket = new Socket(host, porta);
            transmissor = new ObjectOutputStream(socket.getOutputStream());
            receptor = new ObjectInputStream(socket.getInputStream());

            System.out.println("[D-Thread " + threadId + "] Conectado!");
            System.out.println("[D-Thread " + threadId + "] Enviando " +
                               dadosParciais.length + " elementos para contar...");

            Pedido pedido = new Pedido(dadosParciais, procurado);
            transmissor.writeObject(pedido);
            transmissor.flush();

            System.out.println("[D-Thread " + threadId + "] Aguardando resposta...");

            Comunicado comunicado = (Comunicado) receptor.readObject();
            if (comunicado instanceof Resposta)
            {
                Resposta resposta = (Resposta) comunicado;
                contagemParcial = resposta.getContagem();
                sucesso = true;

                System.out.println("[D-Thread " + threadId + "] Resposta recebida: " +
                                   contagemParcial + " ocorrencias encontradas");
            }
        }
        catch (Exception e)
        {
            System.err.println("[D-Thread " + threadId + "] Erro: " + e.getMessage());
            sucesso = false;
        }
    }

    public void encerrarConexao()
    {
        try
        {
            if (transmissor != null && socket != null && !socket.isClosed())
            {
                System.out.println("[D-Thread " + threadId + "] Enviando ComunicadoEncerramento...");

                ComunicadoEncerramento encerramento = new ComunicadoEncerramento();
                transmissor.writeObject(encerramento);
                transmissor.flush();

                receptor.close();
                transmissor.close();
                socket.close();

                System.out.println("[D-Thread " + threadId + "] Conexao encerrada.");
            }
        }
        catch (Exception e)
        {
            System.err.println("[D-Thread " + threadId + "] Erro ao encerrar: " + e.getMessage());
        }
    }

    public int getContagemParcial()
    {
        return this.contagemParcial;
    }

    public boolean isSucesso()
    {
        return this.sucesso;
    }
}
