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

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

/**
 * Utility builder to create a {@link Storage}-implementing object that utilizes a Hikari connection.
 * @param <T> The type of object to return on build
 */
public class ConnectionBuilder<T extends Storage> {
  private static final Set<String> poolNames = new HashSet<>();

  private final StorageCreator<T> constructor;
  private final StorageType engine;
  private String path = "";
  private String host = "localhost";
  private String database = "";
  private String username = "";
  private String password = "";
  private int port;

  private ConnectionBuilder(StorageCreator<T> constructor, StorageType engine) {
    this.constructor = constructor;
    this.engine = engine;
  }

  /**
   * Set the file path for the connection.
   * This is only required for local file databases.
   * You can include jdbc url options by appending them to the path.
   * @param path the path to the database file
   * @return the modified builder instance
   */
  public ConnectionBuilder<T> path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Set the host for the connection.
   * @param host the host
   * @return the modified builder instance
   */
  public ConnectionBuilder<T> host(String host) {
    this.host = host;
    return this;
  }

  /**
   * Set the database name for the connection.
   * @param database the database name
   * @return the modified builder instance
   */
  public ConnectionBuilder<T> database(String database) {
    this.database = database;
    return this;
  }

  /**
   * Set the username for the connection.
   * @param username the username
   * @return the modified builder instance
   */
  public ConnectionBuilder<T> username(String username) {
    this.username = username;
    return this;
  }

  /**
   * Set the password for the connection.
   * @param password the database password
   * @return the modified builder instance
   */
  public ConnectionBuilder<T> password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Set the port for the connection.
   * @param port the database port
   * @return the modified builder instance
   */
  public ConnectionBuilder<T> port(int port) {
    this.port = port;
    return this;
  }

  /**
   * Calls {@link #build(String, Logger, boolean)} with optimize = true
   * @param poolName the hikari poolName to use
   * @param logger the logger to use
   * @return the constructed Storage object if connection was successful, null otherwise.
   */
  public @Nullable T build(String poolName, Logger logger) {
    return build(poolName, logger, true);
  }

  /**
   * Attempt to build.
   * @param poolName the hikari poolName to use
   * @param logger the logger to use
   * @param optimize whether to apply optimizations, (only applies to MySQL/MariaDB)
   * @return the constructed Storage object if connection was successful, null otherwise.
   */
  public @Nullable T build(String poolName, Logger logger, boolean optimize) {
    if (poolNames.contains(poolName)) {
      logger.warn(poolName + " is already registered!");
      return null;
    }
    if (host.isEmpty() || database.isEmpty() || username.isEmpty() || password.isEmpty()) {
      logger.warn("Connection info is invalid! One or more values is empty!");
      return null;
    }
    if (engine.isLocal() && path.isEmpty()) {
      logger.warn("Connection path is missing!");
      return null;
    }

    logger.info("Loading storage provider... [" + engine + "]");

    HikariConfig config = new HikariConfig();
    config.setPoolName(poolName);
    config.setMaximumPoolSize(5);
    config.setMinimumIdle(3);
    config.addDataSourceProperty("serverName", host);
    config.addDataSourceProperty("portNumber", port);
    config.addDataSourceProperty("databaseName", database);
    config.addDataSourceProperty("user", username);
    config.addDataSourceProperty("password", password);

    if (optimize && (engine == StorageType.MARIADB || engine == StorageType.MYSQL)) {
      config.addDataSourceProperty("cachePrepStmts", true);
      config.addDataSourceProperty("prepStmtCacheSize", 250);
      config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
      config.addDataSourceProperty("useServerPrepStmts", true);
      config.addDataSourceProperty("cacheCallableStmts", true);
      config.addDataSourceProperty("cacheResultSetMetadata", true);
      config.addDataSourceProperty("cacheServerConfiguration", true);
      config.addDataSourceProperty("useLocalSessionState", true);
      config.addDataSourceProperty("elideSetAutoCommits", true);
      config.addDataSourceProperty("alwaysSendSetIsolation", false);
    }

    switch (engine) {
      case MYSQL -> {
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
      }
      case MARIADB -> config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
      case POSTGRESQL -> config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
      case SQLITE -> {
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + path);
      }
      case H2 -> {
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:" + path);
      }
      case HSQL -> {
        config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        config.setJdbcUrl("jdbc:hsqldb:file:" + path);
      }
    }
    HikariDataSource ds = new HikariDataSource(config);
    try {
      if (ds.getConnection() != null) {
        poolNames.add(poolName);
        return constructor.create(engine, logger, ds);
      }
    } catch (SQLException e) {
      logger.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * Create a new connection builder to easily create a Hikari database connection
   * Default host is "localhost", everything else is empty.
   * @param constructor the constructor for the storage object
   * @param engine the storage engine to use
   * @param <T> the type to build
   * @return a connection builder of the appropriate type
   */
  public static <T extends Storage> ConnectionBuilder<T> create(StorageCreator<T> constructor, StorageType engine) {
    return new ConnectionBuilder<>(constructor, engine);
  }
}
