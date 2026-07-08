package com.yourname.economyshopsearch.config;

import com.yourname.economyshopsearch.EconomyShopSearch;

public class ConfigManager {
    private final EconomyShopSearch plugin;
    
    private int searchLimit;
    private int searchMinLength;
    private boolean openBuyMenu;
    private boolean enableFuzzySearch;
    private boolean caseSensitive;

    public ConfigManager(EconomyShopSearch plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.reloadConfig();
        this.searchLimit = plugin.getConfig().getInt("search-limit", 500);
        this.searchMinLength = plugin.getConfig().getInt("search-min-length", 2);
        this.openBuyMenu = plugin.getConfig().getBoolean("open-buy-menu", true);
        this.enableFuzzySearch = plugin.getConfig().getBoolean("enable-fuzzy-search", true);
        this.caseSensitive = plugin.getConfig().getBoolean("case-sensitive", false);
    }

    public int getSearchLimit() { return searchLimit; }
    public int getSearchMinLength() { return searchMinLength; }
    public boolean isOpenBuyMenu() { return openBuyMenu; }
    public boolean isEnableFuzzySearch() { return enableFuzzySearch; }
    public boolean isCaseSensitive() { return caseSensitive; }
}
