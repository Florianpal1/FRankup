
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

package fr.florianpal.frankup.gui.subGui;

import co.aikar.commands.CommandIssuer;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.florianpal.frankup.FRankup;
import fr.florianpal.frankup.configurations.ConfirmGuiConfig;
import fr.florianpal.frankup.gui.AbstractGui;
import fr.florianpal.frankup.gui.GuiInterface;
import fr.florianpal.frankup.gui.item.Barrier;
import fr.florianpal.frankup.gui.item.Confirm;
import fr.florianpal.frankup.languages.MessageKeys;
import fr.florianpal.frankup.managers.commandManagers.CommandManager;
import fr.florianpal.frankup.objects.Rank;
import fr.florianpal.frankup.utils.FormatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmGui extends AbstractGui implements GuiInterface {

    private final Rank rank;
    protected final ConfirmGuiConfig confirmConfig;
    private final Map<Integer, Confirm> confirmList = new HashMap<>();

    private CommandManager commandManager;

    ConfirmGui(FRankup plugin, Player player, int page, Rank rank) {
        super(plugin, player, page);
        this.rank = rank;
        this.confirmConfig = plugin.getConfigurationManager().getConfirmConfig();
        this.commandManager = plugin.getCommandManager();
        initGui(confirmConfig.getNameGui(), 27);
    }

    public void initializeItems() {

        for (Barrier barrier : confirmConfig.getBarrierBlocks()) {
            inv.setItem(barrier.getIndex(), createGuiItem(barrier.getMaterial(), barrier.getTitle(), barrier.getDescription()));
        }

        int id = 0;
        for (Map.Entry<Integer, Confirm> entry : confirmConfig.getConfirmBlocks().entrySet()) {
            Confirm confirm = new Confirm(this.rank, entry.getValue().getMaterial(), entry.getValue().isValue());
            confirmList.put(entry.getKey(), confirm);
            inv.setItem(entry.getKey(), createGuiItem(confirm));
            id++;
            if (id >= (confirmConfig.getConfirmBlocks().size())) break;
        }
        openInventory(player);
    }

    private ItemStack createGuiItem(Confirm confirm) {
        ItemStack item = new ItemStack(confirm.getMaterial(), 1);
        ItemMeta meta = item.getItemMeta();
        String title = "";
        if (confirm.isValue()) {
            title = confirmConfig.getTitleTrue();
        } else {
            title = confirmConfig.getTitleFalse();
        }

        List<String> listDescription = new ArrayList<>();
        ItemStack itemStack = new ItemStack(Material.getMaterial(confirm.getRank().getDisplayItem()));
        title = title.replace("{GradeName}", confirm.getRank().getDisplayName());
        title = FormatUtil.format(title);

        for (String desc : confirmConfig.getDescription()) {
            desc = desc.replace("{GradeName}", confirm.getRank().getDisplayName());
            desc = FormatUtil.format(desc);
            listDescription.add(desc);
        }

        if (meta != null) {
            meta.setDisplayName(title);
            meta.setLore(listDescription);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createGuiItem(Material material, String name, List<String> description) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        name = ChatColor.translateAlternateColorCodes('&', name);
        List<String> descriptions = new ArrayList<>();
        for (String desc : description) {
            desc = ChatColor.translateAlternateColorCodes('&', desc);
            descriptions.add(desc);
        }
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(descriptions);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (inv.getHolder() != this || e.getInventory() != inv) {
            return;
        }
        e.setCancelled(true);
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        for (Map.Entry<Integer, Confirm> entry : confirmConfig.getConfirmBlocks().entrySet()) {
            if (entry.getKey() == e.getRawSlot()) {
                CommandIssuer issuerTarget = commandManager.getCommandIssuer(player);
                if(entry.getValue().isValue()) {
                    rank.playerTake(player, plugin);
                    rank.playerGive(player, plugin.getServer());
                    issuerTarget.sendInfo(MessageKeys.RANKUP_SUCCESS, "{DisplayName}", FormatUtil.format(rank.getDisplayName()));

                    String format = plugin.getConfigurationManager().getGlobalConfig().getBroadcast()
                            .replace("{PlayerName}", player.getName())
                            .replace("{GradeName}", rank.getDisplayName());

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF(format);
                    out.writeUTF("");
                    player.sendPluginMessage(plugin, "fannounce:announce", out.toByteArray());

                    inv.close();
                } else {
                    issuerTarget.sendInfo(MessageKeys.RANKUP_CANCEL, "{DisplayName}", FormatUtil.format(rank.getDisplayName()));
                    inv.close();
                }

                return;
            }
        }
    }
}
