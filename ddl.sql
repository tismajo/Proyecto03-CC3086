-- Proyecto: Seguimiento de mantenimiento de maquinaria industrial
-- Grupo 9 - Bases de Datos 1

-- Tabla maquinaria
CREATE TABLE maquinaria (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    serie VARCHAR(255) NOT NULL,
    fecha_adquisicion DATE NOT NULL,
    ubicacion VARCHAR(255) NOT NULL,
    tipo_id INTEGER NOT NULL
);

-- Tabla tipo_maquinaria
CREATE TABLE tipo_maquinaria (
    id SERIAL PRIMARY KEY,
	nombre VARCHAR(255) NOT NULL UNIQUE,
	descripcion TEXT DEFAULT 'Sin descripción'
);

-- Tabla mantenimiento
CREATE TABLE mantenimiento (
    id SERIAL PRIMARY KEY,
    maquinaria_id INTEGER NOT NULL,
    tecnico_id INTEGER NOT NULL,
    tipo_mantenimiento_id INTEGER NOT NULL,
	fecha DATE NOT NULL CHECK (fecha <= CURRENT_DATE),
	duracion_horas DECIMAL(5,2) NOT NULL CHECK (duracion_horas > 0),
	costo DECIMAL(10,2) NOT NULL CHECK (costo >= 0),
    observaciones TEXT DEFAULT 'Sin observaciones'
);

-- Tabla tipo_mantenimiento
CREATE TABLE tipo_mantenimiento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion TEXT DEFAULT 'Sin descripción'
);

-- Tabla tecnico
CREATE TABLE tecnico (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    especialidad VARCHAR(255) NOT NULL,
    telefono VARCHAR(20) NOT NULL CHECK (telefono ~ '^\+?[0-9]+$'),
    correo VARCHAR(255) NOT NULL UNIQUE
);

-- Tabla repuesto
CREATE TABLE repuesto (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT DEFAULT 'Sin descripción',
    stock_actual INTEGER NOT NULL CHECK (stock_actual >= 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario >= 0)
);

-- Tabla mantenimiento_repuesto
CREATE TABLE mantenimiento_repuesto (
    mantenimiento_id INTEGER NOT NULL,
    repuesto_id INTEGER NOT NULL,
    cantidad_usada INTEGER,
    PRIMARY KEY (mantenimiento_id, repuesto_id) -- Clave primaria compuesta
);

-- Tabla falla_reportada
CREATE TABLE falla_reportada (
    id SERIAL PRIMARY KEY,
    maquinaria_id INTEGER NOT NULL,
    descripcion TEXT DEFAULT 'Sin descripción',
	fecha_reporte DATE NOT NULL CHECK (fecha_reporte <= CURRENT_DATE),
	prioridad VARCHAR(50) NOT NULL CHECK (prioridad IN ('Alta', 'Media', 'Baja')) DEFAULT 'Media',
	estado VARCHAR(50) NOT NULL CHECK (estado IN ('Pendiente', 'En proceso', 'Resuelta')) DEFAULT 'Pendiente'
);

-- Tabla historial_falla
CREATE TABLE historial_falla (
    id SERIAL PRIMARY KEY,
    falla_id INTEGER NOT NULL,
    mantenimiento_id INTEGER NOT NULL,
    fecha_solucion DATE,
    solucion TEXT
);

-- Tabla programacion_mantenimiento
CREATE TABLE programacion_mantenimiento (
    id SERIAL PRIMARY KEY,
    maquinaria_id INTEGER NOT NULL,
    tipo_mantenimiento_id INTEGER NOT NULL,
    fecha_programada DATE,
	frecuencia_dias INTEGER NOT NULL CHECK (frecuencia_dias > 0) DEFAULT 30
);

-- Tabla departamento
CREATE TABLE departamento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    responsable VARCHAR(255) NOT NULL
);

-- Tabla maquinaria_departamento
CREATE TABLE maquinaria_departamento (
    maquinaria_id INTEGER NOT NULL,
    departamento_id INTEGER NOT NULL,
    fecha_asignacion DATE,
    PRIMARY KEY (maquinaria_id, departamento_id) -- Clave primaria compuesta
);

-- Restricciones de clave foránea
ALTER TABLE maquinaria
ADD CONSTRAINT fk_maquinaria_tipo_id
FOREIGN KEY (tipo_id)
REFERENCES tipo_maquinaria(id);

ALTER TABLE mantenimiento
ADD CONSTRAINT fk_mantenimiento_maquinaria_id
FOREIGN KEY (maquinaria_id)
REFERENCES maquinaria(id);

ALTER TABLE mantenimiento
ADD CONSTRAINT fk_mantenimiento_tecnico_id
FOREIGN KEY (tecnico_id)
REFERENCES tecnico(id);

ALTER TABLE mantenimiento
ADD CONSTRAINT fk_mantenimiento_tipo_mantenimiento_id
FOREIGN KEY (tipo_mantenimiento_id)
REFERENCES tipo_mantenimiento(id);

ALTER TABLE mantenimiento_repuesto
ADD CONSTRAINT fk_mantenimiento_repuesto_mantenimiento_id
FOREIGN KEY (mantenimiento_id)
REFERENCES mantenimiento(id);

ALTER TABLE mantenimiento_repuesto
ADD CONSTRAINT fk_mantenimiento_repuesto_repuesto_id
FOREIGN KEY (repuesto_id)
REFERENCES repuesto(id);

ALTER TABLE falla_reportada
ADD CONSTRAINT fk_falla_reportada_maquinaria_id
FOREIGN KEY (maquinaria_id)
REFERENCES maquinaria(id);

ALTER TABLE historial_falla
ADD CONSTRAINT fk_historial_falla_falla_id
FOREIGN KEY (falla_id)
REFERENCES falla_reportada(id);

ALTER TABLE historial_falla
ADD CONSTRAINT fk_historial_falla_mantenimiento_id
FOREIGN KEY (mantenimiento_id)
REFERENCES mantenimiento(id);

ALTER TABLE programacion_mantenimiento
ADD CONSTRAINT fk_programacion_mantenimiento_maquinaria_id
FOREIGN KEY (maquinaria_id)
REFERENCES maquinaria(id);

ALTER TABLE programacion_mantenimiento
ADD CONSTRAINT fk_programacion_mantenimiento_tipo_mantenimiento_id
FOREIGN KEY (tipo_mantenimiento_id)
REFERENCES tipo_mantenimiento(id);

ALTER TABLE maquinaria_departamento
ADD CONSTRAINT fk_maquinaria_departamento_maquinaria_id
FOREIGN KEY (maquinaria_id)
REFERENCES maquinaria(id);

ALTER TABLE maquinaria_departamento
ADD CONSTRAINT fk_maquinaria_departamento_departamento_id
FOREIGN KEY (departamento_id)
REFERENCES departamento(id);

-- Triggers
CREATE OR REPLACE FUNCTION validar_fecha_mantenimiento()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.fecha > CURRENT_DATE THEN
        RAISE EXCEPTION 'La fecha del mantenimiento no puede ser futura.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_fecha_mantenimiento
BEFORE INSERT OR UPDATE ON mantenimiento
FOR EACH ROW
EXECUTE FUNCTION validar_fecha_mantenimiento();

