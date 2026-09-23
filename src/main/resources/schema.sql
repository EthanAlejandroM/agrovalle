CREATE TABLE IF NOT EXISTS public.usuario
(
    id serial NOT NULL,
    nombre character varying NOT NULL,
    "ubicacionValle" character varying NOT NULL,
    "tipoDocumento" character varying NOT NULL,
    documento character varying NOT NULL UNIQUE,
    rol character varying NOT NULL,
    "fechaRegistro" date NOT NULL,
    PRIMARY KEY (id)
);