
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

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalConfig {

    private String lang = "fr";

    private String valid;

    private String notValid;

    private Map<String, Material> displayItems;

    private Map<String, List<String>> displayDescriptions;

    private Map<String, String> displayTitles;

    private String broadcast;

    public void load(Configuration config) {
        lang = config.getString("lang");
        valid = config.getString("status.valid");
        notValid = config.getString("status.notValid");
        broadcast = config.getString("broadcast");

        displayItems = new HashMap<>();
        for (String index : config.getConfigurationSection("display").getKeys(false)) {
            displayItems.put(index, Material.getMaterial(config.getString("display." + index)));
        }

        displayDescriptions = new HashMap<>();
        for (String index : config.getConfigurationSection("descriptions").getKeys(false)) {
            displayDescriptions.put(index, config.getStringList("descriptions." + index));
        }

        displayTitles = new HashMap<>();
        for (String index : config.getConfigurationSection("titles").getKeys(false)) {
            displayTitles.put(index, config.getString("titles." + index));
        }
    }

    public String getLang() {
        return lang;
    }

    public String getValid() {
        return valid;
    }

    public String getNotValid() {
        return notValid;
    }

    public Map<String, Material> getDisplayItems() {
        return displayItems;
    }

    public Map<String, List<String>> getDisplayDescriptions() {
        return displayDescriptions;
    }

    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    public String getBroadcast() {
        return broadcast;
    }
}
