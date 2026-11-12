# Gestor personal de tareas
## Descripción del Proyecto

Aplicación Android denominada **Gestor Personal de Tareas**, destinada a organizar y gestionar tareas personales, académicas o profesionales. La aplicación permitirá al usuario registrar tareas, consultar las existentes, modificarlas, marcarlas como completadas y eliminarlas.

Además, incorporará almacenamiento permanente para conservar la información entre ejecuciones, preferencias de usuario, consultas a un servicio web y la posibilidad de compartir tareas mediante otras aplicaciones instaladas en el dispositivo.

El objetivo es aplicar de forma conjunta los conocimientos adquiridos durante el curso, utilizando **Kotlin**, **Jetpack Compose** y algunas de las bibliotecas habituales del desarrollo Android.---

## 📅 Resumen del Plan

| Día | Carga Estimada | Bloque Principal | Entregable Técnico |
| :--- | :--- | :--- | :--- |
| **Día 1 (Jueves)** | Baja (1 - 1.5 h) | Configuración y Arquitectura | Proyecto creado, dependencias Gradle y modelo `Tarea`. |
| **Día 2 (Viernes)** | Baja (1.5 - 2 h) | Datos Locales y Preferencias | Base de datos **Room** (DAO + Entity) y **DataStore** operativos. |
| **Día 3 (Sábado)** | Alta (5 - 6 h) | UI en Compose y Lógica CRUD | Pantallas funcionales, navegación, `ViewModel` y flujo CRUD. |
| **Día 4 (Domingo)** | Media (3 h) | Red e Integración del Sistema | Consumo de API con **Retrofit** e `Intent` para compartir. |
| **Día 5 (Lunes)** | Media-Baja (2 h) | Refinamiento y Cierre | Manejo de errores, estados vacíos, pruebas y documentación. |

---
## 🛠️ Desglose Diario Detallado

### Día 1 (Jueves): Cimientos y Estructura del Proyecto
> **Objetivo:** Dejar configurado el entorno y el modelo de dominio inicial para evitar problemas de dependencias durante el fin de semana.

- [ ] Crear el proyecto en Android Studio eligiendo la plantilla **Empty Activity (Jetpack Compose)**.
- [ ] Configurar las dependencias en `build.gradle.kts` (`libs.versions.toml`):
  - Room (`room-runtime`, `room-ktx`, `room-compiler`)
  - DataStore Preferences (`datastore-preferences`)
  - Retrofit + Conversor JSON (`retrofit`, `converter-gson` o `kotlinx-serialization`)
  - Navigation Compose (`navigation-compose`)
  - ViewModel de Compose (`lifecycle-viewmodel-compose`)
- [ ] Definir la estructura básica del proyecto por paquetes:
  - `data/` (base de datos, api, preferencias, repositorio)
  - `model/` (clases de datos del dominio)
  - `ui/screens/` (pantallas de Compose)
  - `ui/components/` (componentes reutilizables)
- [ ] Crear la clase de datos base `Tarea`:
  - `id`: Int / Long (Clave primaria)
  - `titulo`: String
  - `descripcion`: String
  - `categoria`: String (Personal, Académica, Profesional)
  - `fechaLimite`: String / Long
  - `completada`: Boolean

---

### Día 2 (Viernes): Persistencia Local y Preferencias
> **Objetivo:** Implementar el almacenamiento permanente mediante Room y la gestión de preferencias del usuario.

- [ ] **Base de Datos Local (Room):**
  - Crear la entidad `@Entity` basada en el modelo `Tarea`.
  - Definir la interfaz `@Dao` con operaciones fundamentales:
    - Insertar tarea (`@Insert`)
    - Actualizar tarea (`@Update`)
    - Eliminar tarea (`@Delete`)
    - Consultar tareas ordenadas (`@Query("SELECT * FROM tareas...")` retornando `Flow<List<Tarea>>`)
  - Crear la clase abstracta `@Database` (`AppDatabase`).
