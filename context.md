# Context.md - Sistema Distribuído de Contagem

## Visão Geral do Projeto

**Nome:** Sistema Distribuído de Contagem em Java
**Objetivo:** Implementar um sistema distribuído onde um Distribuidor (D) gera um vetor de bytes e distribui contagem de ocorrências entre múltiplos Receptores (R) que processam em paralelo.

**Tecnologias:**
- Java
- TCP/IP Sockets
- Serialização de Objetos
- Threads/Paralelismo
- Arquitetura Cliente-Servidor

---

## Requisitos do Exercício (ATV.pdf)

### Funcionalidades Principais

1. **Programa D (Distribuidor):**
   - Gera vetor de bytes aleatórios (-100 a 100)
   - Escolhe aleatoriamente um byte para contar
   - Divide vetor em partes
   - Envia partes para diferentes Receptores via TCP/IP
   - Recebe respostas e soma contagens
   - Mede tempo de execução

2. **Programa R (Receptor):**
   - Aceita conexões TCP/IP
   - Recebe Pedidos com partes do vetor
   - Usa threads para processar em paralelo (quantidade = número de processadores)
   - Envia Resposta com contagem
   - Mantém conexão persistente até ComunicadoEncerramento

3. **Classes de Comunicação (Serializáveis):**
   - Comunicado (base)
   - Pedido (byte[] numeros, byte procurado, método contar())
   - Resposta (int contagem)
   - ComunicadoEncerramento

4. **Extras Obrigatórios:**
   - Captura e tratamento de exceções
   - Thread.join() para sincronização
   - Opção de tamanho de vetor configurável
   - Opção de exibir vetor
   - Teste com número inexistente (111)
   - Logs informativos
   - Programa sequencial para comparação
   - Medição de tempo

---

## Arquitetura do Sistema

### Estrutura de Diretórios

```
Exercicio Maligno/
│
├── Comum/                              # Classes compartilhadas (Serializable)
│   ├── Comunicado.java                # Interface base Serializable
│   ├── Pedido.java                    # Pedido: byte[] + byte procurado + contar()
│   ├── Resposta.java                  # Resposta: int contagem + getContagem()
│   └── ComunicadoEncerramento.java    # Sinal de término
│
├── Receptor/                           # Programa R (Servidor)
│   ├── Receptor.java                  # Servidor principal com ServerSocket
│   └── ThreadContadora.java           # Thread para processamento paralelo
│
├── Distribuidor/                       # Programa D (Cliente)
│   ├── Distribuidor.java              # Cliente principal coordenador
│   └── ThreadClienteR.java            # Thread para conexão com cada Receptor
│
├── Sequencial/                         # Programa de comparação
│   └── ContadorSequencial.java        # Versão sem paralelismo/distribuição
│
├── README.md                           # Instruções de uso
├── DIARIO.md                          # Cronologia do desenvolvimento
├── TESTES_REALIZADOS.txt              # Relatório de testes
└── context.md                         # Este arquivo
```

### Fluxo de Comunicação

```
┌─────────────┐                    ┌─────────────┐
│             │  1. Socket Connect │             │
│ Distribuidor│───────────────────>│  Receptor 1 │
│     (D)     │                    │     (R)     │
│             │  2. Pedido         │             │
│             │───────────────────>│             │
│             │                    │             │
│  Thread 1   │  3. Processa com  │ Creates N   │
│             │     threads        │ Threads     │
│             │                    │             │
│             │  4. Resposta       │             │
│             │<───────────────────│             │
│             │                    │             │
│             │  5. ComunicadoEnc. │             │
│             │───────────────────>│             │
└─────────────┘                    └─────────────┘

     │                                    │
     ├──────> Receptor 2 (mesma dinâmica)
     │
     └──────> Receptor 3 (mesma dinâmica)

Após todos responderem:
6. Distribuidor soma contagens parciais
7. Exibe resultado final
```

---

## Detalhes de Implementação

### 1. Classes Comuns (Comum/)

#### Comunicado.java
```java
public class Comunicado implements Serializable
```
- Classe base vazia
- Implementa Serializable para transmissão via TCP/IP
- Serve como superclasse para hierarquia de mensagens

