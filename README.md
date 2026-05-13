# Laser Chess - Cliente Móvil [![Android CI](https://github.com/gracehopper/laser_chess_cliente_movil/actions/workflows/android-ci.yml/badge.svg)](https://github.com/gracehopper/laser_chess_cliente_movil/actions/workflows/android-ci.yml)

Cliente Android del juego de mesa online Laser Chess.

## Estructura del proyecto

```
app/src/main/java/com/gracehopper/laserchessapp/
├── LaserChessApplication.kt          # Inicialización de la aplicación
├── data/
│   ├── manager/
│   │   ├── ActiveGameManager.kt      # Gestión del estado de la partida activa
│   │   ├── CurrentUserManager.kt     # Gestión del usuario en sesión
│   │   ├── GameTimerManager.kt       # Control de los temporizadores de partida
│   │   └── SseManager.kt            # Gestión de eventos SSE en tiempo real
│   ├── model/
│   │   ├── auth/                     # Modelos de autenticación (login, registro)
│   │   ├── game/                     # Modelos de partida (tablero, eventos, WS)
│   │   ├── notifications/            # Modelos de notificaciones push
│   │   ├── ranking/                  # Modelos del ranking de jugadores
│   │   ├── shop/                     # Modelos de la tienda de items
│   │   ├── social/                   # Modelos de amistades y solicitudes
│   │   └── user/                     # Modelos de perfil y cuenta de usuario
│   ├── remote/
│   │   ├── ApiService.kt             # Definición de endpoints REST
│   │   ├── NetworkUtils.kt           # Utilidades de red
│   │   ├── PersistentCookieJar.kt    # Gestión persistente de cookies
│   │   ├── TokenAuthenticator.kt     # Autenticación y refresco de tokens JWT
│   │   ├── fcm/                      # Servicio de mensajería Firebase (FCM)
│   │   └── websocket/                # Cliente WebSocket para partidas amistosas
│   └── repository/
│       ├── AuthRepository.kt         # Repositorio de autenticación
│       ├── ChallengeRepository.kt    # Repositorio de desafíos entre jugadores
│       ├── DeviceRepository.kt       # Repositorio de registro de dispositivo
│       ├── EventStatusRepository.kt  # Repositorio de estado de eventos SSE
│       ├── FriendRepository.kt       # Repositorio de amigos
│       ├── GameHistoryRepository.kt  # Repositorio de historial de partidas
│       ├── GameRepository.kt         # Repositorio de partidas activas
│       ├── ItemRepository.kt         # Repositorio de items de la tienda
│       ├── RankingRepository.kt      # Repositorio del ranking
│       └── UserRepository.kt        # Repositorio de datos de usuario
├── gameLogic/
│   ├── board/
│   │   ├── Board.kt                  # Modelo del tablero de juego
│   │   └── BoardParser.kt           # Parsing del estado del tablero
│   ├── laser/
│   │   └── LaserUtils.kt            # Cálculo del trayecto del láser
│   ├── move/
│   │   ├── CoordsConverter.kt        # Conversión de coordenadas
│   │   ├── Move.kt                   # Representación de movimientos
│   │   └── MoveParser.kt            # Parsing de movimientos
│   └── pieces/
│       ├── Piece.kt                  # Modelo de pieza del tablero
│       └── PieceType.kt             # Tipos de piezas disponibles
└── ui/
    ├── auth/                         # Pantallas de login y registro
    ├── customize/                    # Personalización del tablero
    ├── game/                         # Pantalla de partida y replays
    ├── gameConfig/                   # Configuración de partida (tablero, tiempo)
    ├── history/                      # Historial de partidas
    ├── home/                         # Pantalla principal
    ├── main/                         # Activity principal y navegación
    ├── notifications/                # Diálogo de desafíos pendientes
    ├── ranking/                      # Tabla de clasificación
    ├── settings/                     # Ajustes de la aplicación
    ├── shop/                         # Tienda de items y skins
    ├── social/                       # Gestión de amigos y partidas en curso
    ├── user/                         # Perfil propio y de otros jugadores
    └── utils/                        # Utilidades de UI (ranks, tiempo, items)
```

## Tecnologías

- **Lenguaje:** Kotlin
- **UI:** Android Views + Jetpack Compose (Material3)
- **Red:** Retrofit 2 + OkHttp 4 (REST, WebSocket, SSE)
- **Serialización:** Gson
- **Notificaciones push:** Firebase Cloud Messaging (FCM)
- **Imágenes y GIFs:** Glide
- **Tests:** JUnit 4 + Mockito

## Requisitos

- Android Studio Hedgehog o superior
- JDK 17
- Android SDK 36
- `minSdk` 24 (Android 7.0+)

## Puesta en marcha

```bash
# Clonar el repositorio
git clone https://github.com/gracehopper/laser_chess_cliente_movil.git
cd laser_chess_cliente_movil

# Ejecutar tests unitarios
./gradlew testDebugUnitTest

# Compilar APK debug
./gradlew assembleDebug
```

## CI/CD

El pipeline de GitHub Actions se ejecuta en cada push a las ramas principales y realiza los siguientes pasos:

1. Ejecutar tests unitarios
2. Ejecutar lint
3. Compilar APK debug
4. Publicar el APK como artefacto descargable