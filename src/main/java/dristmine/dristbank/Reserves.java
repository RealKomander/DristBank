package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reserves implements CommandExecutor {
    private final DristBank plugin;
    private final StorageManager storageManager;
    private final MessageManager messageManager;

    public Reserves(DristBank plugin, StorageManager storageManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.storageManager = storageManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("dristbank.admin")) {
            sender.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length != 0) {
            sender.sendMessage(messageManager.getMessage("reserves-usage"));
            return true;
        }

        int totalDebris = (int) storageManager.getTotalDebris();
        sender.sendMessage(messageManager.getMessage("reserves-success", totalDebris));
        return true;
    }
}
