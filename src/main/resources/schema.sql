DROP DATABASE IF EXISTS task_pro_db;
CREATE DATABASE task_pro_db;
USE task_pro_db;

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
    rol_id         INT          NOT NULL,
    fecha_creacion TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rol_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS proyectos
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100)                               NOT NULL,
    descripcion TEXT,
    estado      ENUM ('ACTIVO', 'ARCHIVADO', 'FINALIZADO') NOT NULL DEFAULT 'ACTIVO',
    creador_id  BIGINT                                     NOT NULL,
    FOREIGN KEY (creador_id) REFERENCES usuarios (id)
);

CREATE TABLE IF NOT EXISTS tareas
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    proyecto_id  BIGINT                                                    NOT NULL,
    titulo       VARCHAR(150)                                              NOT NULL,
    descripcion  TEXT,
    prioridad    ENUM ('BAJA', 'MEDIA', 'ALTA', 'URGENTE')                 NOT NULL DEFAULT 'MEDIA',
    estado       ENUM ('BACKLOG', 'TODO', 'IN_PROGRESS', 'REVIEW', 'DONE') NOT NULL DEFAULT 'BACKLOG',
    fecha_limite DATE                                                      NOT NULL,
    FOREIGN KEY (proyecto_id) REFERENCES proyectos (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tarea_asignaciones
(
    tarea_id         BIGINT    NOT NULL,
    usuario_id       BIGINT    NOT NULL,
    fecha_asignacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tarea_id, usuario_id),
    FOREIGN KEY (tarea_id) REFERENCES tareas (id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS historial_tareas
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    tarea_id BIGINT    NOT NULL,
    mensaje  TEXT      NOT NULL,
    fecha    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tarea_id) REFERENCES tareas (id) ON DELETE CASCADE
);

-- =====================================================
-- INSERCIÓN DE DATOS BASE
-- =====================================================

-- 1. ROLES (Deben ir primero porque los usuarios los necesitan)
INSERT IGNORE INTO roles (id, nombre)
VALUES (1, 'ADMINISTRADOR'),
       (2, 'USUARIO'),
       (3, 'INVITADO'),
       (4, 'GESTOR');

-- 2. USUARIOS (Necesitan que los roles ya existan)
-- (Contraseñas simuladas)
INSERT IGNORE INTO usuarios (id, username, email, password_hash, rol_id)
VALUES (1, 'admin_jefe', 'admin@taskpro.com', 'hash_secreto_123', 1),
       (2, 'laura_dev', 'laura@taskpro.com', 'hash_secreto_456', 2),
       (3, 'carlos_gestor', 'carlos@taskpro.com', 'hash_secreto_789', 4);

-- 3. PROYECTOS (Necesitan que los usuarios creadores ya existan)
INSERT IGNORE INTO proyectos (id, nombre, descripcion, estado, creador_id)
VALUES (1, 'Rediseño Web', 'Renovar la landing page corporativa', 'ACTIVO', 3),
       (2, 'Migración Cloud', 'Mover la base de datos a AWS', 'ACTIVO', 1),
       (3, 'App Móvil v2', 'Nuevas funciones para iOS y Android', 'ARCHIVADO',
        3);

-- 4. TAREAS (Necesitan que los proyectos ya existan)
INSERT IGNORE INTO tareas (id, proyecto_id, titulo, descripcion, prioridad,
                           estado, fecha_limite)
VALUES (1, 1, 'Crear mockups de la home',
        'Diseñar en Figma la nueva estructura', 'ALTA', 'IN_PROGRESS',
        '2026-03-10'),
       (2, 1, 'Programar CSS', 'Maquetar los diseños de Figma en código',
        'MEDIA', 'TODO', '2026-03-15'),
       (3, 2, 'Configurar VPC en AWS', 'Crear red privada y subredes seguras',
        'URGENTE', 'BACKLOG', '2026-03-05'),
       (4, 2, 'Volcado de datos SQL',
        'Exportar base de datos antigua e importar', 'ALTA', 'TODO',
        '2026-03-20'),
       (5, 1, 'Revisión de textos', 'Comprobar faltas de ortografía', 'BAJA',
        'DONE', '2026-02-28');

-- 5. ASIGNACIONES (Necesitan tareas y usuarios)
INSERT IGNORE INTO tarea_asignaciones (tarea_id, usuario_id)
VALUES (1, 2), -- Laura hace los mockups
       (2, 2), -- Laura maqueta el CSS
       (3, 1); -- El Admin configura AWS

-- 6. HISTORIAL (Necesita tareas)
INSERT IGNORE INTO historial_tareas (tarea_id, mensaje)
VALUES (1, 'Se ha iniciado el diseño en Figma con la paleta de colores nueva.'),
       (1, 'Primera versión enviada al cliente para revisión.'),
       (3, 'Cuenta de AWS creada y facturación configurada.'),
       (5, 'Textos revisados y validados por marketing.');