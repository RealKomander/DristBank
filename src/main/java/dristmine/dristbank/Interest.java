package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Interest implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;

    public Interest(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
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
            sender.sendMessage(messageManager.getMessage("interest-usage"));
            return true;
        }

        payInterest();
        sender.sendMessage(messageManager.getMessage("interest-paid"));
        return true;
    }

    public void payInterest() {
        double monthlyInterestRate = configManager.getConfig().getDouble("monthly_interest_rate", 0);
        if (monthlyInterestRate <= 0) {
            return;
        }

        double totalDebris = configManager.getConfig().getDouble("total_debris", 0);

        // Check if the configuration section exists
        if (configManager.getConfig().contains("player-info")) {
            // Retrieve keys only if the configuration section exists
            for (String playerUUID : configManager.getConfig().getConfigurationSection("player-info").getKeys(false)) {
                double balance = configManager.getConfig().getDouble("player-info." + playerUUID, 0);
                double interest = balance * monthlyInterestRate / 100.0;
                configManager.getConfig().set("player-info." + playerUUID, balance + interest);
            }
        }

        configManager.saveConfig();
    }
}
