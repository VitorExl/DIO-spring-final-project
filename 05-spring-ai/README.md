# DIO Spring Boot - Desafio Final: Assistente Financeiro com Spring AI

Este repositório contém a minha entrega do desafio final da trilha de Spring Boot da DIO. O objetivo principal foi integrar recursos de inteligência artificial generativa e multimodal utilizando Spring AI em uma API de controle financeiro pessoal, respeitando a arquitetura em camadas e boas práticas de Domain-Driven Design (DDD).

---

## 1. O que o projeto faz

A aplicação funciona como um assistente financeiro capaz de processar comandos em linguagem natural, tanto por texto quanto por áudio. 

O fluxo principal da aplicação acontece da seguinte forma:
1. O usuário envia uma gravação de áudio com um comando de voz (por exemplo: "Gastei 50 reais no supermercado" ou "Quanto já gastei com alimentação?").
2. O modelo Whisper da OpenAI faz a transcrição do áudio para texto em português.
3. O Spring AI (usando ChatClient e o modelo GPT-4o-mini) interpreta a mensagem e identifica qual caso de uso deve ser chamado.
4. O caso de uso executa a operação necessária no banco de dados, como salvar uma nova despesa ou consultar registros existentes.
5. O texto de resposta formulado pela IA é convertido em áudio com o modelo de Text-to-Speech (TTS) da OpenAI, retornando um arquivo de áudio MP3 para o usuário.

---

## 2. Tecnologias utilizadas

- Java 25
- Spring Boot 4
- Spring AI (OpenAI Starter)
- OpenAI API (GPT-4o-mini, Whisper-1 e TTS)
- Spring Data JPA e Hibernate
- Banco de dados MySQL / H2
- Lombok
- JUnit 5 e Mockito
- Gradle

---

## 3. Melhorias implementadas

Para evoluir a aplicação além do código base, desenvolvi as seguintes melhorias:

### Total de gastos por categoria
Criei o caso de uso GetTotalSpentByCategoryUseCase para somar os gastos de uma categoria específica. A funcionalidade foi registrada como uma tool para que a IA possa responder perguntas diretas de soma, como "quanto gastei com alimentação?".

### Total de gastos por mês e ano
Adicionei o campo de data na entidade Transaction e criei o caso de uso GetTotalSpentByMonthUseCase. Isso permite realizar consultas temporais filtrando as transações pelo período desejado, permitindo que a IA processe comandos como "qual foi o total de gastos em maio?".

### Tratamento global de erros
Implementei uma classe de tratamento de exceções (GlobalExceptionHandler) com uma estrutura de resposta padronizada (ErrorResponse). Isso garante que requisições com áudio vazio, categorias inválidas ou parâmetros ausentes retornem mensagens claras e com os códigos HTTP adequados.

---

## 4. Como executar a aplicação

### Pré-requisitos
- JDK 25 instalado
- Uma chave da API da OpenAI (OPENAI_API_KEY)

### Execução

1. Defina a variável de ambiente com a sua chave da OpenAI:

   - Linux ou macOS:
     ```bash
     export OPENAI_API_KEY="sua-chave-aqui"
     ```
   - Windows (PowerShell):
     ```powershell
     $env:OPENAI_API_KEY="sua-chave-aqui"
     ```
   - Windows (CMD):
     ```cmd
     set OPENAI_API_KEY=sua-chave-aqui
     ```

2. Execute os testes para validar o projeto:
   ```bash
   ./gradlew test
   ```

3. Inicie o servidor:
   ```bash
   ./gradlew bootRun
   ```

---

## 5. Como testar o fluxo principal

### Teste por áudio (Interação multimodal)
Envie um arquivo de áudio para o endpoint de IA:

```bash
curl -X POST "http://localhost:8080/transactions/ai" \
  -H "accept: audio/mp3" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@src/test/resources/audio/recording-1.m4a" \
  --output resposta.mp3
```

O arquivo resposta.mp3 conterá a fala da assistente respondendo ao comando do áudio.

### Testes via endpoints REST
Você também pode interagir diretamente via REST para validar os dados:

- Registrar despesa:
  ```bash
  curl -X POST "http://localhost:8080/transactions" \
    -H "Content-Type: application/json" \
    -d '{"description": "Mercado", "category": "GROCERIES", "amount": 15000}'
  ```

- Consultar total por categoria:
  ```bash
  curl -X GET "http://localhost:8080/transactions/total/category/GROCERIES"
  ```

- Consultar total por mês:
  ```bash
  curl -X GET "http://localhost:8080/transactions/total/month?month=5&year=2026"
  ```

---

## 6. O que aprendi durante o desafio

Durante o desenvolvimento deste projeto, aprendi como conectar modelos de inteligência artificial diretamente a serviços Java usando o Spring AI. Foi muito interessante ver na prática como funciona o conceito de Tool Calling, onde o próprio modelo de linguagem decide quando acionar métodos específicos do sistema com base na intenção do usuário.

Além disso, compreendi a importância de manter as fronteiras da arquitetura bem definidas: a IA atua apenas como uma camada de interação inteligente, enquanto as regras de negócio, os cálculos de valores e a persistência continuam organizados nos casos de uso e no domínio da aplicação.
