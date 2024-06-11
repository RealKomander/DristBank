package dristmine.dristbank;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull; // Import the @NotNull annotation

import static dristmine.dristbank.Utils.itemGive;

public class Take implements CommandExecutor {
    private final MessageManager messageManager;
    private final StorageManager storageManager;

    public Take(DristBank plugin, MessageManager messageManager, StorageManager storageManager) {
        this.messageManager = messageManager;
        this.storageManager = storageManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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

        double balance = storageManager.getBalance(player.getUniqueId().toString());
        double amount;
        try {
            if (args[0].equalsIgnoreCase(Utils.MAX_AMOUNT_ARG)) {
                amount = (int) balance;
            } else {
                amount = Double.parseDouble(args[0]);
            }
        } catch (NumberFormatException e) {
            player.sendMessage(messageManager.getMessage("invalid-amount"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(messageManager.getMessage("positive-amount"));
            return true;
        }

        if (!player.hasPermission("dristbank.admin") && balance < amount) {
            player.sendMessage(messageManager.getMessage("insufficient-balance", balance));
            return true;
        }

        int totalDebris = (int) storageManager.getTotalDebris();

        if (totalDebris < amount) {
            player.sendMessage(messageManager.getMessage("insufficient-system-debris"));
            return true;
        }

        itemGive(player, (int) amount, Material.ANCIENT_DEBRIS);

        storageManager.updateBalance(player.getUniqueId().toString(), balance - amount);
        storageManager.removeDebris(amount);

        player.sendMessage(messageManager.getMessage("take-success", amount));
        return true;
    }
}