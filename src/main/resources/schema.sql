-- PostgreSQL database dump
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

CREATE TABLE public.categoria_producto (
    "Id_categoria" integer NOT NULL,
    nombre character varying NOT NULL,
    descripcion character varying
);

CREATE SEQUENCE public."categoria_producto_Id_categoria_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."categoria_producto_Id_categoria_seq" OWNED BY public.categoria_producto."Id_categoria";

CREATE TABLE public.detalle_compra (
    "Id_detalle_compra" integer CONSTRAINT "detalle_compra_Id_detalle_not_null" NOT NULL,
    "Id_orden" integer NOT NULL,
    "Id_producto" integer NOT NULL,
    cantidad integer NOT NULL,
    precio_unitario numeric(10,2),
    total numeric NOT NULL
);

CREATE SEQUENCE public."detalle_compra_Id_detalle_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."detalle_compra_Id_detalle_seq" OWNED BY public.detalle_compra."Id_detalle_compra";

CREATE TABLE public.detalle_lote (
    "Id_detalle_lote" integer NOT NULL,
    "Id_detalle_compra" integer NOT NULL,
    "Id_lote" integer NOT NULL,
    cantidad_asignada integer NOT NULL,
    fecha_asignacion time with time zone NOT NULL
);

CREATE SEQUENCE public."detalle_lote_Id_detalle_lote_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."detalle_lote_Id_detalle_lote_seq" OWNED BY public.detalle_lote."Id_detalle_lote";

CREATE TABLE public.envio (
    "Id_envio" integer NOT NULL,
    "Id_pedido_agricultor" integer CONSTRAINT "envio_Id_orden_not_null" NOT NULL,
    direccion_entrega character varying NOT NULL,
    costo_envio numeric(10,2) NOT NULL,
    numero_guia character varying,
    fecha_envio date NOT NULL
);

CREATE SEQUENCE public."envio_Id_envio_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."envio_Id_envio_seq" OWNED BY public.envio."Id_envio";

CREATE TABLE public.historial_envio (
    "Id_historial_envio" integer CONSTRAINT "estado_envio_Id_estado_envio_not_null" NOT NULL,
    "Id_envio" integer CONSTRAINT "estado_envio_Id_envio_not_null" NOT NULL,
    estado_envio character varying CONSTRAINT estado_envio_estado_envio_not_null NOT NULL,
    fecha_hora timestamp with time zone CONSTRAINT estado_envio_fecha_hora_not_null NOT NULL,
    ubicacion character varying CONSTRAINT estado_envio_ubicacion_not_null NOT NULL,
    descripcion character varying
);

CREATE SEQUENCE public."estado_envio_Id_estado_envio_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."estado_envio_Id_estado_envio_seq" OWNED BY public.historial_envio."Id_historial_envio";

CREATE TABLE public.estado_trazabilidad (
    "Id_estado_trazabilidad" integer NOT NULL,
    "Id_lote" integer NOT NULL,
    estado_lote character varying NOT NULL,
    fecha_hora timestamp with time zone NOT NULL,
    descripcion character varying
);

CREATE SEQUENCE public."estado_trazabilidad_Id_estado_trazabilidad_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."estado_trazabilidad_Id_estado_trazabilidad_seq" OWNED BY public.estado_trazabilidad."Id_estado_trazabilidad";

CREATE TABLE public.lote_produccion (
    "Id_lote" integer NOT NULL,
    codigo_lote character varying NOT NULL,
    "Id_producto" integer NOT NULL,
    fecha_cosecha date NOT NULL,
    cantidad_producida numeric(10,2) NOT NULL,
    cantidad_disponible numeric(10,2) NOT NULL,
    ubicacion_lote character varying NOT NULL,
    version bigint DEFAULT 0 NOT NULL
);

CREATE SEQUENCE public."lote_produccion_Id_lote_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."lote_produccion_Id_lote_seq" OWNED BY public.lote_produccion."Id_lote";

CREATE TABLE public.orden_compra (
    "Id_orden" integer NOT NULL,
    "Id_usuario_comprador" integer NOT NULL,
    total numeric(10,2) NOT NULL,
    fecha_orden time with time zone NOT NULL
);

CREATE SEQUENCE public."orden_compra_Id_orden_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."orden_compra_Id_orden_seq" OWNED BY public.orden_compra."Id_orden";

CREATE TABLE public.pago (
    "Id_pago" integer NOT NULL,
    "Id_orden" integer NOT NULL,
    monto numeric(10,2) NOT NULL,
    metodo_pago character varying NOT NULL,
    estado_pago character varying NOT NULL,
    fecha_pago time with time zone NOT NULL
);

CREATE SEQUENCE public."pago_Id_pago_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."pago_Id_pago_seq" OWNED BY public.pago."Id_pago";

