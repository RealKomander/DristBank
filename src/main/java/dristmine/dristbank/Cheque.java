package dristmine.dristbank;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Cheque implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public Cheque(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        if (args.length != 1) {
            player.sendMessage(messageManager.getMessage("cheque-usage"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(messageManager.getMessage("invalid-amount"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(messageManager.getMessage("positive-amount"));
            return true;
        }

        if (!player.hasPermission("dristbank.admin")) {
            double balance = configManager.getConfig().getDouble("player-info." + player.getUniqueId(), 0);
            if (balance < amount) {
                player.sendMessage(messageManager.getMessage("insufficient-balance", balance));
                return true;
            }
            // Subtract cheque amount from player's balance
            configManager.getConfig().set("player-info." + player.getUniqueId(), balance - amount);
            configManager.saveConfig();
        } else if (player.hasPermission("dristbank.admin")) {
            double balance = configManager.getConfig().getDouble("player-info." + player.getUniqueId(), 0);
            configManager.getConfig().set("player-info." + player.getUniqueId(), balance - amount);
            configManager.saveConfig();
        }

        ItemStack cheque = createCheque(amount);
        Inventory playerInventory = player.getInventory();

        if (playerInventory.firstEmpty() == -1) {
            player.sendMessage(messageManager.getMessage("no-inventory-space"));
            dropItems(player, cheque);
        } else {
            playerInventory.addItem(cheque);
            player.sendMessage(messageManager.getMessage("cheque-given", amount));
        }

        return true;
    }

    private ItemStack createCheque(double amount) {
        ItemStack cheque = new ItemStack(Material.PAPER);
        ItemMeta meta = cheque.getItemMeta();
        if (meta != null) {
            String chequeName = ChatColor.translateAlternateColorCodes('&', messageManager.getMessage("cheque-name"));
            meta.setDisplayName(chequeName);

            List<String> lore = new ArrayList<>();
            String loreLine1 = messageManager.getMessage("cheque-lore-line1", amount);
            String loreLine2 = messageManager.getMessage("cheque-lore-line2");
            lore.add(loreLine1);
            lore.add(loreLine2);
            meta.setLore(lore);

            // Attach custom metadata
            NamespacedKey key = new NamespacedKey(plugin, "cheque-value");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.DOUBLE, amount);

            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

            cheque.setItemMeta(meta);
        }
        return cheque;
    }

    private void dropItems(Player player, ItemStack itemStack) {
        Item item = player.getWorld().dropItem(player.getLocation(), itemStack);
    }
}
