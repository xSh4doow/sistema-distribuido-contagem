import java.util.Scanner;

public class Distribuidor
{
    private static final String[] HOSTS = {"localhost", "localhost", "localhost"};
    private static final int[] PORTAS = {12345, 12346, 12347};

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=======================================================");
        System.out.println("DISTRIBUIDOR (Programa D) - Contagem Distribuída");
        System.out.println("=======================================================\n");

        System.out.println("Receptores configurados:");
        for (int i = 0; i < HOSTS.length; i++)
        {
            System.out.println("  Receptor " + (i + 1) + ": " + HOSTS[i] + ":" + PORTAS[i]);
        }
        System.out.println();

        System.out.print("Quantos elementos deseja ter no vetor? ");
        int tamanhoVetor = scanner.nextInt();

        System.out.print("Deseja exibir o vetor na tela? (S/N): ");
        boolean exibirVetor = scanner.next().toUpperCase().equals("S");

        System.out.print("Deseja testar com numero inexistente (111)? (S/N): ");
        boolean usarNumeroInexistente = scanner.next().toUpperCase().equals("S");

        System.out.println("\n=======================================================");
        System.out.println("GERANDO VETOR");
        System.out.println("=======================================================");

        System.out.println("\nGerando vetor com " + tamanhoVetor + " elementos...");
        long inicioGeracao = System.currentTimeMillis();

        byte[] vetorCompleto = new byte[tamanhoVetor];
        for (int i = 0; i < tamanhoVetor; i++)
        {
            vetorCompleto[i] = (byte) (((int) (Math.random() * 201)) - 100);
        }

        long fimGeracao = System.currentTimeMillis();
        System.out.println("Vetor gerado em " + (fimGeracao - inicioGeracao) + " ms");

        if (exibirVetor)
        {
            System.out.println("\nVetor gerado:");
            for (int i = 0; i < Math.min(vetorCompleto.length, 100); i++)
            {
                System.out.print(vetorCompleto[i] + " ");
                if ((i + 1) % 20 == 0)
                    System.out.println();
            }
            if (vetorCompleto.length > 100)
                System.out.println("... (mostrando apenas os primeiros 100 elementos)");
            System.out.println();
        }

        byte numeroProcurado;
        if (usarNumeroInexistente)
        {
            numeroProcurado = 111;
            System.out.println("\nNumero escolhido para contar: " + numeroProcurado +
                               " (numero inexistente - deve dar 0)");
        }
        else
        {
            int posicaoAleatoria = (int) (Math.random() * tamanhoVetor);
            numeroProcurado = vetorCompleto[posicaoAleatoria];
            System.out.println("\nNumero escolhido para contar: " + numeroProcurado +
                               " (da posicao " + posicaoAleatoria + ")");
        }

        System.out.println("\n=======================================================");
        System.out.println("INICIANDO CONTAGEM DISTRIBUÍDA");
        System.out.println("=======================================================\n");

        long inicioContagem = System.currentTimeMillis();

        int numReceptores = HOSTS.length;
        System.out.println("Dividindo vetor entre " + numReceptores + " Receptores...\n");

        ThreadClienteR[] threads = new ThreadClienteR[numReceptores];
        int tamanhoPorReceptor = tamanhoVetor / numReceptores;

        for (int i = 0; i < numReceptores; i++)
        {
            int inicio = i * tamanhoPorReceptor;
            int fim = (i == numReceptores - 1) ? tamanhoVetor : (i + 1) * tamanhoPorReceptor;
            int tamanho = fim - inicio;

            byte[] dadosParciais = new byte[tamanho];
            System.arraycopy(vetorCompleto, inicio, dadosParciais, 0, tamanho);

            System.out.println("[D] Receptor " + (i + 1) + " vai processar " + tamanho +
                               " elementos (indices " + inicio + " ate " + (fim - 1) + ")");

            threads[i] = new ThreadClienteR(HOSTS[i], PORTAS[i], dadosParciais,
                                            numeroProcurado, i + 1);
            threads[i].start();
        }

        System.out.println("\n[D] Aguardando respostas de todos os Receptores...\n");

        int contagemTotal = 0;
        boolean todasComSucesso = true;

        for (int i = 0; i < numReceptores; i++)
        {
            try
            {
                threads[i].join();

                if (threads[i].isSucesso())
                {
                    int contagemParcial = threads[i].getContagemParcial();
                    contagemTotal += contagemParcial;
                    System.out.println("[D] Receptor " + (i + 1) + " retornou: " +
                                       contagemParcial + " ocorrencias");
                }
                else
                {
                    System.err.println("[D] Receptor " + (i + 1) + " falhou!");
                    todasComSucesso = false;
                }
            }
            catch (InterruptedException e)
            {
                System.err.println("[D] Erro ao aguardar thread " + (i + 1) + ": " + e.getMessage());
                todasComSucesso = false;
            }
        }

        long fimContagem = System.currentTimeMillis();

        System.out.println("\n=======================================================");
        System.out.println("RESULTADO FINAL");
        System.out.println("=======================================================");

        if (todasComSucesso)
        {
            System.out.println("\nNumero procurado: " + numeroProcurado);
            System.out.println("Tamanho do vetor: " + tamanhoVetor + " elementos");
            System.out.println("Total de ocorrencias encontradas: " + contagemTotal);
            System.out.println("\nTempo de contagem distribuida: " + (fimContagem - inicioContagem) + " ms");
        }
        else
        {
            System.err.println("\nErro: Alguns Receptores falharam. Resultado pode estar incompleto.");
        }

        System.out.println("\n=======================================================");
        System.out.println("ENCERRANDO CONEXÕES");
        System.out.println("=======================================================\n");

        for (int i = 0; i < numReceptores; i++)
        {
            if (threads[i].isSucesso())
            {
                threads[i].encerrarConexao();
            }
        }

        System.out.println("\n[D] Distribuidor finalizado.");

        scanner.close();
    }
}