CREATE TABLE public.pedido_agricultor (
    id_pedido_agricultor integer NOT NULL,
    id_orden integer NOT NULL,
    id_usuario_agricultor integer CONSTRAINT pedido_agricultor_id_usuario_not_null NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    estado_pedido character varying NOT NULL,
    subtotal_pedido numeric(10,2) NOT NULL
);

CREATE SEQUENCE public.pedido_agricultor_id_pedido_agricultor_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.pedido_agricultor_id_pedido_agricultor_seq OWNED BY public.pedido_agricultor.id_pedido_agricultor;

CREATE TABLE public.producto (
    "Id_producto" integer NOT NULL,
    "Id_categoria" integer NOT NULL,
    "Id_usuario_agricultor" integer CONSTRAINT "producto_Id_usuario_not_null" NOT NULL,
    nombre character varying NOT NULL,
    tipo_producto character varying NOT NULL,
    unidad_medida character varying NOT NULL,
    precio numeric(10,2) NOT NULL,
    imagen character varying NOT NULL
);

CREATE SEQUENCE public."producto_Id_producto_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public."producto_Id_producto_seq" OWNED BY public.producto."Id_producto";

CREATE TABLE public.usuario (
    "Id_usuario" integer NOT NULL,
    nombre character varying NOT NULL,
    ubicacion_valle character varying NOT NULL,
    tipo_documento character varying NOT NULL,
    documento character varying NOT NULL,
    telefono character varying NOT NULL,
    rol character varying NOT NULL,
    correo character varying NOT NULL,
    "contraseña_hash" character varying NOT NULL,
    fecha_registro date CONSTRAINT usuario_fecha_registros_not_null NOT NULL
);

CREATE SEQUENCE public."usuario_Id_usuario_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public."usuario_Id_usuario_seq" OWNED BY public.usuario."Id_usuario";

ALTER TABLE ONLY public.categoria_producto ALTER COLUMN "Id_categoria" SET DEFAULT nextval('public."categoria_producto_Id_categoria_seq"'::regclass);

ALTER TABLE ONLY public.detalle_compra ALTER COLUMN "Id_detalle_compra" SET DEFAULT nextval('public."detalle_compra_Id_detalle_seq"'::regclass);

ALTER TABLE ONLY public.detalle_lote ALTER COLUMN "Id_detalle_lote" SET DEFAULT nextval('public."detalle_lote_Id_detalle_lote_seq"'::regclass);

ALTER TABLE ONLY public.envio ALTER COLUMN "Id_envio" SET DEFAULT nextval('public."envio_Id_envio_seq"'::regclass);

ALTER TABLE ONLY public.estado_trazabilidad ALTER COLUMN "Id_estado_trazabilidad" SET DEFAULT nextval('public."estado_trazabilidad_Id_estado_trazabilidad_seq"'::regclass);

ALTER TABLE ONLY public.historial_envio ALTER COLUMN "Id_historial_envio" SET DEFAULT nextval('public."estado_envio_Id_estado_envio_seq"'::regclass);

ALTER TABLE ONLY public.lote_produccion ALTER COLUMN "Id_lote" SET DEFAULT nextval('public."lote_produccion_Id_lote_seq"'::regclass);

ALTER TABLE ONLY public.orden_compra ALTER COLUMN "Id_orden" SET DEFAULT nextval('public."orden_compra_Id_orden_seq"'::regclass);

ALTER TABLE ONLY public.pago ALTER COLUMN "Id_pago" SET DEFAULT nextval('public."pago_Id_pago_seq"'::regclass);

ALTER TABLE ONLY public.pedido_agricultor ALTER COLUMN id_pedido_agricultor SET DEFAULT nextval('public.pedido_agricultor_id_pedido_agricultor_seq'::regclass);

ALTER TABLE ONLY public.producto ALTER COLUMN "Id_producto" SET DEFAULT nextval('public."producto_Id_producto_seq"'::regclass);

ALTER TABLE ONLY public.usuario ALTER COLUMN "Id_usuario" SET DEFAULT nextval('public."usuario_Id_usuario_seq"'::regclass);

ALTER TABLE ONLY public.categoria_producto
    ADD CONSTRAINT categoria_producto_pkey PRIMARY KEY ("Id_categoria");

ALTER TABLE ONLY public.detalle_compra
    ADD CONSTRAINT detalle_compra_pkey PRIMARY KEY ("Id_detalle_compra");

ALTER TABLE ONLY public.detalle_lote
    ADD CONSTRAINT detalle_lote_pkey PRIMARY KEY ("Id_detalle_lote");

ALTER TABLE ONLY public.envio
    ADD CONSTRAINT envio_pkey PRIMARY KEY ("Id_envio");

ALTER TABLE ONLY public.historial_envio
    ADD CONSTRAINT estado_envio_pkey PRIMARY KEY ("Id_historial_envio");

