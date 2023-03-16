
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
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultIntegrationManager {
    private Economy economy;
    public VaultIntegrationManager(FRankup plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            if (!setupEconomy()) {
                plugin.getLogger().warning("Failed to initialize Vault Economy");
            } else {
                plugin.getLogger().info("Registered Vault Economy");
            }
        } else {
            plugin.getLogger().warning("Vault is not on the server, some features will not work");
        }
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    public Economy getEconomy() {
        return economy;
    }
}
