
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

import fr.florianpal.frankup.enums.BarrierType;
import fr.florianpal.frankup.gui.item.Barrier;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MainGuiConfig {

    private List<Barrier> barrierBlocks = new ArrayList<>();

    private List<Integer> rankBlocks = new ArrayList<>();
    private List<Barrier> closeBlocks = new ArrayList<>();

    private List<Barrier> confirmBlocks = new ArrayList<>();

    private int size = 27;

    private String nameGui = "";

    private Barrier rankRemplacement;


    public void load(Configuration config) {
        barrierBlocks = new ArrayList<>();
        closeBlocks = new ArrayList<>();
        rankBlocks = new ArrayList<>();
        confirmBlocks = new ArrayList<>();

        for (String index : config.getConfigurationSection("block").getKeys(false)) {
            String utility = config.getString("block." + index + ".utility");
            if(utility == null) {
                continue;
            }

            if (utility.equalsIgnoreCase("barrier")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        BarrierType.valueOf(config.getString("block." + index + ".type")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        new Barrier(
                                Integer.parseInt(index),
                                Material.getMaterial(config.getString("block." + index + ".replacement.material")),
                                BarrierType.valueOf(config.getString("block." + index + ".type")),
                                config.getString("block." + index + ".replacement.title"),
                                config.getStringList("block." + index + ".replacement.description")
                        )
                );
                barrierBlocks.add(barrier);
            } else if (utility.equalsIgnoreCase("close")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description")
                );
                closeBlocks.add(barrier);
            } else if (utility.equalsIgnoreCase("confirm")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description")
                );
                confirmBlocks.add(barrier);
            } else if (utility.equalsIgnoreCase("rank")) {
                rankBlocks.add(Integer.parseInt(index));
            }
        }

        rankRemplacement = new Barrier(-1,
                Material.getMaterial(config.getString("rankRemplacement.material")),
                config.getString("rankRemplacement.title"),
                config.getStringList("rankRemplacement.description")
        );

        size = config.getInt("gui.size");
        nameGui = config.getString("gui.name");
    }


    public String getNameGui() {
        return nameGui;
    }

    public List<Barrier> getBarrierBlocks() {
        return barrierBlocks;
    }

    public List<Barrier> getCloseBlocks() {
        return closeBlocks;
    }

    public int getSize() {
        return size;
    }

    public List<Integer> getRankBlocks() {
        return rankBlocks;
    }

    public List<Barrier> getConfirmBlocks() {
        return confirmBlocks;
    }

    public Barrier getRankRemplacement() {
        return rankRemplacement;
    }
}
