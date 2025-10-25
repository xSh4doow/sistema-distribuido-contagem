# Sistema Distribuído de Contagem em Java

Trabalho de Programação Paralela e Distribuída - Sistema de contagem distribuída de bytes em vetor.

## Descrição

Sistema distribuído que realiza contagem de ocorrências de um byte em um grande vetor, utilizando:
- **Programa D (Distribuidor)**: Cliente que gera vetor, divide entre servidores e coordena contagem
- **Programa R (Receptor)**: Servidor que recebe partes do vetor e processa usando threads paralelas
- **Comunicação**: TCP/IP com serialização de objetos e conexões persistentes

## Estrutura do Projeto

```
Exercicio Maligno/
├── Comum/                          # Classes compartilhadas
│   ├── Comunicado.java            # Classe base para comunicação
│   ├── Pedido.java                # Pedido de contagem
│   ├── Resposta.java              # Resposta com contagem
│   └── ComunicadoEncerramento.java # Sinal de encerramento
├── Receptor/                       # Programa R (Servidor)
│   ├── Receptor.java              # Servidor principal
│   └── ThreadContadora.java       # Thread para processamento paralelo
├── Distribuidor/                   # Programa D (Cliente)
│   ├── Distribuidor.java          # Cliente principal
│   └── ThreadClienteR.java        # Thread para conexão com servidor
└── Sequencial/                     # Programa para comparação
    └── ContadorSequencial.java    # Contagem sem paralelismo
```

## Compilação

### Windows (PowerShell ou CMD)

```bash
# Navegar para o diretório do projeto
cd "C:\Users\xsh4d\IdeaProjects\11) Um sistema com arquitetura Cliente-Servidor\Exercicio Maligno"

# Compilar classes comuns
javac Comum\*.java

# Compilar Receptor (Programa R)
javac -cp ".;Comum" Receptor\*.java

# Compilar Distribuidor (Programa D)
javac -cp ".;Comum" Distribuidor\*.java

# Compilar programa sequencial
javac Sequencial\*.java
```
## Execução

### 1. Preparação Inicial

#### Descobrir endereço IP do computador:

**Windows:**
```bash
ipconfig
```

#### Configurar IPs no Distribuidor:

Edite `Distribuidor/Distribuidor.java` e modifique as linhas:

```java
private static final String[] HOSTS = {"192.168.1.100", "192.168.1.101", "192.168.1.102"};
private static final int[] PORTAS = {12345, 12345, 12345};
```

### 2. Executar Receptores (Servidores)

Em **3 ou 4 terminais/computadores diferentes**, execute:

#### Terminal 1 (Receptor 1):
```bash
# Windows
java -cp ".;Comum;Receptor" Receptor 12345
```

#### Terminal 2 (Receptor 2):
```bash
# Windows
java -cp ".;Comum;Receptor" Receptor 12346
```

#### Terminal 3 (Receptor 3):
```bash
# Windows
java -cp ".;Comum;Receptor" Receptor 12347
```

**Nota:** Se estiver testando localmente, use portas diferentes. Se em máquinas diferentes, pode usar a mesma porta (12345) em todas.

### 3. Executar Distribuidor (Cliente)

Em outro terminal:

```bash
# Windows
java -cp ".;Comum;Distribuidor" Distribuidor

# Linux/macOS
java -cp ".:Comum:Distribuidor" Distribuidor
```

### 4. Executar Programa Sequencial (para comparação)

```bash
# Windows
java -cp "Sequencial" ContadorSequencial

# Linux/macOS
java -cp Sequencial ContadorSequencial
```

## Testes Recomendados

### Teste 1: Vetor Pequeno (Verificação de Funcionamento)
- Tamanho: 100 elementos
- Exibir vetor: SIM
- Número inexistente: NÃO
- **Objetivo:** Verificar visualmente se a contagem está correta

### Teste 2: Número Inexistente
- Tamanho: 1000 elementos
- Exibir vetor: NÃO
- Número inexistente: SIM (111)
- **Resultado esperado:** Contagem = 0

### Teste 3: Vetor Médio
- Tamanho: 1.000.000 elementos
- Exibir vetor: NÃO
- Número inexistente: NÃO
- **Objetivo:** Comparar tempo com programa sequencial

