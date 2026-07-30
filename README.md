# O Concurseiro

Aplicativo Android nativo para preparação de concursos públicos brasileiros.

O projeto permite navegar, filtrar e resolver questões de concursos reais, acompanhar desempenho, histórico de respostas e interagir com comentários por questão.

---

## Stack Atual

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin 2.2.10 + Java 17 |
| UI | Jetpack Compose + Material 3 |
| Navegação | Navigation Compose |
| Arquitetura | MVVM + Modularização |
| Injeção de Dependência | Hilt |
| Rede | Retrofit 3 + OkHttp 5 + Gson |
| Banco Local | Room 2.7 |
| Autenticação | Firebase Auth + Credential Manager |
| Observabilidade | Firebase Analytics + Crashlytics |
| Build | Gradle Kotlin DSL + AGP 9 |
| Minificação | R8 + ProGuard |
| Segurança Local | AndroidX Security Crypto |

---

## Estrutura do Projeto

O projeto está modularizado em múltiplos módulos Android:

```text
:app
:core
:data
:domain
:feature-auth
:feature-questoes
:feature-comentarios
```

### Responsabilidades

| Módulo | Responsabilidade |
|---|---|
| `app` | Aplicação principal, navegação e inicialização |
| `core` | Componentes compartilhados, UI base e utilitários |
| `data` | Retrofit, Room, repositórios e fontes de dados |
| `domain` | Modelos e contratos de domínio |
| `feature-auth` | Fluxo de autenticação |
| `feature-questoes` | Resolução e listagem de questões |
| `feature-comentarios` | Comentários das questões |

---

## Funcionalidades

- Banco de questões consumido por API própria
- Filtros por banca, cargo, disciplina, assunto e ano
- Resolução interativa de questões
- Comentários por questão
- Histórico local via Room
- Desempenho semanal
- Radar de disciplinas
- Login Google via Firebase Auth
- Skeleton loading
- Pull-to-refresh
- Crashlytics
- Analytics

---

## Segurança

O aplicativo atualmente possui:

- HTTPS obrigatório
- Bloqueio de tráfego HTTP em claro
- Minificação R8 em release
- Shrink de recursos
- Assinatura release externa via `local.properties`
- Criptografia local via AndroidX Security Crypto
- Backup automático do banco SQLite desabilitado

---

## Configuração da API

A API utilizada atualmente:

```text
https://concurseiro-api-lnae.onrender.com/
```

Configurada via `buildConfigField`.

---

## Como executar

### Clone o projeto

```bash
git clone https://github.com/carmaunt/concurseiro-app.git
```

### Abra no Android Studio

Requisitos:

- Android Studio recente
- JDK 17
- SDK 36

### Configure Firebase

Adicionar:

```text
app/google-services.json
```

E habilitar:

- Firebase Authentication
- Login Google
- Firebase Analytics
- Firebase Crashlytics

---

## Build Release

A assinatura release utiliza informações do `local.properties`:

```properties
RELEASE_STORE_FILE=
RELEASE_STORE_PASSWORD=
RELEASE_KEY_ALIAS=
RELEASE_KEY_PASSWORD=
```

---

## Roadmap

- Modo escuro
- Simulados cronometrados
- Favoritar questões
- Cache offline completo
- Widget Android
- Estatísticas avançadas
- Publicação Play Store

---

## Status Atual

Projeto em desenvolvimento ativo.

O aplicativo já possui:

- modularização
- autenticação
- persistência local
- observabilidade
- arquitetura MVVM
- build release preparado
- integração com backend próprio

---

## Direitos autorais

Copyright © 2026 Mauricio. Todos os direitos reservados.

Este repositório é disponibilizado publicamente apenas para fins de demonstração e colaboração autorizada. Nenhuma parte do código pode ser copiada, modificada, redistribuída ou utilizada sem autorização expressa do autor.
