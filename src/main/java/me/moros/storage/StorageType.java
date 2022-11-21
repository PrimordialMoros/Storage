/*
 * Copyright 2020-2022 Moros
 *
 * This file is part of Storage.
 *
 * Storage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Storage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Storage. If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.storage;

import java.util.Arrays;

/**
 * Enum holding the different types of supported storage and their schema file name.
 */
public enum StorageType {
  // Remote databases
  /**
   * MySQL, remote
   */
  MYSQL("MySQL", "mariadb.sql", false),
  /**
   * MariaDB, remote
   */
  MARIADB("MariaDB", "mariadb.sql", false),
  /**
   * PostgreSQL, remote
   */
  POSTGRESQL("PostgreSQL", "postgre.sql", false),
  // Local databases
  /**
   * SQLite, local
   */
  SQLITE("SQLite", "sqlite.sql", true),
  /**
   * H2, local
   */
  H2("H2", "h2.sql", true),
  /**
   * HSQL, local
   */
  HSQL("HSQL", "hsql.sql", true);

  private final String name;
  private final String path;
  private final boolean local;

  StorageType(String name, String schemaFileName, boolean local) {
    this.name = name;
    this.path = schemaFileName;
    this.local = local;
  }

  /**
   * Get the schema path for this type.
   * @return the schema path
   */
  public String schemaPath() {
    return path;
  }

  /**
   * Check if this type is a local database.
   * @return whether this type represents a local database type
   */
  public boolean isLocal() {
    return local;
  }

  @Override
  public String toString() {
    return name;
  }

  /**
   * Attempts to parse the given string and return a {@link StorageType} enum.
   * @param name the string to parse
   * @param def the default value
   * @return the parsed result or the default value if parsing was unsuccessful
   */
  public static StorageType parse(String name, StorageType def) {
    return Arrays.stream(values()).filter(t -> name.equalsIgnoreCase(t.name)).findAny().orElse(def);
  }
}

