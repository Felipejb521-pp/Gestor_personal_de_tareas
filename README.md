[readme_md.md](https://github.com/user-attachments/files/35236632/readme_md.md)
# Gestor Personal de Tareas

Aplicación Android nativa desarrollada en **Kotlin** con **Jetpack Compose** para la gestión personal de tareas diarias. Permite crear, editar, filtrar, compartir e importar tareas de ejemplo desde una API externa, manteniendo la persistencia local de los datos.

---

## Características principales

* **Gestión completa de tareas (CRUD):** Creación, edición, listado, marcado como completada y eliminación con diálogo de confirmación (`AlertDialog`).
* **Validación de formularios:** Control de entrada en tiempo real que exige un mínimo de 3 caracteres reales (`.trim()`) para el título, selector desplegable de prioridad (`Alta`, `Media`, `Baja`) y estado (`Pendiente` / `Completada`).
* **Filtro persistente:** Interruptor (`Switch`) para mostrar únicamente las tareas pendientes, recordando la selección del usuario entre sesiones mediante **Preferences DataStore**.
* **Importación desde API externa:** Conexión con el servicio REST de *JSONPlaceholder* (`/todos`) mediante **Retrofit** para descargar tareas de ejemplo e importarlas directamente a la base de datos local con un clic.
* **Compartir tareas:** Envío rápido de los detalles de cualquier tarea (título, prioridad y estado) a otras aplicaciones del dispositivo mediante el selector nativo de Android (`Intent.ACTION_SEND`).

---

## Arquitectura y Tecnologías

El proyecto sigue el patrón arquitectónico **MVVM (Model-View-ViewModel)** con una interfaz declarativa y navegación imperativa basada en estados (`mutableStateOf` / `rememberSaveable`) sin librerías externas de navegación.

* **Lenguaje:** Kotlin
* **Interfaz de Usuario:** Jetpack Compose + Material Design 3
* **Arquitectura:** MVVM (`TareaViewModel` con corrutinas en `viewModelScope`)
* **Base de datos local:** Room (`TareaEntity`, `TareaDAO`, `TareaDataBase` Singleton) con procesamiento mediante **KSP**
* **Preferencias de usuario:** Jetpack Preferences DataStore (`PreferenciasManager`)
* **Consumo de API REST:** Retrofit 2 + Gson Converter (`TareaApiService`)

---

## Estructura del Proyecto

```text
app/src/main/java/com/example/gestor_personal_de_tareas/
├── data/
│   └── TareaDataBase.kt       # Instancia Singleton de la base de datos Room
├── pantallas/
│   ├── PantallaApi.kt          # Vista para consultar e importar tareas desde la API REST
│   ├── PantallaFormulario.kt   # Vista para la creación y edición de tareas con validaciones
│   └── PantallaPrincipal.kt    # Vista principal con listado, filtro por estado y acciones
├── MainActivity.kt            # Punto de entrada y gestión del flujo de navegación entre pantallas
├── PreferenciasManager.kt     # Gestión de preferencias persistentes con DataStore
├── TareaApiService.kt         # Interfaz Retrofit y DTO para consumo de datos externos
├── TareaDAO.kt                # Interfaz de acceso a datos SQL para Room
├── TareaEntity.kt             # Modelo de datos / Entidad de la tabla "tareas"
└── TareaViewModel.kt          # ViewModel para la lógica de negocio y control del estado de UI
```

---

## Requisitos e Instalación

1. **Requisitos previos:**
   * Android Studio (compatible con Kotlin integrado y KSP).
   * JDK 11 o superior.
   * Dispositivo físico o emulador con **Android 7.0 (API 24)** o superior.

2. **Pasos para ejecutar el proyecto:**
   1. Clona o descarga este repositorio en tu equipo.
   2. Ábrelo desde **Android Studio** (`File > Open`).
   3. Sincroniza las dependencias con **Gradle Sync**.
   4. Ejecuta la aplicación mediante **Run (`Shift + F10`)** en tu emulador o dispositivo físico.

## Capturas
<img width="375" height="817" alt="Captura de pantalla 2026-11-22 170345" src="https://github.com/user-attachments/assets/2214ffc2-3088-41f7-834e-b32e51431428" />

<img width="386" height="822" alt="Captura de pantalla 2026-11-22 164512" src="https://github.com/user-attachments/assets/138f42bb-e5b1-454f-a2c3-0c20f098f8a2" />

<img width="368" height="831" alt="Captura de pantalla 2026-11-22 163554" src="https://github.com/user-attachments/assets/180d49d0-6873-45d8-841f-139a14aaafcf" />
<img width="375" height="817" alt="Captura de pantalla 2026-11-22 170445" src="https://github.com/user-attachments/assets/ef699209-9a6c-4694-bc65-8f8782594d05" />

