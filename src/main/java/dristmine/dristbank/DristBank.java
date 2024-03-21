package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;

public class DristBank extends JavaPlugin implements Listener {
    private ConfigManager configManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.setupConfig();
        messageManager = new MessageManager(this);

        // Register commands
        getCommand("put").setExecutor(new Put(this, configManager, messageManager));
        getCommand("take").setExecutor(new Take(this, configManager, messageManager));
        getCommand("pay").setExecutor(new Pay(this, configManager, messageManager));
        getCommand("balance").setExecutor(new Balance(this, configManager, messageManager));
        getCommand("interest").setExecutor(new SetInterest(this, configManager, messageManager));
        getCommand("reserves").setExecutor(new Reserves(this, configManager, messageManager));
        getCommand("payinterest").setExecutor(new Interest(this, configManager, messageManager));
        getCommand("bank").setExecutor(new Bank(this, configManager, messageManager));
        getCommand("cheque").setExecutor(new Cheque(this, configManager, messageManager));

        // Register listener
        getServer().getPluginManager().registerEvents(new ChequeListener(this, configManager, messageManager), this);

        // Check and pay interest if new month
        checkAndPayInterest();
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this::checkAndPayInterest, 0L, 20L * 60 * 60 * 24);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bank")) {
            Bank bankCommand = new Bank(this, configManager, messageManager);
            return bankCommand.onCommand(sender, command, label, args);
        } else if (command.getName().equalsIgnoreCase("payinterest")) {
            Interest interestCommand = new Interest(this, configManager, messageManager);
            return interestCommand.onCommand(sender, command, label, args);
        }
        return false;
    }

    private void checkAndPayInterest() {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int lastPaidMonth = configManager.getConfig().getInt("last_month_paid_interest", 0);

        if (currentMonth != lastPaidMonth) {
            Interest interestHandler = new Interest(this, configManager, messageManager);
            interestHandler.payInterest();

            // Update last month paid interest in config
            configManager.getConfig().set("last_month_paid_interest", currentMonth);
            configManager.saveConfig();
        }
    }
}
