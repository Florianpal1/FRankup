package fr.florianpal.frankup.objects;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skills;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import fr.florianpal.fentreprise.FEntreprise;
import fr.florianpal.fentreprise.objects.Entreprise;
import fr.florianpal.frankup.configurations.FishConfig;
import fr.florianpal.frankup.enums.RankType;
import fr.florianpal.frankup.utils.FormatUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmoitems.MMOItems;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.UUID;

import static java.lang.Double.parseDouble;

public class Need {
    private final String id;
    private final RankType rankType;

    private FishConfig fishConfig;
    private final String itemName;
    private final double quantity;

    private String mmoItemType;
    NamespacedKey FISH_WEIGHT_FLAG = new NamespacedKey(Bukkit.getPluginManager().getPlugin("NoOneFishing"), "nf-fish-weight");
    NamespacedKey FISH_NAME_FLAG = new NamespacedKey(Bukkit.getPluginManager().getPlugin("NoOneFishing"), "nf-fish-name");
    public Need(String id, FishConfig fishConfig) {
        this.fishConfig = fishConfig;
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

        if(rankType == RankType.MMOITEMS) {
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
        if(rankType == RankType.MMOITEMS) {
            return MMOItems.plugin.getItem(MMOItems.plugin.getTypes().get(mmoItemType.toUpperCase()),itemName);
        }
        return null;
    }

    public ItemStack getItemStack() {

        ItemStack itemStack;
        try {
            if (rankType == RankType.MMOITEMS) {
                ItemStack mmoItem = getMMOItem();
                itemStack = new ItemStack(mmoItem.getType());
                itemStack.setAmount((int)quantity);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.displayName(mmoItem.displayName());
                itemStack.setItemMeta(itemMeta);

            } else if (rankType == RankType.POISSON) {
                itemStack = new ItemStack(Material.PLAYER_HEAD, (int)quantity);
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

                GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);

                Field field = skullMeta.getClass().getDeclaredField("profile");

                gameProfile.getProperties().put("textures", new Property("textures", fishConfig.getRanks().get(itemName).getTexture()));

                field.setAccessible(true); // We set as accessible to modify.
                field.set(skullMeta, gameProfile);

                skullMeta.setDisplayName(FormatUtil.format(fishConfig.getRanks().get(itemName).getName())); // We set a displayName to the skull
                itemStack.setItemMeta(skullMeta);
                itemStack.setAmount((int)quantity);
            } else {
                itemStack = new ItemStack(Material.getMaterial(itemName), (int)quantity);
            }

        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
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
                    String namespacedKeyName = itemStack.getItemMeta().getPersistentDataContainer().get(FISH_NAME_FLAG, PersistentDataType.STRING);
                    Float namespacedKeyWeight = itemStack.getItemMeta().getPersistentDataContainer().get(FISH_WEIGHT_FLAG, PersistentDataType.FLOAT);

                    if (namespacedKeyName != null && namespacedKeyName.equalsIgnoreCase(fishConfig.getRanks().get(itemName).getName()) && namespacedKeyWeight != null && namespacedKeyWeight >= quantity) {
                        return true;
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
