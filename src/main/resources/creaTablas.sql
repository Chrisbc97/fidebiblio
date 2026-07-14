USE fidebiblio;
SET SQL_SAFE_UPDATES = 0;

-- Tabla de categorías de libros
CREATE TABLE categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(60) NOT NULL,
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_categoria)
) ENGINE = InnoDB;

-- Tabla de usuarios (Administrador, Bibliotecario, Estudiante)
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  identificacion VARCHAR(20) NOT NULL UNIQUE,
  nombre VARCHAR(60) NOT NULL,
  primer_apellido VARCHAR(60) NOT NULL,
  segundo_apellido VARCHAR(60),
  correo VARCHAR(100) NOT NULL UNIQUE,
  telefono VARCHAR(20),
  password VARCHAR(255) NOT NULL,
  rol VARCHAR(20) NOT NULL, -- ADMINISTRADOR, BIBLIOTECARIO, ESTUDIANTE
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario)
) ENGINE = InnoDB;

-- Catálogo
CREATE TABLE libro (
  id_libro INT NOT NULL AUTO_INCREMENT,
  isbn VARCHAR(20) NOT NULL UNIQUE,
  titulo VARCHAR(150) NOT NULL,
  autor VARCHAR(120) NOT NULL,
  editorial VARCHAR(100),
  anio_publicacion INT,
  id_categoria INT,
  cantidad_ejemplares INT DEFAULT 0,
  ejemplares_disponibles INT DEFAULT 0,
  ubicacion_fisica VARCHAR(60),
  ruta_imagen TEXT,
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_libro),
  FOREIGN KEY fk_libro_categoria (id_categoria) REFERENCES categoria(id_categoria)
) ENGINE = InnoDB;

-- Tabla de préstamos 
CREATE TABLE prestamo (
  id_prestamo INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_libro INT NOT NULL,
  fecha_prestamo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_limite DATE NOT NULL,
  fecha_devolucion DATE,
  estado VARCHAR(20) DEFAULT 'ACTIVO', -- ACTIVO, DEVUELTO, VENCIDO
  PRIMARY KEY (id_prestamo),
  FOREIGN KEY fk_prestamo_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_prestamo_libro (id_libro) REFERENCES libro(id_libro)
) ENGINE = InnoDB;

-- Tabla de reservas 
CREATE TABLE reserva (
  id_reserva INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_libro INT NOT NULL,
  fecha_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  posicion INT,
  estado VARCHAR(20) DEFAULT 'ACTIVA', -- ACTIVA, CANCELADA, COMPLETADA
  PRIMARY KEY (id_reserva),
  FOREIGN KEY fk_reserva_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_reserva_libro (id_libro) REFERENCES libro(id_libro)
) ENGINE = InnoDB;

