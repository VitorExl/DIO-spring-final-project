# DIO Spring Boot Learning Track - Projeto Final com Spring AI

Este repositório contém a trilha de aprendizado de Spring Boot da DIO e a entrega do desafio de projeto final.

> 🚀 **Para conhecer em detalhes o projeto desenvolvido, as melhorias implementadas, como executar e a explicação completa da entrega, acesse a pasta do módulo principal:**
> 
> 👉 **[Acessar Documentação do Desafio (05-spring-ai)](05-spring-ai/README.md)**

---

## Estrutura do Repositório

O projeto final foi construído sobre o módulo `05-spring-ai`, aplicando conceitos arquiteturais sólidos desenvolvidos ao longo de toda a formação:

- **[`05-spring-ai`](05-spring-ai/README.md)**  
  Projeto final: Assistente financeiro multimodal com Spring AI (Whisper, GPT-4o-mini, TTS), Tool Calling, Clean Architecture e DDD.

---

## Guia Arquitetural Compartilhado

As diretrizes arquiteturais seguidas no projeto incluem:

### Arquitetura em Camadas (DDD)
- `domain/`: modelos, entidades, contratos de repositório e invariantes de negócio.
- `application/`: casos de uso expostos para REST e ferramentas de IA (`@Tool`).
- `infrastructure/`: adaptadores HTTP, persistência JPA, integrações externas e tratamento global de erros.

### Java Class vs Java Record
- Classes para entidades de domínio com identidade e ciclo de vida (`Transaction`).
- Records para identificadores tipados (`TransactionId`), DTOs de entrada/saída (`PersistTransactionInput`, `CategoryTotalOutput`) e respostas de erro (`ErrorResponse`).

### Pattern Repository e Casos de Uso
- O domínio define os contratos de persistência de forma desacoplada da tecnologia de banco de dados.
- Cada caso de uso representa uma funcionalidade única do sistema, facilitando a testabilidade e o reuso tanto por APIs REST quanto por agentes de IA.

---

## Como Executar os Testes do Projeto

Para validar o funcionamento de todas as regras de negócio e novas funcionalidades:

```bash
cd 05-spring-ai
./gradlew test
```

Para mais instruções sobre como rodar e testar a aplicação completa, consulte o **[README do Módulo 05-spring-ai](05-spring-ai/README.md)**.