ALTER TABLE ONLY public.estado_trazabilidad
    ADD CONSTRAINT estado_trazabilidad_pkey PRIMARY KEY ("Id_estado_trazabilidad");

ALTER TABLE ONLY public.lote_produccion
    ADD CONSTRAINT lote_produccion_codigo_lote_key UNIQUE (codigo_lote);

ALTER TABLE ONLY public.lote_produccion
    ADD CONSTRAINT lote_produccion_pkey PRIMARY KEY ("Id_lote");

ALTER TABLE ONLY public.orden_compra
    ADD CONSTRAINT orden_compra_pkey PRIMARY KEY ("Id_orden");

ALTER TABLE ONLY public.pago
    ADD CONSTRAINT "pago_Id_orden_key" UNIQUE ("Id_orden");

ALTER TABLE ONLY public.pago
    ADD CONSTRAINT pago_pkey PRIMARY KEY ("Id_pago");

ALTER TABLE ONLY public.pedido_agricultor
    ADD CONSTRAINT pedido_agricultor_pkey PRIMARY KEY (id_pedido_agricultor);

ALTER TABLE ONLY public.producto
    ADD CONSTRAINT producto_pkey PRIMARY KEY ("Id_producto");

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_correo_key UNIQUE (correo);

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_documento_key UNIQUE (documento);

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY ("Id_usuario");

ALTER TABLE ONLY public.detalle_compra
    ADD CONSTRAINT "detalle_compra_Id_orden_fkey" FOREIGN KEY ("Id_orden") REFERENCES public.orden_compra("Id_orden") NOT VALID;

ALTER TABLE ONLY public.detalle_compra
    ADD CONSTRAINT "detalle_compra_Id_producto_fkey" FOREIGN KEY ("Id_producto") REFERENCES public.producto("Id_producto") NOT VALID;

ALTER TABLE ONLY public.detalle_lote
    ADD CONSTRAINT "detalle_lote_Id_detalle_compra_fkey" FOREIGN KEY ("Id_detalle_compra") REFERENCES public.detalle_compra("Id_detalle_compra") NOT VALID;

ALTER TABLE ONLY public.detalle_lote
    ADD CONSTRAINT "detalle_lote_Id_lote_fkey" FOREIGN KEY ("Id_lote") REFERENCES public.lote_produccion("Id_lote") NOT VALID;

ALTER TABLE ONLY public.envio
    ADD CONSTRAINT "envio_Id_pedido_agricultor_fkey" FOREIGN KEY ("Id_pedido_agricultor") REFERENCES public.pedido_agricultor(id_pedido_agricultor) NOT VALID;

ALTER TABLE ONLY public.estado_trazabilidad
    ADD CONSTRAINT "estado_trazabilidad_Id_lote_fkey" FOREIGN KEY ("Id_lote") REFERENCES public.lote_produccion("Id_lote") NOT VALID;

ALTER TABLE ONLY public.historial_envio
    ADD CONSTRAINT "historial_envio_Id_envio_fkey" FOREIGN KEY ("Id_envio") REFERENCES public.envio("Id_envio") NOT VALID;

ALTER TABLE ONLY public.lote_produccion
    ADD CONSTRAINT "lote_produccion_Id_producto_fkey" FOREIGN KEY ("Id_producto") REFERENCES public.producto("Id_producto") NOT VALID;

ALTER TABLE ONLY public.orden_compra
    ADD CONSTRAINT "orden_compra_Id_usuario_comprador_fkey" FOREIGN KEY ("Id_usuario_comprador") REFERENCES public.usuario("Id_usuario") NOT VALID;

ALTER TABLE ONLY public.pago
    ADD CONSTRAINT "pago_Id_orden_fkey" FOREIGN KEY ("Id_orden") REFERENCES public.orden_compra("Id_orden") NOT VALID;

ALTER TABLE ONLY public.pedido_agricultor
    ADD CONSTRAINT pedido_agricultor_id_orden_fkey FOREIGN KEY (id_orden) REFERENCES public.orden_compra("Id_orden") NOT VALID;

ALTER TABLE ONLY public.pedido_agricultor
    ADD CONSTRAINT pedido_agricultor_id_usuario_agricultor_fkey FOREIGN KEY (id_usuario_agricultor) REFERENCES public.usuario("Id_usuario") NOT VALID;

ALTER TABLE ONLY public.producto
    ADD CONSTRAINT "producto_Id_categoria_fkey" FOREIGN KEY ("Id_categoria") REFERENCES public.categoria_producto("Id_categoria") NOT VALID;

ALTER TABLE ONLY public.producto
    ADD CONSTRAINT "producto_Id_usuario_agricultor_fkey" FOREIGN KEY ("Id_usuario_agricultor") REFERENCES public.usuario("Id_usuario") NOT VALID;



