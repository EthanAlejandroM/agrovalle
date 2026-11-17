package com.agrovalle;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Igual que la estrategia por defecto de Spring Boot, pero respeta los
 * nombres de columna escritos entre comillas (por ejemplo "Id_usuario").
 */
public class QuotedAwareNamingStrategy extends CamelCaseToUnderscoresNamingStrategy {

  @Override
  public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
    if (name != null && name.isQuoted()) {
      return name;
    }
    return super.toPhysicalColumnName(name, context);
  }
}
