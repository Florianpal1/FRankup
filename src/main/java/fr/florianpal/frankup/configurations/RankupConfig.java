
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

import fr.florianpal.frankup.objects.Need;
import fr.florianpal.frankup.objects.Rank;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.util.LinkedHashMap;
import java.util.List;

public class RankupConfig {

    private LinkedHashMap<String, Rank> ranks;

    public void load(Configuration config, FishConfig fishConfig) {
        ranks = new LinkedHashMap<>();

        for (String index : config.getConfigurationSection("ranks").getKeys(false)) {
            String displayName = config.getString("ranks." + index + ".displayName");
            String displayItem = config.getString("ranks." + index + ".displayItem");
            String nextGrade = config.getString("ranks." + index + ".nextGrade");
            String barrierMaterial = config.getString("ranks." + index + ".barrierMaterial");

            LinkedHashMap<String, Need> needsMap = new LinkedHashMap<>();
            List<String> needsList = config.getStringList("ranks." + index + ".needs");
            for(String need : needsList) {
                needsMap.put(need, new Need(need, fishConfig));
            }

            List<String> results = config.getStringList("ranks." + index + ".results");

            ranks.put(index, new Rank(displayName, displayItem, nextGrade, Material.valueOf(barrierMaterial), needsMap, results));
        }

    }

    public LinkedHashMap<String, Rank> getRanks() {
        return ranks;
    }
}
