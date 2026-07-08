package com.yourname.economyshopsearch.command;

import com.yourname.economyshopsearch.EconomyShopSearch;
import com.yourname.economyshopsearch.gui.SearchGUI;
import me.gypopo.economyshopgui.objects.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchCommand implements CommandExecutor {

    private final EconomyShopSearch plugin;

    public SearchCommand(EconomyShopSearch plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("economyshopsearch.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use the shop search.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /search <item>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && player.hasPermission("economyshopsearch.admin")) {
            plugin.getConfigManager().load();
            plugin.getIndexManager().rebuildIndex();
            player.sendMessage(ChatColor.GREEN + "EconomyShopSearch reloaded and index rebuilt.");
            return true;
        }

        String query = String.join(" ", args);
        if (query.length() < plugin.getConfigManager().getSearchMinLength()) {
            player.sendMessage(ChatColor.RED + "Search query must be at least " + plugin.getConfigManager().getSearchMinLength() + " characters.");
            return true;
        }

        List<ShopItem> results = plugin.getIndexManager().search(query);
        if (results.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "No items found matching '" + query + "'.");
            return true;
        }

        new SearchGUI(plugin, player, results, query).open(0);
        return true;
    }
}
