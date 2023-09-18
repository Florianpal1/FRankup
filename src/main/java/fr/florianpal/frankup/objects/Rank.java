package fr.florianpal.frankup.objects;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skills;
import com.willfp.ecoitems.items.EcoItem;
import fr.florianpal.fentreprise.FEntreprise;
import fr.florianpal.fentreprise.objects.Entreprise;
import fr.florianpal.frankup.FRankup;
import fr.florianpal.frankup.enums.RankType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Double.parseDouble;

public class Rank {

    private final String displayName;
    private final String displayItem;
    private final String nextGrade;
    private final Material barrierMaterial;
    private final LinkedHashMap<String, Need> needs;
    private final List<String> result;

    NamespacedKey FISH_WEIGHT_FLAG = new NamespacedKey(Bukkit.getPluginManager().getPlugin("NoOneFishing"), "nf-fish-weight");
    NamespacedKey FISH_NAME_FLAG = new NamespacedKey(Bukkit.getPluginManager().getPlugin("NoOneFishing"), "nf-fish-name");
    public Rank(String displayName, String displayItem, String nextGrade, Material barrierMaterial, LinkedHashMap<String, Need> needs, List<String> result) {
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.nextGrade = nextGrade;
        this.barrierMaterial = barrierMaterial;
        this.needs = needs;
        this.result = result;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayItem() {
        return displayItem;
    }

    public String getNextGrade() {
        return nextGrade;
    }

    public Material getBarrierMaterial() {
        return barrierMaterial;
    }

    public LinkedHashMap<String, Need> getNeeds() {
        return needs;
    }

    public List<String> getResult() {
        return result;
    }

    public boolean playerCanMake(Player player, FRankup fRankup) {
        for(Map.Entry<String, Need> entry : needs.entrySet()) {
            if(entry.getValue().getRankType() == RankType.MMOITEMS || entry.getValue().getRankType() == RankType.MINECRAFT) {
                int quantity = 0;
                for(ItemStack itemStack : player.getInventory().getContents()) {
                    if(itemStack == null) {
                        continue;
                    }

                    ItemStack mmoItem = entry.getValue().getMMOItem();
                    if(itemStack.isSimilar(entry.getValue().getItemStack()) || (mmoItem != null && mmoItem.isSimilar(itemStack))) {
                        quantity = quantity + itemStack.getAmount();
                    }
                }

                if(quantity < entry.getValue().getQuantity()) {
                    return false;
                }

            } else if(entry.getValue().getRankType() == RankType.EXPERIENCE) {
                if(player.getTotalExperience() < (entry.getValue().getQuantity())) {
                    return false;
                }
            } else if(entry.getValue().getRankType() == RankType.MONEY && !fRankup.getVaultIntegrationManager().getEconomy().has(player, entry.getValue().getQuantity())) {
                return false;
            } else if(entry.getValue().getRankType() == RankType.SKILLS) {
                if(AureliumAPI.getSkillLevel(player, Skills.valueOf(entry.getValue().getItemName())) < entry.getValue().getQuantity()) {
                    return false;
                }
            } else if (entry.getValue().getRankType() == RankType.POISSON) {
                boolean take = false;
                for(ItemStack itemStack : player.getInventory().getContents()) {
                    if(itemStack == null) {
                        continue;
                    }

                    String namespacedKeyName = itemStack.getItemMeta().getPersistentDataContainer().get(FISH_NAME_FLAG, PersistentDataType.STRING);
                    Float namespacedKeyWeight = itemStack.getItemMeta().getPersistentDataContainer().get(FISH_WEIGHT_FLAG, PersistentDataType.FLOAT);

                    if(namespacedKeyName != null && namespacedKeyName.equalsIgnoreCase(fRankup.getConfigurationManager().getFishConfig().getRanks().get(entry.getValue().getItemName()).getName()) && namespacedKeyWeight!= null && namespacedKeyWeight >= entry.getValue().getQuantity()) {
                        take = true;
                    }

                }
                if(!take) {
                    return false;
                }
            } else if (entry.getValue().getRankType() == RankType.TOTAL_SKILLS) {
                if(AureliumAPI.getTotalLevel(player) < entry.getValue().getQuantity()) {
                    return false;
                }
            } else if (entry.getValue().getRankType() == RankType.COMPANY) {
                Entreprise entreprise = FEntreprise.getEntrepriseByUUID(player);
                if((entreprise == null || entreprise.getLevel() < entry.getValue().getQuantity())) {
                    return false;
                }
            } else if (entry.getValue().getRankType() == RankType.DIVERSITY) {
                if((parseDouble(PlaceholderAPI.setPlaceholders(player, "%luckperms_meta_karma%"))) > entry.getValue().getQuantity()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void playerTake(Player player, FRankup fRankup) {
        for (Map.Entry<String, Need> entry : needs.entrySet()) {
            if (entry.getValue().getRankType() == RankType.MMOITEMS || entry.getValue().getRankType() == RankType.MINECRAFT) {
                int quantity = (int)entry.getValue().getQuantity();
                for (int i = 0; i < player.getInventory().getContents().length; i++) {

                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }

                    ItemStack mmoItem = entry.getValue().getMMOItem();
                    if (itemStack.isSimilar(entry.getValue().getItemStack()) || (mmoItem != null && mmoItem.isSimilar(itemStack))) {
                        if (itemStack.getAmount() > quantity) {
                            itemStack.setAmount(itemStack.getAmount() - quantity);
                            break;
                        } else if (itemStack.getAmount() == quantity) {
                            player.getInventory().setItem(i, null);
                            break;
                        } else {
                            quantity = quantity - itemStack.getAmount();
                            player.getInventory().setItem(i, null);
                        }
                    }
                }
            } else if (entry.getValue().getRankType() == RankType.EXPERIENCE) {
                player.giveExp(-((int)entry.getValue().getQuantity()));
            } else if (entry.getValue().getRankType() == RankType.MONEY) {
                fRankup.getVaultIntegrationManager().getEconomy().withdrawPlayer(player, entry.getValue().getQuantity());
            } else if (entry.getValue().getRankType() == RankType.POISSON) {
                for (int i = 0; i < player.getInventory().getContents().length; i++) {

                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }

                    String namespacedKeyName = itemStack.getItemMeta().getPersistentDataContainer().get(FISH_NAME_FLAG, PersistentDataType.STRING);
                    Float namespacedKeyWeight = itemStack.getItemMeta().getPersistentDataContainer().get(FISH_WEIGHT_FLAG, PersistentDataType.FLOAT);

                    if (namespacedKeyName != null && namespacedKeyName.equalsIgnoreCase(fRankup.getConfigurationManager().getFishConfig().getRanks().get(entry.getValue().getItemName()).getName()) && namespacedKeyWeight != null && namespacedKeyWeight >= entry.getValue().getQuantity()) {
                        if(itemStack.getAmount() > 1) {
                            itemStack.setAmount(itemStack.getAmount() - 1);
                        } else {
                            player.getInventory().setItem(i, null);
                        }

                        break;
                    }
                }

            }
        }
    }

    public void playerGive(Player player, Server server) {
        for(String command : result) {
            String[] commands = command.split(":");
            server.dispatchCommand(server.getConsoleSender(), commands[1].replace("{PlayerName}", player.getName()));
        }
    }
}
