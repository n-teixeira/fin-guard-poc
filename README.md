# FinGuard | Interceptor de Eventos Bancários

> **⚠️ POC** — Projeto experimental para demonstração de conceito.

App mobile que intercepta push notifications de bancos via Android Notification Listener API, aplica **Regex Pattern Matching** para extrair valor, tipo e origem da transação, e mantém fluxo de caixa pessoal atualizado em tempo real — **sem depender de Open Finance ou APIs bancárias**.

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?logo=spring)
![Ionic](https://img.shields.io/badge/Ionic-7-3880FF?logo=ionic)
![Angular](https://img.shields.io/badge/Angular-17-DD0031?logo=angular)

---

## Configuração (MVP sem .env)

Para rodar localmente **não é necessário .env**:

- **Backend:** H2 em memória (dev) — zero config
- **Mobile:** `apiUrl` em `environment.ts` aponta para `http://localhost:8080`
- **Docker:** variáveis definidas no `docker-compose.yml`

Para publicar ou customizar, veja o arquivo `env.example` (opcional).

---

## Arquitetura

O projeto segue **Domain-Driven Design (DDD)** e **Clean Architecture**:

```
fin-guard/
├── backend/                    # API Spring Boot
│   └── br.com.finguard/
│       ├── domain/              # Entidades, Value Objects, Ports
│       ├── application/         # Use Cases, Services
│       └── infrastructure/      # Adapters (persistence, web, extractor)
├── mobile/                     # App Ionic + Angular
│   └── src/app/
│       ├── core/                # Domain, Infrastructure
│       └── pages/
└── mobile/plugins/             # Capacitor plugin Notification Listener
```

### Backend (DDD/Clean Arch)

| Camada | Responsabilidade |
|--------|------------------|
| **Domain** | `Transaction`, `CashFlow`, `TransactionType`, repositórios (ports) |
| **Application** | Use cases (`ProcessNotificationUseCase`, `GetCashFlowUseCase`) |
| **Infrastructure** | `RegexTransactionExtractor`, JPA adapters, REST controllers |

### Fluxo Principal

1. **Mobile** intercepta notificação via `NotificationListenerService` (Android)
2. Envia payload para **API** (`POST /api/v1/notifications/process`)
3. **RegexTransactionExtractor** extrai valor, tipo e origem
4. Transação é persistida e **CashFlow** atualizado em tempo real
5. App exibe fluxo de caixa via **RxJS** `BehaviorSubject`

---

## Requisitos

- Java 21 (mínimo 17 para Spring Boot 3)
- Node.js 20+
- Maven 3.8+
- Docker (opcional)
- Android Studio (para build mobile nativo)

---

## Execução Rápida

### Backend

```bash
cd backend
mvn spring-boot:run
```

API disponível em `http://localhost:8080`

### Mobile (Web)

```bash
cd mobile
npm install
npm run start
```

### Com Docker

```bash
docker-compose up -d
```

API em `http://localhost:8080`, PostgreSQL em `5432`.

---

## API REST

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/notifications/process` | Processa notificação e extrai transação |
| GET | `/api/v1/cash-flow` | Retorna fluxo de caixa do usuário |
| GET | `/api/v1/transactions` | Lista transações do usuário |

**Headers:** `X-User-Id` (opcional, default: `default-user`)

**Exemplo processar notificação:**

```bash
curl -X POST http://localhost:8080/api/v1/notifications/process \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-1" \
  -d '{"content":"Pix recebido de João Silva. Valor R$ 150,00","packageName":"com.nubank"}'
```

---

## Testes

Os testes cobrem os **casos de uso** documentados em [`docs/USE_CASES.md`](docs/USE_CASES.md).

### Backend

```bash
cd backend
mvn test
```

- `ProcessNotificationServiceTest` — UC01 (processar notificação)
- `GetCashFlowServiceTest` — UC02 (obter fluxo de caixa)
- `ListTransactionsServiceTest` — UC03 (listar transações)
- `RegexTransactionExtractorTest` — extração por regex (PIX, transferência, pagamento)
- `NotificationControllerTest`, `CashFlowControllerTest`, `TransactionControllerTest` — API REST

### Mobile

```bash
cd mobile
npm run test
```

- `ApiService` — processNotification, getCashFlow, getTransactions
- `CashFlowService` — integração com API

---

## CI/CD

- **CI** (`.github/workflows/ci.yml`): build e testes em push/PR
- **CD** (`.github/workflows/cd.yml`): build Docker em `main`/`master`

Para habilitar deploy, edite `cd.yml` e configure seu ambiente (K8s, ECS, etc.).

---

## Android Notification Listener

O plugin `@finguard/notification-listener` usa a **Notification Listener API** do Android:

1. Usuário ativa permissão em **Configurações > Apps > Acesso especial > Notificações**
2. App filtra apenas pacotes de bancos configurados
3. Notificações são encaminhadas para processamento

** Bancos suportados (exemplos):** Nubank, Itaú, Bradesco, BB, Santander, Inter, C6, Original, Safra, Banrisul.

---

## Padrões Regex (extrator)

- **Valor:** `R$\s*[\d.,]+`, `valor:\s*R$\s*[\d.,]+`
- **Tipo:** PIX recebido/enviado, transferência, pagamento, crédito/débito
- **Origem:** `de X`, `para X`, `origem: X`

Customize em `RegexTransactionExtractor.java` conforme necessidade.

---

## Publicação (quando quiser subir)

| Item | O que fazer |
|------|-------------|
| **Backend (API)** | Definir `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` no ambiente |
| **Mobile** | Alterar `apiUrl` em `environment.prod.ts` para a URL da sua API |
| **Auth** | Não há autenticação no POC — em produção seria necessário JWT/OAuth |
| **HTTPS** | Configurar certificado no servidor; mobile usar URL `https://` |

---

## Licença

MIT
