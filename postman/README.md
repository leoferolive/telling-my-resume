# 📮 Postman Collection - Telling My Resume API

Esta pasta contém a collection do Postman para testar todos os endpoints da aplicação **Telling My Resume**.

## 📁 Arquivos

- `Telling_My_Resume.postman_collection.json` - Collection principal com todos os endpoints

## 🚀 Como Importar a Collection

### Método 1: Import via Arquivo
1. Abra o Postman
2. Clique no botão **"Import"** (canto superior esquerdo)
3. Clique em **"Upload Files"**
4. Selecione o arquivo `Telling_My_Resume.postman_collection.json`
5. Clique **"Import"**

### Método 2: Arrastar e Soltar
1. Abra o Postman
2. Arraste o arquivo `Telling_My_Resume.postman_collection.json` diretamente para a janela do Postman
3. A collection será importada automaticamente

## 🔧 Configuração Inicial

### 1. Variáveis de Ambiente
Antes de usar os endpoints de IA, configure a seguinte variável de ambiente:

```
API_GEMINI_KEY = sua-chave-do-gemini
```

**Nota**: O endpoint do Claude está temporariamente desabilitado devido à limitação de créditos.

**Como configurar:**
1. Clique no ícone de "olho" 👁️ no canto superior direito
2. Clique em **"Add"** ao lado de "Environment"
3. Nome: `Telling My Resume Env`
4. Adicione as variáveis acima
5. Salve e selecione o ambiente

### 2. Iniciar a Aplicação
Certifique-se de que a aplicação está rodando:

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 📋 Endpoints Disponíveis

### 🗂️ **Resume Management**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/resume/upload` | Upload de currículo (PDF, DOCX, TXT) |
| GET | `/resume/read/{fileName}` | Lê conteúdo bruto do arquivo |

### 🤖 **AI Analysis**
| Método | Endpoint | Descrição | Status |
|--------|----------|-----------|--------|
| GET | `/resume/generate/{fileName}` | Análise com Gemini AI | ✅ Ativo |
| GET | `/resume/generateClaude/{fileName}` | Análise com Claude AI | ❌ Desabilitado |

### 🌐 **Web Interface**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/resume/view/{fileName}` | Visualização formatada da análise |
| GET | `/` | Página inicial da aplicação |

### 📚 **Documentation & Debug**
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/swagger-ui.html` | Interface Swagger interativa |
| GET | `/v3/api-docs` | Especificação OpenAPI JSON |
| GET | `/h2-console` | Console do banco H2 |

## 🧪 Fluxo de Teste Recomendado

### 1. **Upload de Currículo**
```
POST /resume/upload
- Body: form-data
- Key: file
- Value: Selecione um arquivo PDF, DOCX ou TXT
```

### 2. **Verificar Conteúdo**
```
GET /resume/read/nome-do-arquivo.pdf
- Substitua 'nome-do-arquivo.pdf' pelo nome real do arquivo
```

### 3. **Gerar Análise com IA**
```
GET /resume/generate/nome-do-arquivo.pdf        (Gemini - Ativo)
```
**Nota**: Endpoint do Claude temporariamente indisponível.

### 4. **Visualizar Resultado Formatado**
```
GET /resume/view/nome-do-arquivo.pdf
- Retorna HTML formatado para visualização
```

## 🔧 Variáveis da Collection

A collection já vem com as seguintes variáveis pré-configuradas:

- `baseUrl`: `http://localhost:8080`
- `fileName`: `resume.pdf` (exemplo - substitua pelo nome real do arquivo)

**Para alterar as variáveis:**
1. Clique com botão direito na collection
2. Selecione **"Edit"**
3. Vá na aba **"Variables"**
4. Modifique conforme necessário

## 🗄️ Acesso ao Banco de Dados

Para debugar ou verificar os dados armazenados:

1. Acesse: `http://localhost:8080/h2-console`
2. Use as configurações:
   - **JDBC URL**: `jdbc:h2:file:./data/resumedb`
   - **User Name**: `sa`
   - **Password**: `password`

## 📝 Exemplos de Arquivos para Teste

Você pode testar com diferentes tipos de arquivo:

- **PDF**: Currículo em formato PDF
- **DOCX**: Documento Word
- **TXT**: Arquivo de texto simples

## ❗ Solução de Problemas

### Erro 404 - Currículo não encontrado
- Verifique se o arquivo foi enviado com sucesso
- Confirme o nome exato do arquivo na variável `fileName`

### Erro 500 - Erro interno do servidor
- Verifique se as chaves de API estão configuradas corretamente
- Confirme se a aplicação está rodando
- Verifique os logs da aplicação

### Rate Limiting
- A aplicação tem rate limiting implementado
- Aguarde alguns segundos entre requisições se necessário

## 📞 Suporte

Para problemas ou dúvidas:
1. Verifique os logs da aplicação
2. Consulte a documentação Swagger em `/swagger-ui.html`
3. Acesse o console H2 para verificar dados

---

**✨ Pronto para usar! Importe a collection e comece a testar sua API de análise de currículos com IA.**