<div align="center">

<img src="https://img.shields.io/badge/⚽-MUNDIAL%202026-D4AF37?style=for-the-badge&labelColor=0A0E1A&color=D4AF37" alt="Mundial 2026" />

# 🏆 Sistema de Gestión — Mundial 2026

> **Aplicación de escritorio en Java** para la gestión completa del torneo FIFA World Cup 2026.
> Administra equipos, partidos, pronósticos y clasificaciones en tiempo real.

<br/>

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-5C6BC0?style=flat-square&logo=java&logoColor=white)
![JDBC](https://img.shields.io/badge/Conector-MySQL%20JDBC%209.7-00A86B?style=flat-square)
![Estado](https://img.shields.io/badge/Estado-En%20Desarrollo-D4AF37?style=flat-square)

</div>

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Base de Datos](#-base-de-datos)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [Roles de Usuario](#-roles-de-usuario)

---

## 🌍 Descripción

**Sistema de Gestión Mundial 2026** es una aplicación de escritorio desarrollada en **Java con interfaz gráfica Swing**, diseñada para gestionar de forma completa el torneo de fútbol más grande del mundo.

La aplicación cuenta con un sistema de **autenticación por roles**, donde los administradores pueden gestionar toda la información del torneo, mientras que los usuarios normales pueden hacer **pronósticos de partidos** y competir en un **ranking global**.

La interfaz sigue un diseño **oscuro premium** con acentos dorados, inspirada en la elegancia y el prestigio del Mundial de Fútbol.

---

## ✨ Características

### 👑 Para Administradores

| Módulo | Descripción |
|--------|-------------|
| 🏟️ **Estadios** | Alta, edición y eliminación de sedes del torneo |
| 🚩 **Fases** | Gestión de fases (Grupos, Octavos, Cuartos, etc.) |
| 🔤 **Grupos** | Administración de los grupos de la fase inicial |
| ⚽ **Equipos** | Registro de las 48 selecciones participantes |
| 📅 **Partidos** | Programación y registro de resultados reales |
| 👤 **Usuarios** | Gestión de cuentas y asignación de roles |
| 📊 **Auditoría** | Revisión de todos los pronósticos realizados |
| 🏅 **Tabla de Posiciones** | Clasificación actualizada por grupo |

### 🎮 Para Jugadores

| Módulo | Descripción |
|--------|-------------|
| 🔮 **Mis Pronósticos** | Registrar predicciones de resultados |
| 🏅 **Tabla de Posiciones** | Ver clasificación actual del torneo |
| 🏆 **Ranking Global** | Competir con otros usuarios por puntos |
| 📖 **Reglas** | Consultar el sistema de puntuación |

---

## 🛠️ Tecnologías

```
├── Lenguaje            →  Java 17+
├── Interfaz GUI        →  Java Swing
├── Base de Datos       →  MySQL 8.0
├── Conector BD         →  MySQL Connector/J 9.7.0
├── IDE                 →  IntelliJ IDEA
└── Control de versiones→  Git / GitHub
```

---

## 📁 Estructura del Proyecto

```
Mundial/
│
├── 📁 src/
│   └── com/mundial/
│       ├── 📁 config/
│       │   └── ConexionDB.java            # Conexión a MySQL
│       │
│       ├── 📁 dao/                        # Capa de acceso a datos
│       │   ├── EquipoDAO.java
│       │   ├── EstadioDAO.java
│       │   ├── FaseDAO.java
│       │   ├── GrupoDAO.java
│       │   ├── PartidoDAO.java
│       │   ├── PosicionDAO.java
│       │   ├── PronosticoDAO.java
│       │   ├── PuntoDAO.java
│       │   └── UsuarioDAO.java
│       │
│       ├── 📁 modelo/                     # Entidades del dominio
│       │   ├── Equipo.java
│       │   ├── Estadio.java
│       │   ├── Fase.java
│       │   ├── Grupo.java
│       │   ├── Partido.java
│       │   ├── Pronostico.java
│       │   ├── Usuario.java
│       │   ├── FilaPosicion.java
│       │   └── FilaRanking.java
│       │
│       └── 📁 vista/                      # Interfaz gráfica
│           ├── Main.java                  # Punto de entrada
│           ├── VentanaLogin.java          # Pantalla de autenticación
│           ├── VentanaDashboard.java      # Panel principal
│           ├── VentanaEquipos.java
│           ├── VentanaEstadios.java
│           ├── VentanaFases.java
│           ├── VentanaGrupos.java
│           ├── VentanaPartidos.java
│           ├── VentanaPosiciones.java
│           ├── VentanaRanking.java
│           ├── VentanaUsuarios.java
│           ├── VentanaAuditoriaPronosticos.java
│           ├── VentanaReglas.java
│           └── 📁 componentes/
│               ├── GestorEstilos.java
│               ├── CustomScrollBarUI.java
│               ├── ModalEquipo.java
│               ├── ModalEstadio.java
│               ├── ModalFase.java
│               ├── ModalGrupo.java
│               ├── ModalPartido.java
│               ├── ModalPronostico.java
│               ├── ModalUsuario.java
│               ├── TableActionCellEditor.java
│               └── TableActionCellRender.java
│
├── 📁 database/
│   ├── mundial_db.sql                     # Esquema de la base de datos
│   └── datos_mundial_2026.sql             # Datos precargados del torneo
│
├── 📁 lib/
│   └── mysql-connector-j-9.7.0.jar        # Driver JDBC
│
└── README.md
```

---

## 🗄️ Base de Datos

El sistema utiliza **MySQL** con las siguientes tablas principales:

```sql
mundial_db
├── fases          -- Fase grupal, octavos, cuartos, semifinal y final
├── estadios       -- Sedes del torneo
├── grupos         -- Grupos A–L de la fase inicial
├── equipos        -- Las 48 selecciones participantes
├── usuarios       -- Cuentas de usuario (ADMIN / USER)
├── partidos       -- Calendario y resultados reales
├── pronosticos    -- Predicciones de los jugadores
└── puntos         -- Sistema de puntuación del ranking
```

---

## 🚀 Instalación

### Prerrequisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- ☕ **Java JDK 17** o superior → [Descargar](https://adoptium.net/)
- 🐬 **MySQL Server 8.0** → [Descargar](https://dev.mysql.com/downloads/mysql/)
- 💡 **IntelliJ IDEA** (recomendado) → [Descargar](https://www.jetbrains.com/idea/)

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/emmanuelpersonal2024-glitch/gestion-mundial-2026.git
cd gestion-mundial-2026
```

### Paso 2 — Configurar la Base de Datos

Abre **MySQL Workbench** o tu cliente preferido y ejecuta los scripts en orden:

```bash
# 1. Crear el esquema y las tablas
mysql -u root -p < database/mundial_db.sql

# 2. Cargar los datos del torneo
mysql -u root -p < database/datos_mundial_2026.sql
```

### Paso 3 — Configurar la Conexión

Edita el archivo `src/com/mundial/config/ConexionDB.java` con tus credenciales de MySQL:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/mundial_db";
private static final String USER = "root";   // ← Tu usuario MySQL
private static final String PASS = "";       // ← Tu contraseña MySQL
```

### Paso 4 — Abrir en IntelliJ IDEA

1. `File` → `Open` → Selecciona la carpeta del proyecto
2. Verifica que el **JDK 17+** esté configurado correctamente
3. Confirma que `lib/mysql-connector-j-9.7.0.jar` esté en las dependencias del proyecto
4. Ejecuta `Main.java` ▶️

---

## 🎮 Uso

Al iniciar la aplicación verás la **pantalla de inicio de sesión** donde puedes:

- **Iniciar sesión** con una cuenta existente
- **Registrarte** como nuevo usuario (rol `USER` por defecto)

> ⚠️ **Nota:** Para crear el primer usuario `ADMIN`, hazlo directamente en la base de datos o configúralo en el script de datos iniciales SQL.

---

## 👥 Roles de Usuario

| Rol | Descripción | Acceso |
|-----|-------------|--------|
| 🔑 `ADMIN` | Administrador del torneo | Gestión completa del sistema |
| 🎮 `USER` | Jugador / Participante | Pronósticos, ranking y posiciones |

---

## 🎨 Diseño de la Interfaz

La interfaz sigue una paleta de colores **oscura y premium**:

| Elemento | Color | Código Hex |
|----------|-------|------------|
| Fondo principal | Azul marino oscuro | `#0A0E1A` |
| Paneles / Sidebar | Azul noche | `#14182E` |
| Acento / Títulos | Dorado FIFA | `#D4AF37` |
| Texto secundario | Gris azulado | `#B4BED2` |
| Éxito / Confirmación | Verde esmeralda | `#00A86B` |
| Error / Alerta | Rojo suave | `#DC5050` |

---

## 👨‍💻 Autor

<div align="center">

**Emmanuel** — Desarrollador Java
📧 Proyecto académico — Mundial 2026
🔗 [github.com/emmanuelpersonal2024-glitch](https://github.com/emmanuelpersonal2024-glitch)

<br/>

⭐ *Si te gustó el proyecto, ¡dale una estrella en GitHub!* ⭐

</div>

---

<div align="center">
<sub>Desarrollado con ☕ Java y pasión por el fútbol ⚽ | FIFA World Cup 2026 🏆</sub>
</div>