CREATE OR REPLACE FUNCTION disminuir_stock_repuesto()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE repuesto
    SET stock_actual = stock_actual - NEW.cantidad_usada
    WHERE id = NEW.repuesto_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_disminuir_stock
AFTER INSERT ON mantenimiento_repuesto
FOR EACH ROW
EXECUTE FUNCTION disminuir_stock_repuesto();

CREATE OR REPLACE FUNCTION cerrar_falla()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE falla_reportada
    SET estado = 'Resuelta'
    WHERE id = NEW.falla_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_cerrar_falla
AFTER INSERT ON historial_falla
FOR EACH ROW
EXECUTE FUNCTION cerrar_falla();

-- INSERTS
INSERT INTO tipo_maquinaria (nombre, descripcion) VALUES
('Excavadora', 'Maquinaria pesada para excavación'),
('Grúa', 'Maquinaria para elevación de cargas'),
('Cinta transportadora', 'Sistema de transporte continuo'),
('Torno', 'Herramienta de mecanizado'),
('Fresadora', 'Herramienta rotativa de corte'),
('Prensa hidráulica', 'Utilizada para moldeado y prensado'),
('Compresor de aire', 'Suministro de aire comprimido'),
('Generador', 'Genera energía eléctrica'),
('Montacargas', 'Elevación y transporte de materiales'),
('Soldadora', 'Maquinaria para soldadura de metales'),
('Retroexcavadora', 'Combina las funciones de excavadora y cargadora frontal'),
('Motoniveladora', 'Utilizada para nivelar superficies y crear pendientes'),
('Pavimentadora', 'Máquina para la extensión de asfalto'),
('Hormigonera', 'Mezcla de cemento, arena, grava y agua'),
('Compactadora', 'Compacta el suelo o asfalto'),
('Perforadora', 'Perfora agujeros en diferentes materiales'),
('Pulverizador', 'Aplica líquidos a presión'),
('Cizalla', 'Corta materiales con cuchillas'),
('Lijadora', 'Utilizada para alisar superficies'),
('Taladro', 'Realiza perforaciones en diferentes materiales'),
('Bulldozer', 'Maquinaria pesada para empuje y nivelación'),
('Cargadora frontal', 'Carga y transporte de materiales a corta distancia'),
('Zanjadora', 'Excava zanjas para tuberías y cables'),
('Apisonadora', 'Compacta el suelo con vibración'),
('Planta de asfalto', 'Produce asfalto para pavimentación'),
('Hormigonera móvil', 'Mezcla hormigón en el sitio de trabajo'),
('Rompedora', 'Rompe concreto y roca'),
('Martillo hidráulico', 'Demolición y fragmentación'),
('Grúa torre', 'Grúa de gran altura para construcción'),
('Grúa móvil', 'Grúa con capacidad de movilidad'),
('Plataforma elevadora', 'Acceso a alturas para trabajos'),
('Manipulador telescópico', 'Levanta y transporta cargas con brazo extensible'),
('Barredora', 'Limpia superficies de residuos'),
('Quitanieves', 'Retira nieve de carreteras'),
('Esparcidor de sal', 'Distribuye sal para deshielo'),
('Cosechadora', 'Cosecha cultivos agrícolas'),
('Tractor', 'Vehículo de tracción para diversos trabajos'),
('Sembradora', 'Siembra semillas en el suelo'),
('Fumigadora', 'Aplica pesticidas y fertilizantes'),
('Empacadora', 'Empaca heno y otros materiales'),
('Desbrozadora', 'Corta hierba y maleza'),
('Podadora', 'Corta ramas de árboles'),
('Motosierra', 'Corta madera con una cadena dentada'),
('Astilladora', 'Reduce madera a astillas'),
('Soplador de hojas', 'Limpia hojas y residuos con aire'),
('Cortacésped', 'Corta el césped'),
('Motocultor', 'Prepara el suelo para la siembra'),
('Retroaraña', 'Excavadora con patas de araña para terrenos difíciles'),
('Excavadora de cadenas', 'Excavadora con orugas para terrenos blandos'),
('Excavadora de ruedas', 'Excavadora con ruedas para mayor movilidad'),
('Minicargadora', 'Cargadora compacta para espacios reducidos'),
('Dumper', 'Transporta materiales en obras'),
('Volquete', 'Vehículo para transportar materiales a granel'),
('Camión hormigonera', 'Transporta y mezcla hormigón'),
('Camión grúa', 'Camión con grúa incorporada'),
('Camión plataforma', 'Transporta cargas pesadas y voluminosas'),
('Furgoneta', 'Vehículo utilitario para transporte de mercancías'),
('Carretilla elevadora', 'Levanta y transporta cargas paletizadas'),
('Transpaleta', 'Transporta cargas paletizadas manualmente'),
('Apilador', 'Apila cargas paletizadas'),
('Soldadora por arco', 'Soldadura de metales por arco eléctrico'),
('Soldadora MIG/MAG', 'Soldadura de metales con gas protector'),
('Soldadora TIG', 'Soldadura de metales con tungsteno y gas inerte'),
('Máquina de corte por plasma', 'Corta metales con plasma'),
('Máquina de corte por láser', 'Corta metales con láser'),
('Plegadora', 'Dobla láminas de metal'),
('Corte por chorro de agua', 'Corta materiales con agua a alta presión'),
('Máquina de soldadura por puntos', 'Solda metales por puntos'),
('Rectificadora', 'Rectifica superficies metálicas'),
('Mandrinadora', 'Aumenta el diámetro de agujeros'),
('Cepilladora', 'Mecaniza superficies planas'),
('Escariadora', 'Acaba agujeros con precisión'),
('Roscadora', 'Crea roscas en agujeros'),
('Sierra de cinta', 'Corta materiales con una cinta dentada'),
('Sierra circular', 'Corta materiales con una hoja circular'),
('Sierra de vaivén', 'Corta materiales con movimiento de vaivén'),
('Taladro de columna', 'Taladro estacionario para mayor precisión'),
('Taladro de mano', 'Taladro portátil'),
('Fresadora CNC', 'Fresadora controlada por computadora'),
('Torno CNC', 'Torno controlado por computadora'),
('Lijadora orbital', 'Lija con movimiento orbital'),
('Lijadora de banda', 'Lija con una banda abrasiva'),
('Pulidora', 'Pule superficies para dar brillo'),
('Granalladora', 'Limpia superficies con granalla'),
('Arenadora', 'Limpia superficies con arena a presión'),
('Máquina de equilibrado', 'Equilibra rotores y piezas giratorias'),
('Robot de soldadura', 'Solda automáticamente'),
('Robot de pintura', 'Pinta automáticamente'),
('Impresora 3D', 'Crea objetos tridimensionales'),
('Escáner 3D', 'Digitaliza objetos en 3D'),
('Máquina de corte por hilo', 'Corta metales con un hilo conductor'),
('Máquina de electroerosión', 'Corta metales con descargas eléctricas'),
('Calibradora', 'Mide con precisión'),
('Medidor de espesores', 'Mide el espesor de materiales'),
('Analizador de vibraciones', 'Analiza vibraciones en máquinas'),
('Termómetro infrarrojo', 'Mide la temperatura sin contacto'),
('Cámara termográfica', 'Detecta calor y crea imágenes térmicas'),
('Detector de fugas de gas', 'Detecta fugas de gas'),
('Detector de metales', 'Detecta metales'),
('Nivel láser', 'Proyecta líneas láser para nivelación')
;

