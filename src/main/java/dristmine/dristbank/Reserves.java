package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reserves implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public Reserves(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
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

        int totalDebris = configManager.getConfig().getInt("total_debris", 0);
        sender.sendMessage(messageManager.getMessage("reserves-success", totalDebris));
        return true;
    }
}
