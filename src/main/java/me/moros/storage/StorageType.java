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

import java.util.Locale;

/**
 * Enum holding the different types of supported storage.
 */
public enum StorageType {
  // Remote databases
  /**
   * MySQL, remote
   */
  MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "com.mysql.cj.jdbc.MysqlDataSource", false),
  /**
   * MariaDB, remote
   */
  MARIADB("MariaDB", "org.mariadb.jdbc.Driver", "org.mariadb.jdbc.MariaDbDataSource", false),
  /**
   * PostgreSQL, remote
   */
  POSTGRESQL("PostgreSQL", "org.postgresql.ds.PGSimpleDataSource", "org.postgresql.Driver", false),
  // Local databases
  /**
   * SQLite, local
   */
  SQLITE("SQLite", "org.sqlite.JDBC", "org.sqlite.SQLiteDataSource", true),
  /**
   * H2, local
   */
  H2("H2", "org.h2.Driver", "org.h2.jdbcx.JdbcDataSource", true),
  /**
   * HSQL, local
   */
  HSQL("HSQLDB", "org.hsqldb.jdbc.JDBCDriver", "org.hsqldb.jdbc.JDBCDataSource", true);

  private final String name;
  private final String driver;
  private final String dataSource;
  private final boolean local;

  StorageType(String name, String driver, String dataSource, boolean local) {
    this.name = name;
    this.driver = driver;
    this.dataSource = dataSource;
    this.local = local;
  }

  @Override
  public String toString() {
    return name;
  }

  /**
   * Get the real name for this storage type.
   * @return the real name in lowercase
   */
  public String realName() {
    return name.toLowerCase(Locale.ROOT);
  }

  /**
   * Get the driver class name for this type.
   * @return the driver class name
   */
  public String driver() {
    return driver;
  }

  /**
   * Get the data source class name for this type.
   * @return the data source class name
   */
  public String dataSource() {
    return dataSource;
  }

  /**
   * Check if this type is a local database.
   * @return whether this type represents a local database type
   */
  public boolean isLocal() {
    return local;
  }
}

