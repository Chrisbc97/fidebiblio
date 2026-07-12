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
  rol VARCHAR(20) NOT NULL,
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario)
) ENGINE = InnoDB;

-- Tabla de libros (catálogo)
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
  ruta_imagen VARCHAR(255),
  activo BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id_libro),
  FOREIGN KEY fk_libro_categoria (id_categoria) REFERENCES categoria(id_categoria)
) ENGINE = InnoDB;

-- Tabla de préstamos (HU-05, HU-06)
CREATE TABLE prestamo (
  id_prestamo INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_libro INT NOT NULL,
  fecha_prestamo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_limite DATE NOT NULL,
  fecha_devolucion DATE,
  estado VARCHAR(20) DEFAULT 'ACTIVO',
  PRIMARY KEY (id_prestamo),
  FOREIGN KEY fk_prestamo_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_prestamo_libro (id_libro) REFERENCES libro(id_libro)
) ENGINE = InnoDB;

-- Tabla de reservas (HU-08)
CREATE TABLE reserva (
  id_reserva INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_libro INT NOT NULL,
  fecha_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  posicion INT,
  estado VARCHAR(20) DEFAULT 'ACTIVA',
  PRIMARY KEY (id_reserva),
  FOREIGN KEY fk_reserva_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_reserva_libro (id_libro) REFERENCES libro(id_libro)
) ENGINE = InnoDB;

-- Tabla de sugerencias de adquisición (HU-15)
CREATE TABLE sugerencia (
  id_sugerencia INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  titulo VARCHAR(150) NOT NULL,
  autor VARCHAR(120),
  votos INT DEFAULT 1,
  estado VARCHAR(20) DEFAULT 'PENDIENTE',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_sugerencia),
  FOREIGN KEY fk_sugerencia_usuario (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;

-- Categorías de ejemplo
INSERT INTO categoria (nombre) VALUES
('Ficción'), ('Tecnología'), ('Historia'), ('Ciencia'), ('Educación');

-- Usuarios del equipo del proyecto
INSERT INTO usuario (identificacion, nombre, primer_apellido, segundo_apellido, correo, telefono, password, rol) VALUES
('101110111', 'Christopher', 'Brenes', '', 'cbrenes00620@ufide.ac.cr', '88881111', 'admin123', 'ADMINISTRADOR'),
('202220222', 'Joseph', 'Esquivel', '', 'jesquivel90257@ufide.ac.cr', '88882222', 'biblio123', 'BIBLIOTECARIO'),
('303330333', 'King', 'Castillo', '', 'scastillo40405@ufide.ac.cr', '88883333', 'estu123', 'ESTUDIANTE');

-- Libros de ejemplo 
INSERT INTO libro (isbn, titulo, autor, editorial, anio_publicacion, id_categoria, cantidad_ejemplares, ejemplares_disponibles, ubicacion_fisica) VALUES
('978-0134685991', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', 2018, 2, 3, 3, 'Est. A-1'),
('978-0596007126', 'Head First Design Patterns', 'Freeman & Robson', 'O''Reilly', 2004, 2, 2, 2, 'Est. A-2'),
('978-8420471839', 'Cien Años de Soledad', 'Gabriel García Márquez', 'Cátedra', 1967, 1, 4, 4, 'Est. B-1'),
('978-0132350884', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', 2008, 2, 2, 1, 'Est. A-3'),
('978-9977641234', 'Historia de Costa Rica', 'Varios Autores', 'EUNED', 2015, 3, 3, 3, 'Est. C-1');

-- Préstamo de ejemplo
INSERT INTO prestamo (id_usuario, id_libro, fecha_limite, estado) VALUES
(3, 4, DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'ACTIVO');