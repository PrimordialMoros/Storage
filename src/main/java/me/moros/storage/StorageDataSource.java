/*
 * Copyright 2020-2024 Moros
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

import com.zaxxer.hikari.HikariDataSource;

import static java.util.Objects.requireNonNull;

/**
 * Represents an immutable data structure for wrapping a {@link HikariDataSource}.
 */
public interface StorageDataSource {
  /**
   * The type of storage this data holds.
   * @return the storage type
   */
  StorageType type();

  /**
   * The data source this data holds.
   * @return the data source
   */
  HikariDataSource source();

  /**
   * Create a new builder.
   * @param engine the storage engine to use
   * @return a new builder
   */
  static Builder builder(StorageType engine) {
    return new Builder(requireNonNull(engine));
  }
}
