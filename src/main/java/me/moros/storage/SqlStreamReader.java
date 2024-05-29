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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to parse SQL queries.
 */
public final class SqlStreamReader {
  private SqlStreamReader() {
  }

  /**
   * Try to parse queries.
   * @param is the input stream to parse from
   * @return an ordered list of all parsed queries
   */
  public static List<String> parseQueries(InputStream is) {
    List<String> queries = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith("--")) continue;
        sb.append(line);
        if (line.endsWith(";")) {
          sb.deleteCharAt(sb.length() - 1);
          String result = sb.toString().trim();
          if (!result.isEmpty()) {
            queries.add(result);
          }
          sb = new StringBuilder();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return queries;
  }
}