### Teste 4: Vetor Grande (Benchmark)
- Tamanho: 10.000.000 elementos
- Exibir vetor: NÃO
- Número inexistente: NÃO
- **Objetivo:** Demonstrar ganho de performance com distribuição

**Nota:** Para vetores muito grandes, execute com mais memória:
```bash
java -Xmx4G -cp ".;Comum;Distribuidor" Distribuidor
```

## Exemplo de Logs

### Receptor (Servidor):
```
=================================================
RECEPTOR (Programa R) iniciado na porta 12345
Processadores disponiveis: 8
Usando 8 threads para processamento
=================================================

[R] Aguardando conexao de um Distribuidor...

[R] Cliente conectado: 192.168.1.50

[R] Pedido recebido do cliente 192.168.1.50
[R] Vetor recebido: 3333333 elementos
[R] Numero a contar: 42
[R] Dividindo em 8 threads...

  [R-Thread 1] Processando indices 0 ate 416665 (total: 416666 elementos)
  [R-Thread 2] Processando indices 416666 ate 833331 (total: 416666 elementos)
  ...
  [R-Thread 1] Contagem parcial: 2053
  [R-Thread 2] Contagem parcial: 2048
  ...

[R] Contagem total: 16384
[R] Resposta enviada ao cliente 192.168.1.50
```

### Distribuidor (Cliente):
```
=======================================================
DISTRIBUIDOR (Programa D) - Contagem Distribuída
=======================================================

Receptores configurados:
  Receptor 1: 192.168.1.100:12345
  Receptor 2: 192.168.1.101:12345
  Receptor 3: 192.168.1.102:12345

[D] Receptor 1 vai processar 3333333 elementos (indices 0 ate 3333332)
[D] Receptor 2 vai processar 3333333 elementos (indices 3333333 ate 6666665)
[D] Receptor 3 vai processar 3333334 elementos (indices 6666666 ate 9999999)

[D-Thread 1] Conectando ao Receptor 192.168.1.100:12345...
[D-Thread 2] Conectando ao Receptor 192.168.1.101:12345...
[D-Thread 3] Conectando ao Receptor 192.168.1.102:12345...

[D] Receptor 1 retornou: 16384 ocorrencias
[D] Receptor 2 retornou: 16401 ocorrencias
[D] Receptor 3 retornou: 16398 ocorrencias

=======================================================
RESULTADO FINAL
=======================================================

Total de ocorrencias encontradas: 49183
Tempo de contagem distribuida: 1523 ms
```

## Funcionalidades Implementadas

- ✅ Geração de vetor grande de bytes aleatórios (-100 a 100)
- ✅ Escolha aleatória de byte para contar
- ✅ Divisão do vetor entre múltiplos servidores
- ✅ Processamento paralelo em cada servidor (threads)
- ✅ Comunicação via TCP/IP com serialização
- ✅ Conexões persistentes
- ✅ Opção para exibir vetor na tela
- ✅ Teste com número inexistente (111)
- ✅ Logs informativos em ambos programas
- ✅ Programa sequencial para comparação
- ✅ Medição de tempo de execução
- ✅ Tratamento de exceções

## Descobrindo Quantidade de Processadores

```java
int numProcessadores = Runtime.getRuntime().availableProcessors();
System.out.println("Processadores disponíveis: " + numProcessadores);
```

## Observações Importantes

1. **Teste Local:** Para testar localmente, execute múltiplos Receptores em portas diferentes (12345, 12346, 12347)
2. **Teste em Rede:** Para testar em máquinas diferentes, configure os IPs corretos no Distribuidor
3. **Firewall:** Certifique-se de que as portas não estão bloqueadas pelo firewall
4. **Memória:** Para vetores muito grandes, aumente a memória da JVM com `-Xmx`
5. **Performance:** O ganho de performance é mais visível com:
   - Vetores grandes (milhões de elementos)
   - Múltiplos processadores/núcleos
   - Múltiplas máquinas físicas

## Autores

Desenvolvido como trabalho acadêmico de Programação Paralela e Distribuída.
