# Guia de Deploy - Sistema Distribuído

## 🐳 Opção 1: Docker (Mais Rápido - Local)

### Pré-requisitos
- Docker Desktop instalado
- Docker Compose instalado

### Executar

```bash
# 1. Navegar para o diretório
cd "C:\Users\xsh4d\IdeaProjects\11) Um sistema com arquitetura Cliente-Servidor\Exercicio Maligno"

# 2. Buildar e iniciar os 3 receptores
docker-compose up --build

# 3. Em outro terminal, executar Distribuidor
java -cp ".;Comum;Distribuidor" Distribuidor
```

**Hosts no Distribuidor:** `localhost` (já está configurado!)
**Portas:** 12345, 12346, 12347

### Parar
```bash
docker-compose down
```

---

## ☁️ Opção 2: AWS EC2 Free Tier

### Passo 1: Criar 3 VMs

1. Acesse **AWS Console** → EC2 → Launch Instance
2. Configure:
   - **Name:** Receptor1, Receptor2, Receptor3
   - **AMI:** Ubuntu Server 22.04 LTS
   - **Instance type:** t2.micro (Free tier)
   - **Key pair:** Criar novo ou usar existente
   - **Security Group:**
     ```
     Type: SSH, Port: 22, Source: My IP
     Type: Custom TCP, Port: 12345, Source: Anywhere (0.0.0.0/0)
     ```
3. Launch (repetir 3 vezes)

### Passo 2: Configurar cada VM

```bash
# Conectar à VM
ssh -i sua-chave.pem ubuntu@<IP-PUBLICO-VM>

# Instalar Java
sudo apt update
sudo apt install -y openjdk-17-jdk

# Criar diretório
mkdir -p ~/sistema/Comum ~/sistema/Receptor
```

### Passo 3: Enviar arquivos

```bash
# Do seu PC Windows (PowerShell)
scp -i sua-chave.pem -r "Comum\*" ubuntu@<IP-VM1>:~/sistema/Comum/
scp -i sua-chave.pem -r "Receptor\*" ubuntu@<IP-VM1>:~/sistema/Receptor/

# Repetir para VM2 e VM3
```

### Passo 4: Executar Receptor em cada VM

```bash
# Em cada VM
cd ~/sistema
java -cp ".:Comum:Receptor" Receptor 12345
```

### Passo 5: Atualizar Distribuidor.java

```java
private static final String[] HOSTS = {
    "ec2-XX-XX-XX-XX.compute-1.amazonaws.com",  // IP público da VM1
    "ec2-YY-YY-YY-YY.compute-1.amazonaws.com",  // IP público da VM2
    "ec2-ZZ-ZZ-ZZ-ZZ.compute-1.amazonaws.com"   // IP público da VM3
};
private static final int[] PORTAS = {12345, 12345, 12345};
```

### Passo 6: Executar Distribuidor

```bash
javac -cp ".;Comum" Distribuidor\*.java
java -cp ".;Comum;Distribuidor" Distribuidor
```

---

## ☁️ Opção 3: Google Cloud Platform

### Passo 1: Criar 3 VMs

1. Acesse **GCP Console** → Compute Engine → VM Instances
2. Create Instance (3x):
   ```
   Name: receptor1, receptor2, receptor3
   Region: us-central1
   Machine type: e2-micro (Free tier)
   Boot disk: Ubuntu 22.04 LTS
   Firewall: ✅ Allow HTTP/HTTPS traffic
   ```

### Passo 2: Configurar Firewall

```bash
# No GCP Console
VPC Network → Firewall → Create Firewall Rule

Name: allow-receptor
Targets: All instances in network
Source IP ranges: 0.0.0.0/0
Protocols and ports: tcp:12345
```

### Passo 3: Setup (similar ao AWS)

```bash
# SSH direto pelo navegador (GCP tem console integrado)

# Instalar Java
sudo apt update && sudo apt install -y openjdk-17-jdk

# Criar diretório
mkdir -p ~/sistema
```

### Passo 4: Upload via SCP ou Console

```bash
# Via gcloud CLI (se instalado)
gcloud compute scp --recurse Comum/ receptor1:~/sistema/
gcloud compute scp --recurse Receptor/ receptor1:~/sistema/

# Ou upload manual via console web
```

---

## 🌐 Opção 4: Render.com (Deploy Web - Experimental)

**Limitação:** Render é mais para web apps, mas pode funcionar com workarounds

1. Criar conta em render.com
2. Deploy como "Background Worker"
3. Expor porta via render.yaml

---

## 🏠 Opção 5: Rede Local (Computadores Físicos)

### Se você tem acesso a 3 computadores na mesma rede:

1. **Descobrir IPs:**
   ```bash
   # Windows
   ipconfig

   # Anote o IPv4 Address (ex: 192.168.1.100)
   ```

2. **Em cada computador:**
   ```bash
   # Copiar pastas Comum/ e Receptor/
   # Compilar
   javac Comum\*.java
   javac -cp ".;Comum" Receptor\*.java

   # Executar
   java -cp ".;Comum;Receptor" Receptor 12345
   ```

3. **No computador do Distribuidor:**
   ```java
   private static final String[] HOSTS = {
       "192.168.1.100",  // PC1
       "192.168.1.101",  // PC2
       "192.168.1.102"   // PC3
   };
   ```

4. **Firewall:**
   ```
   Windows: Permitir Java nas configurações de firewall
   Porta: 12345 (entrada)
   ```

---

## 📊 Testes Recomendados

### Teste 1: Pequeno (verificar funcionamento)
```
Elementos: 10.000
Exibir: Não
Número inexistente: Não
```

### Teste 2: Médio (comparar tempos)
```
Elementos: 1.000.000
Exibir: Não
Número inexistente: Não
```

### Teste 3: Grande (demonstrar ganho)
```
Elementos: 100.000.000
Exibir: Não
Número inexistente: Não
```

---

## 🔍 Verificar IPs Públicos

### AWS
```bash
curl http://checkip.amazonaws.com
# Ou no console: Instances → Description → Public IPv4
```

### GCP
```bash
curl ifconfig.me
# Ou no console: VM Instances → External IP
```

---

## 💡 Dicas

1. **Firewall:** Sempre liberar porta 12345 TCP
2. **Security Groups:** Configurar entrada na porta 12345
3. **IPs:** Usar IPs públicos se for entre máquinas diferentes
4. **Local:** Docker é mais rápido para testes
5. **Cloud:** AWS/GCP para demonstração real
6. **Logs:** Acompanhar em tempo real com `tail -f`

---

## 🐛 Troubleshooting

### "Connection refused"
- Receptor não está rodando
- Firewall bloqueando porta
- IP incorreto

### "No route to host"
- Security Group não configurado
- Firewall bloqueando

### Solução:
```bash
# Verificar se Receptor está ouvindo
netstat -an | grep 12345

# Testar conectividade
telnet <IP> 12345
```
