
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
import com.archyx.aureliumskills.api.AureliumAPI;
import fr.florianpal.frankup.FRankup;
import fr.florianpal.frankup.configurations.MainGuiConfig;
import fr.florianpal.frankup.enums.BarrierType;
import fr.florianpal.frankup.enums.RankType;
import fr.florianpal.frankup.gui.AbstractGui;
import fr.florianpal.frankup.gui.GuiInterface;
import fr.florianpal.frankup.gui.item.Barrier;
import fr.florianpal.frankup.languages.MessageKeys;
import fr.florianpal.frankup.managers.commandManagers.CommandManager;
import fr.florianpal.frankup.objects.Need;
import fr.florianpal.frankup.objects.Rank;
import fr.florianpal.frankup.utils.FormatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainGui extends AbstractGui implements GuiInterface {

    private final Rank rank;
    MainGuiConfig mainGuiConfig;

    private CommandManager commandManager;

    public MainGui(FRankup plugin, Player player, int page) {
        super(plugin, player, page);
        mainGuiConfig = plugin.getConfigurationManager().getMainGuiConfig();
        rank = plugin.getConfigurationManager().getLastRank(player);
        commandManager = plugin.getCommandManager();
        initGui(mainGuiConfig.getNameGui().replace("{DisplayName}", rank.getDisplayName()), mainGuiConfig.getSize());
    }

    public void initializeItems() {


        String titleInv = FormatUtil.format(mainGuiConfig.getNameGui().replace("{DisplayName}", rank.getDisplayName()));
        inv = Bukkit.createInventory(this, mainGuiConfig.getSize(), titleInv);

        initBarrier();
        initRank();
        openInventory(player);
    }


    private void initBarrier() {

        for (Barrier barrier : mainGuiConfig.getBarrierBlocks()) {
            if(barrier.getType() == BarrierType.BARRIER) {
                inv.setItem(barrier.getIndex(), createGuiItem(barrier.getMaterial(), barrier.getTitle(), barrier.getDescription()));
            } else {
                inv.setItem(barrier.getIndex(), createGuiItem(rank.getBarrierMaterial(), barrier.getTitle(), barrier.getDescription()));
            }
        }

        for (Barrier close : mainGuiConfig.getCloseBlocks()) {
            inv.setItem(close.getIndex(), createGuiItem(close.getMaterial(), close.getTitle(), close.getDescription()));
        }

        for (Barrier confirm : mainGuiConfig.getConfirmBlocks()) {
            inv.setItem(confirm.getIndex(), createGuiItem(confirm.getMaterial(), confirm.getTitle(), confirm.getDescription()));
        }
    }

    private void initRank() {
        for(int i = 0; i < mainGuiConfig.getRankBlocks().size(); i++) {
            Integer barrier = mainGuiConfig.getRankBlocks().get(i);

            if (rank.getNeeds().size() <= i) {
                inv.setItem(barrier, createGuiItem(mainGuiConfig.getRankRemplacement().getMaterial(), mainGuiConfig.getRankRemplacement().getTitle(), mainGuiConfig.getRankRemplacement().getDescription()));
            } else {
                Need need = null;
                int j = 0;
                for (Map.Entry<String, Need> n : rank.getNeeds().entrySet()) {
                    if (i == j) {
                        need = n.getValue();
                        break;
                    }
                    j = j + 1;
                }

                if (need == null) {
                    continue;
                }

                ItemStack needItemStack;
                if (globalConfig.getDisplayItems().containsKey(need.getRankType().toString())) {
                    needItemStack = new ItemStack(globalConfig.getDisplayItems().get(need.getRankType().toString()), (int)need.getQuantity());
                    if(need.getRankType() == RankType.EXPERIENCE || need.getRankType() == RankType.MONEY || need.getRankType() == RankType.POISSON) {
                        needItemStack.setAmount(1);
                    }
                } else {
                    needItemStack = new ItemStack(need.getItemStack());
                }

                ItemMeta itemMeta = needItemStack.getItemMeta();

                List<String> descriptions = new ArrayList<>();
                String title = "";
                if (need.getRankType() == RankType.MMOITEMS || need.getRankType() == RankType.MINECRAFT) {
                    descriptions = globalConfig.getDisplayDescriptions().get("item");
                    title = globalConfig.getDisplayTitles().get("item");
                } else if (need.getRankType() == RankType.POISSON) {
                    descriptions = globalConfig.getDisplayDescriptions().get("fish");
                    title = globalConfig.getDisplayTitles().get("fish");
                } else if (need.getRankType() == RankType.MONEY) {
                    descriptions = globalConfig.getDisplayDescriptions().get("money");
                    title = globalConfig.getDisplayTitles().get("money");
                } else if (need.getRankType() == RankType.EXPERIENCE) {
                    descriptions = globalConfig.getDisplayDescriptions().get("experience");
                    title = globalConfig.getDisplayTitles().get("experience");
                } else if (need.getRankType() == RankType.SKILLS) {
                    descriptions = globalConfig.getDisplayDescriptions().get("skills");
                    title = globalConfig.getDisplayTitles().get("skills");
                } else if (need.getRankType() == RankType.TOTAL_SKILLS) {
                    descriptions = globalConfig.getDisplayDescriptions().get("total_skills");
                    title = globalConfig.getDisplayTitles().get("total_skills");
                } else if (need.getRankType() == RankType.COMPANY) {
                    descriptions = globalConfig.getDisplayDescriptions().get("company");
                    title = globalConfig.getDisplayTitles().get("company");
                } else if (need.getRankType() == RankType.DIVERSITY) {
                    descriptions = globalConfig.getDisplayDescriptions().get("diversity");
                    title = globalConfig.getDisplayTitles().get("diversity");
                }

                String displayName = "";
                if(need.getRankType() == RankType.SKILLS) {
                    displayName = AureliumAPI.getPlugin().getSkillRegistry().getSkill(need.getItemName().toUpperCase()).getDisplayName(Locale.FRANCE);
                } else {
                    if(plugin.getConfigurationManager().getRenameConfig().getRenameMap().containsKey(need.getItemName())) {
                        displayName = plugin.getConfigurationManager().getRenameConfig().getRenameMap().get(need.getItemName());
                    } else {
                        displayName = LegacyComponentSerializer.legacyAmpersand().serialize(needItemStack.displayName().hoverEvent(null)).replace("[", "").replace("]", "");
                    }
                }

                List<String> descriptionsFormatted = new ArrayList<>();
                for (String desc : descriptions) {
                    desc = desc.replace("{DisplayNameItem}", FormatUtil.formatWithout(displayName));
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(0);
                    desc = desc.replace("{Quantity}", df.format(need.getQuantity()));
                    if (need.getStatus(player, plugin.getVaultIntegrationManager().getEconomy())) {
                        desc = desc.replace("{Status}", globalConfig.getValid());
                    } else {
                        desc = desc.replace("{Status}", globalConfig.getNotValid());
                    }

                    desc = FormatUtil.format(desc);
                    descriptionsFormatted.add(desc);
                }

                title = title.replace("{DisplayNameItem}", FormatUtil.formatWithout(displayName));
                title = title.replace("{Quantity}", String.valueOf(need.getQuantity()));
                if (need.getStatus(player, plugin.getVaultIntegrationManager().getEconomy())) {
                    title = title.replace("{Status}", globalConfig.getValid());
                } else {
                    title = title.replace("{Status}", globalConfig.getNotValid());
                }

                itemMeta.setDisplayName(FormatUtil.format(title));
                itemMeta.setLore(descriptionsFormatted);
                needItemStack.setItemMeta(itemMeta);
                this.inv.setItem(barrier, needItemStack);
            }
        }
    }

    public ItemStack createGuiItem(Material material, String name, List<String> description) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        name = FormatUtil.format(name);
        List<String> descriptions = new ArrayList<>();
        for (String desc : description) {
            desc = FormatUtil.format(desc);
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

        for (Barrier close : mainGuiConfig.getCloseBlocks()) {
            if (e.getRawSlot() == close.getIndex()) {
                inv.close();
                return;
            }
        }

        for(Barrier confirm : mainGuiConfig.getConfirmBlocks()) {
            if (e.getRawSlot() == confirm.getIndex()) {
                if(rank.playerCanMake(player, plugin)) {
                    ConfirmGui confirmGui = new ConfirmGui(plugin, player, 1, rank);
                    confirmGui.initializeItems();
                } else {
                    CommandIssuer issuerTarget = commandManager.getCommandIssuer(player);
                    issuerTarget.sendInfo(MessageKeys.RANKUP_CANNOT, "{DisplayName}", FormatUtil.format(rank.getDisplayName()));
                }
                return;
            }
        }
    }
}