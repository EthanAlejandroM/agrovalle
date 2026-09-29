CREATE TABLE IF NOT EXISTS public.usuario
(
    id serial NOT NULL,
    nombre character varying NOT NULL,
    ubicacion_valle character varying NOT NULL,
    tipo_documento character varying NOT NULL,
    documento character varying NOT NULL UNIQUE,
    rol character varying NOT NULL,
    fecha_registro date NOT NULL,
    PRIMARY KEY (id)
);