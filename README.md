# Gestor Personal de Tareas

Aplicación Android nativa desarrollada en **Kotlin** con **Jetpack Compose** para la gestión personal de tareas diarias. Permite crear, editar, filtrar, compartir e importar tareas de ejemplo desde una API externa, manteniendo la persistencia local de los datos.

---

## Características principales

* **Gestión completa de tareas (CRUD):** Creación, edición, listado, marcado como completada y eliminación con diálogo de confirmación (`AlertDialog`).
* **Validación de formularios:** Control de entrada en tiempo real que exige un mínimo de 3 caracteres reales (aplicando `.trim()`) para el título, selector desplegable de prioridad (`Alta`, `Media`, `Baja`) y estado (`Pendiente` / `Completada`).
* **Filtro persistente:** Interruptor (`Switch`) para mostrar únicamente las tareas pendientes, recordando la selección del usuario entre sesiones mediante **Preferences DataStore**.
* **Importación desde API externa:** Conexión con el servicio REST de *JSONPlaceholder* (`/todos`) mediante **Retrofit** para descargar tareas de ejemplo e importarlas directamente a la base de datos local con un clic.
* **Compartir tareas:** Envío rápido de los detalles de cualquier tarea (título, prioridad y estado) a otras aplicaciones del dispositivo mediante el selector nativo de Android (`Intent.ACTION_SEND`).

---

## Arquitectura y Tecnologías

El proyecto sigue el patrón arquitectónico **MVVM (Model-View-ViewModel)** con una interfaz declarativa y navegación imperativa basada en estados (`mutableStateOf` / `rememberSaveable`) sin librerías externas de navegación.

* **Lenguaje:** Kotlin
* **Interfaz de Usuario:** Jetpack Compose + Material Design 3
* **Arquitectura:** MVVM (`TareaViewModel` con corrutinas en `viewModelScope`)
* **Base de datos local:** Room (`TareaEntity`, `TareaDAO`, `TareaDataBase` Singleton) con procesamiento de anotaciones mediante **KSP**
* **Preferencias de usuario:** Jetpack Preferences DataStore (`PreferenciasManager`)
* **Consumo de API REST:** Retrofit 2 + Gson Converter (`TareaApiService`)

---

## Estructura del Proyecto

```text
app/src/main/java/com/example/gestor_personal_de_tareas/
├── data/
│   └── TareaDataBase.kt       # Instancia Singleton de la base de datos Room
├── MainActivity.kt            # Punto de entrada y pantallas Compose (Principal, Formulario, API)
├── PreferenciasManager.kt     # Gestión de preferencias con DataStore
├── TareaApiService.kt         # Interfaz Retrofit y DTO para ejemplos externos
├── TareaDAO.kt                # Operaciones SQL (Insertar, Obtener, Actualizar, Eliminar)
├── TareaEntity.kt             # Entidad/Tabla "tareas" de Room
└── TareaViewModel.kt          # Lógica de negocio, estado de UI y conexión con DAO/DataStore
