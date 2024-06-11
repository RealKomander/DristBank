package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDate;

public class DristBank extends JavaPlugin implements Listener {
    private ConfigManager configManager;
    private MessageManager messageManager;
    private StorageManager storageManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.setupConfig();
        messageManager = new MessageManager(this);

        storageManager = new StorageManager(this);
        storageManager.migrateDataFromConfig(configManager);

        // Register commands
        getCommand("put").setExecutor(new Put(this, configManager, messageManager, storageManager));
        getCommand("take").setExecutor(new Take(this, messageManager, storageManager));
        getCommand("pay").setExecutor(new Pay(this, configManager, messageManager, storageManager));
        getCommand("balance").setExecutor(new Balance(this, storageManager, messageManager));
        getCommand("interest").setExecutor(new SetInterest(this, configManager, messageManager));
        getCommand("reserves").setExecutor(new Reserves(this, storageManager, messageManager));
        getCommand("payinterest").setExecutor(new Interest(this, configManager, messageManager, storageManager));
        getCommand("bank").setExecutor(new Bank(this, configManager, messageManager));
        getCommand("cheque").setExecutor(new Cheque(this, storageManager, messageManager));
        getCommand("dbank").setExecutor(new DBank(this, storageManager, messageManager));

        // Register listener
        getServer().getPluginManager().registerEvents(new ChequeListener(this, storageManager, messageManager), this);

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
            Interest interestCommand = new Interest(this, configManager, messageManager, storageManager);
            return interestCommand.onCommand(sender, command, label, args);
        }
        return false;
    }

    private void checkAndPayInterest() {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int lastPaidMonth = configManager.getConfig().getInt("last_month_paid_interest", 0);

        if (currentMonth != lastPaidMonth) {
            Interest interestHandler = new Interest(this, configManager, messageManager, storageManager);
            interestHandler.payInterest();

            // Update last month paid interest in config
            configManager.getConfig().set("last_month_paid_interest", currentMonth);
            configManager.saveConfig();
        }
    }
}