INSERT INTO tecnico (nombre, especialidad, telefono, correo) VALUES
('Juan Pérez', 'Mecánico', '5550101', 'juan.perez@example.com'),
('María López', 'Electricista', '5550102', 'maria.lopez@example.com'),
('Carlos Hernández', 'Mecánico', '5550103', 'carlos.hernandez@example.com'),
('Ana García', 'Técnico en Refrigeración', '5550104', 'ana.garcia@example.com'),
('Luis Martínez', 'Mecánico', '5550105', 'luis.martinez@example.com'),
('Elena Ruiz', 'Electricista', '5550106', 'elena.ruiz@example.com'),
('Jorge Torres', 'Mecánico', '5550107', 'jorge.torres@example.com'),
('Sofía Sánchez', 'Técnico en Automatización', '5550108', 'sofia.sanchez@example.com'),
('Ricardo Gómez', 'Mecánico', '5550109', 'ricardo.gomez@example.com'),
('Laura Díaz', 'Técnico en Electrónica', '5550110', 'laura.diaz@example.com'),
('Fernando Ramírez', 'Mecánico', '5550111', 'fernando.ramirez@example.com'),
('Patricia Jiménez', 'Electricista', '5550112', 'patricia.jimenez@example.com'),
('Andrés Morales', 'Mecánico', '5550113', 'andres.morales@example.com'),
('Valeria Castro', 'Técnico en Mantenimiento', '5550114', 'valeria.castro1@example.com'),
('Diego Romero', 'Mecánico', '5550115', 'diego.romero@example.com'),
('Claudia Mendoza', 'Electricista', '5550116', 'claudia.mendoza@example.com'),
('Samuel Vargas', 'Mecánico', '5550117', 'samuel.vargas@example.com'),
('Natalia Ortega', 'Técnico en Refrigeración', '5550118', 'natalia.ortega@example.com'),
('Javier Castillo', 'Mecánico', '5550119', 'javier.castillo@example.com'),
('Lucía Aguirre', 'Técnico en Electrónica', '5550120', 'lucia.aguirre@example.com'),
('Pablo Silva', 'Mecánico', '5550121', 'pablo.silva@example.com'),
('Mónica Peña', 'Electricista', '5550122', 'monica.pena@example.com'),
('Iván Medina', 'Mecánico', '5550123', 'ivan.medina@example.com'),
('Carmen Castillo', 'Técnico en Automatización', '5550124', 'carmen.castillo@example.com'),
('Rafael Salazar', 'Mecánico', '5550125', 'rafael.salazar@example.com'),
('Jessica Torres', 'Electricista', '5550126', 'jessica.torres@example.com'),
('Esteban Ríos', 'Mecánico', '5550127', 'esteban.rios@example.com'),
('Silvia Romero', 'Técnico en Mantenimiento', '5550128', 'silvia.romero@example.com'),
('Antonio Fernández', 'Mecánico', '5550129', 'antonio.fernandez@example.com'),
('Gabriela Soto', 'Electricista', '5550130', 'gabriela.soto@example.com'),
('Hugo Morales', 'Mecánico', '5550131', 'hugo.morales@example.com'),
('Teresa León', 'Técnico en Refrigeración', '5550132', 'teresa.leon@example.com'),
('Arturo López', 'Mecánico', '5550133', 'arturo.lopez@example.com'),
('Ana Belén', 'Técnico en Electrónica', '5550134', 'ana.belen@example.com'),
('Miguel Ángel', 'Mecánico', '5550135', 'miguel.angel@example.com'),
('Clara Ríos', 'Electricista', '5550136', 'clara.rios@example.com'),
('Ricardo Pinto', 'Mecánico', '5550137', 'ricardo.pinto@example.com'),
('Samantha Vega', 'Técnico en Automatización', '5550138', 'samantha.vega@example.com'),
('Fernando Méndez', 'Mecánico', '5550139', 'fernando.mendez@example.com'),
('Lina Castro', 'Técnico en Mantenimiento', '5550140', 'lina.castro@example.com'),
('Gustavo Romero', 'Mecánico', '5550141', 'gustavo.romero@example.com'),
('Nadia Medina', 'Electricista', '5550142', 'nadia.medina@example.com'),
('Alberto Flores', 'Mecánico', '5550143', 'alberto.flores@example.com'),
('Marisol Aguirre', 'Técnico en Refrigeración', '5550144', 'marisol.aguirre@example.com'),
('Emanuel Torres', 'Mecánico', '5550145', 'emanuel.torres@example.com'),
('Cristina Silva', 'Técnico en Electrónica', '5550146', 'cristina.silva@example.com'),
('Héctor Ramírez', 'Mecánico', '5550147', 'hector.ramirez@example.com'),
('Pamela Jiménez', 'Electricista', '5550148', 'pamela.jimenez@example.com'),
('Luis Fernando', 'Mecánico', '5550149', 'luis.fernando@example.com'),
('Julia Soto', 'Técnico en Automatización', '5550150', 'julia.soto@example.com'),
('Omar Vargas', 'Mecánico', '5550151', 'omar.vargas@example.com'),
('Lorena Díaz', 'Técnico en Mantenimiento', '5550152', 'lorena.diaz1@example.com'),
('Sergio Morales', 'Mecánico', '5550153', 'sergio.morales@example.com'),
('Florencia Castro', 'Electricista', '5550154', 'florencia.castro@example.com'),
('Nicolás Pérez', 'Mecánico', '5550155', 'nicolas.perez@example.com'),
('Alba López', 'Técnico en Refrigeración', '5550156', 'alba.lopez1@example.com'),
('Felipe Torres', 'Mecánico', '5550157', 'felipe.torres@example.com'),
('Valentina Gutiérrez', 'Técnico en Electrónica', '5550158', 'valentina.gutierrez@example.com'),
('Cristian Herrera', 'Mecánico', '5550159', 'cristian.herrera@example.com'),
('Diana Ruiz', 'Electricista', '5550160', 'diana.ruiz@example.com'),
('Roberto Sánchez', 'Mecánico', '5550161', 'roberto.sanchez@example.com'),
('Tatiana Medina', 'Técnico en Automatización', '5550162', 'tatiana.medina@example.com'),
('Rodolfo Salazar', 'Mecánico', '5550163', 'rodolfo.salazar@example.com'),
('Susana León', 'Técnico en Mantenimiento', '5550164', 'susana.leon@example.com'),
('Julio Torres', 'Mecánico', '5550165', 'julio.torres@example.com'),
('María Fernanda', 'Electricista', '5550166', 'maria.fernanda@example.com'),
('Emilio Gómez', 'Mecánico', '5550167', 'emilio.gomez@example.com'),
('Lorena Ortiz', 'Técnico en Refrigeración', '5550168', 'lorena.ortiz@example.com'),
('Victor Hugo', 'Mecánico', '5550169', 'victor.hugo@example.com'),
('Nadia Gómez', 'Técnico en Electrónica', '5550170', 'nadia.gomez@example.com'),
('Luis Alberto', 'Mecánico', '5550171', 'luis.alberto@example.com'),
('Cecilia Castro', 'Electricista', '5550172', 'cecilia.castro@example.com'),
('Emiliano Ríos', 'Mecánico', '5550173', 'emiliano.rios@example.com'),
('Verónica Vega', 'Técnico en Automatización', '5550174', 'veronica.vega@example.com'),
('Jhonny Fernández', 'Mecánico', '5550175', 'jhonny.fernandez@example.com'),
('Gloria Medina', 'Técnico en Mantenimiento', '5550176', 'gloria.medina@example.com'),
('Carlos Alberto', 'Mecánico', '5550177', 'carlos.alberto@example.com'),
('Santiago Pérez', 'Electricista', '5550178', 'santiago.perez@example.com'),
('Fernando Sánchez', 'Mecánico', '5550179', 'fernando.sanchez@example.com'),
('Marta López', 'Técnico en Refrigeración', '5550180', 'marta.lopez@example.com'),
('Juan Pérez1', 'Electricidad', '5551234567', 'juan.perez1@example.com'),
('María García1', 'Mecánica', '5559876543', 'maria.garcia1@example.com'),
('Pedro López1', 'Electrónica', '5552468013', 'pedro.lopez1@example.com'),
('Ana Martínez1', 'Informática', '5551357924', 'ana.martinez1@example.com'),
('Luis Rodríguez1', 'Telecomunicaciones', '5557890123', 'luis.rodriguez1@example.com'),
('Sofía Fernández1', 'Aire acondicionado', '5553692581', 'sofia.fernandez1@example.com'),
('Carlos Gómez1', 'Fontanería', '5558529630', 'carlos.gomez1@example.com'),
('Laura Sánchez1', 'Carpintería', '5551472583', 'laura.sanchez1@example.com'),
('Javier Torres1', 'Soldadura', '5553579514', 'javier.torres1@example.com'),
('Elena Ramírez1', 'Refrigeración', '5554680246', 'elena.ramirez1@example.com'),
('Manuel Flores1', 'Automatización', '5555791357', 'manuel.flores1@example.com'),
('Isabel Cruz1', 'Robótica', '5556802468', 'isabel.cruz1@example.com'),
('Ricardo Medina1', 'Redes', '5557913579', 'ricardo.medina1@example.com'),
('Carmen Silva1', 'Domótica', '5558024680', 'carmen.silva1@example.com'),
('Francisco Herrera1', 'Energías renovables', '5559135791', 'francisco.herrera1@example.com'),
('Patricia Castro1', 'Sistemas de seguridad', '5550246802', 'patricia.castro1@example.com'),
('David Vargas1', 'Mantenimiento industrial', '5551357923', 'david.vargas1@example.com'),
('Marta Ortega1', 'Control de calidad', '5552468014', 'marta.ortega1@example.com'),
('Sergio Jiménez1', 'Diseño CAD', '5553579135', 'sergio.jimenez1@example.com'),
('Andrea Ruiz1', 'Impresión 3D', '5554680247', 'andrea.ruiz1@example.com')
;

