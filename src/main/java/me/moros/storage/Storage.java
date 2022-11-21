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

import java.io.Closeable;

/**
 * Represents a connected database source.
 */
public interface Storage extends Closeable {
  /**
   * Get the type of this storage.
   * @return the type
   */
  StorageType type();

  @Override
  void close();
}
