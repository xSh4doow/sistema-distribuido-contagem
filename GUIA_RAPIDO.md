# 🚀 Guia Rápido - Deploy

## ⚠️ Replit NÃO funciona!

Replit não suporta TCP direto (só HTTP). Use uma das opções abaixo:

---

## ✅ Opção 1: Docker (MAIS RÁPIDO - 2 minutos)

### Pré-requisito
- Instalar Docker Desktop: https://www.docker.com/products/docker-desktop/

### Executar
```bash
# Basta rodar o script:
start-docker.bat

# Ou manualmente:
docker-compose up --build
```

### Testar
```bash
# Em outro terminal:
java -cp ".;Comum;Distribuidor" Distribuidor
```

**Hosts:** localhost (já configurado!)
**Portas:** 12345, 12346, 12347

---

## ✅ Opção 2: Oracle Cloud (SEMPRE GRÁTIS)

### Vantagens
- ✅ SEMPRE grátis (sem expiração)
- ✅ 4 VMs ARM potentes
- ✅ TCP funciona perfeitamente
- ✅ Demonstração REAL distribuída

### Passo 1: Criar conta
1. Acesse: https://cloud.oracle.com/free
2. Criar conta (pode precisar de cartão, mas não cobra)

### Passo 2: Criar 3 VMs
1. Menu → Compute → Instances → Create Instance
2. Configurar:
   - **Name:** receptor1 (repetir para receptor2, receptor3)
   - **Image:** Ubuntu 22.04
   - **Shape:** VM.Standard.A1.Flex (ARM - Always Free!)
     - OCPUs: 1
     - Memory: 6GB
   - **VCN:** Criar novo ou usar default
   - **Public IP:** Sim
   - **SSH Keys:** Gerar novo par ou upload

3. Repetir 3 vezes

### Passo 3: Configurar Firewall
1. VCN → Security Lists → Default Security List
2. Add Ingress Rule:
   - Source CIDR: 0.0.0.0/0
   - IP Protocol: TCP
   - Destination Port: 12345

### Passo 4: Configurar cada VM
```bash
# Conectar via SSH
ssh -i sua-chave.key ubuntu@<IP-PUBLICO-VM>

# Instalar Java
sudo apt update
sudo apt install -y openjdk-17-jdk

# Criar diretórios
mkdir -p ~/sistema/Comum ~/sistema/Receptor
```

### Passo 5: Enviar arquivos
```bash
# Do seu PC (PowerShell)
scp -i sua-chave.key -r Comum ubuntu@<IP-VM1>:~/sistema/
scp -i sua-chave.key -r Receptor ubuntu@<IP-VM1>:~/sistema/

# Repetir para VM2 e VM3
```

### Passo 6: Executar Receptor em cada VM
```bash
# SSH em cada VM
cd ~/sistema
java -cp ".:Comum:Receptor" Receptor 12345
```

### Passo 7: Atualizar Distribuidor.java
```java
private static final String[] HOSTS = {
    "IP-PUBLICO-VM1",
    "IP-PUBLICO-VM2",
    "IP-PUBLICO-VM3"
};
private static final int[] PORTAS = {12345, 12345, 12345};
```

### Passo 8: Executar Distribuidor
```bash
javac -cp ".;Comum" Distribuidor\*.java
java -cp ".;Comum;Distribuidor" Distribuidor
```

---

## ✅ Opção 3: Local (3 PCs na mesma rede)

Se tiver 3 computadores:

1. **Descobrir IPs:**
   ```bash
   ipconfig  # Windows
   # Anote IPv4 de cada PC
   ```

2. **Em cada PC:**
   ```bash
   # Copiar pastas Comum/ e Receptor/
   javac Comum\*.java
   javac -cp ".;Comum" Receptor\*.java
   java -cp ".;Comum;Receptor" Receptor 12345
   ```

3. **Firewall:**
   - Windows: Permitir Java na porta 12345
   - Configurações → Firewall → Permitir app

4. **No PC do Distribuidor:**
   ```java
   private static final String[] HOSTS = {
       "192.168.1.100",  // IP do PC1
       "192.168.1.101",  // IP do PC2
       "192.168.1.102"   // IP do PC3
   };
   ```

---

## 📊 Comparação

| Opção | Tempo Setup | Custo | TCP Funciona? | Demonstração Real? |
|-------|-------------|-------|---------------|-------------------|
| Docker | 2 min | $0 | ✅ | Não (local) |
| Oracle Cloud | 30 min | $0 | ✅ | ✅ SIM |
| 3 PCs locais | 10 min | $0 | ✅ | ✅ SIM |
| Replit | N/A | N/A | ❌ | ❌ |

---

## 🎯 Recomendação

### Para testar agora:
→ **Docker** (2 minutos)

### Para demonstrar ao professor:
→ **Oracle Cloud** (distribuição REAL em VMs)

### Sem internet/cloud:
→ **3 PCs na rede local**

---

## 🐛 Troubleshooting Docker

### "Docker not found"
- Instalar Docker Desktop
- Reiniciar terminal após instalação

### "Port already in use"
```bash
docker-compose down
netstat -ano | findstr :12345
taskkill /PID <PID> /F
```

### Ver logs
```bash
docker-compose logs -f
```

### Parar tudo
```bash
docker-compose down
```

---

## 📝 Notas

- **Replit não funciona** porque só suporta HTTP, não TCP direto
- **Railway, Fly.io, Render** têm a mesma limitação
- **AWS/GCP/Azure** funcionam, mas expiram (12 meses)
- **Oracle Cloud** é SEMPRE grátis! ⭐
