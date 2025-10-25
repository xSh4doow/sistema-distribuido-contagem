import java.util.Scanner;

public class ContadorSequencial
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=======================================================");
        System.out.println("CONTADOR SEQUENCIAL - Contagem Sem Paralelismo");
        System.out.println("=======================================================\n");

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

        byte[] vetor = new byte[tamanhoVetor];
        for (int i = 0; i < tamanhoVetor; i++)
        {
            vetor[i] = (byte) (((int) (Math.random() * 201)) - 100);
        }

        long fimGeracao = System.currentTimeMillis();
        System.out.println("Vetor gerado em " + (fimGeracao - inicioGeracao) + " ms");

        if (exibirVetor)
        {
            System.out.println("\nVetor gerado:");
            for (int i = 0; i < Math.min(vetor.length, 100); i++)
            {
                System.out.print(vetor[i] + " ");
                if ((i + 1) % 20 == 0)
                    System.out.println();
            }
            if (vetor.length > 100)
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
            numeroProcurado = vetor[posicaoAleatoria];
            System.out.println("\nNumero escolhido para contar: " + numeroProcurado +
                               " (da posicao " + posicaoAleatoria + ")");
        }

        System.out.println("\n=======================================================");
        System.out.println("INICIANDO CONTAGEM SEQUENCIAL");
        System.out.println("=======================================================\n");

        System.out.println("Percorrendo vetor sequencialmente...");
        long inicioContagem = System.currentTimeMillis();

        int contagem = 0;
        for (int i = 0; i < vetor.length; i++)
        {
            if (vetor[i] == numeroProcurado)
                contagem++;
        }

        long fimContagem = System.currentTimeMillis();

        System.out.println("\n=======================================================");
        System.out.println("RESULTADO FINAL");
        System.out.println("=======================================================");

        System.out.println("\nNumero procurado: " + numeroProcurado);
        System.out.println("Tamanho do vetor: " + tamanhoVetor + " elementos");
        System.out.println("Total de ocorrencias encontradas: " + contagem);
        System.out.println("\nTempo de contagem sequencial: " + (fimContagem - inicioContagem) + " ms");

        System.out.println("\n=======================================================");
        System.out.println("COMPARAÇÃO");
        System.out.println("=======================================================");
        System.out.println("\nPara comparar com o sistema distribuído:");
        System.out.println("1. Execute o Distribuidor com o mesmo tamanho de vetor");
        System.out.println("2. Compare os tempos de execução");
        System.out.println("3. O sistema distribuído deve ser mais rápido para vetores grandes");
        System.out.println("\nNota: A diferença de tempo será mais significativa com:");
        System.out.println("  - Vetores muito grandes (milhões de elementos)");
        System.out.println("  - Múltiplos processadores/núcleos");
        System.out.println("  - Múltiplos Receptores em máquinas diferentes");

        scanner.close();
    }
}
