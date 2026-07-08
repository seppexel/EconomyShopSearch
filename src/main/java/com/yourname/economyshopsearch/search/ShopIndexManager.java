package com.yourname.economyshopsearch.search;

import com.yourname.economyshopsearch.EconomyShopSearch;
import me.gypopo.economyshopgui.EconomyShopGUI;
import me.gypopo.economyshopgui.objects.ShopItem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopIndexManager {
    private final EconomyShopSearch plugin;
    private final List<ShopItem> cachedIndex;

    public ShopIndexManager(EconomyShopSearch plugin) {
        this.plugin = plugin;
        this.cachedIndex = new ArrayList<>();
    }

    /**
     * Reads every shop item once via reflection from EconomyShopGUI's ShopManager
     * O(n) mapping to avoid hitting configurations during live search.
     */
    public void rebuildIndex() {
        cachedIndex.clear();
        try {
            // Because the public API lacks a direct `getAllShopItems()`, we hook into the plugin's loaded sections.
            EconomyShopGUI ecoShop = EconomyShopGUI.getInstance();
            if (ecoShop != null && ecoShop.getShopManager() != null) {
                // Fetch all items from all active shop categories.
                for (String section : ecoShop.getShopManager().getSections()) {
                    List<ShopItem> items = ecoShop.getShopManager().getShopItems(section);
                    if (items != null) {
                        cachedIndex.addAll(items);
                    }
                }
                plugin.getLogger().info("Successfully indexed " + cachedIndex.size() + " shop items.");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to build shop index! Ensure EconomyShopGUI is running.");
            e.printStackTrace();
        }
    }

    public List<ShopItem> search(String query) {
        List<ShopItem> results = new ArrayList<>();
        boolean fuzzy = plugin.getConfigManager().isEnableFuzzySearch();
        boolean caseSensitive = plugin.getConfigManager().isCaseSensitive();
        
        String processedQuery = processString(query, fuzzy, caseSensitive);

        for (ShopItem shopItem : cachedIndex) {
            ItemStack stack = shopItem.getItemToGive();
            if (stack == null || stack.getType().isAir()) continue;

            String materialName = processString(stack.getType().name(), fuzzy, caseSensitive);
            String displayName = "";
            
            if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
                // Strip colors before checking
                displayName = processString(stack.getItemMeta().getDisplayName().replaceAll("§.", ""), fuzzy, caseSensitive);
            }

            if (materialName.contains(processedQuery) || displayName.contains(processedQuery)) {
                results.add(shopItem);
                if (results.size() >= plugin.getConfigManager().getSearchLimit()) break;
            }
        }
        return results;
    }

    private String processString(String input, boolean fuzzy, boolean caseSensitive) {
        if (!caseSensitive) input = input.toLowerCase();
        if (fuzzy) input = input.replace("_", "").replace(" ", "");
        return input;
    }

    public void clearIndex() {
        cachedIndex.clear();
    }
}
