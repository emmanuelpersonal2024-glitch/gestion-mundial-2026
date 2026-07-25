DROP DATABASE IF EXISTS mundial_db;
CREATE DATABASE mundial_db;
USE mundial_db;

CREATE TABLE fases (
    id_fase INT AUTO_INCREMENT PRIMARY KEY,
    nombre_fase VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE estadios (
    id_estadio INT AUTO_INCREMENT PRIMARY KEY,
    nombre_estadio VARCHAR(100) NOT NULL UNIQUE,
    ciudad_estadio VARCHAR(50) NOT NULL
);

CREATE TABLE grupos (
    id_grupo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_grupo VARCHAR(2) NOT NULL UNIQUE
);

CREATE TABLE equipos (
    id_equipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre_equipo VARCHAR(50) NOT NULL UNIQUE,
    id_grupo INT NOT NULL,
    FOREIGN KEY (id_grupo) REFERENCES grupos(id_grupo)
);

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    password_usuario VARCHAR(255) NOT NULL,
    rol_usuario VARCHAR(20) DEFAULT 'USER',
    fecha_registro_usuario TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE partidos (
    id_partido INT AUTO_INCREMENT PRIMARY KEY,
    id_fase INT NOT NULL,
    id_estadio INT NOT NULL,
    id_equipo_a INT NULL,
    id_equipo_b INT NULL,
    goles_equipo_a_real INT DEFAULT NULL,
    goles_equipo_b_real INT DEFAULT NULL,
    fecha_partido DATE NOT NULL,
    hora_partido TIME NOT NULL,
    estado_partido VARCHAR(20) DEFAULT 'Programado',
    FOREIGN KEY (id_fase) REFERENCES fases(id_fase),
    FOREIGN KEY (id_estadio) REFERENCES estadios(id_estadio),
    FOREIGN KEY (id_equipo_a) REFERENCES equipos(id_equipo),
    FOREIGN KEY (id_equipo_b) REFERENCES equipos(id_equipo)
);

CREATE TABLE pronosticos (
    id_pronostico INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_partido INT NOT NULL,
    goles_equipo_a_pronostico INT NOT NULL,
    goles_equipo_b_pronostico INT NOT NULL,
    fecha_registro_pronostico TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_usuario, id_partido),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_partido) REFERENCES partidos(id_partido)
);

CREATE TABLE puntos (
    id_puntos INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL UNIQUE,
    puntos_totales INT DEFAULT 0,
    ultima_actualizacion_puntos TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);