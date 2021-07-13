/*
 *   Copyright 2020-2021 Moros <https://github.com/PrimordialMoros>
 *
 *    This file is part of Storage.
 *
 *   Storage is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Storage is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with Storage.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;

@FunctionalInterface
public interface StorageCreator<T extends Storage> {
  T create(StorageType engine, Logger logger, HikariDataSource source);
}