INSERT INTO repuesto (nombre, descripcion, stock_actual, precio_unitario) VALUES
('Filtro de aire', 'Filtro de aire para excavadora', 50, 35.50),
('Bomba hidráulica', 'Bomba hidráulica para excavadora', 10, 450.00),
('Cilindro hidráulico', 'Cilindro hidráulico para excavadora', 15, 280.00),
('Cuchilla', 'Cuchilla para bulldozer', 30, 120.00),
('Cadena', 'Cadena para bulldozer', 8, 800.00),
('Motor de arranque', 'Motor de arranque para grúa', 25, 180.00),
('Cable de acero', 'Cable de acero para grúa', 40, 95.00),
('Gancho', 'Gancho para grúa', 18, 75.00),
('Rodillo', 'Rodillo para cinta transportadora', 60, 45.00),
('Correa', 'Correa para cinta transportadora', 70, 30.00),
('Engranaje', 'Engranaje para torno', 20, 60.00),
('Portaherramientas', 'Portaherramientas para torno', 12, 100.00),
('Fresa', 'Fresa para fresadora', 35, 55.00),
('Husillo', 'Husillo para fresadora', 15, 110.00),
('Junta', 'Junta para prensa hidráulica', 80, 15.00),
('Válvula', 'Válvula para prensa hidráulica', 22, 85.00),
('Filtro de aceite', 'Filtro de aceite para compresor', 45, 28.00),
('Manguera', 'Manguera para compresor', 55, 40.00),
('Bobina', 'Bobina para generador', 10, 250.00),
('Bujía', 'Bujía para generador', 75, 12.00),
('Horquilla', 'Horquilla para montacargas', 12, 300.00),
('Rueda', 'Rueda para montacargas', 30, 150.00),
('Electrodo', 'Electrodo para soldadora', 100, 8.00),
('Boquilla', 'Boquilla para soldadora', 65, 18.00),
('Brazo', 'Brazo para retroexcavadora', 14, 350.00),
('Cazo', 'Cazo para retroexcavadora', 28, 200.00),
('Cuchilla', 'Cuchilla para motoniveladora', 32, 130.00),
('Hoja', 'Hoja para motoniveladora', 22, 250.00),
('Paleta', 'Paleta para pavimentadora', 40, 70.00),
('Tornillo', 'Tornillo para pavimentadora', 150, 2.00),
('Eje', 'Eje para hormigonera', 18, 90.00),
('Paleta', 'Paleta para hormigonera', 42, 65.00),
('Placa vibratoria', 'Placa vibratoria para compactadora', 25, 110.00),
('Rodillo', 'Rodillo para compactadora', 38, 80.00),
('Broca', 'Broca para perforadora', 70, 25.00),
('Corona', 'Corona para perforadora', 16, 95.00),
('Boquilla', 'Boquilla para pulverizador', 80, 10.00),
('Filtro', 'Filtro para pulverizador', 62, 20.00),
('Cuchilla', 'Cuchilla para cizalla', 35, 45.00),
('Cuchilla', 'Cuchilla para cizalla', 35, 45.00),
('Banda', 'Banda para lijadora', 120, 5.00),
('Disco', 'Disco para lijadora', 90, 12.00),
('Portabrocas', 'Portabrocas para taladro', 55, 30.00),
('Broca', 'Broca para taladro', 110, 10.00),
('Cuchilla', 'Cuchilla para bulldozer', 30, 120.00),
('Cadena', 'Cadena para bulldozer', 8, 800.00),
('Motor', 'Motor para cargadora frontal', 15, 600.00),
('Neumático', 'Neumático para cargadora frontal', 20, 350.00),
('Cuchilla', 'Cuchilla para zanjadora', 40, 140.00),
('Cadena', 'Cadena para zanjadora', 10, 900.00),
('Placa vibratoria', 'Placa vibratoria para apisonadora', 30, 120.00),
('Rodillo', 'Rodillo para apisonadora', 45, 90.00),
('Bomba', 'Bomba para planta de asfalto', 12, 550.00),
('Quemador', 'Quemador para planta de asfalto', 25, 220.00),
('Eje', 'Eje para hormigonera móvil', 20, 100.00),
('Paleta', 'Paleta para hormigonera móvil', 45, 70.00),
('Martillo', 'Martillo para rompedora', 18, 400.00),
('Punta', 'Punta para rompedora', 60, 30.00),
('Motor', 'Motor para martillo hidráulico', 10, 700.00),
('Cilindro', 'Cilindro para martillo hidráulico', 15, 300.00),
('Cable', 'Cable para grúa torre', 35, 110.00),
('Polea', 'Polea para grúa torre', 28, 80.00),
('Motor', 'Motor para grúa móvil', 14, 650.00),
('Contrapeso', 'Contrapeso para grúa móvil', 22, 280.00),
('Batería', 'Batería para plataforma elevadora', 50, 75.00),
('Rueda', 'Rueda para plataforma elevadora', 30, 160.00),
('Brazo', 'Brazo para manipulador telescópico', 16, 400.00),
('Horquilla', 'Horquilla para manipulador telescópico', 25, 320.00),
('Cepillo', 'Cepillo para barredora', 70, 25.00),
('Motor', 'Motor para barredora', 12, 480.00),
('Cuchilla', 'Cuchilla para quitanieves', 35, 130.00),
('Arado', 'Arado para quitanieves', 20, 270.00),
('Boquilla', 'Boquilla para esparcidor de sal', 80, 10.00),
('Motor', 'Motor para esparcidor de sal', 15, 450.00),
('Cuchilla', 'Cuchilla para cosechadora', 40, 150.00),
('Correa', 'Correa para cosechadora', 65, 35.00),
('Neumático', 'Neumático para tractor', 25, 400.00),
('Filtro de aceite', 'Filtro de aceite para tractor', 55, 30.00),
('Sembradora', 'Sembradora', 18, 650.00),
('Bomba', 'Bomba para fumigadora', 20, 220.00),
('Boquilla', 'Boquilla para fumigadora', 90, 15.00),
('Cuchilla', 'Cuchilla para empacadora', 30, 140.00),
('Correa', 'Correa para empacadora', 70, 40.00),
('Cuchilla', 'Cuchilla para desbrozadora', 45, 50.00),
('Hilo', 'Hilo para desbrozadora', 120, 8.00),
('Cuchilla', 'Cuchilla para podadora', 50, 60.00),
('Cadena', 'Cadena para motosierra', 40, 45.00),
('Espada', 'Espada para motosierra', 25, 70.00),
('Cuchilla', 'Cuchilla para astilladora', 35, 110.00),
('Cuchilla', 'Cuchilla para astilladora', 35, 110.00),
('Motor', 'Motor para soplador de hojas', 15, 280.00),
('Aspa', 'Aspa para soplador de hojas', 80, 20.00),
('Cuchilla', 'Cuchilla para cortacésped', 60, 28.00),
('Motor', 'Motor para cortacésped', 20, 180.00),
('Cuchilla', 'Cuchilla para motocultor', 40, 55.00),
('Arado', 'Arado para motocultor', 30, 85.00),
('Cadena', 'Cadena para retroaraña', 10, 800.00),
('Cazo', 'Cazo para retroaraña', 25, 250.00),
('Cuchilla', 'Cuchilla para excavadora de cadenas', 30, 120.00),
('Cadena', 'Cadena para excavadora de cadenas', 8, 800.00)
;