#### Pedido.java
```java
public class Pedido extends Comunicado
{
    private byte[] numeros;      // Parte do vetor
    private byte procurado;      // Byte a contar

    public int contar() { ... }  // Conta ocorrências
}
```
- Contém parte do vetor que o Receptor deve processar
- Método contar() implementado mas não usado (Receptor usa threads)
- Getters para acesso aos dados

#### Resposta.java
```java
public class Resposta extends Comunicado
{
    private int contagem;        // Resultado da contagem

    public int getContagem() { ... }
}
```
- Retorna resultado int (não byte, para evitar overflow)
- Simples container de resultado

#### ComunicadoEncerramento.java
```java
public class ComunicadoEncerramento extends Comunicado
```
- Classe vazia
- Serve como sinal para Receptor fechar conexão
- Receptor volta a aceitar novas conexões após receber

---

### 2. Receptor (Servidor)

#### ThreadContadora.java
```java
public class ThreadContadora extends Thread
{
    private byte[] numeros;      // Vetor completo
    private byte procurado;
    private int inicio, fim;     // Fração que esta thread processa
    private int contagem;        // Resultado parcial
}
```

**Responsabilidades:**
- Processar UMA fração do vetor recebido
- Contar ocorrências do byte procurado em sua fração
- Armazenar resultado para ser coletado com getContagem()

**Detalhes:**
- Cada thread processa índices [inicio, fim)
- Loop simples: `if (numeros[i] == procurado) contagem++;`
- Logs mostram qual fração está processando

#### Receptor.java
```java
public class Receptor
{
    private static int numThreads;  // Runtime.getRuntime().availableProcessors()
}
```

**Fluxo Principal:**
1. Cria ServerSocket na porta especificada (argumento)
2. Loop infinito:
   ```java
   while (true) {
       Socket socket = serverSocket.accept();
       // Conexão aceita

       while (conexaoAtiva) {
           Comunicado msg = receptor.readObject();

           if (msg instanceof Pedido) {
               // Processa pedido com threads
           }
           else if (msg instanceof ComunicadoEncerramento) {
               // Fecha conexão
               conexaoAtiva = false;
           }
       }
   }
   ```

**Processamento de Pedido:**
1. Recebe Pedido
2. Extrai byte[] e byte procurado
3. Descobre quantidade de threads (= processadores)
4. Divide vetor em N partes (uma por thread)
5. Cria e inicia ThreadContadora[]
6. join() em todas
7. Soma contagens parciais
8. Envia Resposta

**Características:**
- Conexões persistentes (não fecha após cada pedido)
- Fecha apenas ao receber ComunicadoEncerramento
- Volta a aceitar conexões após encerrar uma
- Logs com IP do cliente

---

### 3. Distribuidor (Cliente)

#### ThreadClienteR.java
```java
public class ThreadClienteR extends Thread
{
    private String host;
    private int porta;
    private byte[] dadosParciais;  // Parte do vetor para este Receptor
    private byte procurado;
    private int contagemParcial;   // Resultado recebido
    private boolean sucesso;
}
```

**Responsabilidades:**
- Conectar a UM Receptor específico
- Enviar Pedido com parte do vetor
- Receber Resposta
- Armazenar resultado para Distribuidor coletar

**Fluxo:**
1. new Socket(host, porta)
2. Cria ObjectOutputStream e ObjectInputStream
3. Envia Pedido
4. Aguarda Resposta
5. Armazena contagem
6. (Método encerrarConexao() envia ComunicadoEncerramento)

#### Distribuidor.java
```java
public class Distribuidor
{
    // IPs e portas HARD CODED
    private static final String[] HOSTS = {"localhost", "localhost", "localhost"};
    private static final int[] PORTAS = {12345, 12346, 12347};
}
```

**Fluxo Principal:**
1. **Configuração Interativa:**
   ```
   - Quantidade de elementos no vetor?
   - Exibir vetor? (S/N)
   - Usar número inexistente 111? (S/N)
   ```

2. **Geração do Vetor:**
   ```java
   byte[] vetorCompleto = new byte[tamanho];
   for (int i = 0; i < tamanho; i++) {
       vetorCompleto[i] = (byte)(Math.random() * 201 - 100);  // -100 a 100
   }
   ```

