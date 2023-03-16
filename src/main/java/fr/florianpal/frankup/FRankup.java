package fr.florianpal.frankup;

import fr.florianpal.frankup.commands.RankupCommand;
import fr.florianpal.frankup.managers.ConfigurationManager;
import fr.florianpal.frankup.managers.VaultIntegrationManager;
import fr.florianpal.frankup.managers.commandManagers.CommandManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class FRankup extends JavaPlugin {

    private ConfigurationManager configurationManager;

    private LuckPerms luckPerms;

    private VaultIntegrationManager vaultIntegrationManager;

    private CommandManager commandManager;

    @Override
    public void onEnable() {
        configurationManager = new ConfigurationManager(this);
        vaultIntegrationManager = new VaultIntegrationManager(this);

        File languageFile = new File(getDataFolder(), "lang_" + configurationManager.getGlobalConfig().getLang() + ".yml");
        createDefaultConfiguration(languageFile, "lang_" + configurationManager.getGlobalConfig().getLang() + ".yml");

        commandManager = new CommandManager(this);
        commandManager.registerDependency(ConfigurationManager.class, configurationManager);

        commandManager.registerCommand(new RankupCommand(this));

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        } else {
            getServer().getPluginManager().disablePlugin(this);
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "fannounce:announce");
    }

    public void createDefaultConfiguration(File actual, String defaultName) {
        // Make parent directories
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (actual.exists()) {
            return;
        }

        InputStream input = null;
        try {
            JarFile file = new JarFile(this.getFile());
            ZipEntry copy = file.getEntry(defaultName);
            if (copy == null) throw new FileNotFoundException();
            input = file.getInputStream(copy);
        } catch (IOException e) {
            getLogger().severe("Unable to read default configuration: " + defaultName);
        }

        if (input != null) {
            FileOutputStream output;
            try {
                output = new FileOutputStream(actual);
                byte[] buf = new byte[8192];
                int length;
                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                getLogger().info("Default configuration file written: " + actual.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void reloadConfig() {
        configurationManager.reload();
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public VaultIntegrationManager getVaultIntegrationManager() {
        return vaultIntegrationManager;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
