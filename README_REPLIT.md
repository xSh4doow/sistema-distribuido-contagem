# 🚀 Deploy no Replit - Sistema Distribuído

## Como usar este projeto no Replit

### Opção 1: Import direto do GitHub (Depois do push)

1. Acesse [replit.com](https://replit.com)
2. Clique em **"Create Repl"**
3. Selecione **"Import from GitHub"**
4. Cole a URL do repositório
5. Clique em **"Import from GitHub"**
6. Clique em **"Run"** ▶️

### Opção 2: Upload manual

1. Acesse [replit.com](https://replit.com)
2. Clique em **"Create Repl"** → **"Java"**
3. Delete os arquivos padrão
4. Upload das pastas:
   - `Comum/`
   - `Receptor/`
5. Upload dos arquivos:
   - `.replit`
   - `replit.nix`
6. Clique em **"Run"** ▶️

---

## 📝 Criar 3 Receptores

Você precisa criar **3 Repls separados** (um para cada Receptor):

### Receptor 1
- Nome: `receptor1-sistema-distribuido`
- Porta: 12345 (padrão)

### Receptor 2
- Nome: `receptor2-sistema-distribuido`
- Porta: 12345 (padrão)

### Receptor 3
- Nome: `receptor3-sistema-distribuido`
- Porta: 12345 (padrão)

---

## 🔗 Obter URLs públicas

Após clicar em **Run** em cada Repl:

1. Vá em **"Webview"** (aba no topo)
2. Copie a URL (formato: `https://receptor1-sistema-distribuido.USERNAME.repl.co`)
3. O Replit automaticamente mapeia a porta

**Importante:** Use a URL do Webview, mas conecte via porta exposta.

---

## 🖥️ Executar Distribuidor localmente

No seu computador:

1. Edite `Distribuidor/Distribuidor.java`:

```java
private static final String[] HOSTS = {
    "receptor1-sistema-distribuido.USERNAME.repl.co",
    "receptor2-sistema-distribuido.USERNAME.repl.co",
    "receptor3-sistema-distribuido.USERNAME.repl.co"
};
private static final int[] PORTAS = {443, 443, 443}; // Replit usa HTTPS
```

**Nota:** Replit expõe via HTTPS na porta 443, mas você pode precisar ajustar.

2. Compile e execute:

```bash
javac -cp ".;Comum" Distribuidor\*.java
java -cp ".;Comum;Distribuidor" Distribuidor
```

---

## ⚠️ Limitações do Replit (Free Tier)

- **Sleep:** Repls dormem após inatividade (pode levar ~30s para acordar)
- **Recursos:** Limitado em CPU/RAM
- **Conexões:** Pode ter limites de conexões simultâneas

**Dica:** Mantenha os Repls ativos abrindo as URLs antes de executar o Distribuidor.

---

## 🎯 Alternativa: Replit Always-On (Pago)

Se quiser Repls que nunca dormem:
- Replit Hacker Plan: $7/mês
- Always-On para todos os Repls

---

## 🐛 Troubleshooting

### "Connection refused"
- O Repl pode estar dormindo
- Abra a URL do Repl no navegador para acordá-lo
- Aguarde 30 segundos e tente novamente

### "Timeout"
- Verifique se usou a URL correta
- Confirme que o Repl está rodando (luz verde)

### "Port already in use"
- Normal no Replit, ele gerencia as portas
- Use sempre porta 12345 no código

---

## ✅ Verificar se funciona

1. Abra cada Repl
2. Veja os logs mostrando: `RECEPTOR iniciado na porta 12345`
3. Execute o Distribuidor
4. Deve funcionar! 🎉

---

## 📚 Mais Info

- [Documentação Replit](https://docs.replit.com)
- [Replit Java Docs](https://docs.replit.com/programming-ide/getting-started-java)
