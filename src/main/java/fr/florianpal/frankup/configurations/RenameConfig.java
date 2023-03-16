
/*
 * Copyright (C) 2022 Florianpal
 *
 * This program is free software;
 * you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 *
 * Last modification : 07/01/2022 23:07
 *
 *  @author Florianpal.
 */

package fr.florianpal.frankup.configurations;

import org.bukkit.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;

public class RenameConfig {

    private Map<String, String> renameMap;

    public void load(Configuration config) {
        renameMap = new HashMap<>();
        for (String index : config.getConfigurationSection("item").getKeys(false)) {
           renameMap.put(index, config.getString("item." + index));
        }
    }

    public Map<String, String> getRenameMap() {
        return renameMap;
    }
}