- [ ] **Preferencias de Usuario (DataStore):**
  - Configurar DataStore Preferences para guardar ajustes simples:
    - Criterio de ordenación (por fecha o prioridad).
    - Ocultar o mostrar tareas completadas.
- [ ] **Capa de Repositorio:**
  - Crear `TareaRepository` que centralice el acceso a las operaciones de Room y DataStore.

---

### Día 3 (Sábado): Interfaz con Jetpack Compose y Lógica CRUD
> **Objetivo:** Construir toda la interfaz gráfica y conseguir que las tareas se puedan crear, listar, modificar, completar y eliminar.

- [ ] ** ViewModel y Gestión de Estado:**
  - Crear `TareasViewModel` cargando tareas desde el repositorio y exponiendo un `StateFlow`.
  - Gestionar el estado de la UI (lista de tareas, estado de carga, filtros activos).
- [ ] **Navegación:**
  - Configurar `NavHost` para navegar entre la Pantalla Principal y la Pantalla de Formulario.
- [ ] **Pantalla Principal (`ListadoTareasScreen`):**
  - Diseñar un `TopAppBar` con títulos y acceso a filtros o ajustes.
  - Implementar un `LazyColumn` para renderizar las tarjetas (`Card`) de cada tarea.
  - Añadir un `Checkbox` en la tarjeta para cambiar el estado de completada en tiempo real.
  - Colocar un `FloatingActionButton` (FAB) para navegar a la creación de tareas.
- [ ] **Pantalla/Diálogo de Formulario (`FormularioTareaScreen`):**
  - Campos de entrada para título, descripción y fecha.
  - Desplegable / Selector para la categoría.
  - Reutilizar el formulario tanto para **crear** como para **editar** una tarea existente.
  - Validación básica (evitar guardar tareas con título vacío).
- [ ] **Eliminación de Tareas:**
  - Permitir borrar tareas (por deslizamiento o botón dedicado) incorporando confirmación o `Snackbar`.

---

### Día 4 (Domingo): Integración con Servicio Web y Compartir
> **Objetivo:** Conectar la aplicación con un servicio web externo e integrar capacidades del sistema Android.

- [ ] **Servicio Web (Retrofit):**
  - Crear la interfaz `ApiService` con peticiones GET para consumir datos remotos.
  - Configurar la instancia de Retrofit con su base URL.
  - Integrar la funcionalidad en la UI (ej. importar plantillas de tareas, o cargar un mensaje/frase del día en la pantalla principal).
  - Manejar los estados de la red (Cargando, Éxito, Error sin conexión).
- [ ] **Compartir Tareas (Intent Nactivo):**
  - Añadir la opción de "Compartir" dentro del detalle o tarjeta de la tarea.
  - Crear un `Intent(Intent.ACTION_SEND)` con MIME type `"text/plain"`.
  - Construir la cadena de texto estructurada con el título, la descripción y el estado de la tarea para enviarla a WhatsApp, correo, notas, etc.

---

### Día 5 (Lunes): Refinamiento, Pruebas y Entrega
> **Objetivo:** Asegurar la estabilidad del proyecto, verificar el cumplimiento de los requisitos y preparar la documentación.

- [ ] **Experiencia de Usuario y Pulido:**
  - Añadir diseño para el estado vacío (cuando no hay tareas registradas).
  - Revisar colores, espaciados y tipografías en Jetpack Compose.
- [ ] **Pruebas Integrales:**
  - Probar persistencia (cerrar la app completamente y reabrirla).
  - Probar comportamiento sin conexión a Internet (modo avión) para comprobar resiliencia en Retrofit.
  - Verificar la rotación de pantalla y la retención de estado.
- [ ] **Documentación y Entrega:**
  - Revisar y limpiar el código fuente (eliminar imports sin usar, logs de depuración).
  - Redactar el archivo `README.md` del repositorio con las instrucciones de compilación, arquitectura usada y capturas de pantalla de la app.
