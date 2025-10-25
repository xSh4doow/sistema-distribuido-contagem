public class ThreadContadora extends Thread
{
    private byte[] numeros;
    private byte procurado;
    private int inicio;
    private int fim;
    private int contagem;
    private int threadId;

    public ThreadContadora(byte[] numeros, byte procurado, int inicio, int fim, int threadId)
    {
        this.numeros = numeros;
        this.procurado = procurado;
        this.inicio = inicio;
        this.fim = fim;
        this.contagem = 0;
        this.threadId = threadId;
    }

    @Override
    public void run()
    {
        System.out.println("  [R-Thread " + threadId + "] Processando indices " +
                           inicio + " ate " + (fim - 1) + " (total: " + (fim - inicio) + " elementos)");

        for (int i = inicio; i < fim; i++)
        {
            if (numeros[i] == procurado)
                contagem++;
        }

        System.out.println("  [R-Thread " + threadId + "] Contagem parcial: " + contagem);
    }

    public int getContagem()
    {
        return this.contagem;
    }
}
