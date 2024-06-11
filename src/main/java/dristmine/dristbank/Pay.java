package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Pay implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final StorageManager storageManager;

    public Pay(DristBank plugin, ConfigManager configManager, MessageManager messageManager, StorageManager storageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.storageManager = storageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;
        String playerName = player.getName();

        if (!player.hasPermission("dristbank.use")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(messageManager.getMessage("pay-usage"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(messageManager.getMessage("player-not-found"));
            return true;
        }

        if (player.equals(target)) {
            player.sendMessage(messageManager.getMessage("transfer-to-self"));
            return true;
        }

        double balance = storageManager.getBalance(player.getUniqueId().toString());
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            if (!args[1].equalsIgnoreCase(Utils.MAX_AMOUNT_ARG)) {
                player.sendMessage(messageManager.getMessage("invalid-amount"));
                return true;
            }

            amount = balance;
        }

        if (amount <= 0) {
            player.sendMessage(messageManager.getMessage("positive-amount"));
            return true;
        }

        if (!player.hasPermission("dristbank.admin") && balance < amount) {
            player.sendMessage(messageManager.getMessage("insufficient-balance", balance));
            return true;
        }

        storageManager.updateBalance(player.getUniqueId().toString(), balance - amount);
        storageManager.updateBalance(target.getUniqueId().toString(), storageManager.getBalance(target.getUniqueId().toString()) + amount);

        String transferMessage = messageManager.getMessage("transfer-message", playerName, amount, target.getName());
        player.sendMessage(transferMessage);
        String receivedMessage = messageManager.getMessage("received-message", playerName, amount);
        target.sendMessage(receivedMessage);

        return true;
    }
}
