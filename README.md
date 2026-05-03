# O Concurseiro

Aplicativo Android nativo para preparação de concursos públicos brasileiros. Permite navegar, filtrar e resolver questões de provas reais, com acompanhamento de desempenho e histórico de respostas.

---

## Screenshots

> _Adicione capturas de tela do app aqui — sugestão: Home, Resolver Questão, Filtros e Desempenho._

---

## Funcionalidades

- **Banco de questões** — questões reais de concursos públicos brasileiros, carregadas via API
- **Filtros avançados** — filtre por banca, cargo, disciplina, assunto e ano
- **Resolução interativa** — selecione a alternativa, veja o gabarito e o comentário da questão
- **Comentários** — leia e publique comentários por questão com paginação
- **Histórico local** — todas as respostas salvas localmente via Room (funciona offline)
- **Desempenho** — total de questões resolvidas, acertos e erros dos últimos 7 dias
- **Radar de disciplinas** — top 5 disciplinas com aproveitamento e barra de progresso animada
- **Autenticação Google** — login com conta Google via Firebase Auth + Credential Manager
- **Skeleton loading** — animação shimmer enquanto os dados carregam
- **Pull-to-refresh** — arraste para recarregar a tela inicial

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin 2.0 + Java 17 |
| UI | Jetpack Compose + Material 3 |
| Navegação | Navigation Compose |
| ViewModel | Lifecycle ViewModel + Hilt |
| Injeção de dependência | Hilt (Dagger) |
| Rede | Retrofit 2 + OkHttp + Gson |
| Banco local | Room 2.7 |
| Autenticação | Firebase Auth + Google Credential Manager |
| Fonte | Inter (Google Fonts via `ui-text-google-fonts`) |
| Build | Gradle 8 + AGP 8 |
| Minificação | R8 + ProGuard (release) |

---

## Arquitetura

O projeto segue **MVVM** com separação clara de responsabilidades:

```
app/
└── src/main/java/br/com/mauricio/oconcurseiro/
    ├── data/
    │   ├── auth/           # Firebase Auth + Google Sign-In
    │   ├── local/          # Room: entidades, DAOs, banco
    │   ├── mapper/         # Conversão API → modelo local
    │   ├── model/          # Data classes da API
    │   ├── remote/         # Retrofit: ApiService, RetrofitClient
    │   └── repository/     # Repositórios (fonte única da verdade)
    ├── di/
    │   └── AppModule.kt    # Módulo Hilt com todas as dependências
    ├── ui/
    │   ├── components/     # Componentes reutilizáveis (AppHeader, etc.)
    │   ├── screens/
    │   │   ├── home/       # Tela inicial + skeletons
    │   │   ├── questao/    # Resolução de questões + comentários
    │   │   ├── filtro/     # Tela de filtros
    │   │   ├── auth/       # Tela de login
    │   │   └── splash/     # Splash screen
    │   ├── state/          # Classes de estado da UI
    │   ├── theme/          # Color.kt, Type.kt, Theme.kt
    │   └── viewmodel/      # HomeViewModel, QuestaoViewModel, etc.
    └── util/
        ├── DateUtils.kt    # Formatação de datas
        └── ErrorUtils.kt   # Mapeamento de erros de rede
```

---

## Pré-requisitos

- Android Studio Meerkat (2024.3) ou superior
- JDK 17
- Conta Firebase com projeto configurado
- Acesso à API backend (ver seção abaixo)

---

## Como rodar

### 1. Clone o repositório

```bash
git clone https://github.com/carmaunt/concurseiro-app.git
cd concurseiro-app
```

### 2. Configure o Firebase

- Acesse [console.firebase.google.com](https://console.firebase.google.com)
- Crie um projeto e adicione um app Android com o package `br.com.mauricio.oconcurseiro`
- Baixe o arquivo `google-services.json` e coloque em `app/`
- Habilite o provedor **Google** em Authentication → Sign-in method

### 3. Configure a URL da API

Em `app/build.gradle.kts`, ajuste o `buildConfigField` conforme seu ambiente:

```kotlin
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"http://SEU_IP:8080/\"")
    }
    release {
        buildConfigField("String", "BASE_URL", "\"https://sua-api.onrender.com/\"")
    }
}
```

> A API de produção pública está em `https://concurseiro-api-lnae.onrender.com/`

### 4. Rode o projeto

Abra no Android Studio e clique em **Run**, ou via terminal:

```bash
./gradlew installDebug
```

---

## Variáveis de build

| Campo | Descrição | Exemplo |
|---|---|---|
| `BASE_URL` | URL base da API REST | `https://concurseiro-api-lnae.onrender.com/` |

Configurado via `buildConfigField` no `app/build.gradle.kts` — sem necessidade de arquivo `.env`.

---

## API Backend

O app consome uma API REST Spring Boot. Endpoints principais:

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/questoes` | Lista questões paginadas com filtros |
| `GET` | `/questoes/{id}` | Busca questão por ID |
| `GET` | `/questoes/filtros` | Retorna bancas, cargos, disciplinas e anos disponíveis |
| `GET` | `/questoes/{id}/comentarios` | Lista comentários de uma questão |
| `POST` | `/questoes/{id}/comentarios` | Publica novo comentário |

Repositório do backend: _a adicionar_

---

## Status do projeto

**Em desenvolvimento ativo** — versão `1.0` (pré-lançamento)

### Concluído

- [x] Arquitetura MVVM + Hilt
- [x] Autenticação Google via Firebase
- [x] Listagem e resolução de questões
- [x] Filtros por banca, cargo, disciplina, assunto e ano
- [x] Comentários com paginação
- [x] Histórico local com Room
- [x] Desempenho semanal e total
- [x] Radar de disciplinas com progresso animado
- [x] Skeleton loading com animação shimmer
- [x] Pull-to-refresh
- [x] Tipografia Inter via Google Fonts
- [x] ProGuard + R8 para release
- [x] Java/Kotlin 17
- [x] Tratamento de erros de rede centralizado

### Roadmap

- [ ] Modo escuro
- [ ] Simulados cronometrados
- [ ] Notificações de estudo diário
- [ ] Favoritar questões
- [ ] Exportar desempenho em PDF
- [ ] Modo offline completo (cache de questões)
- [ ] Widget de progresso na tela inicial do Android
- [ ] Publicação na Google Play Store

---

## Contribuindo

1. Fork o repositório
2. Crie uma branch: `git checkout -b feature/minha-melhoria`
3. Commit: `git commit -m "feat: descrição da melhoria"`
4. Push: `git push origin feature/minha-melhoria`
5. Abra um Pull Request

---

## Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.