3. **Escolha do Número:**
   ```java
   if (usarNumeroInexistente) {
       numeroProcurado = 111;  // Não existe em -100 a 100
   } else {
       int pos = (int)(Math.random() * tamanho);
       numeroProcurado = vetorCompleto[pos];
   }
   ```

4. **Divisão do Vetor:**
   ```java
   int numReceptores = HOSTS.length;
   int tamanhoPorReceptor = tamanho / numReceptores;

   for (int i = 0; i < numReceptores; i++) {
       int inicio = i * tamanhoPorReceptor;
       int fim = (i == numReceptores - 1) ? tamanho : (i + 1) * tamanhoPorReceptor;

       byte[] dadosParciais = new byte[fim - inicio];
       System.arraycopy(vetorCompleto, inicio, dadosParciais, 0, fim - inicio);

       threads[i] = new ThreadClienteR(HOSTS[i], PORTAS[i], dadosParciais, ...);
       threads[i].start();
   }
   ```

5. **Sincronização e Soma:**
   ```java
   int contagemTotal = 0;
   for (int i = 0; i < numReceptores; i++) {
       threads[i].join();  // Espera terminar
       contagemTotal += threads[i].getContagemParcial();
   }
   ```

6. **Encerramento:**
   ```java
   for (int i = 0; i < numReceptores; i++) {
       threads[i].encerrarConexao();  // Envia ComunicadoEncerramento
   }
   ```

**Características:**
- Medição de tempo: `System.currentTimeMillis()`
- Logs com prefixo `[D]` e `[D-Thread X]`
- Tratamento de erros (isSucesso())
- Interface amigável

---

### 4. Programa Sequencial

#### ContadorSequencial.java

**Objetivo:** Baseline para comparação de performance

**Fluxo:**
1. Gera mesmo tipo de vetor que Distribuidor
2. Escolhe byte para contar (mesma lógica)
3. Loop simples:
   ```java
   for (int i = 0; i < vetor.length; i++) {
       if (vetor[i] == numeroProcurado) contagem++;
   }
   ```
4. Mede tempo

**Comparação:**
- Vetores pequenos (< 10K): Sequencial mais rápido (sem overhead)
- Vetores grandes (> 1M): Distribuído muito mais rápido

---

## Compilação e Execução

### Windows

**Compilação:**
```batch
cd "C:\Users\xsh4d\IdeaProjects\11) Um sistema com arquitetura Cliente-Servidor\Exercicio Maligno"

javac Comum\Comunicado.java Comum\Pedido.java Comum\Resposta.java Comum\ComunicadoEncerramento.java
javac -cp ".;Comum" Receptor\ThreadContadora.java Receptor\Receptor.java
javac -cp ".;Comum" Distribuidor\ThreadClienteR.java Distribuidor\Distribuidor.java
javac Sequencial\ContadorSequencial.java
```

**Execução (3 terminais para Receptores):**
```batch
# Terminal 1
java -cp ".;Comum;Receptor" Receptor 12345

# Terminal 2
java -cp ".;Comum;Receptor" Receptor 12346

# Terminal 3
java -cp ".;Comum;Receptor" Receptor 12347
```

**Execução Distribuidor:**
```batch
java -cp ".;Comum;Distribuidor" Distribuidor
```

**Execução Sequencial:**
```batch
java -cp "Sequencial" ContadorSequencial
```

### Linux/macOS

**Compilação:**
```bash
cd "/caminho/para/Exercicio Maligno"

javac Comum/*.java
javac -cp ".:Comum" Receptor/*.java
javac -cp ".:Comum" Distribuidor/*.java
javac Sequencial/*.java
```

**Execução:** (mesma lógica, mas usar `:` em vez de `;` no classpath)

---

## Testes Realizados

### Teste 1: Vetor Pequeno (100 elementos)
- **Config:** 100 elementos, não exibir, número aleatório
- **Resultado:** 2 ocorrências do número 4
- **Distribuição:** R1(0) + R2(0) + R3(2) = 2 ✅
- **Tempo:** 144ms
- **Status:** PASSOU

