CREATE TABLE IF NOT EXISTS roles
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    rol_id         INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rol_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS proyectos
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion TEXT,
    estado      ENUM ('ACTIVO', 'ARCHIVADO', 'FINALIZADO') DEFAULT 'ACTIVO',
    creador_id  BIGINT,
    FOREIGN KEY (creador_id) REFERENCES usuarios (id)
);

CREATE TABLE IF NOT EXISTS tareas
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id  BIGINT       NOT NULL,
    titulo       VARCHAR(150) NOT NULL,
    descripcion  TEXT,
    prioridad    ENUM ('BAJA', 'MEDIA', 'ALTA', 'URGENTE')                 DEFAULT 'MEDIA',
    estado       ENUM ('BACKLOG', 'TODO', 'IN_PROGRESS', 'REVIEW', 'DONE') DEFAULT 'BACKLOG',
    fecha_limite DATE,
    FOREIGN KEY (proyecto_id) REFERENCES proyectos (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tarea_asignaciones
(
    tarea_id         BIGINT NOT NULL,
    usuario_id       BIGINT NOT NULL,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tarea_id, usuario_id),
    FOREIGN KEY (tarea_id) REFERENCES tareas (id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS historial_tareas
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    tarea_id BIGINT NOT NULL,
    mensaje  TEXT   NOT NULL,
    fecha    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tarea_id) REFERENCES tareas (id) ON DELETE CASCADE
);