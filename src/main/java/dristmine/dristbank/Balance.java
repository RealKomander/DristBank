package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Balance implements CommandExecutor {
    private final DristBank plugin;
    private final StorageManager storageManager;
    private final MessageManager messageManager;

    public Balance(DristBank plugin, StorageManager storageManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.storageManager = storageManager;
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

        String playerUUID = player.getUniqueId().toString();
        double balance = storageManager.getBalance(playerUUID);
        String balanceMessage = messageManager.getMessage("balance-message", String.format("%.2f", balance));
        player.sendMessage(balanceMessage);
        return true;
    }
}