### Teste 2: Número Inexistente (1000 elementos)
- **Config:** 1000 elementos, não exibir, usar 111
- **Resultado:** 0 ocorrências (correto, 111 não existe em -100 a 100)
- **Distribuição:** R1(0) + R2(0) + R3(0) = 0 ✅
- **Tempo:** 66ms
- **Status:** PASSOU

### Teste 3: Sequencial (1000 elementos)
- **Config:** 1000 elementos, não exibir, número aleatório
- **Resultado:** 4 ocorrências do número 18
- **Tempo:** < 1ms
- **Status:** PASSOU

### Observações
- Sistema distribuído: 66-144ms (overhead de rede + threads)
- Sistema sequencial: < 1ms (sem overhead)
- Para vetores pequenos, sequencial é mais rápido (esperado)
- Para vetores grandes (milhões), distribuído seria muito mais rápido

---

## Configurações Importantes

### IPs e Portas (HARD CODED em Distribuidor.java)

```java
private static final String[] HOSTS = {"localhost", "localhost", "localhost"};
private static final int[] PORTAS = {12345, 12346, 12347};
```

**Para testar em máquinas diferentes:**
1. Descobrir IPs: `ipconfig` (Windows) ou `ifconfig` (Linux)
2. Modificar array HOSTS:
   ```java
   private static final String[] HOSTS = {
       "192.168.1.100",
       "192.168.1.101",
       "192.168.1.102"
   };
   private static final int[] PORTAS = {12345, 12345, 12345};
   ```
3. Recompilar Distribuidor

### Quantidade de Threads (Receptor.java)

```java
numThreads = Runtime.getRuntime().availableProcessors();
```

**Máquina de teste:** 16 processadores detectados
**Resultado:** Cada Receptor cria 16 ThreadContadora

**Para forçar quantidade específica:**
```java
numThreads = 4;  // Em vez de availableProcessors()
```

### Tamanho de Vetor para Testes

**Pequeno (verificar funcionamento):** 100 - 1.000
**Médio (comparar tempos):** 10.000 - 1.000.000
**Grande (demonstrar ganho):** 10.000.000+

**Para vetores muito grandes:**
```bash
java -Xmx8G -cp ".;Comum;Distribuidor" Distribuidor
```

---

## Decisões Técnicas

### 1. Por que int para contagem e não byte?

```java
// Em Resposta.java
private int contagem;  // NÃO byte
```

**Motivo:** Evitar overflow. Um vetor de 10.000.000 elementos pode ter muito mais que 127 ocorrências de um número.

### 2. Por que conexões persistentes?

**Especificação:** "Cada servidor R deve manter sua conexão aberta até receber explicitamente um comunicado de encerramento."

**Implementação:**
```java
while (conexaoAtiva) {
    Comunicado msg = receptor.readObject();

    if (msg instanceof ComunicadoEncerramento) {
        conexaoAtiva = false;  // Sai do loop
    }
}
```

**Diferença do SistemaDistribuido:**
- SistemaDistribuido: fecha após cada pedido
- Este projeto: mantém aberta até ComunicadoEncerramento

### 3. Por que threads no Distribuidor?

**Paralelizar conexões aos Receptores:**
- Thread 1 conecta ao Receptor 1 (pode demorar)
- Thread 2 conecta ao Receptor 2 (simultaneamente)
- Thread 3 conecta ao Receptor 3 (simultaneamente)

**Sem threads:** Conectaria sequencialmente (mais lento)

**Com threads:** Conecta e processa em paralelo

### 4. Por que método contar() em Pedido se não é usado?

**Requisito do PDF:** "Método int contar( ): percorre o vetor e retorna quantas vezes o número procurado foi encontrado"

**Implementado:** Sim
**Usado:** Não (Receptor usa ThreadContadora para paralelismo)

**Poderia ser usado:** Se Receptor não usasse threads, chamaria `pedido.contar()`

---

## Problemas Comuns e Soluções

### 1. "Address already in use"
**Problema:** Porta já em uso
**Solução:**
- Trocar porta no argumento do Receptor
- Matar processo: `netstat -ano | findstr :12345` (Windows)

### 2. "Connection refused"
**Problema:** Receptor não está rodando
**Solução:** Iniciar Receptor antes de Distribuidor

