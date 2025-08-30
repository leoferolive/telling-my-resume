# 📮 Postman Collection - Telling My Resume API v2.0

Esta pasta contém a collection do Postman para testar todos os endpoints da aplicação **Telling My Resume** com a nova arquitetura de **Application Services** (Phase 3 completa).

## 🆕 **Novidades da v2.0**
- ✨ **Smart Analysis** - Endpoint inteligente que seleciona automaticamente o melhor provedor de IA
- 🔍 **AI Provider Management** - Monitoramento e status dos provedores de IA
- 🏗️ **Application Services** - Arquitetura refatorada com camada de orquestração
- 🚀 **Melhor tratamento de erros** e fallbacks automáticos
- 📊 **Endpoints de monitoramento** para health check dos serviços

## 📁 Arquivos

- `Telling_My_Resume.postman_collection.json` - Collection v2.0 com endpoints da nova arquitetura

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
Para usar todos os recursos de IA, configure as seguintes variáveis de ambiente:

```
API_GEMINI_KEY = sua-chave-do-gemini
API_CLAUDE_KEY = sua-chave-do-claude
```

**🔥 Nova funcionalidade**: O sistema agora possui **Smart Analysis** que automaticamente usa o melhor provedor disponível, com fallback inteligente entre Claude e Gemini.

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
| POST | `/resume/upload` | Upload de currículo (PDF, DOCX, TXT) com validação |
| GET | `/resume/read/{fileName}` | Lê conteúdo processado do arquivo |

### 🤖 **AI Analysis** 
| Método | Endpoint | Descrição | Status |
|--------|----------|-----------|--------|
| GET | `/resume/analyze/{fileName}` | 🆕 **Smart Analysis** (melhor provedor) | ✅ Recomendado |
| GET | `/resume/generate/{fileName}` | Análise específica com Gemini AI | ✅ Ativo |
| GET | `/resume/generateClaude/{fileName}` | Análise específica com Claude AI | ✅ Ativo |

### 🔍 **AI Provider Management** (🆕)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/ai/status` | Status completo do sistema de IA |
| GET | `/ai/providers` | Lista provedores disponíveis |
| GET | `/ai/preferred` | Provedor preferido atual |
| GET | `/ai/providers/{provider}/status` | Status de provedor específico |

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

### 🆕 **Novo Fluxo Inteligente (Recomendado)**

### 1. **Verificar Status do Sistema**
```
GET /ai/status
- Verifica quais provedores estão disponíveis
- Mostra provedor preferido atual
```

### 2. **Upload de Currículo**
```
POST /resume/upload
- Body: form-data
- Key: file (obrigatório)
- Key: customFileName (opcional)
- Validação automática de tipo e tamanho
```

### 3. **Smart Analysis (🔥 Novo)**
```
GET /resume/analyze/nome-do-arquivo.pdf
- ✨ Usa automaticamente o melhor provedor disponível
- ✨ Fallback inteligente se um provedor falhar
- ✅ RECOMENDADO para uso em produção
```

### 4. **Visualizar Resultado Formatado**
```
GET /resume/view/nome-do-arquivo.pdf
- Usa Smart Analysis internamente
- Retorna HTML formatado com melhor análise disponível
```

### **Fluxo Clássico (Específico por Provedor)**

### 3a. **Análise Específica**
```
GET /resume/generate/nome-do-arquivo.pdf        (Gemini específico)
GET /resume/generateClaude/nome-do-arquivo.pdf  (Claude específico)
```

## 🔧 Variáveis da Collection

A collection v2.0 já vem com as seguintes variáveis pré-configuradas:

- `baseUrl`: `http://localhost:8080`
- `fileName`: `resume.pdf` (exemplo - substitua pelo nome real do arquivo)
- `providerName`: `Claude` (🆕 para endpoints específicos de provedor)

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

### Erro 503 - Serviço Indisponível
- 🆕 **Nenhum provedor de IA disponível**: Verifique `/ai/status`
- Configure pelo menos uma chave de API (Gemini ou Claude)
- Use o endpoint `/ai/preferred` para ver qual provedor está ativo

### Erro 500 - Erro interno do servidor
- Verifique se as chaves de API estão configuradas corretamente
- Confirme se a aplicação está rodando
- Use `/ai/status` para diagnosticar problemas de IA

### 🆕 **Debugging com Novos Endpoints**
```
GET /ai/status           - Status completo do sistema
GET /ai/providers        - Lista provedores ativos
GET /ai/preferred        - Provedor preferido atual
```

### Rate Limiting
- A aplicação tem rate limiting implementado
- Aguarde alguns segundos entre requisições se necessário

## 📞 Suporte

Para problemas ou dúvidas:
1. Verifique os logs da aplicação
2. Consulte a documentação Swagger em `/swagger-ui.html`
3. Acesse o console H2 para verificar dados

---

## 🚀 **Arquitetura v2.0 - Application Services**

A collection v2.0 reflete a nova arquitetura implementada:

```
Controllers → Application Services → Domain Services → Infrastructure
     ↓              ↓                      ↓              ↓
ResumeController  ResumeAnalysisService  ResumeService  StorageService
AIProviderController  AIProviderService   AIAnalysisService  DatabaseStorageService
```

### ✨ **Benefícios da Nova Arquitetura**
- **🤖 Smart Analysis**: Seleção automática do melhor provedor
- **🔄 Fallback Inteligente**: Se um provedor falha, usa outro automaticamente  
- **📊 Monitoramento**: Endpoints para verificar health dos serviços
- **🏗️ Escalabilidade**: Fácil adição de novos provedores de IA
- **🛡️ Robustez**: Melhor tratamento de erros e exceções

---

**🔥 Nova experiência! Importe a collection v2.0 e descubra os recursos avançados da arquitetura Application Services.**