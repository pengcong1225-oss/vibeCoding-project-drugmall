# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**DrugMall** is an online pharmaceutical e-commerce platform with patient consultation, prescription management, AI health assistant, and IM chat features.

## Monorepo Structure

```
DrugMall/
├── frontend/                  # Patient-facing SPA (Vue 3)
├── frontend-doctor/           # Doctor-facing SPA (Vue 3)
├── backend/                   # Main API server (Spring Boot 3.2, Java 17)
├── drugmall-admin/            # Admin panel SPA
├── drugmall-admin-backend/    # Admin API server (Spring Boot 3.2)
├── docs/                      # Product docs, design docs, test docs
├── sql/                       # Database migration scripts
└── test_screenshots/
```

## Commands

### Patient Frontend (`frontend/`)

```bash
cd frontend
npm install
npm run dev            # Vite dev server at http://localhost:3003
npm run build          # Production build (vue-tsc + vite)
npm run lint           # ESLint
npm run test           # Vitest unit tests
npm run type-check     # vue-tsc --noEmit
```

### Doctor Frontend (`frontend-doctor/`)

```bash
cd frontend-doctor
npm install
npm run dev
npm run build
```

### Main Backend (`backend/`)

```bash
cd backend
mvn clean install
mvn spring-boot:run    # Starts on http://localhost:8080, context-path /api
```

API docs: `http://localhost:8080/doc.html` (Knife4j) or `http://localhost:8080/swagger-ui.html`

### Admin Backend (`drugmall-admin-backend/`)

```bash
cd drugmall-admin-backend
mvn clean install
mvn spring-boot:run
```

## Architecture

### Backend (`backend/`)

Standard layered architecture under `com.drugmall`:

- **`controller/`** — REST controllers. Key controllers: `DrugController`, `OrderController`, `PrescriptionController`, `ConsultationController`, `PatientController`, `DoctorController`, `AIAssistantController`, `SearchController`, `UserController`, `CartController`, `AddressController`, `StoreController`, `HomeController`. Doctor-side: `PatientConsultationController`, `PatientPrescriptionController`, `IncomeController`, `BusinessDataController`.
- **`service/` + `service/impl/`** — Business logic layer
- **`mapper/`** — MyBatis Plus mappers (XML in `resources/mapper/**/*.xml`)
- **`entity/`** — 42 entity classes including `Drug`, `Order`, `User`, `Doctor`, `Patient`, `Consultation`, `Prescription`, `Store`, `CartItem`, `Coupon`, `Refund`, `Address`, etc.
- **`dto/`** / **`vo/`** — Data transfer objects and view objects
- **`common/`** — `Result<T>` (unified response: code/message/data/timestamp), `ResultCode` enum (error codes grouped by domain: 1xxx user, 2xxx drug, 3xxx cart, 4xxx order, 5xxx address, 6xxx prescription, 7xxx doctor/IM), `GlobalExceptionHandler` (@RestControllerAdvice), `BusinessException`, `UserContext`
- **`config/`** — `MockDataService` (toggle via `drugmall.mock.enabled`), `WebConfig` (CORS), `MybatisPlusConfig`, `RestTemplateConfig`, `AIConfig`
- **`im/`** — Tencent Cloud IM integration: `IMController`, `IMService`, `IMUserSigService`, `TencentIMRestService`. Real/mock mode controlled by `tencent.im.mock-mode`.

Global path prefix: `/api` (defined in `server.servlet.context-path`). All endpoints are `/api/**`.

### Patient Frontend (`frontend/`)

- **Vite** dev server port 3003, proxies `/api` to `localhost:8080`
- **Path aliases**: `@` → `src/`, `#` → `types/`
- **SCSS**: `@/styles/variables` auto-injected into all components
- **Router** (`src/router/index.ts`): `MainLayout` wraps 4 tab routes (Home, Category, Cart, User). Separate routes for drug detail, order flow, prescription, inquiry/chat, search, doctor detail, AI assistant, patient management, etc. Navigation guard checks token for auth-required routes.
- **Stores** (Pinia): `user`, `cart`, `home`, `im`, `order`, `prescription`
- **API layer** (`src/api/`): Axios-based. `request.ts` (interceptors), `index.ts`, `modules/` (domain-specific API modules). Import pattern: `@/api/modules/<domain>`.
- **IM**: `tim-js-sdk` (Tencent Cloud IM) managed via `imSDK` utility with real/Mock dual mode, orchestrated through `stores/im.ts`
- **Views** organized by domain: `home/`, `drug/`, `order/`, `inquiry/`, `prescription/`, `cart/`, `user/`, `patient/`, `doctor/`, `search/`, `ai-assistant/`, `store/`, `address/`, `category/`, `login/`, etc.

### Mock Data

The project has a two-level mock system:
1. Backend `drugmall.mock.enabled` — when true, controllers return mock data from `MockDataService` instead of hitting the database
2. Frontend `stores/im.ts` — IM SDK has real/Mock dual mode, Mock queries backend APIs; real mode uses TIM SDK directly

## Key Business Domains

- **Drugs & Stores**: Product catalog, categories, store inventory, reviews, FAQs
- **Orders**: Cart → confirm → pay flow, order status lifecycle
- **Prescriptions**: Electronic prescriptions, doctor application flow, patient management
- **Consultations (Inquiry)**: Doctor search → triage → prepay → waiting → IM chat flow
- **AI Assistant**: Baichuan LLM-powered health Q&A (see `ai.baichuan.*` config)
- **IM**: Tencent Cloud TIM for doctor-patient real-time messaging

## Git Workflow (from AGENTS.md)

- **All git add/commit/push must go through the `devops-architect` agent**, not executed directly
- Commit messages follow Conventional Commits: `feat:` / `fix:` / `docs:` / `refactor:` / `test:` / `chore:`
- Branch strategy: `master` (production) → `develop` (integration) → `feature/xxx` | `bugfix/xxx` | `hotfix/xxx`

## Pharmaceutical Compliance

When modifying features involving prescriptions, user health data, or drug recommendations, the `compliance-checker` agent must be consulted. Key rules: prescription drugs require prescription review, all drug purchases require real-name verification, user health data must be encrypted, and electronic prescriptions must connect to licensed doctors.
