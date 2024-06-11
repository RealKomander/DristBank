package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class Interest implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final StorageManager storageManager;

    public Interest(DristBank plugin, ConfigManager configManager, MessageManager messageManager, StorageManager storageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.storageManager = storageManager;
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

        double totalDebris = storageManager.getTotalDebris();
        if (totalDebris <= 0) {
            return;
        }

        Map<String, Double> balances = storageManager.getAllBalances();
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            String playerUUID = entry.getKey();
            double balance = entry.getValue();
            double interest = balance * monthlyInterestRate / 100.0;
            storageManager.updateBalance(playerUUID, balance + interest);
        }
    }
}
