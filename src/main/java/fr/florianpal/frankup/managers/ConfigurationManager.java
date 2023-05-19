
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

package fr.florianpal.frankup.managers;

import fr.florianpal.frankup.FRankup;
import fr.florianpal.frankup.configurations.*;
import fr.florianpal.frankup.objects.Rank;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class ConfigurationManager {

    FRankup plugin;

    private final MainGuiConfig mainGuiConfig = new MainGuiConfig();
    private final File mainGuiFile;
    private FileConfiguration mainGuiConfiguration;

    private final GlobalConfig globalConfig = new GlobalConfig();
    private final File globalFile;
    private FileConfiguration globalConfiguration;

    private final RankupConfig rankupConfig = new RankupConfig();
    private final File rankupFile;
    private FileConfiguration rankupConfiguration;

    private final ConfirmGuiConfig confirmConfig = new ConfirmGuiConfig();
    private FileConfiguration confirmConfiguration;
    File confirmFile;

    private final RenameConfig renameConfig = new RenameConfig();
    private final File renameFile;
    private FileConfiguration renameConfiguration;

    public ConfigurationManager(FRankup core) {
        plugin = core;

        mainGuiFile = new File(core.getDataFolder(), "mainGui.yml");
        core.createDefaultConfiguration(mainGuiFile, "mainGui.yml");
        mainGuiConfiguration = YamlConfiguration.loadConfiguration(mainGuiFile);

        globalFile = new File(core.getDataFolder(), "config.yml");
        core.createDefaultConfiguration(globalFile, "config.yml");
        globalConfiguration = YamlConfiguration.loadConfiguration(globalFile);

        rankupFile = new File(core.getDataFolder(), "rankup.yml");
        core.createDefaultConfiguration(rankupFile, "rankup.yml");
        rankupConfiguration = YamlConfiguration.loadConfiguration(rankupFile);

        confirmFile = new File(core.getDataFolder(), "confirmGui.yml");
        core.createDefaultConfiguration(confirmFile, "confirmGui.yml");
        confirmConfiguration = YamlConfiguration.loadConfiguration(confirmFile);

        renameFile = new File(core.getDataFolder(), "rename.yml");
        core.createDefaultConfiguration(renameFile, "rename.yml");
        renameConfiguration = YamlConfiguration.loadConfiguration(renameFile);

        renameConfig.load(renameConfiguration);
        rankupConfig.load(rankupConfiguration);
        globalConfig.load(globalConfiguration);
        mainGuiConfig.load(mainGuiConfiguration);
    }

    public void reload() {
        mainGuiConfiguration = YamlConfiguration.loadConfiguration(mainGuiFile);
        globalConfiguration = YamlConfiguration.loadConfiguration(globalFile);
        rankupConfiguration = YamlConfiguration.loadConfiguration(rankupFile);
        confirmConfiguration = YamlConfiguration.loadConfiguration(confirmFile);
        renameConfiguration = YamlConfiguration.loadConfiguration(renameFile);

        renameConfig.load(renameConfiguration);
        confirmConfig.load(confirmConfiguration);
        rankupConfig.load(rankupConfiguration);
        globalConfig.load(globalConfiguration);
        mainGuiConfig.load(mainGuiConfiguration);
    }

    public Rank getLastRank(Player player) {
        String groupeJoueur = PlaceholderAPI.setPlaceholders(player, "%luckperms_last_group_on_tracks_joueurs%");
        return rankupConfig.getRanks().get(groupeJoueur);
    }

    public MainGuiConfig getMainGuiConfig() {
        return mainGuiConfig;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public RankupConfig getRankupConfig() {
        return rankupConfig;
    }

    public ConfirmGuiConfig getConfirmConfig() {
        return confirmConfig;
    }

    public RenameConfig getRenameConfig() {
        return renameConfig;
    }
}
