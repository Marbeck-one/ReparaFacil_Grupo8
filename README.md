# ReparaFacil - Gestión de Servicios Técnicos 🛠️

## 👥 Integrantes
* **Vincent Farenden Cerón**
* **Rodrigo Martínez Becker**
* **Sección:** DSY1105
* **Equipo:** Grupo 8

-----

## Descripción General

ReparaFácil permite que clientes soliciten reparaciones y técnicos visualicen servicios disponibles. Cada usuario tiene un rol específico (cliente o técnico) que determina el flujo de la aplicación. La aplicación consume una API REST desplegada en Render y cuenta con validación de calidad mediante pruebas unitarias.

-----

## Stack Tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose (Material Design 3)
- **Arquitectura:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Persistencia:** DataStore Preferences
- **API REST:** Retrofit + Gson + OkHttp
- **Backend:** NestJS desplegado en Render
- **Testing:** JUnit + MockK + Coroutines Test (Cobertura > 80% en lógica)
- **Permisos:** Accompanist Permissions
- **Imágenes:** Coil Compose
- **Navegación:** Navigation Compose
- **Min SDK:** 33 | **Target SDK:** 36

-----

## Estructura del Proyecto

```
app/src/main/java/com/grupo8/reparafacil/
├── model/              (Usuario, Servicio, UiState)
├── network/            (Retrofit, ApiService)
├── data/               (DataStore Manager)
├── repository/         (Auth, Servicios)
├── viewmodel/          (Auth, Perfil, Servicios)
├── navigation/         (Rutas, eventos)
├── ui/screens/         (Pantallas principales)
├── ui/components/      (Loading, Error, Empty)
└── ui/theme/           (Colores, tipografía)
```

-----

## 🔗 Integración con Backend (API)

La aplicación se conecta a un servidor remoto desplegado en Render.

**Base URL:** `https://reparafacil-api.onrender.com/`

**Endpoints Principales:**
* `POST /api/auth/login`: Autenticación y obtención de Token JWT.
* `POST /api/auth/signup`: Registro de usuarios.
* `GET /api/reparacion`: Listado de servicios (filtrado por rol en backend).
* `POST /api/reparacion`: Creación de solicitudes.
* `PATCH /api/reparacion/{id}`: Actualización de estados por técnicos.

-----

## 🧪 Calidad y Pruebas Unitarias

El proyecto cumple con el estándar de calidad exigido, cubriendo más del **80% de la lógica de negocio** en los ViewModels mediante pruebas unitarias.

**Tecnologías usadas:**
* **JUnit 4:** Framework de pruebas.
* **MockK:** Para simular dependencias (Repositorios, Contexto, DataStore).
* **Kotlin Coroutines Test:** Para probar flujos asíncronos (`viewModelScope`).

**Cómo ejecutar los tests:**
1. En Android Studio, ir a la carpeta `app/src/test/java`.
2. Clic derecho sobre el paquete `com.grupo8.reparafacil`.
3. Seleccionar **"Run Tests in '...'"** o **"Run with Coverage"**.

-----

## 📦 Entregables (Build)

Se ha generado el ejecutable firmado para producción:
* **Archivo APK:** `app-release.apk` (Ubicado en la carpeta `release/` o raíz).
* **Firma:** `keystore.jks` (Llave de firma incluida en el repositorio).
* **Configuración:** El archivo `build.gradle.kts` incluye la configuración `signingConfigs` para generar el build automáticamente.

-----

## Funcionalidades Implementadas

### Autenticación
- Login con validación de email y password (≥6 caracteres)
- Registro de nuevos usuarios (cliente o técnico)
- Token JWT persistido en DataStore
- Sesión automática al reiniciar app

### Navegación
- **LoginScreen:** Acceso a la aplicación
- **RegistroScreen:** Crear nueva cuenta
- **HomeClienteScreen:** Lista de servicios solicitados
- **HomeTecnicoScreen:** Bandeja de entrada de servicios disponibles
- **SolicitudServicioScreen:** Formulario para pedir reparación
- **PerfilScreen:** Datos del usuario, cierre de sesión y foto

-----

## Animaciones de Transición

La aplicación implementa transiciones suaves entre pantallas:
- Fade In/Out: Al transicionar entre Login y Registro (300ms)
- Slide In/Out: Al navegar desde HomeCliente a Perfil (300ms)
- AnimatedVisibility: Mensajes de error y éxito aparecen con animación
- Transiciones automáticas: Navigation Compose maneja slide horizontal en pop back

-----

## Dependencias Principales

```kotlin
// API
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Persistencia
implementation("androidx.datastore:datastore-preferences:1.0.0")

// UI
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("io.coil-kt:coil-compose:2.5.0")

// Navegación
implementation("androidx.navigation:navigation-compose:2.7.7")

// Testing
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.10")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("androidx.arch.core:core-testing:2.2.0")

// Otros
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("com.google.accompanist:accompanist-permissions:0.32.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")