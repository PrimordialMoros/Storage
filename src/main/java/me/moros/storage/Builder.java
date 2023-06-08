/*
 * Copyright 2020-2023 Moros
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

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Consumer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Utility builder to create {@link StorageDataSource}.
 */
public class Builder {
  private final StorageType engine;
  private final HikariConfig config;
  private final Properties dataSourceProperties;

  private Path path = null;
  private String host = "localhost";
  private String database = "";
  private int port;
  private boolean optimize;
  private boolean memory;

  Builder(StorageType engine) {
    this.engine = engine;
    this.config = new HikariConfig();
    this.dataSourceProperties = new Properties();
    this.optimize = engine == StorageType.MYSQL;
  }

  /**
   * Set the file path for the connection.
   * This is only required for local file databases.
   * @param path the path to the database file
   * @return the modified builder instance
   */
  public Builder path(Path path) {
    this.path = requireNonNull(path);
    return this;
  }

  /**
   * Set the host for the connection.
   * @param host the host
   * @return the modified builder instance
   */
  public Builder host(String host) {
    this.host = requireNonNull(host);
    dataSourceProperties.put("serverName", this.host);
    return this;
  }

  /**
   * Set the database name for the connection.
   * @param database the database name
   * @return the modified builder instance
   */
  public Builder database(String database) {
    this.database = requireNonNull(database);
    dataSourceProperties.put("databaseName", this.database);
    return this;
  }

  /**
   * Set the username for the connection.
   * @param username the username
   * @return the modified builder instance
   */
  public Builder username(@Nullable String username) {
    config.setUsername(username);
    return this;
  }

  /**
   * Set the password for the connection.
   * @param password the database password
   * @return the modified builder instance
   */
  public Builder password(@Nullable String password) {
    config.setPassword(password);
    return this;
  }

  /**
   * Set the port for the connection.
   * @param port the database port
   * @return the modified builder instance
   */
  public Builder port(int port) {
    this.port = port;
    dataSourceProperties.put("portNumber", this.host);
    return this;
  }

  /**
   * Configure the hikari config using a consumer.
   * @param consumer the config consumer
   * @return the modified builder instance
   */
  public Builder configure(Consumer<HikariConfig> consumer) {
    consumer.accept(config);
    return this;
  }

  /**
   * Configure the hikari data source properties using a consumer.
   * @param consumer the config consumer
   * @return the modified builder instance
   */
  public Builder properties(Consumer<Properties> consumer) {
    consumer.accept(dataSourceProperties);
    return this;
  }

  /**
   * Do not include optimization properties on MySQL storage type.
   * @return the modified builder instance
   */
  public Builder noOptimization() {
    this.optimize = false;
    return this;
  }

  /**
   * Set whether the database should be in memory. Only affects local storage types.
   * @param memory whether the database should be in-memory
   * @return the modified builder instance
   */
  public Builder memory(boolean memory) {
    this.memory = memory;
    return this;
  }

  /**
   * Attempt to build.
   * @param poolName the hikari poolName to use
   * @return the constructed Storage object if connection was successful, null otherwise.
   */
  public @Nullable StorageDataSource build(String poolName) {
    if (engine.isLocal() && path == null) {
      throw new IllegalStateException("Connection path is missing!");
    }
    config.setPoolName(poolName);
    if (optimize) {
      addOptimizedProperties();
    }
    config.setDataSourceProperties(dataSourceProperties);
    String url = engine.isLocal() ? path.toString() : ("//" + host + ":" + port + "/" + database);
    setDriverClassAndUrl(engine.driver(), formatUrl(url));
    HikariDataSource ds = new HikariDataSource(config);
    try (Connection ignored = ds.getConnection()) {
      return new SimpleStorage(engine, ds);
    } catch (SQLException ignored) {
    }
    return null;
  }

  private String formatUrl(String extra) {
    StringBuilder sb = new StringBuilder();
    sb.append("jdbc:").append(engine.realName()).append(":");
    if (engine.isLocal()) {
      if (memory) {
        sb.append(engine == StorageType.SQLITE ? ":memory:" : "mem:");
      } else if (engine == StorageType.HSQL) {
        sb.append("file:");
      }
    }
    return sb.append(extra).toString();
  }

  private void setDriverClassAndUrl(String driverClassName, String jdbcUrl) {
    if (nullOrEmpty(config.getDriverClassName())) {
      config.setDriverClassName(driverClassName);
    }
    if (nullOrEmpty(config.getJdbcUrl())) {
      config.setJdbcUrl(jdbcUrl);
    }
  }

  private boolean nullOrEmpty(@Nullable String value) {
    return value == null || value.isEmpty();
  }

  private void addOptimizedProperties() {
    // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
    dataSourceProperties.put("cachePrepStmts", true);
    dataSourceProperties.put("prepStmtCacheSize", 250);
    dataSourceProperties.put("prepStmtCacheSqlLimit", 2048);
    dataSourceProperties.put("useServerPrepStmts", true);
    dataSourceProperties.put("useLocalSessionState", true);
    dataSourceProperties.put("rewriteBatchedStatements", true);
    dataSourceProperties.put("cacheResultSetMetadata", true);
    dataSourceProperties.put("cacheServerConfiguration", true);
    dataSourceProperties.put("elideSetAutoCommits", true);
    dataSourceProperties.put("maintainTimeStats", false);
  }

  private record SimpleStorage(StorageType type, HikariDataSource source) implements StorageDataSource {
  }
}
