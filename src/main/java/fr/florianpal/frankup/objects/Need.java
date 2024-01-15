package fr.florianpal.frankup.objects;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skills;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.florianpal.fentreprise.FEntreprise;
import fr.florianpal.fentreprise.objects.Entreprise;
import fr.florianpal.frankup.enums.RankType;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmoitems.MMOItems;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static java.lang.Double.parseDouble;

public class Need {
    private final String id;
    private final RankType rankType;
    private final String itemName;
    private final double quantity;
    private String mmoItemType;

    public Need(String id) {
        this.id = id;
        String[] strings = id.split(":");

        switch (strings[0]) {
            case "exp" -> rankType = RankType.EXPERIENCE;
            case "mmoitems" -> rankType = RankType.MMOITEMS;
            case "money" -> rankType = RankType.MONEY;
            case "fish" -> rankType = RankType.POISSON;
            case "skills" -> rankType = RankType.SKILLS;
            case "totalSkills" -> rankType = RankType.TOTAL_SKILLS;
            case "diversity" -> rankType = RankType.DIVERSITY;
            case "company" -> rankType = RankType.COMPANY;
            default -> rankType = RankType.MINECRAFT;
        }
        itemName = strings[1];
        quantity = Double.parseDouble(strings[2]);

        if(rankType == RankType.MMOITEMS || rankType == RankType.POISSON) {
            mmoItemType = strings[3];
        }
    }

    public RankType getRankType() {
        return rankType;
    }

    public String getItemName() {
        return itemName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getId() {
        return id;
    }

    public ItemStack getMMOItem() {
        if(rankType == RankType.MMOITEMS || rankType == RankType.POISSON) {
            return MMOItems.plugin.getItem(MMOItems.plugin.getTypes().get(mmoItemType.toUpperCase()), itemName);
        }
        return null;
    }

    public ItemStack getItemStack() {

        ItemStack itemStack;
        if (rankType == RankType.MMOITEMS || rankType == RankType.POISSON) {
            ItemStack mmoItem = getMMOItem();
            itemStack = new ItemStack(mmoItem.getType());
            itemStack.setAmount((int)quantity);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(mmoItem.displayName());
            itemStack.setItemMeta(itemMeta);

        } else {
            itemStack = new ItemStack(Material.getMaterial(itemName), (int)quantity);
        }

        return itemStack;
    }

    public boolean getStatus(Player player, Economy economy) {
        switch (rankType) {
            case MINECRAFT, MMOITEMS -> {
                int sum = (int)quantity;
                for (int i = 0; i < player.getInventory().getContents().length; i++) {

                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }

                    ItemStack mmoItem = getMMOItem();
                    if (itemStack.isSimilar(getItemStack()) || (mmoItem != null && mmoItem.isSimilar(itemStack))) {
                        if (itemStack.getAmount() > sum) {
                            sum = 0;
                        } else if (itemStack.getAmount() == sum) {
                            sum = 0;
                        } else {
                            sum = sum - itemStack.getAmount();
                        }
                    }

                    if (sum == 0) {
                        return true;
                    }
                }

                return sum == 0;
            }
            case POISSON -> {
                for (int i = 0; i < player.getInventory().getContents().length; i++) {
                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }
                    NBTItem nbti = new NBTItem(itemStack);
                    NBTCompound nbtCompound = nbti.getCompound("CustomFishing");
                    String name = nbti.getString("MMOITEMS_ITEM_ID");

                    if (nbtCompound != null && name != null && nbtCompound.hasTag("size")) {
                        float size = nbtCompound.getFloat("size");

                        if (name.equalsIgnoreCase(itemName) && size >= quantity) {
                            return true;
                        }
                    }
                }
            }
            case EXPERIENCE -> {
                return player.getTotalExperience() > quantity;
            }
            case MONEY -> {
                return economy.has(player, quantity);
            }
            case SKILLS -> {
                return AureliumAPI.getSkillLevel(player, Skills.valueOf(itemName.toUpperCase())) >= quantity;
            } case TOTAL_SKILLS -> {
                return AureliumAPI.getTotalLevel(player) >= quantity;
            } case COMPANY -> {
                Entreprise entreprise = FEntreprise.getEntrepriseByUUID(player);
                return (entreprise != null && entreprise.getLevel() >= quantity);
            } case DIVERSITY -> {
                return (parseDouble(PlaceholderAPI.setPlaceholders(player, "%luckperms_meta_karma%"))) < quantity;
            }
        }
        return false;
    }
}
