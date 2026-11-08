# CheckList general sobre el modelo de datos de AgroValle

## Diseño del Modelado MER y DER
[ ] Los actores y entidades: usuario, producto, categoria_producto, detalle_compra, orden_compra, pago y envio, estan claramente definidos.

[ ] Cada entidad contiene los atributos indispensables.

[ ] Las relaciones están bien dimensionadas (N:M, 1:M).

[ ] El esquema está en Tercera Forma Normal (3NF) para evitar redundancias y anomalías de actualización.

---
## Diseño Estructural para el esquema DDL
[ ] Todas las tablas cuentan con una clave primaria definida,es decir, con identificadores autoincrementales o UUIDs.

[ ] Las claves foráneas mantienen la integridad referencial.

[ ] Se aplicaron restricciones lógicas mediante código SQL, Ejemplo: CHECK precio > 0, CHECK stock >= 0, campos obligatorios con NOT NULL, y correos electrónicos o documentos únicos con UNIQUE.

[ ] Se eligieron los tipos de datos adecuados en PostgreSQL

[ ] Se identificaron las columnas clave para búsquedas frecuentes para la futura creación de índices B-Tree.

---
## Diseño Entidades JPA
[ ] Cada clase entidad cuenta con las anotaciones correctas de persistencia.

[ ] Las relaciones: *@ManyToOne*, *@OneToMany*, *@ManyToMany* están configuradas adecuadamente en ambos extremos.

[ ] Se configuraron las relaciones colecciones *@OneToMany* con carga perezosa *FetchType.LAZY* para evitar consultas innecesarias a la base de datos y problemas de rendimiento tipo N+1.

[ ] Se definieron las reglas de cascada *CascadeType* y huérfanos *orphanRemoval*.

[ ] El modelo de datos de los pedidos soporta un flujo de estados claros mediante enums.