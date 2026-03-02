# O Concurseiro - Android App

## Overview
Native Android application for Brazilian public examination (concursos públicos) preparation. Users can browse, filter, and solve practice questions from various institutions and boards.

## Tech Stack
- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose with Material 3
- **Networking**: Retrofit 3.0.0 + OkHttp 5.3.2
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
    │   └── questao/QuestaoScreen.kt
    ├── theme/
    └── viewmodel/QuestaoViewModel.kt
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
- **Filter params**: texto, disciplina, disciplinaId, assunto, assuntoId, banca, bancaId, instituicao, instituicaoId, ano, cargo, nivel, modalidade

## Configuration
- Base URL is configured in `RetrofitClient.kt` (currently `http://192.168.10.20:8080/`)
- `usesCleartextTraffic=true` in AndroidManifest.xml for HTTP traffic

## Key Patterns
- **Shared ViewModel**: QuestaoViewModel is created at AppNavigation level and shared between QuestaoScreen and FiltroScreen
- **Catalog-based filtering**: FiltroScreen uses DropdownSelector components backed by catalog data from backend API (disciplinas, bancas, instituicoes, assuntos)
- **Reactive catalog loading**: LaunchedEffect restores selections when catalog data arrives asynchronously
- **Cascading filters**: Selecting a disciplina triggers loading of related assuntos; clearing disciplina clears assuntos

## Notes
- This is an Android project requiring Android SDK to build — cannot be compiled in Replit environment
- Single Activity architecture with Compose navigation
- No local database (Room) — relies entirely on remote API
