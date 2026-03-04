# O Concurseiro - Android App

## Overview
Native Android application for Brazilian public examination (concursos públicos) preparation. Users can browse, filter, and solve practice questions from various institutions and boards.

## Tech Stack
- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose with Material 3
- **Networking**: Retrofit 3.0.0 + OkHttp 5.3.2
- **Local DB**: Room 2.7.1 (with KSP 2.0.21-1.0.28)
- **JSON**: Gson
- **Build**: Gradle (Kotlin DSL) with Version Catalogs
- **Min SDK**: 24 (Android 7.0) / **Target SDK**: 36

## Architecture
MVVM pattern with Clean Architecture inspiration:
- **UI Layer** (`ui/`): Jetpack Compose screens and components
- **ViewModel** (`ui/viewmodel/`): Business logic and UI state management
- **Data Layer** (`data/`): API communication, DTOs, repository, and mappers

## Project Structure
```
app/src/main/java/br/com/mauricio/oconcurseiro/
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt (Room singleton)
│   │   ├── RespostaDao.kt (DAO + DesempenhoStats)
│   │   └── RespostaEntity.kt (answer history entity)
│   ├── mapper/QuestaoMapper.kt
│   ├── model/
│   │   ├── FiltroParams.kt
│   │   └── Questao.kt (Questao, Alternativa, CatalogoItem)
│   ├── remote/
│   │   ├── ConcurseiroApi.kt
│   │   ├── Dtos.kt (PageResponse, QuestaoDto, CatalogoItemDto)
│   │   └── RetrofitClient.kt
│   └── repository/QuestaoRepository.kt
└── ui/
    ├── components/AppHeader.kt
    ├── navigation/AppNavigation.kt
    ├── screens/
    │   ├── filtro/FiltroScreen.kt
    │   ├── home/HomeScreen.kt
    │   └── questao/QuestaoScreen.kt
    ├── state/UiState.kt
    ├── theme/
    │   ├── Color.kt (semantic brand colors)
    │   ├── Theme.kt (Material 3 color scheme)
    │   └── Type.kt (typography scale)
    └── viewmodel/
        ├── HomeViewModel.kt
        └── QuestaoViewModel.kt
```

## Backend API
- **Repository**: github.com/carmaunt/concurseiro-api (Spring Boot / Java)
- **Base path**: `/api/v1/`
- **Main endpoints**:
  - `GET /api/v1/questoes` - Paginated questions with filters
  - `GET /api/v1/questoes/{id}` - Single question by ID
  - `GET /api/v1/catalogo/disciplinas` - List disciplines
  - `GET /api/v1/catalogo/bancas` - List boards
  - `GET /api/v1/catalogo/instituicoes` - List institutions
  - `GET /api/v1/catalogo/disciplinas/{id}/assuntos` - Subjects by discipline
  - `GET /api/v1/questoes/{questaoId}/comentarios` - List comments (params: page, size, ordenar=curtidas|recentes)
  - `POST /api/v1/questoes/{questaoId}/comentarios` - Create comment (body: autor, texto)
  - `POST /api/v1/comentarios/{id}/curtir` - Like a comment
  - `POST /api/v1/comentarios/{id}/descurtir` - Dislike a comment
- **Filter params**: texto, disciplina, disciplinaId, assunto, assuntoId, banca, bancaId, instituicao, instituicaoId, ano, cargo, nivel, modalidade

## Configuration
- Base URL is configured via `BuildConfig.BASE_URL` (debug: `http://192.168.10.20:8080/`, release: `https://api.oconcurseiro.com.br/`)
- `usesCleartextTraffic=true` in AndroidManifest.xml for HTTP traffic in debug
- Network timeouts: connect 15s, read 30s, write 30s

## Theme & Design System
- **Brand identity**: Navy blue (#2D3E50) + Cream (#F2E6D0) + Sage accent (#7D9B91) — derived from logo
- **Color tokens**: `BrandPrimary` (navy), `BrandPrimaryLight` (selected backgrounds), `BrandPrimaryDisabled` (muted), `BrandPrimaryBackground` (subtle), `BrandCream` (warm surfaces), `BrandAccent`/`BrandAccentLight` (sage green)
- **Semantic colors**: All defined in `Color.kt` — no hardcoded colors in screens
- **Typography**: Full scale defined in `Type.kt` (display, headline, title, body, label)
- **Dynamic color disabled**: App always uses brand theme regardless of Android 12+ wallpaper
- Theme wraps app via `OConcurseiroTheme` in MainActivity

## Key Patterns
- **Home screen**: HomeScreen loads stats (total questions, disciplinas, bancas, instituicoes counts) and local performance data (last 7 days) via HomeViewModel on startup; stats refresh when returning from questions
- **Answer history**: When user taps "Resolver", the answer is saved to Room DB (questaoId, disciplina, acertou, respostaSelecionada, gabarito, timestamp). Performance stats on Home screen reflect real data.
- **Shared ViewModel**: QuestaoViewModel is created at AppNavigation level and shared between QuestaoScreen and FiltroScreen
- **Catalog-based filtering**: FiltroScreen uses DropdownSelector components backed by catalog data from backend API (disciplinas, bancas, instituicoes, assuntos)
- **Reactive catalog loading**: LaunchedEffect restores selections when catalog data arrives asynchronously
- **Cascading filters**: Selecting a disciplina triggers loading of related assuntos; clearing disciplina clears assuntos
- **Error handling**: User-friendly error messages for network errors (UnknownHostException, SocketTimeoutException, HTTP codes), auto-retry for catalog failures, manual retry via `recarregar()`
- **Empty state**: Distinct UI for "no results" vs "error" states with actionable buttons
- **Comments**: Full comments system with like/dislike, sorting (most liked / most recent), pagination, and optimistic UI updates for likes. Backend files in `backend-changes/` directory need to be copied to the concurseiro-api Spring Boot project under `src/main/java/br/com/concurseiro/api/comentario/`. Backend package is `br.com.concurseiro.api` (not oconcurseiro). The backend's `ApiResponseEnvelopeAdvice` auto-wraps all `/api/v1/` responses in `{"success":true,"data":...}` format. Table auto-created by JPA ddl-auto=update.

## Permissions
- `android.permission.INTERNET` (required for Retrofit)
- `android.permission.ACCESS_NETWORK_STATE` (connectivity checks)

## Notes
- This is an Android project requiring Android SDK to build — cannot be compiled in Replit environment
- Single Activity architecture with Compose navigation
- Both ViewModels use AndroidViewModel for database access (Application context)
- Room database `concurseiro.db` stores answer history locally
