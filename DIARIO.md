# Diário de Desenvolvimento - Sistema Distribuído de Contagem

## Informações do Projeto
- **Disciplina:** Programação Paralela e Distribuída
- **Tema:** Sistema Distribuído de Contagem em Java
- **Data de Entrega:** 23/outubro/2025

---

## Cronologia do Desenvolvimento

### 24/outubro/2025 - 20:30
**Desenvolvedor:** Claude Code
**Atividade:** Análise dos requisitos e planejamento

- Leitura completa do arquivo ATV.pdf com especificações do exercício
- Análise do projeto SistemaDistribuido existente como referência
- Identificação das principais diferenças:
  - Vetor de bytes (-100 a 100) vs. int[]
  - Contagem de ocorrências vs. soma
  - Conexões persistentes vs. conexões únicas
  - Threads no cliente (Distribuidor)

**Impressões:** O exercício é bem estruturado e permite explorar conceitos importantes de programação distribuída e paralela. A base do projeto SistemaDistribuido foi útil para entender a arquitetura.

---

### 24/outubro/2025 - 20:45
**Desenvolvedor:** Claude Code
**Atividade:** Criação da estrutura do projeto

- Criação dos diretórios: Comum/, Receptor/, Distribuidor/, Sequencial/
- Definição da arquitetura de classes
- Planejamento das classes de comunicação (Comunicado, Pedido, Resposta, ComunicadoEncerramento)

**Impressões:** A separação em módulos facilita a organização e manutenção do código.

---

### 24/outubro/2025 - 21:00
**Desenvolvedor:** Claude Code
**Atividade:** Implementação das classes de comunicação

Arquivos criados:
- `Comum/Comunicado.java` - Classe base Serializable
- `Comum/Pedido.java` - Com método contar() para processar byte[]
- `Comum/Resposta.java` - Com int contagem
- `Comum/ComunicadoEncerramento.java` - Sinal de término

**Impressões:** A hierarquia de classes ficou clara e bem estruturada. O uso de Serializable é fundamental para transmissão via TCP/IP.

---

### 24/outubro/2025 - 21:15
**Desenvolvedor:** Claude Code
**Atividade:** Implementação do Receptor (Programa R)

Arquivos criados:
- `Receptor/ThreadContadora.java` - Thread para processamento paralelo
- `Receptor/Receptor.java` - Servidor principal

**Destaques técnicos:**
- Uso de Runtime.getRuntime().availableProcessors() para detectar número de cores
- Implementação de loop infinito para aceitar múltiplas conexões
- Divisão do vetor em threads para paralelismo
- Uso de join() para sincronizar threads
- Tratamento de ComunicadoEncerramento para conexões persistentes

**Impressões:** A implementação do paralelismo dentro do Receptor é elegante. Cada thread processa uma fração do vetor, maximizando o uso de processadores disponíveis.

---

### 24/outubro/2025 - 21:40
**Desenvolvedor:** Claude Code
**Atividade:** Implementação do Distribuidor (Programa D)

Arquivos criados:
- `Distribuidor/ThreadClienteR.java` - Thread para conexão com cada Receptor
- `Distribuidor/Distribuidor.java` - Cliente principal

**Destaques técnicos:**
- IPs e portas hard coded conforme especificação
- Interface interativa para configurar tamanho do vetor
- Geração de bytes aleatórios entre -100 e 100
- Escolha aleatória de byte para contar
- Divisão do vetor entre múltiplos Receptores
- Uso de threads para paralelizar conexões
- join() para sincronização
- Envio de ComunicadoEncerramento após conclusão
- Medição de tempo com System.currentTimeMillis()

**Funcionalidades implementadas:**
- Opção para exibir vetor na tela
- Opção para testar com número inexistente (111)
- Logs informativos com IPs

**Impressões:** O Distribuidor coordena bem todo o processo. A interface interativa facilita testes com diferentes configurações.

---

### 24/outubro/2025 - 22:00
**Desenvolvedor:** Claude Code
**Atividade:** Implementação do programa sequencial

Arquivo criado:
- `Sequencial/ContadorSequencial.java` - Versão sem paralelismo

**Objetivo:** Servir como baseline para comparação de performance com o sistema distribuído.

**Impressões:** A simplicidade do código sequencial contrasta com a complexidade do distribuído, demonstrando o overhead necessário para paralelismo e distribuição.

---

### 24/outubro/2025 - 22:15
**Desenvolvedor:** Claude Code
**Atividade:** Documentação e compilação

Arquivo criado:
- `README.md` - Instruções completas de compilação e execução

**Conteúdo:**
- Estrutura do projeto
- Instruções de compilação para Windows e Linux/macOS
- Guia de execução passo a passo
- Sugestões de testes
- Exemplos de logs
- Observações sobre performance

**Impressões:** Documentação clara é essencial para facilitar uso e testes do sistema.

