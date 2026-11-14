# Gestor Personal de Tareas — Planificación de Desarrollo

## Descripción del Proyecto

Aplicación Android denominada **Gestor Personal de Tareas**, desarrollada en **Kotlin** y **Jetpack Compose (Material 3)** utilizando una única `MainActivity` y navegación sencilla basada en estados. 

La aplicación permite gestionar tareas personales (crear, consultar, editar, marcar como completadas y eliminar) con persistencia local mediante **Room**, guardar la preferencia de filtrado con **Preferences DataStore**, consultar un listado remoto de tareas de ejemplo mediante **Retrofit** (`JSONPlaceholder`) y compartir tareas con otras aplicaciones instaladas mediante un `Intent` implícito.

---

## 📅 Resumen del Plan

| Día | Carga Estimada | Bloque Principal | Entregable Técnico |
| :--- | :--- | :--- | :--- |
| **Día 1 (Jueves)** | Baja (1 - 1.5 h) | Configuración y Modelo | Proyecto creado, dependencias Gradle, permiso `INTERNET` y entidad `TareaEntity`. |
| **Día 2 (Viernes)** | Baja (1.5 - 2 h) | Persistencia y Estado | Base de datos **Room** (`TareaDAO` + `TareaDataBase`), **DataStore** (`PreferenciasManager`) y `TareaViewModel`. |
| **Día 3 (Sábado)** | Alta (5 - 6 h) | Pantallas Compose y CRUD | Navegación por estados, listado con `LazyColumn`, `Switch` de filtro, formulario con validaciones y `AlertDialog` de borrado. |
| **Día 4 (Domingo)** | Media (3 h) | API REST y Compartir | Apartado «Tareas de ejemplo» con **Retrofit** (`GET /todos?userId=1`) e `Intent` implícito `ACTION_SEND`. |
| **Día 5 (Lunes)** | Media-Baja (2 h) | Pruebas, APK y Entrega | Ejecución de las 12 pruebas en 2 emuladores, generación del APK, capturas y `README.md`. |

---

## 🛠️ Desglose Diario Detallado

### Día 1 (Jueves): Cimientos y Configuración del Proyecto
> **Objetivo:** Dejar configurado el proyecto Android, las dependencias necesarias y el modelo de datos exacto que exige el enunciado.

- [ ] Crear el proyecto en Android Studio con la plantilla **Empty Activity (Jetpack Compose)**.
- [ ] Configurar las dependencias necesarias en `app/build.gradle.kts` (`libs.versions.toml`):
  - **Room:** `room-runtime`, `room-ktx` y `room-compiler` (KSP).
  - **Preferences DataStore:** `datastore-preferences`.
  - **Retrofit + Conversor JSON:** `retrofit` y `converter-gson` (o `converter-kotlinx-serialization`).
  - **ViewModel Compose:** `lifecycle-viewmodel-compose`.
- [ ] Declarar únicamente el permiso necesario en `AndroidManifest.xml`:
  - `<uses-permission android:name="android.permission.INTERNET" />`
- [ ] Organizar las carpetas/paquetes por responsabilidad (Modelo/Almacenamiento, Preferencias, Comunicaciones, Interfaz).
- [ ] Crear la entidad de Room `TareaEntity.kt` con los campos obligatorios:
  - `id`: Int (Clave primaria autogenerada).
  - `titulo`: String.
  - `descripcion`: String.
  - `prioridad`: String (*Baja*, *Media*, *Alta*).
  - `estado`: String (*Pendiente* / *Completada*).

---

### Día 2 (Viernes): Persistencia Local (Room + DataStore) y ViewModel
> **Objetivo:** Implementar el almacenamiento permanente de tareas y preferencias, y conectar ambos con el `ViewModel` sin bloquear el hilo principal.

- [ ] **Acceso a Datos (`TareaDAO.kt`):**
  - Definir la interfaz `@Dao` con operaciones `suspend` para consultar todas las tareas, insertar, actualizar y eliminar registros.
- [ ] **Base de Datos (`TareaDataBase.kt`):**
  - Crear la clase abstracta `@Database` implementando el patrón Singleton (`obtenerBaseDeDatos`).
- [ ] **Preferencias de Usuario (`PreferenciasManager.kt`):**
  - Configurar **Preferences DataStore** para guardar y leer un único booleano: `mostrar_solo_pendientes` (valor inicial `false`: mostrar todas las tareas).
- [ ] **Gestión de Estado (`TareaViewModel.kt`):**
  - Recibir `TareaDAO` y `PreferenciasManager`.
  - Exponer la lista de tareas y el estado del filtro mediante `mutableStateOf`.
  - Implementar las operaciones asíncronas con `viewModelScope.launch`: cargar tareas, cargar/guardar preferencia, filtrar tareas visibles, crear, editar, cambiar estado completado y eliminar.
- [ ] **Comprobación inicial:** Instanciar `TareaDataBase`, `PreferenciasManager` y `TareaViewModel` en `MainActivity.kt` y verificar que compila sin errores.

---

### Día 3 (Sábado): Interfaz en Jetpack Compose y Lógica CRUD
> **Objetivo:** Construir todas las pantallas de gestión de tareas personales en `MainActivity.kt` mediante navegación sencilla basada en estados.

- [ ] **Navegación basada en estados:**
  - Usar una variable de estado (`rememberSaveable` / `mutableStateOf`) para alternar entre las vistas (`LISTADO`, `FORMULARIO`, `API`) sin librerías externas de navegación.
