package fr.florianpal.frankup.placeholders;

import fr.florianpal.frankup.FRankup;
import fr.florianpal.frankup.objects.Rank;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FPlaceholderExpansion extends PlaceholderExpansion {

    private final FRankup plugin;

    public FPlaceholderExpansion(FRankup plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "frankup";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Florianpal";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.contains("canRankup")) {
            Rank rank = plugin.getConfigurationManager().getLastRank(player);
            return rank.playerCanMake(player, plugin) ? "Oui" : "Non";
        }
        return null;
    }

}
