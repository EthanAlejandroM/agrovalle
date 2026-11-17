package com.agrovalle;

/**
 * Crea categorías para las pruebas. CategoriaProducto tiene constructor
 * protected, así que solo se puede instanciar desde este paquete.
 */
public final class CategoriaProductoFabrica {

    private CategoriaProductoFabrica() {
    
    }

    public static CategoriaProducto crear(
        int id, String nombre, String descripcion) {

        CategoriaProducto categoria = new CategoriaProducto();

        categoria.setIdCategoria(id);
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);

        return categoria;
    }
}
