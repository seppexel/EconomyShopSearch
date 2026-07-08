package com.yourname.economyshopsearch;

import com.yourname.economyshopsearch.command.SearchCommand;
import com.yourname.economyshopsearch.config.ConfigManager;
import com.yourname.economyshopsearch.gui.SearchGUI;
import com.yourname.economyshopsearch.search.ShopIndexManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconomyShopSearch extends JavaPlugin {

    private ConfigManager configManager;
    private ShopIndexManager indexManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.indexManager = new ShopIndexManager(this);

        // Build index on startup (delay by 1 tick to ensure EconomyShopGUI finishes loading)
        Bukkit.getScheduler().runTaskLater(this, () -> indexManager.rebuildIndex(), 1L);

        // Register Command
        getCommand("search").setExecutor(new SearchCommand(this));

        // Register GUI Listener
        getServer().getPluginManager().registerEvents(new SearchGUI.GUIListener(this), this);

        getLogger().info("EconomyShopSearch has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        if (indexManager != null) {
            indexManager.clearIndex();
        }
        getLogger().info("EconomyShopSearch disabled.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ShopIndexManager getIndexManager() {
        return indexManager;
    }
}
