package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Pay implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public Pay(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
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

        double balance = configManager.getConfig().getDouble("player-info." + player.getUniqueId(), 0);
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            args[1] = args[1].toLowerCase();

            if (!args[1].equals(Utils.MAX_AMOUNT_COMMAND)) {
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

        configManager.getConfig().set("player-info." + player.getUniqueId(), balance - amount);
        configManager.getConfig().set("player-info." + target.getUniqueId(), configManager.getConfig().getDouble("player-info." + target.getUniqueId(), 0) + amount);
        configManager.saveConfig();

        String transferMessage = messageManager.getMessage("transfer-message", playerName, amount, target.getName());
        player.sendMessage(transferMessage);
        String receivedMessage = messageManager.getMessage("received-message", playerName, amount);
        target.sendMessage(receivedMessage);

        return true;
    }
}