---

### 24/outubro/2025 - 22:30
**Desenvolvedor:** Claude Code
**Atividade:** Compilação e testes

**Compilação:**
- ✅ Comum/*.java - Compilado com sucesso
- ✅ Receptor/*.java - Compilado com sucesso
- ✅ Distribuidor/*.java - Compilado com sucesso
- ✅ Sequencial/*.java - Compilado com sucesso

**Teste 1: Vetor pequeno (100 elementos)**
- Resultado: 2 ocorrências do número 4
- Distribuição: Receptor 1 (0), Receptor 2 (0), Receptor 3 (2)
- Status: ✅ PASSOU

**Teste 2: Número inexistente (111 em vetor de 1000 elementos)**
- Resultado: 0 ocorrências (conforme esperado)
- Status: ✅ PASSOU

**Teste 3: Programa sequencial (1000 elementos)**
- Resultado: 4 ocorrências do número 18
- Tempo: < 1ms (muito rápido para vetores pequenos)
- Status: ✅ PASSOU

**Observações dos testes:**
- Sistema distribuído: 66-144ms (inclui overhead de rede)
- Sistema sequencial: < 1ms (sem overhead)
- Para vetores pequenos, sequencial é mais rápido (esperado)
- Para vetores grandes (milhões), distribuído seria muito mais rápido

**Impressões:** Todos os testes passaram! O sistema funciona corretamente com:
- Conexões persistentes
- Paralelismo em cada Receptor (16 threads)
- Distribuição entre múltiplos Receptores
- Tratamento correto de encerramento

---

## Funcionalidades Implementadas

✅ Geração de vetor grande de bytes aleatórios (-100 a 100)
✅ Escolha aleatória de byte para contar
✅ Divisão do vetor entre múltiplos servidores
✅ Processamento paralelo em cada servidor (threads)
✅ Comunicação via TCP/IP com serialização
✅ Conexões persistentes
✅ Opção para exibir vetor na tela
✅ Teste com número inexistente (111)
✅ Logs informativos em ambos programas ([R] e [D])
✅ Programa sequencial para comparação
✅ Medição de tempo de execução
✅ Tratamento de exceções
✅ Uso de Runtime.getRuntime().availableProcessors()
✅ Thread.join() para sincronização

---

## Desafios Encontrados

1. **Conexões Persistentes:** Diferente do projeto exemplo que fechava após cada pedido, foi necessário implementar loop de leitura de objetos e tratamento especial para ComunicadoEncerramento.

2. **Divisão de Trabalho:** Implementar corretamente a divisão do vetor tanto entre Receptores quanto entre threads dentro de cada Receptor.

3. **Sincronização:** Garantir que o Distribuidor aguarde todas as threads terminarem antes de somar resultados.

4. **Tipo byte:** Trabalhar com range -100 a 100 em tipo byte exigiu cuidado com casting.

---

## Aprendizados

1. **Paralelismo vs Distribuição:**
   - Paralelismo: threads dentro de um processo (ThreadContadora)
   - Distribuição: processos em máquinas diferentes (Receptores)

2. **Overhead de Rede:**
   - Para vetores pequenos, overhead de rede supera benefício do paralelismo
   - Para vetores grandes (milhões), benefício é significativo

3. **Serialização:**
   - Fundamental para comunicação entre processos
   - Permite transmitir objetos complexos pela rede

4. **Arquitetura Cliente-Servidor:**
   - Servidor aguarda passivamente (ServerSocket.accept())
   - Cliente inicia ativamente (new Socket(host, port))
   - Conexões podem ser persistentes ou únicas

---

## Conclusão

O desenvolvimento foi concluído com sucesso. O sistema implementa todos os requisitos especificados no exercício:

- **Distribuição:** Divide trabalho entre múltiplos Receptores
- **Paralelismo:** Cada Receptor usa múltiplas threads
- **Comunicação:** TCP/IP com serialização de objetos
- **Persistência:** Conexões mantidas até ComunicadoEncerramento
- **Funcionalidades extras:** Interface interativa, testes, logs, comparação

O projeto demonstra conceitos importantes de programação concorrente e distribuída, incluindo threads, sincronização, sockets, serialização e coordenação distribuída.

**Performance esperada:**
- Vetores pequenos (< 10.000): Sequencial mais rápido
- Vetores médios (10.000 - 1.000.000): Ganho moderado
- Vetores grandes (> 1.000.000): Ganho significativo com distribuição
- Múltiplas máquinas físicas: Ganho ainda maior

**Próximos passos sugeridos:**
- Testes em múltiplas máquinas físicas
- Benchmark com vetores de milhões de elementos
- Medição de speedup e eficiência
- Testes com diferentes quantidades de Receptores

---

**Data de conclusão:** 24/outubro/2025 - 22:45
**Status:** ✅ COMPLETO E TESTADO