-- Tabla de sugerencias de adquisición 
CREATE TABLE sugerencia (
  id_sugerencia INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  titulo VARCHAR(150) NOT NULL,
  autor VARCHAR(120),
  votos INT DEFAULT 1,
  estado VARCHAR(20) DEFAULT 'PENDIENTE', -- PENDIENTE, APROBADA, RECHAZADA
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_sugerencia),
  FOREIGN KEY fk_sugerencia_usuario (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;


-- Datos: usuarios, categorías y libros del prototipo 


-- Usuarios de prueba 
INSERT INTO usuario (identificacion, nombre, primer_apellido, segundo_apellido, correo, telefono, password, rol) VALUES
('101110111', 'Christopher', 'Brenes', '', 'cbrenes00620@ufide.ac.cr', '88881111', 'admin123', 'ADMINISTRADOR'),
('404040404', 'Andrea', 'Solís', '', 'asolis00001@ufide.ac.cr', '88882222', 'biblio123', 'BIBLIOTECARIO'),
('505050505', 'Mateo', 'Vargas', '', 'mvargas00002@ufide.ac.cr', '88883333', 'estu123', 'ESTUDIANTE');

-- Categorías según el prototipo
INSERT INTO categoria (nombre) VALUES
('Ingeniería Informática'), ('Filosofía'), ('Medicina'), ('Arquitectura'),
('Física'), ('Historia'), ('Economía');

-- 8 Libros
INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, id_categoria, cantidad_ejemplares, ejemplares_disponibles, ubicacion_fisica, ruta_imagen) VALUES
('978-1000000001', 'Algoritmos Modernos', 'Robert Sedgewick', 'Fidelitas Editorial', 2023, 1, 3, 3, 'Est. A-1', 'https://firebasestorage.googleapis.com/v0/b/fidebiblio.firebasestorage.app/o/fidebiblio%2Flibro%2FAlgoritmos%20modernos.jpg?alt=media&token=edb0c800-ff1e-4ecb-8d97-b230a50bc080'),
('978-1000000002', 'El Dilema de la Conciencia', 'Elena Valdés', 'Fidelitas Editorial', 2022, 2, 2, 0, 'Est. B-1', 'https://firebasestorage.googleapis.com/v0/b/fidebiblio.firebasestorage.app/o/fidebiblio%2Flibro%2FEl%20dilema%20de%20la%20convinencia.jpg?alt=media&token=9f6b39e1-0653-44f0-9e8f-6c3f90af04a3'),
('978-1000000003', 'Anatomía Humana Tomo I', 'Lucas Méndez', 'Fidelitas Editorial', 2021, 3, 2, 2, 'Est. C-1', 'https://firebasestorage.googleapis.com/v0/b/fidebiblio.firebasestorage.app/o/fidebiblio%2Flibro%2FAnatomia%20Humana%20Tomo%201.jpg?alt=media&token=202ddb60-0b77-4d0d-8163-a6486700f12f'),
('978-1000000004', 'Estructuras y Flujos', 'Sonia Brenes', 'Fidelitas Editorial', 2023, 4, 2, 2, 'Est. D-1', 'https://firebasestorage.googleapis.com/v0/b/fidebiblio.firebasestorage.app/o/fidebiblio%2Flibro%2FEstructuras%20y%20flujos.jpg?alt=media&token=0e86802d-d0f5-4698-969b-1db54c5d1ec5'),
('978-1000000005', 'Mecánica Cuántica para Principiantes', 'Arthur Rodgers', 'Fidelitas Editorial', 2024, 5, 3, 3, 'Est. E-1', 'https://firebasestorage.googleapis.com/v0/b/fidebiblio.firebasestorage.app/o/fidebiblio%2Flibro%2FMecánica%20Cuántica%20para%20principiantes.jpg?alt=media&token=87ff4d13-1514-4d6c-bca9-40403cd1a3fe'),
('978-1000000006', 'Un Análisis Político', 'Isabel Allende', 'Fidelitas Editorial', 2020, 6, 2, 0, 'Est. F-1', 'https://firebasestorage.googleapis.com/v0/b/fidebiblio.firebasestorage.app/o/fidebiblio%2Flibro%2FUn%20análisis%20Político.jpg?alt=media&token=a5260317-3469-4b68-b024-668ddb2cb015'),
('978-1000000007', 'Economía Avanzada', 'Mark Thompson', 'Fidelitas Editorial', 2022, 7, 2, 2, 'Est. G-1', 'https://firebasestorage.googleapis.com/v0/b/fidebiblio.firebasestorage.app/o/fidebiblio%2Flibro%2FEconomía%20Avanzada.jpg?alt=media&token=dcf80e43-acc9-4753-9173-a99a89e27614'),
('978-1000000008', 'Big Data y Data Mining', 'Victor Ramos', 'Fidelitas Editorial', 2024, 1, 3, 3, 'Est. H-1', 'https://firebasestorage.googleapis.com/v0/b/fidebiblio.firebasestorage.app/o/fidebiblio%2Flibro%2FBig%20Data%20y%20Data%20mining.jpg?alt=media&token=f1591336-da27-4825-a96f-49599fbad250');

-- Préstamo de ejemplo
INSERT INTO prestamo (id_usuario, id_libro, fecha_limite, estado) VALUES
(3, 2, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ACTIVO');