package com.agrovalle;

public enum UnidadMedida {

  KILOGRAMO("Kilogramo", "kg"),
  GRAMO("Gramo", "g"),
  TONELADA("Tonelada", "ton"),
  ARROBA("Arroba", "@"),
  BULTO("Bulto / Saco", "bulto"),
  LITRO("Litro", "L"),
  MILILITRO("Mililitro", "ml"),
  GALON("Galón", "gal"),
  UNIDAD("Unidad", "und");

  private final String nombre;
  private final String simbolo;

  UnidadMedida(String nombre, String simbolo) {
    this.nombre = nombre;
    this.simbolo = simbolo;
  }

  public String getNombre() {
    return nombre;
  }

  public String getSimbolo() {
    return simbolo;
  }
}