### 3. Vetores muito grandes causam OutOfMemoryError
**Problema:** JVM sem memória suficiente
**Solução:**
```bash
java -Xmx8G -cp ".;Comum;Distribuidor" Distribuidor
```

### 4. Classpath incorreto (ClassNotFoundException)
**Problema:** Classes não encontradas
**Solução:**
- Windows: usar `;` como separador
- Linux/Mac: usar `:` como separador
- Incluir todos os diretórios: `.;Comum;Distribuidor`

### 5. Firewall bloqueia conexão
**Problema:** Firewall bloqueia porta
**Solução:**
- Adicionar exceção para Java
- Ou desativar firewall temporariamente (teste local)

---

## Melhorias Futuras Possíveis

### 1. Configuração Dinâmica de Receptores
```java
// Em vez de hard coded
System.out.print("Quantos receptores? ");
int numReceptores = scanner.nextInt();
for (int i = 0; i < numReceptores; i++) {
    System.out.print("Host: ");
    hosts[i] = scanner.next();
    System.out.print("Porta: ");
    portas[i] = scanner.nextInt();
}
```

### 2. Múltiplos Pedidos por Conexão
- Atualmente: 1 conexão = 1 pedido
- Melhoria: Reutilizar conexão para múltiplos vetores

### 3. Pool de Threads no Receptor
```java
ExecutorService executor = Executors.newFixedThreadPool(numThreads);
// Em vez de criar threads manualmente
```

### 4. Balanceamento de Carga
- Atualmente: divide igualmente
- Melhoria: considerar capacidade de cada Receptor

### 5. Interface Gráfica
- Atualmente: console
- Melhoria: JavaFX ou Swing

### 6. Protocolo de Heartbeat
- Detectar se Receptor caiu
- Redistribuir trabalho

### 7. Compressão de Dados
- Comprimir byte[] antes de enviar
- Reduzir uso de rede

---

## Conceitos Demonstrados

### 1. Programação Distribuída
- Divisão de trabalho entre máquinas
- Comunicação via rede (TCP/IP)
- Coordenação distribuída

### 2. Programação Paralela
- Threads simultâneas
- Sincronização com join()
- Divisão de dados (data parallelism)

### 3. Arquitetura Cliente-Servidor
- ServerSocket (servidor passivo)
- Socket (cliente ativo)
- Conexões persistentes

### 4. Serialização
- Objetos → bytes → rede → bytes → objetos
- Hierarquia de classes Serializable

### 5. Sincronização
- Thread.join() para aguardar conclusão
- Coleta de resultados parciais

### 6. Padrões de Design
- Command Pattern (Pedido/Resposta)
- Thread Pool (ThreadContadora[])
- Template Method (Comunicado)

---

## Diferenças entre SistemaDistribuido e Este Projeto

| Aspecto | SistemaDistribuido | Este Projeto |
|---------|-------------------|--------------|
| **Dados** | int[] | byte[] |
| **Operação** | Soma | Contagem de ocorrências |
| **Conexões** | Única (fecha após pedido) | Persistente (até ComunicadoEncerramento) |
| **Range** | Qualquer int | -100 a 100 (byte) |
| **Cliente** | Pede IPs interativamente | IPs hard coded |
| **Threads** | Fixo (4) | Dinâmico (availableProcessors) |
| **Testes** | Sem número inexistente | Com teste 111 |
| **Documentação** | Básica | Completa (README, DIARIO, TESTES) |

---

## Estrutura de Logs

### Receptor (Programa R)
```
=================================================
RECEPTOR (Programa R) iniciado na porta 12345
Processadores disponiveis: 16
Usando 16 threads para processamento
=================================================

[R] Aguardando conexao de um Distribuidor...
[R] Cliente conectado: 127.0.0.1
[R] Pedido recebido do cliente 127.0.0.1
[R] Vetor recebido: 33 elementos
[R] Numero a contar: 4
[R] Dividindo em 16 threads...

  [R-Thread 1] Processando indices 0 ate 1 (total: 2 elementos)
  [R-Thread 1] Contagem parcial: 0
  ...

[R] Contagem total: 0
[R] Resposta enviada ao cliente 127.0.0.1
[R] ComunicadoEncerramento recebido de 127.0.0.1
[R] Fechando conexao e voltando a aceitar novas conexoes...
```