INSERT INTO maquinaria (nombre, modelo, serie, fecha_adquisicion, ubicacion, tipo_id) VALUES
('Excavadora Caterpillar 320', 'C320', 'CAT3201234', '2022-01-15', 'Obra A', 1),
('Grúa Liebherr LTM 1200', 'LTM1200', 'LIEB1200567', '2021-06-10', 'Obra B', 2),
('Cinta transportadora Powerscreen', 'CTP202', 'POW2023456', '2020-05-20', 'Obra C', 3),
('Torno Haas TL-1', 'TL1', 'HAAS123456', '2019-02-05', 'Obra D', 4),
('Fresadora Bridgeport', 'BRIDGEPORT', 'BRIDGE567891', '2020-11-11', 'Obra E', 5),
('Prensa hidráulica Enerpac', 'EHYD123', 'ENER123456', '2021-03-22', 'Obra F', 6),
('Compresor de aire Ingersoll Rand', 'IR123', 'ING123456', '2022-07-07', 'Obra G', 7),
('Generador Honda EU2200i', 'EU2200i', 'HONDA123456', '2021-09-15', 'Obra H', 8),
('Montacargas Toyota 8FGCU25', '8FGCU25', 'TOYOTA123456', '2022-10-30', 'Obra I', 9),
('Soldadora Lincoln Electric', 'POWER MIG 210', 'LINCOLN123456', '2020-04-12', 'Obra J', 10),
('Retroexcavadora John Deere 310', '310', 'JD3101234', '2021-01-18', 'Obra K', 11),
('Motoniveladora Caterpillar 12M', '12M', 'CAT12M5678', '2022-08-15', 'Obra L', 12),
('Pavimentadora Volvo EC950F', 'EC950F', 'VOLVO123456', '2021-05-25', 'Obra M', 13),
('Hormigonera Cifa K43L', 'K43L', 'CIFA123456', '2020-03-14', 'Obra N', 14),
('Compactadora Ammann ASC 100', 'ASC100', 'AMMANN123456', '2021-11-20', 'Obra O', 15),
('Perforadora Atlas Copco', 'Bohrgerät', 'ATLAS123456', '2022-02-28', 'Obra P', 16),
('Pulverizador John Deere', 'R4045', 'JD40451234', '2021-08-09', 'Obra Q', 17),
('Cizalla Haco', 'HACO123', 'HACO123456', '2020-06-18', 'Obra R', 18),
('Lijadora Makita', 'BO3710', 'MAKITA123456', '2021-07-07', 'Obra S', 19),
('Taladro Bosch', 'GBH 2-26', 'BOSCH123456', '2020-08-16', 'Obra T', 20),
('Bulldozer Caterpillar D6T', 'D6T', 'CATD61234', '2022-03-05', 'Obra U', 21),
('Cargadora frontal Case 570N', '570N', 'CASE123456', '2021-04-14', 'Obra V', 22),
('Zanjadora Vermeer', 'RTX1250', 'VER123456', '2020-09-21', 'Obra W', 23),
('Apisonadora Wacker Neuson', 'DV70', 'WACKER123456', '2021-02-12', 'Obra X', 24),
('Planta de asfalto ADM', 'EX102', 'ADM123456', '2022-06-30', 'Obra Y', 25),
('Hormigonera móvil Cifa', 'K35H', 'CIFA123456', '2021-10-10', 'Obra Z', 26),
('Rompedora Hilti', 'TE 1000-AVR', 'HILTI123456', '2020-12-15', 'Obra AA', 27),
('Martillo hidráulico JCB', 'HM 12', 'JCB123456', '2021-01-02', 'Obra AB', 28),
('Grúa torre Potain', 'MC 85', 'POTAIN123456', '2022-04-18', 'Obra AC', 29),
('Grúa móvil Grove', 'GMK4100L', 'GROVE123456', '2020-10-28', 'Obra AD', 30),
('Plataforma elevadora Genie', 'Z-45/25J', 'GENIE123456', '2021-03-19', 'Obra AE', 31),
('Manipulador telescópico JLG', 'G6-42A', 'JLG123456', '2022-05-05', 'Obra AF', 32),
('Barredora industrial Tennant', 'M30', 'TENNANT123456', '2020-11-30', 'Obra AG', 33),
('Quitanieves Bobcat', 'S570', 'BOBCAT123456', '2021-12-22', 'Obra AH', 34),
('Esparcidor de sal SnowEx', 'SP-575', 'SNOWEX123456', '2022-01-14', 'Obra AI', 35),
('Cosechadora Case IH', 'Axial-Flow 8250', 'CASEIH123456', '2021-09-03', 'Obra AJ', 36),
('Tractor John Deere', '8R 310', 'JD83101234', '2020-07-15', 'Obra AK', 37),
('Sembradora de precisión Monosem', 'NG+', 'MONO123456', '2021-08-21', 'Obra AL', 38),
('Fumigadora GVM', 'Hurricane', 'GVM123456', '2022-02-05', 'Obra AM', 39),
('Empacadora New Holland', 'BB1290 Plus', 'NH123456', '2021-10-13', 'Obra AN', 40),
('Desbrozadora Stihl', 'FS 131', 'STIHL123456', '2020-04-30', 'Obra AO', 41),
('Podadora de árboles Husqvarna', 'T435', 'HUSQ123456', '2021-06-01', 'Obra AP', 42),
('Motosierra Echo', 'CS-590', 'ECHO123456', '2020-08-12', 'Obra AQ', 43),
('Astilladora WoodMaxx', 'WM-8H', 'WOOD123456', '2021-09-22', 'Obra AR', 44),
('Soplador de hojas Echo', 'PB-580T', 'ECHO123456', '2020-11-11', 'Obra AS', 45),
('Cortacésped Honda', 'HRX217VLA', 'HONDA123456', '2022-03-17', 'Obra AT', 46),
('Motocultor BCS', '738', 'BCS123456', '2021-07-10', 'Obra AU', 47),
('Retroexcavadora Caterpillar 420F', '420F', 'CAT4204567', '2022-04-23', 'Obra AV', 48),
('Excavadora de cadenas Hitachi', 'ZX210LC-5', 'HITACHI123456', '2021-05-30', 'Obra AW', 49),
('Excavadora de ruedas Komatsu', 'PW160-11', 'KOMATSU123456', '2022-06-15', 'Obra AX', 50),
('Minicargadora Bobcat S650', 'S650', 'BOBCAT123456', '2021-08-12', 'Obra AY', 51),
('Dumper Volvo A25G', 'A25G', 'VOLVO123456', '2020-09-10', 'Obra AZ', 52),
('Volquete Scania', 'G450', 'SCANIA123456', '2021-02-28', 'Obra BA', 53),
('Camión hormigonera MAN', 'TGS 32.400', 'MAN123456', '2022-01-10', 'Obra BB', 54),
('Cargadora de ruedas Komatsu', 'WA380-8', 'KOMATSU123456', '2021-10-01', 'Obra BC', 55),
('Cosechadora de forraje Claas', 'JAGUAR 960', 'CLAAS123456', '2022-03-25', 'Obra BD', 56),
('Fresadora de pavimento Wirtgen', 'W 210i', 'WIRTGEN123456', '2021-04-18', 'Obra BE', 57),
('Cortadora de concreto Husqvarna', 'K 970', 'HUSQVARNA123456', '2020-11-29', 'Obra BF', 58),
('Equipo de corte por plasma Hypertherm', 'Powermax45', 'HYPER123456', '2021-08-08', 'Obra BG', 59),
('Robot de soldadura KUKA', 'KR 16', 'KUKA123456', '2022-05-12', 'Obra BH', 60),
('Impresora 3D Ultimaker', 'S5', 'ULTIMAKER123456', '2021-01-24', 'Obra BI', 61),
('Cortadora de césped Toro', 'TimeCutter', 'TORO123456', '2020-07-04', 'Obra BJ', 62),
('Barredora de calles Elgin', 'Eagle', 'ELGIN123456', '2021-06-20', 'Obra BK', 63),
('Excavadora de orugas Kobelco', 'SK210LC', 'KOBELCO123456', '2020-08-30', 'Obra BL', 64),
('Grúa articulada Genie', 'Z-60/37', 'GENIE123456', '2021-02-15', 'Obra BM', 65),
('Grúa de pluma Manitou', 'MHT 10130', 'MANITOU123456', '2022-04-04', 'Obra BN', 66),
('Cosechadora de caña de azúcar Case', 'A8800', 'CASE123456', '2021-07-15', 'Obra BO', 67),
('Tractor de oruga Caterpillar', 'D11', 'CATD110123', '2020-09-29', 'Obra BP', 68),
('Cargadora sobre ruedas Hyundai', 'HL970A', 'HYUNDAI123456', '2021-10-10', 'Obra BQ', 69),
('Fresadora de metales DMG Mori', 'DMU 50', 'DMG123456', '2022-01-05', 'Obra BR', 70),
('Máquina de corte por láser Trumpf', 'TruLaser 3030', 'TRUMPF123456', '2021-03-22', 'Obra BS', 71),
('Pulidora de pisos HTC', 'Duratiq 5', 'HTC123456', '2022-06-15', 'Obra BT', 72),
('Máquina de inyección de plástico Arburg', 'ALLROUNDER 370', 'ARBURG123456', '2021-04-18', 'Obra BU', 73),
('Plataforma de trabajo móvil Altrad', 'PMP', 'ALTRAD123456', '2020-11-15', 'Obra BV', 74),
('Compresor de tornillo Atlas Copco', 'GA 11', 'ATLAS123456', '2021-05-05', 'Obra BW', 75),
('Hidrolavadora Karcher', 'K 5', 'KARCHER123456', '2021-02-12', 'Obra BX', 76),
('Cortadora de azulejos Rubi', 'Fórmula', 'RUB123456', '2020-12-01', 'Obra BY', 77),
('Máquina de soldadura por arco Miller', 'Multimatic 215', 'MILLER123456', '2021-08-22', 'Obra BZ', 78),
('Máquina de moldeo por inyección Engel', 'Victory 200', 'ENGEL123456', '2022-03-08', 'Obra CA', 79),
('Compresor de aire Sullair', '185', 'SULLAIR123456', '2021-01-30', 'Obra CB', 80),
('Mezcladora de cemento CEMEX', 'M-300', 'CEMEX123456', '2020-10-18', 'Obra CC', 81),
('Plataforma elevadora Skyjack', 'SJIII 3219', 'SKY123456', '2021-09-17', 'Obra CD', 82),
('Compresora de aire Boge', 'C 30', 'BOGE123456', '2022-02-20', 'Obra CE', 83),
('Cortadora de césped John Deere', 'X350', 'JD123456', '2021-06-03', 'Obra CF', 84),
('Grúa de carga Terex', 'AC 100', 'TEREX123456', '2020-11-28', 'Obra CG', 85),
('Compactadora de suelos Case', 'CB224E', 'CASE123456', '2021-04-07', 'Obra CH', 86),
('Martillo perforador Bosch', 'GBH 18V-21', 'BOSCH123456', '2021-07-25', 'Obra CI', 87),
('Cortadora de tubos Ridgid', '12-R', 'RIDGID123456', '2022-05-09', 'Obra CJ', 88),
('Sierra de cinta Grizzly', 'G0555LX', 'GRIZZLY123456', '2020-08-19', 'Obra CK', 89),
('Máquina de chorro de arena', 'BlastPro', 'BLAST123456', '2021-10-11', 'Obra CL', 90),
('Lavadora de alta presión Kärcher', 'K5 Premium', 'KAERCHER123456', '2021-02-08', 'Obra CM', 91),
('Prensa de troquelado', 'Dake', 'DAKE123456', '2022-04-01', 'Obra CN', 92),
('Cortadora de pernos', 'Pneumatic', 'PNEUM123456', '2021-06-24', 'Obra CO', 93),
('Cortadora de mármol', 'Diamond', 'DIAMOND123456', '2020-11-02', 'Obra CP', 94),
('Pulidora de metales', 'CNC', 'CNC123456', '2021-01-15', 'Obra CQ', 95),
('Máquina de pulido', 'Polisher', 'POLISH123456', '2022-03-18', 'Obra CR', 96),
('Máquina de corte CNC', 'CNC-3000', 'CNC123456', '2021-05-30', 'Obra CS', 97),
('Máquina de soldadura TIG', 'WeldMaster', 'WELD123456', '2020-08-11', 'Obra CT', 98),
('Máquina de corte por chorro de agua', 'AquaCut', 'AQUA123456', '2021-10-15', 'Obra CU', 99),
('Plataforma de trabajo Genie', 'Z-33/18', 'GENIE123456', '2022-02-14', 'Obra CV', 100)
;

