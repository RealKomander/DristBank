package dristmine.dristbank;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static dristmine.dristbank.Utils.itemGive;

public class Put implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public Put(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
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

        if (!player.hasPermission("dristbank.use")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 0) {
            player.sendMessage(messageManager.getMessage("put-usage"));
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        int amount;
        if (itemInHand.getType() == Material.ANCIENT_DEBRIS) {
            amount = itemInHand.getAmount();
        } else if (itemInHand.getType() == Material.NETHERITE_SCRAP) {
            amount = itemInHand.getAmount();
        } else if (itemInHand.getType() == Material.NETHERITE_INGOT) {
            amount = itemInHand.getAmount() * 4;
            itemGive(player, amount, Material.GOLD_INGOT);
        } else if (itemInHand.getType() == Material.NETHERITE_BLOCK) {
            amount = itemInHand.getAmount() * 36;
            itemGive(player, amount, Material.GOLD_INGOT);
        } else {
            player.sendMessage(messageManager.getMessage("put-invalid-item"));
            return true;
        }

        // Add amount to player's bank account
        double balance = configManager.getConfig().getDouble("player-info." + player.getUniqueId(), 0);
        configManager.getConfig().set("player-info." + player.getUniqueId(), balance + amount);
        configManager.saveConfig();

        // Remove ancient debris from player's hand
        itemInHand.setAmount(0);
        player.getInventory().setItemInMainHand(itemInHand);

        // Update total physical debris stored in the system
        int totalDebris = configManager.getConfig().getInt("total_debris", 0);
        configManager.getConfig().set("total_debris", totalDebris + amount);
        configManager.saveConfig();

        player.sendMessage(messageManager.getMessage("put-success", amount, balance + amount));

        return true;
    }
}
