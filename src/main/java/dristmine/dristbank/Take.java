package dristmine.dristbank;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dristmine.dristbank.Utils.itemGive;

public class Take implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public Take(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
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

        if (args.length != 1) {
            player.sendMessage(messageManager.getMessage("take-usage"));
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

        double balance = configManager.getConfig().getDouble("player-info." + player.getUniqueId(), 0);

        if (!player.hasPermission("dristbank.admin") && balance < amount) {
            player.sendMessage(messageManager.getMessage("insufficient-balance", balance));
            return true;
        }

        int totalDebris = configManager.getConfig().getInt("total_debris", 0);

        if (totalDebris < amount) {
            player.sendMessage(messageManager.getMessage("insufficient-system-debris"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(messageManager.getMessage("invalid-amount"));
            return true;
        }

        itemGive(player, (int) amount, Material.ANCIENT_DEBRIS);

        configManager.getConfig().set("player-info." + player.getUniqueId(), balance - amount);
        configManager.getConfig().set("total_debris", totalDebris - amount);
        configManager.saveConfig();

        player.sendMessage(messageManager.getMessage("take-success", amount));
        return true;
    }
}