INSERT INTO tipo_mantenimiento (nombre, descripcion) VALUES
('Correctivo', 'Reparación tras falla'),
('Preventivo', 'Mantenimiento programado'),
('Predictivo', 'Basado en condiciones'),
('Lubricación', 'Aplicación de lubricantes'),
('Inspección', 'Revisión general'),
('Calibración', 'Ajuste de instrumentos'),
('Limpieza', 'Remoción de residuos'),
('Reemplazo', 'Cambio de partes'),
('Ajuste', 'Corrección menor'),
('Diagnóstico', 'Detección de fallas')
;

INSERT INTO mantenimiento (maquinaria_id, tecnico_id, tipo_mantenimiento_id, fecha, duracion_horas, costo, observaciones) VALUES
(1, 1, 2, '2024-05-05', 4, 250.00, 'Mantenimiento preventivo rutinario.'),
(2, 2, 1, '2024-05-10', 8, 675.50, 'Reparación de fuga hidráulica.'),
(3, 3, 3, '2024-05-15', 2, 120.75, 'Análisis de vibración y ajuste de banda.'),
(4, 4, 4, '2024-05-20', 1, 55.00, 'Lubricación de rodamientos principales.'),
(5, 5, 5, '2024-05-25', 3, 0.00, 'Inspección visual y funcional.'),
(6, 6, 6, '2024-05-30', 2, 180.20, 'Calibración de sensor de presión.'),
(7, 7, 7, '2024-06-05', 1, 30.50, 'Limpieza de filtros de aire.'),
(8, 8, 8, '2024-06-10', 6, 410.90, 'Reemplazo de batería.'),
(9, 9, 9, '2024-06-15', 1, 45.00, 'Ajuste de tensión de cadena.'),
(10, 10, 10, '2024-06-20', 3, 225.30, 'Diagnóstico de problema de arranque.'),
(11, 11, 2, '2024-06-25', 4, 265.00, 'Mantenimiento preventivo de motor.'),
(12, 12, 1, '2024-06-30', 7, 590.80, 'Reparación de sistema de dirección.'),
(13, 13, 3, '2024-07-05', 2, 110.15, 'Medición de desgaste de neumáticos.'),
(14, 14, 4, '2024-07-10', 1, 60.00, 'Lubricación de juntas.'),
(15, 15, 5, '2024-07-15', 2, 0.00, 'Revisión de niveles de fluidos.'),
(16, 16, 6, '2024-07-20', 3, 210.50, 'Calibración de sistema de pesaje.'),
(17, 17, 7, '2024-07-25', 1, 25.75, 'Limpieza de cabina.'),
(18, 18, 8, '2024-07-30', 5, 380.40, 'Reemplazo de filtro de aceite.'),
(19, 19, 9, '2024-08-05', 1, 50.00, 'Ajuste de frenos.'),
(20, 20, 10, '2024-08-10', 4, 290.60, 'Diagnóstico de ruido inusual.'),
(21, 21, 2, '2024-08-15', 3, 230.00, 'Mantenimiento preventivo de transmisión.'),
(22, 22, 1, '2024-08-20', 9, 710.20, 'Reparación de brazo hidráulico.'),
(23, 23, 3, '2024-08-25', 1, 95.90, 'Inspección de soldaduras.'),
(24, 24, 4, '2024-08-30', 1, 70.00, 'Lubricación de cilindros.'),
(25, 25, 5, '2024-09-05', 3, 0.00, 'Verificación de luces y señales.'),
(26, 26, 6, '2024-09-10', 2, 195.80, 'Calibración de sensor de temperatura.'),
(27, 27, 7, '2024-09-15', 1, 35.25, 'Limpieza de sistema de enfriamiento.'),
(28, 28, 8, '2024-09-20', 7, 450.10, 'Reemplazo de correa.'),
(29, 29, 9, '2024-09-25', 1, 40.00, 'Ajuste de holgura de dirección.'),
(30, 30, 10, '2024-10-01', 2, 185.70, 'Diagnóstico de bajo rendimiento.'),
(31, 1, 2, '2024-10-05', 5, 280.00, 'Mantenimiento preventivo de frenos.'),
(32, 2, 1, '2024-10-10', 6, 630.50, 'Reparación de fuga de aceite de motor.'),
(33, 3, 3, '2024-10-15', 3, 130.45, 'Análisis de aceite hidráulico.'),
(34, 4, 4, '2024-10-20', 1, 65.00, 'Lubricación de engranajes.'),
(35, 5, 5, '2024-10-25', 2, 0.00, 'Inspección de mangueras y conexiones.'),
(36, 6, 6, '2024-10-30', 4, 240.90, 'Calibración de tacómetro.'),
(37, 7, 7, '2024-11-05', 1, 28.10, 'Limpieza de radiador.'),
(38, 8, 8, '2024-11-10', 8, 515.60, 'Reemplazo de bujías.'),
(39, 9, 9, '2024-11-15', 1, 55.00, 'Ajuste de embrague.'),
(40, 10, 10, '2024-11-20', 3, 200.30, 'Diagnóstico de sobrecalentamiento.'),
(41, 11, 2, '2024-11-25', 4, 270.00, 'Mantenimiento preventivo de sistema eléctrico.'),
(42, 12, 1, '2024-11-30', 7, 615.70, 'Reparación de sistema de elevación.'),
(43, 13, 3, '2024-12-05', 2, 105.20, 'Medición de presión de neumáticos.'),
(44, 14, 4, '2024-12-10', 1, 75.00, 'Lubricación de cables.'),
(45, 15, 5, '2024-12-15', 3, 0.00, 'Verificación de alarmas.'),
(46, 16, 6, '2024-12-20', 2, 170.40, 'Calibración de indicador de nivel.'),
(47, 17, 7, '2024-12-25', 1, 32.95, 'Limpieza de filtros hidráulicos.'),
(48, 18, 8, '2024-12-30', 6, 435.80, 'Reemplazo de manguera hidráulica.'),
(49, 19, 9, '2025-01-05', 1, 48.00, 'Ajuste de válvulas.'),
(50, 20, 10, '2025-01-10', 4, 255.10, 'Diagnóstico de vibración excesiva.'),
(51, 21, 2, '2025-01-15', 3, 245.00, 'Mantenimiento preventivo de dirección.'),
(52, 22, 1, '2025-01-20', 8, 695.30, 'Reparación de motor.'),
(53, 23, 3, '2025-01-25', 1, 100.65, 'Inspección de estructura.'),
(54, 24, 4, '2025-01-30', 1, 68.00, 'Lubricación de tornillos sin fin.'),
(55, 25, 5, '2025-02-05', 2, 0.00, 'Verificación de sistemas de seguridad.'),
(56, 26, 6, '2025-02-10', 3, 220.70, 'Calibración de caudalímetro.'),
(57, 27, 7, '2025-02-15', 1, 37.50, 'Limpieza de inyectores.'),
(58, 28, 8, '2025-02-20', 7, 480.20, 'Reemplazo de rodamiento.'),
(59, 29, 9, '2025-02-25', 1, 42.00, 'Ajuste de transmisión.'),
(60, 30, 10, '2025-03-01', 2, 190.90, 'Diagnóstico de pérdida de potencia.'),
(61, 1, 2, '2025-03-05', 4, 295.00, 'Mantenimiento preventivo de refrigeración.'),
(62, 2, 1, '2025-03-10', 6, 570.10, 'Reparación de bomba hidráulica.'),
(63, 3, 3, '2025-03-15', 3, 125.80, 'Análisis de partículas en aceite.'),
(64, 4, 4, '2025-03-20', 1, 72.00, 'Lubricación de guías.'),
(65, 5, 5, '2025-03-25', 2, 0.00, 'Inspección de cintas transportadoras.'),
(66, 6, 6, '2025-03-30', 4, 250.60, 'Calibración de sensor de velocidad.'),
(67, 7, 7, '2025-04-05', 1, 30.15, 'Limpieza de quemadores.'),
(68, 8, 8, '2025-04-10', 8, 535.90, 'Reemplazo de termostato.'),
(69, 9, 9, '2025-04-15', 1, 58.00, 'Ajuste de alineación.'),
(70, 10, 10, '2025-04-20', 3, 215.40, 'Diagnóstico de consumo excesivo de combustible.'),
(71, 11, 2, '2025-04-25', 5, 310.00, 'Mantenimiento preventivo de sistema neumático.'),
(72, 12, 1, '2025-04-30', 7, 655.20, 'Reparación de chasis.'),
(73, 13, 3, '2025-05-01', 2, 115.55, 'Medición de holgura de rodamientos.'),
(74, 14, 4, '2025-05-01', 1, 80.00, 'Lubricación de husillos.'),
(75, 15, 5, '2025-05-01', 3, 0.00, 'Verificación de indicadores.'),
(76, 16, 6, '2025-05-01', 2, 185.10, 'Calibración de manómetro.'),
(77, 17, 7, '2025-05-01', 1, 39.75, 'Limpieza de electrodos.'),
(78, 18, 8, '2025-05-01', 6, 460.30, 'Reemplazo de filtro de combustible.'),
(79, 19, 9, '2025-05-01', 1, 45.00, 'Ajuste de tensión de correas.'),
(80, 20, 10, '2025-05-01', 4, 270.80, 'Diagnóstico de ruido en transmisión.'),
(81, 21, 2, '2025-05-01', 3, 255.00, 'Mantenimiento preventivo de sistema de frenos.'),
(82, 22, 1, '2025-05-01', 9, 730.40, 'Reparación de sistema de dirección asistida.'),
(83, 23, 3, '2025-05-01', 1, 90.30, 'Inspección de soldaduras estructurales.'),
(84, 24, 4, '2025-05-01', 1, 78.00, 'Lubricación de articulaciones.'),
(85, 25, 5, '2025-05-01', 2, 0.00, 'Verificación de luces de emergencia.'),
(86, 26, 6, '2025-05-01', 3, 230.90, 'Calibración de sensor de posición.'),
(87, 27, 7, '2025-05-01', 1, 34.65, 'Limpieza de turbocompresor.'),
(88, 28, 8, '2025-05-01', 7, 495.50, 'Reemplazo de junta.'),
(89, 29, 9, '2025-05-01', 1, 41.00, 'Ajuste de juego de válvulas.'),
(90, 30, 10, '2025-05-01', 2, 205.20, 'Diagnóstico de fallo intermitente.'),
(91, 1, 2, '2025-05-01', 4, 305.00, 'Mantenimiento preventivo de motor diésel.'),
(92, 2, 1, '2025-05-01', 6, 585.60, 'Reparación de transmisión automática.'),
(93, 3, 3, '2025-05-01', 3, 135.15, 'Análisis de gases de escape.'),
(94, 4, 4, '2025-05-01', 1, 70.00, 'Lubricación de rodamientos de rueda.'),
(95, 5, 5, '2025-05-01', 2, 0.00, 'Inspección de sistema de escape.'),
(96, 6, 6, '2025-05-02', 4, 260.70, 'Calibración de sensor de nivel de combustible.'),
(97, 7, 7, '2025-05-02', 1, 36.90, 'Limpieza de prefiltro de combustible.'),
(98, 8, 8, '2025-05-01', 8, 550.80, 'Reemplazo de amortiguadores.'),
(99, 9, 9, '2025-05-02', 1, 43.00, 'Ajuste de convergencia.'),
(100, 10, 10, '2025-05-01', 3, 220.50, 'Diagnóstico de problema de encendido.')
;

INSERT INTO falla_reportada (maquinaria_id, descripcion, fecha_reporte, prioridad, estado)
VALUES 
    (1, 'Algo 1 Alto', '2025-04-20', 'Alta', 'Pendiente'),
	(2, 'Algo 1 Medio', '2025-04-20', 'Media', 'En proceso'),
	(3, 'Algo 1 Bajo', '2025-04-20', 'Baja', 'Resuelta'),
	(4, 'Algo 2 Alto', '2025-04-20', 'Alta', 'Pendiente'),
	(5, 'Algo 2 Medio', '2025-04-20', 'Media', 'En proceso'),
	(6, 'Algo 2 Bajo', '2025-04-20', 'Baja', 'Resuelta'),
	(7, 'Algo 3 Alto', '2025-04-20', 'Alta', 'Pendiente'),
	(8, 'Algo 3 Medio', '2025-04-20', 'Media', 'Pendiente'),
	(9, 'Algo 3 Bajo', '2025-04-20', 'Baja', 'Pendiente'),
	(10, 'Algo 1', '2025-04-20', 'Alta', 'Pendiente');