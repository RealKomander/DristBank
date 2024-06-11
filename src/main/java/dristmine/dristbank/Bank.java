package dristmine.dristbank;

import dristmine.dristalerts.DristAlerts;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

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
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }
        Player player = (Player) sender;

        DristAlerts.sendPrivateMessage(messageManager.getMessage("bank-welcome"), "dristbank.access", Sound.ENTITY_PLAYER_LEVELUP, player);
        DristAlerts.sendPrivateMessage(messageManager.getMessage("bank-commands"), "dristbank.access", null, player);
        DristAlerts.sendPrivateMessage(messageManager.getMessage("balance-command"), "dristbank.access", null, player);
        DristAlerts.sendPrivateMessage(messageManager.getMessage("put-command"), "dristbank.access", null, player);
        DristAlerts.sendPrivateMessage(messageManager.getMessage("take-command"), "dristbank.access", null, player);
        DristAlerts.sendPrivateMessage(messageManager.getMessage("pay-command"), "dristbank.access", null, player);
        DristAlerts.sendPrivateMessage(messageManager.getMessage("cheque-command"), "dristbank.access", null, player);

        if (player.hasPermission("dristbank.admin")) {
            DristAlerts.sendPrivateMessage(messageManager.getMessage("admin-commands"), "dristbank.admin", null, player);
            DristAlerts.sendPrivateMessage(messageManager.getMessage("interest-command"), "dristbank.admin", null, player);
            DristAlerts.sendPrivateMessage(messageManager.getMessage("reserves-command"), "dristbank.admin", null, player);
            DristAlerts.sendPrivateMessage(messageManager.getMessage("payinterest-command"), "dristbank.admin", null, player);
        }

        double monthlyInterestRate = configManager.getConfig().getDouble("monthly_interest_rate", 0);
        DristAlerts.sendPrivateMessage(messageManager.getMessage("current-interest-rate", monthlyInterestRate), "dristbank.access", null, player);
        return true;
    }
}
