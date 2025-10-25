import java.net.*;
import java.io.*;

public class Receptor
{
    private static int numThreads;

    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("Uso: java Receptor <PORTA>");
            System.err.println("Exemplo: java Receptor 12345");
            return;
        }

        int porta = Integer.parseInt(args[0]);
        numThreads = Runtime.getRuntime().availableProcessors();

        try
        {
            ServerSocket serverSocket = new ServerSocket(porta);
            System.out.println("=================================================");
            System.out.println("RECEPTOR (Programa R) iniciado na porta " + porta);
            System.out.println("Processadores disponiveis: " + numThreads);
            System.out.println("Usando " + numThreads + " threads para processamento");
            System.out.println("=================================================\n");

            while (true)
            {
                System.out.println("[R] Aguardando conexao de um Distribuidor...\n");

                Socket clienteSocket = serverSocket.accept();
                String clienteIP = clienteSocket.getInetAddress().getHostAddress();
                System.out.println("[R] Cliente conectado: " + clienteIP + "\n");

                try
                {
                    ObjectOutputStream transmissor = new ObjectOutputStream(clienteSocket.getOutputStream());
                    ObjectInputStream receptor = new ObjectInputStream(clienteSocket.getInputStream());

                    boolean conexaoAtiva = true;
                    while (conexaoAtiva)
                    {
                        Comunicado comunicado = (Comunicado) receptor.readObject();

                        if (comunicado instanceof Pedido)
                        {
                            System.out.println("[R] Pedido recebido do cliente " + clienteIP);

                            Pedido pedido = (Pedido) comunicado;
                            byte[] numeros = pedido.getNumeros();
                            byte procurado = pedido.getProcurado();

                            System.out.println("[R] Vetor recebido: " + numeros.length + " elementos");
                            System.out.println("[R] Numero a contar: " + procurado);
                            System.out.println("[R] Dividindo em " + numThreads + " threads...\n");

                            ThreadContadora[] threads = new ThreadContadora[numThreads];
                            int tamanhoPorThread = numeros.length / numThreads;

                            for (int i = 0; i < numThreads; i++)
                            {
                                int inicio = i * tamanhoPorThread;
                                int fim = (i == numThreads - 1) ? numeros.length : (i + 1) * tamanhoPorThread;

                                threads[i] = new ThreadContadora(numeros, procurado, inicio, fim, i + 1);
                                threads[i].start();
                            }

                            int contagemTotal = 0;
                            for (int i = 0; i < numThreads; i++)
                            {
                                threads[i].join();
                                contagemTotal += threads[i].getContagem();
                            }

                            System.out.println("\n[R] Contagem total: " + contagemTotal);

                            Resposta resposta = new Resposta(contagemTotal);
                            transmissor.writeObject(resposta);
                            transmissor.flush();

                            System.out.println("[R] Resposta enviada ao cliente " + clienteIP + "\n");
                        }
                        else if (comunicado instanceof ComunicadoEncerramento)
                        {
                            System.out.println("[R] ComunicadoEncerramento recebido de " + clienteIP);
                            System.out.println("[R] Fechando conexao e voltando a aceitar novas conexoes...\n");

                            receptor.close();
                            transmissor.close();
                            clienteSocket.close();

                            conexaoAtiva = false;
                        }
                    }
                }
                catch (Exception e)
                {
                    System.err.println("[R] Erro ao processar cliente: " + e.getMessage());
                    e.printStackTrace();

                    if (clienteSocket != null && !clienteSocket.isClosed())
                        clienteSocket.close();
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("[R] Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
