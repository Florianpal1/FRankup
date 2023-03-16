package fr.florianpal.frankup.objects;

import com.archyx.aureliumskills.api.AureliumAPI;
import com.archyx.aureliumskills.skills.Skills;
import fr.florianpal.frankup.enums.RankType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Need {
    private final String id;
    private final RankType rankType;
    private final String itemName;
    private final double quantity;

    public Need(String id) {
        this.id = id;
        String[] strings = id.split(":");

        switch (strings[0]) {
            case "exp" -> rankType = RankType.EXPERIENCE;
            case "money" -> rankType = RankType.MONEY;
            case "skills" -> rankType = RankType.SKILLS;
            case "totalSkills" -> rankType = RankType.TOTAL_SKILLS;
            default -> rankType = RankType.MINECRAFT;
        }
        itemName = strings[1];
        quantity = Double.parseDouble(strings[2]);
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

    public ItemStack getItemStack() {
        return new ItemStack(Material.getMaterial(itemName), (int)quantity);
    }

    public boolean getStatus(Player player, Economy economy) {
        switch (rankType) {
            case MINECRAFT -> {
                int sum = (int)quantity;
                for (int i = 0; i < player.getInventory().getContents().length; i++) {

                    ItemStack itemStack = player.getInventory().getItem(i);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }

                    if (itemStack.isSimilar(getItemStack())) {
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
            }
        }
        return false;
    }
}
