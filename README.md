## Descripción General

ReparaFácil permite que clientes soliciten reparaciones y técnicos visualicen servicios disponibles. Cada usuario tiene un rol específico (cliente o técnico) que determina el flujo de la aplicación.

-----

## Stack Tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose
- **Arquitectura:** MVVM (ViewModel + StateFlow)
- **Persistencia:** DataStore Preferences
- **API REST:** Retrofit + Gson
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
- **HomeTecnicoScreen:** Servicios disponibles
- **PerfilScreen:** Datos personales e imagen de perfil

### Formularios Validados

- Email con formato válido
- Password mínimo 6 caracteres
- Feedback visual de errores bajo cada campo

### Persistencia Local

- **DataStore:** Token de sesión y datos de usuario.
- **Imagen de perfil:** URI guardada localmente por ID de usuario, persiste entre sesiones.
- **Cerrar sesión:** Limpia solo los datos de la sesión activa, preservando datos de perfiles (fotos).

### Recursos Nativos

- **Cámara:** Capturar foto de perfil
- **Galería:** Seleccionar imagen existente
- **Permisos:** Solicitados con Accompanist Permissions

### Gestión de Estado

- **Loading:** Indicador de carga visible
- **Success:** Datos mostrados correctamente
- **Error:** Mensajes de error con opción reintentar
- **Empty:** Estado vacío con instrucciones

### Consumo de API

- Autenticación: `/auth/login`, `/auth/signup`
- Perfil: `GET /auth/me`
- Servicios: `GET /servicios`
- Manejo automático de errores 400/401/500
- Token Bearer en headers

-----

## Cómo Ejecutar

### Requisitos

- Android Studio (Jellyfish o superior)
- JDK 11+
- Emulador o dispositivo Android 12+

### Pasos

1.  Abre el proyecto en Android Studio
2.  Sincroniza Gradle: `File → Sync Now`
3.  Crea un emulador: `Tools → Device Manager → Create Virtual Device`
4.  Ejecuta: `Run → Run 'app'` o `Ctrl+R`

-----

## Credenciales para Pruebas

```
Email: usuario@example.com
Password: password123
Rol: cliente
```

O crea una nueva cuenta durante el registro.

-----

## Diseño Visual

- **Material Design 3** con colores consistentes
- **Componentes reutilizables** (Loading, Error, Empty screens)
- **Transiciones suaves** entre pantallas (fade, slide)
- **Tipografía escalable** con estilos predefinidos
- **Espaciado uniforme** basado en Material Design

-----

## API Base

```
https://x8ki-letl-twmt.n7.xano.io/api:Rfm_61dW/
```

### Endpoints Principales

| Método | Endpoint | Autenticación |
|--------|----------|---|
| POST | `/auth/login` | No |
| POST | `/auth/signup` | No |
| GET | `/auth/me` | Sí |
| GET | `/servicios` | Sí |

-----

## Flujo de Usuario

### Cliente

1.  Login → HomeCliente
2.  Visualiza sus servicios solicitados
3.  Perfil: ver/editar datos y foto

### Técnico

1.  Login → HomeTecnico
2.  Visualiza servicios disponibles
3.  Perfil: ver datos y foto

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
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

// Persistencia
implementation 'androidx.datastore:datastore-preferences:1.0.0'

// UI
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'
implementation 'io.coil-kt:coil-compose:2.5.0'

// Navegación
implementation 'androidx.navigation:navigation-compose:2.7.7'

// Otros
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0'
implementation 'com.google.accompanist:accompanist-permissions:0.32.0'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
```

-----

## Notas

- La aplicación automáticamente redirige según el rol del usuario (cliente vs técnico)
- Los datos se persisten en DataStore entre reinicios
- Las fotos se guardan localmente y se recuperan con Coil
- Todos los formularios validan antes de enviar
- Los errores se muestran con transiciones suaves

-----

## Autor

Grupo 8 - Proyecto ReparaFácil - Rodrigo Martínez Becker & Vincent Farenden