### Distribuidor (Programa D)
```
=======================================================
DISTRIBUIDOR (Programa D) - Contagem Distribuída
=======================================================

[D] Receptor 1 vai processar 33 elementos (indices 0 ate 32)
[D] Receptor 2 vai processar 33 elementos (indices 33 ate 65)
[D] Receptor 3 vai processar 34 elementos (indices 66 ate 99)

[D-Thread 1] Conectando ao Receptor localhost:12345...
[D-Thread 1] Conectado!
[D-Thread 1] Enviando 33 elementos para contar...
[D-Thread 1] Aguardando resposta...
[D-Thread 1] Resposta recebida: 0 ocorrencias encontradas

[D] Receptor 1 retornou: 0 ocorrencias
...

Total de ocorrencias encontradas: 2
Tempo de contagem distribuida: 144 ms
```

---

## Checklist de Requisitos (Todos Implementados)

- [x] Programa D gera vetor de bytes (-100 a 100)
- [x] D escolhe aleatoriamente número para contar
- [x] D divide vetor em partes
- [x] D envia partes para diferentes R
- [x] Comunicação via TCP/IP
- [x] Serialização de objetos
- [x] Conexões persistentes
- [x] R mantém conexão até ComunicadoEncerramento
- [x] R usa threads (quantidade = processadores)
- [x] Classes: Comunicado, Pedido, Resposta, ComunicadoEncerramento
- [x] Pedido tem método contar()
- [x] Tratamento de exceções
- [x] Thread.join() para sincronização
- [x] Opção de configurar tamanho do vetor
- [x] Opção de exibir vetor
- [x] Teste com número inexistente (111 → 0)
- [x] Testes com vetores pequenos
- [x] Logs informativos
- [x] Programa sequencial para comparação
- [x] Medição de tempo
- [x] Documentação (README)
- [x] Diário de desenvolvimento

---

## Informações de Hardware/Ambiente

**Máquina de Desenvolvimento:**
- OS: Windows
- Processadores: 16 cores detectados
- Java: Versão compatível com Serializable
- Rede: Localhost (127.0.0.1) para testes

**Teste em Produção (Sugestão):**
- 3-4 máquinas diferentes na mesma rede local
- Descobrir IPs com ipconfig/ifconfig
- Atualizar HOSTS[] em Distribuidor.java
- Verificar firewall não bloqueia portas 12345-12347

---

## Comandos Úteis

### Descobrir IPs
```bash
# Windows
ipconfig

# Linux/macOS
ifconfig
ip addr show
```

### Verificar Portas em Uso
```bash
# Windows
netstat -ano | findstr :12345

# Linux/macOS
lsof -i :12345
netstat -tulpn | grep 12345
```

### Matar Processo na Porta
```bash
# Windows
taskkill /PID <PID> /F

# Linux/macOS
kill -9 <PID>
```

### Aumentar Memória da JVM
```bash
java -Xmx8G ...  # 8GB de heap
java -Xms2G -Xmx8G ...  # 2GB inicial, 8GB máximo
```

---

## Status do Projeto

✅ **COMPLETO E TESTADO**

- Todos os requisitos implementados
- Todos os testes passaram
- Documentação completa
- Pronto para demonstração ao professor
- Data: 24/outubro/2025

---

## Próximos Passos Sugeridos

1. **Demonstração ao Professor:** 23/outubro (seguinte à quinta-feira)
2. **Testes em Múltiplas Máquinas:** Testar em rede local com IPs reais
3. **Benchmark:** Comparar tempos com vetores de 10.000.000 elementos
4. **Análise de Performance:** Calcular speedup e eficiência
5. **Variações:** Testar com 2, 4, 8 Receptores diferentes

---

## Referências

- **ATV.pdf:** Especificação original do exercício
- **SistemaDistribuido/:** Projeto de referência usado como base
- **README.md:** Instruções de uso
- **DIARIO.md:** Cronologia detalhada do desenvolvimento
- **TESTES_REALIZADOS.txt:** Relatório de testes

---

**Última Atualização:** 24/outubro/2025 - 23:00
**Versão:** 1.0 - Completa e Testada
**Autor:** Claude Code
**Status:** PRONTO PARA ENTREGA
