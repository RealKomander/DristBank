package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Bank implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public Bank(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(messageManager.getMessage("bank-welcome"));
        sender.sendMessage(messageManager.getMessage("bank-commands"));
        sender.sendMessage(messageManager.getMessage("balance-command"));
        sender.sendMessage(messageManager.getMessage("put-command"));
        sender.sendMessage(messageManager.getMessage("take-command"));
        sender.sendMessage(messageManager.getMessage("pay-command"));
        sender.sendMessage(messageManager.getMessage("cheque-command"));

        if (sender.hasPermission("dristbank.admin")) {
            sender.sendMessage(messageManager.getMessage("admin-commands"));
            sender.sendMessage(messageManager.getMessage("interest-command"));
            sender.sendMessage(messageManager.getMessage("reserves-command"));
            sender.sendMessage(messageManager.getMessage("payinterest-command"));
        }

        double monthlyInterestRate = configManager.getConfig().getDouble("monthly_interest_rate", 0);
        sender.sendMessage(messageManager.getMessage("current-interest-rate", monthlyInterestRate));
        return true;
    }
}
