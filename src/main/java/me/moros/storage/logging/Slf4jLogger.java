/*
 *   Copyright 2020 Moros <https://github.com/PrimordialMoros>
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

package me.moros.storage.logging;

public class Slf4jLogger implements Logger {
	private final org.slf4j.Logger logger;

	public Slf4jLogger(org.slf4j.Logger logger) {
		this.logger = logger;
	}

	@Override
	public void info(String s) {
		logger.info(s);
	}

	@Override
	public void warn(String s) {
		logger.warn(s);
	}

	@Override
	public void severe(String s) {
		logger.error(s);
	}
}
