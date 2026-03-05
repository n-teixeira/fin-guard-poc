# Casos de Uso — FinGuard

> Documentação dos casos de uso do sistema FinGuard, com referência aos testes que os validam.

---

## Visão Geral

O FinGuard permite que usuários mantenham um controle de fluxo de caixa pessoal em tempo real, extraindo dados de transações a partir de notificações push de apps bancários — sem depender de Open Finance ou APIs bancárias.

---

## UC01 — Processar Notificação Bancária

**Descrição:** Sistema recebe uma notificação bancária interceptada pelo app mobile e extrai os dados da transação (valor, tipo, origem) para persistir e atualizar o fluxo de caixa.

### Ator Principal
- App mobile (Android Notification Listener)

### Pré-condições
- Usuário ativou permissão de leitura de notificações
- App bancário está na lista de pacotes permitidos
- API está disponível

### Fluxo Principal
1. Mobile intercepta notificação do banco (`NotificationListenerService`)
2. App envia payload para `POST /api/v1/notifications/process` com `content` e `packageName`
3. Sistema aplica regex para extrair:
   - **Valor:** padrões `R$ X,XX`, `valor: R$ X,XX`, `X reais`
   - **Tipo:** PIX recebido/enviado, transferência, pagamento, crédito/débito
   - **Origem:** `de X`, `para X`, `origem: X`
4. Transação é persistida
5. Fluxo de caixa do usuário é atualizado (ou criado se inexistente)
6. API retorna a transação processada

### Fluxo Alternativo — Conteúdo inválido
- 3a. Se conteúdo for vazio ou não contiver valor: API retorna `400 Bad Request` com `IllegalArgumentException`

### Pós-condições
- Transação salva no repositório
- CashFlow do usuário atualizado com novo saldo

### Tipos de transação reconhecidos

| Tipo        | Palavras-chave (regex)                         | Efeito no saldo |
|-------------|-------------------------------------------------|-----------------|
| PIX_IN      | pix recebido, entrada pix                       | + valor         |
| PIX_OUT     | pix enviado, saída pix                          | - valor         |
| TRANSFER_IN | transferência recebida, transferência entrada   | + valor         |
| TRANSFER_OUT| transferência enviada, transferência saída      | - valor         |
| PAYMENT     | pagamento, débito, cartão, compra               | - valor         |
| CREDIT      | crédito, depósito, recebido                     | + valor         |
| DEBIT       | débito, saque, retirada                         | - valor         |
| UNKNOWN     | não reconhecido                                 | neutro          |

### Testes relacionados
- `ProcessNotificationServiceTest.shouldProcessNotificationAndUpdateCashFlow`
- `ProcessNotificationServiceTest.shouldThrowWhenExtractionFails`
- `NotificationControllerTest.shouldProcessNotification`
- `RegexTransactionExtractorTest.shouldExtractPixReceived`
- `RegexTransactionExtractorTest.shouldExtractPayment`
- `RegexTransactionExtractorTest.shouldReturnEmptyForBlankContent`
- `RegexTransactionExtractorTest.shouldReturnEmptyWhenNoAmountFound`

---

## UC02 — Obter Fluxo de Caixa

**Descrição:** Usuário (ou app) consulta o fluxo de caixa atual: saldo, total de créditos e total de débitos.

### Ator Principal
- Usuário via app mobile ou integração HTTP

### Pré-condições
- Nenhuma (usuário pode não ter transações ainda)

### Fluxo Principal
1. Cliente envia `GET /api/v1/cash-flow` com header opcional `X-User-Id`
2. Sistema busca CashFlow do usuário no repositório
3. Se existe: retorna `balance`, `totalCredits`, `totalDebits`, `userId`
4. Se não existe: retorna fluxo vazio (todos zeros)

### Pós-condições
- Nenhuma alteração de estado

### Cálculo do saldo
- **Créditos** (PIX_IN, TRANSFER_IN, CREDIT): somados em `totalCredits`
- **Débitos** (PIX_OUT, TRANSFER_OUT, PAYMENT, DEBIT): somados em `totalDebits`
- **Saldo:** `totalCredits - totalDebits`

### Testes relacionados
- `CashFlowTest.shouldCreateEmptyCashFlow`
- `CashFlowTest.shouldApplyCreditTransaction`
- `CashFlowTest.shouldApplyDebitTransaction`
- `GetCashFlowServiceTest` (a ser criado)
- `CashFlowControllerTest` (a ser criado)
- `cash-flow.service.spec.ts` / `api.service.spec.ts` (getCashFlow)

---

## UC03 — Listar Transações

**Descrição:** Usuário consulta o histórico de transações processadas.

### Ator Principal
- Usuário via app mobile ou integração HTTP

### Pré-condições
- Nenhuma

### Fluxo Principal
1. Cliente envia `GET /api/v1/transactions` com header opcional `X-User-Id`
2. Sistema busca todas as transações do usuário
3. Retorna lista ordenada (por data de criação, mais recente primeiro — conforme implementação)
4. Cada item inclui: `id`, `amount`, `type`, `origin`, `rawContent`, `createdAt`, `bankIdentifier`

### Fluxo Alternativo — Sem transações
- 2a. Retorna lista vazia `[]`

### Pós-condições
- Nenhuma alteração de estado

### Testes relacionados
- `ListTransactionsServiceTest` (a ser criado)
- `TransactionControllerTest` (a ser criado)
- `api.service.spec.ts` (getTransactions)

---

## Diagrama de Dependência (Casos de Uso ↔ Camadas)

```
┌─────────────────────────────────────────────────────────────────┐
│  REST Controllers                                                │
│  NotificationController │ CashFlowController │ TransactionController
└─────────────────────────┼───────────────────┼───────────────────┘
                          │                   │
┌─────────────────────────┼───────────────────┼───────────────────┐
│  Use Cases              ▼                   ▼                   │
│  ProcessNotificationUseCase │ GetCashFlowUseCase │ ListTransactionsUseCase
└─────────────────────────────┼───────────────────┼───────────────────┘
                              │                   │
┌─────────────────────────────┼───────────────────┼───────────────────┐
│  Ports (out)                 ▼                   ▼                   │
│  TransactionExtractionPort │ TransactionPersistencePort │ CashFlowPersistencePort
└─────────────────────────────┼───────────────────┼───────────────────┘
```

---

## Resumo — Testes por Caso de Uso

| UC  | Descrição                | Testes Backend                                              | Testes Mobile          |
|-----|--------------------------|-------------------------------------------------------------|------------------------|
| UC01| Processar notificação    | ProcessNotificationService, NotificationController, Extractor| ApiService.processNotification |
| UC02| Obter fluxo de caixa     | CashFlow (domain), GetCashFlowService, CashFlowController    | CashFlowService, ApiService |
| UC03| Listar transações        | ListTransactionsService, TransactionController              | ApiService.getTransactions |

---

## Como rodar os testes

```bash
# Backend
cd backend && mvn test

# Mobile
cd mobile && npm run test
```
