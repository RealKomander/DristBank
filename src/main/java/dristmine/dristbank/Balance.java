package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Balance implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public Balance(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
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

        double balance = configManager.getConfig().getDouble("player-info." + player.getUniqueId(), 0);
        String balanceMessage = messageManager.getMessage("balance-message", balance);
        player.sendMessage(balanceMessage);
        return true;
    }
}
