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

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;

/**
 * Represents a specialized {@link Storage} constructor.
 * @param <T> the type of storage object to construct
 */
@FunctionalInterface
public interface StorageCreator<T extends Storage> {
  /**
   * Create a new specialized Storage.
   * @param engine the type of database
   * @param logger the logger to assign
   * @param source the data source to wrap
   * @return the specialized storage instance
   */
  T create(StorageType engine, Logger logger, HikariDataSource source);
}
