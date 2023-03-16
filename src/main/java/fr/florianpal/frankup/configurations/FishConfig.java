
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

import fr.florianpal.frankup.objects.Fish;
import org.bukkit.configuration.Configuration;

import java.util.LinkedHashMap;

public class FishConfig {

    private LinkedHashMap<String, Fish> fish;

    public void load(Configuration config) {
        fish = new LinkedHashMap<>();

        for (String index : config.getConfigurationSection("fish").getKeys(false)) {
            String name = config.getString("fish." + index + ".name");
            String texture = config.getString("fish." + index + ".texture");

            fish.put(index, new Fish(index, name, texture));
        }

    }

    public LinkedHashMap<String, Fish> getRanks() {
        return fish;
    }
}
