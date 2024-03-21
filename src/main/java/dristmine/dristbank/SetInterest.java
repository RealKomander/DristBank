package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetInterest implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public SetInterest(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
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

        if (args.length != 1) {
            sender.sendMessage(messageManager.getMessage("set-interest-usage"));
            return true;
        }

        double interestRate;
        try {
            interestRate = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(messageManager.getMessage("invalid-percentage"));
            return true;
        }

        if (interestRate < 0) {
            sender.sendMessage(messageManager.getMessage("negative-interest"));
            return true;
        }

        configManager.getConfig().set("monthly_interest_rate", interestRate);
        configManager.saveConfig();
        sender.sendMessage(messageManager.getMessage("set-interest-success", interestRate));

        return true;
    }
}
