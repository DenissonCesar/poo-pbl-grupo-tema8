# Gerenciador de Oficina — PBL POO · Tema 8

Sistema de gerenciamento de ordens de serviço e orçamento para oficinas mecânicas, desenvolvido como projeto de Problem-Based Learning (PBL) na disciplina de Programação Orientada a Objetos.

---

## Equipe

| Papel | Integrante | Responsabilidade |
|-------|-----------|-----------------|
| A1 | Luis | Domínio DDD — Entidades, Value Objects e States |
| A2 | Denisson | Testes — TDD com JUnit 5 |
| A3 | Samir | Casos de Uso (Application Layer) |
| A4 | Guilherme | Infraestrutura e integração |

---

## Sobre o Projeto

O sistema modela o fluxo completo de uma oficina mecânica — do check-in do veículo até a finalização do serviço — aplicando os princípios de **Domain-Driven Design (DDD)**, **Programação Orientada a Objetos** e o **padrão State** para controle de estados da Ordem de Serviço.

### Fluxo principal

```
Check-in do veículo
      ↓
Abertura da O.S.  →  [PENDENTE]
      ↓
Adição de peças e serviços ao orçamento  →  [AGUARDANDO_APROVACAO]
      ↓
Aprovação pelo cliente  →  [APROVADA]  ou  [RECUSADA]
      ↓
Execução do serviço  →  [EM_EXECUCAO]
      ↓
Finalização  →  [CONCLUIDA]
```

---

## Arquitetura

O projeto segue a arquitetura em camadas do DDD:

```
src/
├── domain/                         # Núcleo do negócio (sem dependências externas)
│   ├── entities/                   # Entidades com identidade e ciclo de vida
│   │   ├── OrdemDeServico.java
│   │   ├── Veiculo.java
│   │   └── Orcamento.java
│   ├── valueobjects/               # Objetos imutáveis definidos pelo seu valor
│   │   ├── Placa.java              # Valida formatos antigo (ABC-1234) e Mercosul (ABC1D23)
│   │   ├── ItemOrcamento.java
│   │   ├── Quilometragem.java
│   │   └── ValorMonetario.java
│   ├── states/                     # State Pattern — transições de status da O.S.
│   │   ├── PendenteState.java
│   │   ├── OrcamentoState.java
│   │   ├── AprovadoState.java
│   │   ├── RejeitadoState.java
│   │   └── ConcluidoState.java
│   ├── repositories/               # Contratos (interfaces) de persistência
│   │   ├── IOrdemDeServicoRepository.java
│   │   └── IVeiculoRepository.java
│   └── services/                   # Contratos de serviços externos
│       └── INotificacaoWhatsApp.java
│
├── application/                    # Casos de uso — orquestra o domínio
│   └── usecases/
│       ├── AbrirOrdemDeServico.java
│       ├── AdicionarPecaAoOrcamento.java
│       ├── AprovarOrcamento.java
│       └── FinalizarServico.java
│
├── infrastructure/                 # Implementações concretas (adapters)
│   ├── repositories/
│   │   ├── OrdemDeServicoRepositorioMemoria.java
│   │   └── VeiculoRepositorioMemoria.java
│   └── notifications/
│       └── FakeWhatsappNotificacao.java
│
└── tests/                          # Testes TDD com JUnit 5
    ├── PlacaTest.java
    ├── VeiculoTest.java
    ├── OrdemDeServicoTest.java
    ├── OrdemDeServicoTriagemTest.java
    ├── OrcamentoTest.java
    ├── ItemDeOrcamentoTest.java
    ├── QuilometragemTest.java
    ├── ValorMonetarioTest.java
    ├── AbrirOrdemDeServicoTest.java
    ├── AdicionarPecasAoOrcamentoTest.java
    ├── AprovarOrcamentoEFinalizarServicoTest.java
    ├── RepositoriosMemoriaTest.java
    └── FakeWhatsappNotificacaoTest.java
```

---

## Padrões e Conceitos Aplicados

| Conceito | Onde é aplicado |
|----------|----------------|
| **DDD — Bounded Contexts** | Triagem/Check-in, Orçamentação, Execução do Serviço |
| **Entidades** | `OrdemDeServico`, `Veiculo`, `Orcamento` |
| **Value Objects** | `Placa`, `ItemOrcamento`, `Quilometragem`, `ValorMonetario` |
| **State Pattern** | Estados da O.S.: Pendente → Orçamento → Aprovado/Rejeitado → Concluído |
| **Repository Pattern** | Interfaces no domínio, implementações na infra |
| **TDD** | Todos os testes escritos antes ou junto com a implementação |
| **DTO (Input/Output)** | Cada caso de uso encapsula entrada e saída em classes estáticas internas |

---

## Tecnologias

- **Java 17**
- **Maven** — gerenciamento de build e dependências
- **JUnit 5** (jupiter-api, jupiter-engine, jupiter-params) — testes
- **Mockito 5** — mocks nos testes de casos de uso
- **GitHub Actions** — CI configurado para rodar `mvn test` a cada push

---

## Como executar os testes

O projeto não possui `main` — a entrega são os testes passando. Para rodá-los:

**Via terminal (Maven):**
```bash
# na raiz do projeto, onde está o pom.xml
mvn test
```

**Via IntelliJ IDEA:**
- Clique com botão direito na pasta `tests` → **Run All Tests**
- Ou abra qualquer arquivo de teste e clique no ▶ verde ao lado da classe
- Atalho: `Ctrl+Shift+F10` (Windows/Linux) · `Ctrl+R` (macOS)

---

## Regras de Negócio Implementadas

**Abertura de O.S.**
- O veículo deve estar previamente cadastrado
- Não pode existir outra O.S. aberta para o mesmo veículo

**Orçamento**
- Itens só podem ser adicionados enquanto a O.S. estiver em diagnóstico
- A mesma peça (por ID) não pode ser adicionada duas vezes

**Aprovação**
- Só é possível aprovar ou recusar uma O.S. no status `AGUARDANDO_APROVACAO`
- O cliente é notificado via WhatsApp em ambos os casos
- Falha na notificação não reverte a transição de estado

**Finalização**
- Se a O.S. está `APROVADA`, a execução é iniciada automaticamente antes de finalizar
- O cliente é notificado ao término do serviço

**Placa (Value Object)**
- Aceita formato antigo: `ABC-1234`
- Aceita formato Mercosul: `ABC1D23`
- Rejeita nulo, vazio, letras minúsculas, espaços e formatos inválidos

---

## Arquivos de configuração

| Arquivo | Função |
|---------|--------|
| `pom.xml` | Configuração Maven — dependências, compilador, Surefire |
| `project-meta.json` | Metadados do projeto (nome, tema, equipe, CI) |
| `.github/workflows/` | Pipeline GitHub Actions |
