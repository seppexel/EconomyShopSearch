package com.yourname.economyshopsearch.gui;

import com.yourname.economyshopsearch.EconomyShopSearch;
import me.gypopo.economyshopgui.EconomyShopGUI;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.objects.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SearchGUI implements InventoryHolder {

    private final EconomyShopSearch plugin;
    private final Player player;
    private final List<ShopItem> results;
    private final String query;
    private Inventory inventory;
    private int currentPage = 0;
    
    public SearchGUI(EconomyShopSearch plugin, Player player, List<ShopItem> results, String query) {
        this.plugin = plugin;
        this.player = player;
        this.results = results;
        this.query = query;
    }

    public void open(int page) {
        this.currentPage = page;
        
        int totalItems = results.size();
        int itemsPerPage = 45; // Max items per page before nav bar
        int maxPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        // Calculate dynamic size (9, 18, 27, 36, 45, 54) based on page contents
        int itemsOnThisPage = Math.min(totalItems - (page * itemsPerPage), itemsPerPage);
        int rowsNeeded = (int) Math.ceil((double) itemsOnThisPage / 9.0);
        int inventorySize = (rowsNeeded + 1) * 9; // +1 row for navigation
        if (inventorySize > 54) inventorySize = 54;
        if (inventorySize < 18) inventorySize = 18;

        inventory = Bukkit.createInventory(this, inventorySize, "Search Results: " + query);

        // Populate items
        int startIndex = page * itemsPerPage;
        for (int i = 0; i < itemsOnThisPage; i++) {
            ShopItem shopItem = results.get(startIndex + i);
            ItemStack displayItem = shopItem.getItemToGive().clone();
            
            // Add click-to-purchase tooltip
            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.YELLOW + "Click to purchase");
                lore.add(ChatColor.GRAY + "Shift-click for info");
                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }
            inventory.setItem(i, displayItem);
        }

        // Navigation Bar (Bottom Row)
        int navRowStart = inventorySize - 9;
        
        if (currentPage > 0) {
            inventory.setItem(navRowStart + 3, createNavItem(Material.ARROW, "&aPrevious Page"));
        }
        
        inventory.setItem(navRowStart + 4, createNavItem(Material.BARRIER, "&cClose"));
        inventory.setItem(navRowStart + 8, createNavItem(Material.PAPER, "&ePage " + (currentPage + 1) + "/" + Math.max(1, maxPages)));
        
        if (currentPage < maxPages - 1) {
            inventory.setItem(navRowStart + 5, createNavItem(Material.ARROW, "&aNext Page"));
        }

        player.openInventory(inventory);
    }

    private ItemStack createNavItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    // Static listener bound on startup
    public static class GUIListener implements Listener {
        private final EconomyShopSearch plugin;

        public GUIListener(EconomyShopSearch plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder() instanceof SearchGUI gui)) return;
            event.setCancelled(true); // Prevent item stealing

            if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();
            int inventorySize = gui.inventory.getSize();

            // Navigation clicks
            if (slot >= inventorySize - 9) {
                if (slot == inventorySize - 6 && gui.currentPage > 0) { // Previous
                    player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                    gui.open(gui.currentPage - 1);
                } else if (slot == inventorySize - 4 && event.getCurrentItem().getType() == Material.ARROW) { // Next
                    player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
                    gui.open(gui.currentPage + 1);
                } else if (slot == inventorySize - 5) { // Close
                    player.closeInventory();
                }
                return;
            }

            // Clicked a ShopItem
            int itemIndex = (gui.currentPage * 45) + slot;
            if (itemIndex < gui.results.size()) {
                ShopItem clickedShopItem = gui.results.get(itemIndex);
                
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    // Just informational (could expand via messages)
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
                    player.sendMessage(ChatColor.AQUA + "Information: " + clickedShopItem.getItemPath());
                } else {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                    if (plugin.getConfigManager().isOpenBuyMenu()) {
                        // Directly open EconomyShopGUI's transaction screen utilizing their internal manager
                        EconomyShopGUI.getInstance().getTransactionManager().openTransactionScreen(player, clickedShopItem);
                    }
                }
            }
        }
    }
}
