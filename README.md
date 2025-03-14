# Telling My Resume

Uma aplicação web Java que utiliza inteligência artificial para analisar e melhorar currículos para a área de TI.

## 📋 Descrição

Telling My Resume é uma ferramenta que permite aos usuários fazer upload de seus currículos (PDF, DOCX ou TXT) e receber uma análise detalhada realizada por modelos de IA (Claude e Gemini). A aplicação destaca os pontos fortes, competências técnicas e realizações profissionais relevantes para o setor de TI, ajudando candidatos a destacarem suas qualificações de forma mais eficaz.

## ✨ Funcionalidades

- Upload de currículos em formatos PDF, DOCX e TXT
- Leitura e visualização de currículos armazenados
- Geração de análises personalizadas usando modelo Gemini
- Geração de análises personalizadas usando modelo Claude (Anthropic)
- Visualização formatada dos resultados de análise
- API RESTful documentada com Swagger/OpenAPI

## 🛠️ Tecnologias

- Java 21
- Spring Boot 3.3.3
- Spring AI (Anthropic)
- Thymeleaf para templates
- H2 Database para armazenamento
- Apache PDFBox e POI para processamento de documentos
- Resilience4j para implementação de circuit breakers e fallbacks
- Bucket4j para rate limiting
- SpringDoc/OpenAPI para documentação da API

## ⚙️ Pré-requisitos

- JDK 21+
- Maven 3.6+
- Chave de API para o modelo Gemini da Google
- Chave de API para o modelo Claude da Anthropic

## 🚀 Instalação e Execução

1. Clone o repositório:
   ```bash
   git clone https://github.com/leoferolive/telling-my-resume.git
   cd telling-my-resume
   ```

2. Configure as variáveis de ambiente:
   ```bash
   export API_GEMINI_KEY=sua-chave-do-gemini
   export API_CLAUDE_KEY=sua-chave-do-claude
   ```

3. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Acesse a aplicação em seu navegador:
   ```
   http://localhost:8080
   ```

## 📝 Uso da API

### Endpoints disponíveis

- `POST /resume/upload` - Enviar um currículo
- `GET /resume/read/{fileName}` - Ler um currículo específico
- `GET /resume/generate/{fileName}` - Gerar análise com Gemini
- `GET /resume/generateClaude/{fileName}` - Gerar análise com Claude
- `GET /resume/view/{fileName}` - Visualizar a análise formatada

### Documentação da API

Acesse a documentação completa da API em:
```
http://localhost:8080/swagger-ui.html
```

## 📊 Exemplos

### Exemplo de upload de currículo

```bash
curl -X POST \
  http://localhost:8080/resume/upload \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@path/to/your/resume.pdf'
```

### Exemplo de geração de análise

```bash
curl -X GET http://localhost:8080/resume/generate/resume.pdf
```

## 🔒 Configuração

Principais configurações no arquivo `application.properties`:

```properties
# Porta do servidor
server.port=8080

# Configuração do banco de dados
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Configuração de upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB

# Configuração da IA
spring.ai.anthropic.chat.options.model=claude-3-5-sonnet-20241022
spring.ai.anthropic.chat.options.temperature=0.7
```

## 🖥️ Interface Web

A aplicação inclui uma interface web para interação com os recursos:

- **Página Inicial**: Upload e gerenciamento de currículos
- **Página de Visualização**: Exibição formatada da análise do currículo

## 🚧 Roadmap

- [ ] Implementar comparação entre diferentes versões de currículos
- [ ] Adicionar suporte para outros idiomas
- [ ] Incorporar análises específicas para diferentes setores além de TI
- [ ] Implementar extração automática de palavras-chave
- [ ] Adicionar recursos de edição de currículos

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor, siga estes passos:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/amazing-feature`)
3. Commit suas mudanças (`git commit -m 'Add some amazing feature'`)
4. Push para a branch (`git push origin feature/amazing-feature`)
5. Abra um Pull Request

## 📬 Contato

Leonardo Oliveira - leoferolive@gmail.com

Link do projeto: [https://github.com/leoferolive/telling-my-resume](https://github.com/leoferolive/telling-my-resume)