- [ ] **Pantalla Principal (`PantallaListado`):**
  - Incorporar un `Switch` superior con el texto **«Mostrar solo pendientes»** conectado a DataStore.
  - Renderizar las tareas en un `LazyColumn` mostrando **únicamente** título, prioridad y estado.
  - Mostrar un mensaje informativo claro cuando la lista esté vacía.
  - Incluir un `Checkbox` en cada tarjeta para alternar entre pendiente y completada (guardando el cambio en Room).
  - Incluir controles en cada tarea para **Editar**, **Eliminar** y **Compartir** (este último se conectará el Día 4).
  - Añadir botón para crear nueva tarea y botón/acceso para ir a **«Tareas de ejemplo»**.
- [ ] **Pantalla de Formulario (`PantallaFormulario` — reutilizable para Crear y Editar):**
  - Campo de texto para **Título**, campo de texto para **Descripción** y **menú desplegable** (`DropdownMenu`) para **Prioridad** (*Baja*, *Media*, *Alta*).
  - Botón **Guardar** y botón **Cancelar / Regresar**.
  - **Validaciones antes de guardar:**
    - El título no puede estar vacío ni contener solo espacios, y debe tener **al menos 3 caracteres** tras eliminar espacios exteriores (`.trim()`).
    - La prioridad debe ser una de las 3 opciones permitidas.
    - La descripción puede quedar vacía.
    - Mostrar mensaje de error comprensible junto al campo correspondiente si falla.
    - Evitar duplicados si el usuario pulsa *Guardar* repetidamente (deshabilitando el botón mientras se guarda).
- [ ] **Confirmación de Eliminación:**
  - Mostrar un `AlertDialog` pidiendo confirmación antes de borrar; si el usuario cancela, la tarea permanece intacta.
- [ ] **Recurso gráfico local:**
  - Incorporar al menos un icono o recurso gráfico local en la interfaz.

---

### Día 4 (Domingo): Consulta a API REST (Retrofit) y Compartir Tareas
> **Objetivo:** Completar las funcionalidades de red y comunicación con otras aplicaciones del sistema.

- [ ] **Servicio Web con Retrofit (`Comunicaciones`):**
  - Crear el modelo de datos remoto (`id`, `title`, `completed`).
  - Definir la interfaz `ApiService` con `@GET("todos")` y `@Query("userId") userId: Int = 1` apuntando a `https://jsonplaceholder.typicode.com/`.
  - Configurar el cliente Retrofit con su conversor JSON.
- [ ] **Apartado «Tareas de ejemplo» (`PantallaApi`):**
  - Separar claramente estas tareas de las personales (no se guardan en Room ni se editan).
  - Botón para solicitar las tareas (deshabilitado mientras la petición está en curso para evitar llamadas simultáneas).
  - Indicador de carga visible (`CircularProgressIndicator`) mientras espera respuesta.
  - Listado mostrando identificador, título y estado de cada tarea recibida.
  - Control de errores (`try / catch`): mostrar mensaje claro si falla la conexión y ofrecer un botón **Reintentar**.
- [ ] **Compartir Tarea (`Intent` implícito):**
  - Preparar el texto con **título, descripción y prioridad** de la tarea seleccionada.
  - Lanzar `Intent(Intent.ACTION_SEND)` con tipo `"text/plain"` mediante `Intent.createChooser(...)`.
  - Controlar con `try / catch` si no hay aplicaciones compatibles instaladas.
  - No mostrar mensajes afirmando que la tarea se ha enviado tras abrir el selector.

---

### Día 5 (Lunes): Pruebas Obligatorias, APK y Documentación
> **Objetivo:** Validar los 12 casos de prueba en dos emuladores distintos, generar el APK y preparar la entrega en GitHub.

- [ ] **Ejecutar las 12 pruebas obligatorias en 2 perfiles de emulador (uno compacto y otro grande):**
  1. Crear una tarea válida (se guarda y aparece en la lista).
  2. Intentar guardar un título vacío o de < 3 caracteres (muestra error y no crea tarea).
  3. Editar una tarea (los cambios se guardan y muestran).
  4. Marcar/desmarcar una tarea como completada (cambia estado y persiste).
  5. Cancelar eliminación en el `AlertDialog` (la tarea no desaparece).
  6. Confirmar eliminación (desaparece de Room).
  7. Cerrar y volver a abrir la app (recupera tareas de Room).
  8. Activar *«Mostrar solo pendientes»* y reiniciar la app (conserva preferencia de DataStore).
  9. Consultar API con Internet (muestra tareas recibidas).
  10. Consultar API sin conexión / modo avión (muestra error y permite reintentar).
  11. Compartir una tarea (abre selector de aplicaciones compatibles).
  12. Cambiar orientación del dispositivo (sigue funcionando y conserva estado/datos).
- [ ] **Generar APK de depuración:**
  - En Android Studio: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
- [ ] **Preparar entrega en GitHub (público o compartido con `manuel.granados@eusa.es`):**
  - Subir proyecto completo de Android Studio + archivo `.apk`.
  - Tomar al menos **4 capturas de pantalla** (listado principal, formulario, consulta API e interacción relevante como filtro/borrado/compartir).
  - Redactar `README.md` incluyendo: descripción, funcionalidades, organización del proyecto, bibliotecas, instrucciones de ejecución, perfiles de los 2 emuladores, tabla de las 12 pruebas realizadas, enlace al chat de IA e incidencias conocidas.