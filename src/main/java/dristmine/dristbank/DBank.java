package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DBank implements CommandExecutor {
	private final DristBank plugin;
	private final ConfigManager configManager;
	private final MessageManager messageManager;

	private final String SUBTRACT_ARG = "subtract";
	private final String TRANSFER_ARG = "transfer";

	public DBank(DristBank plugin, ConfigManager configManager, MessageManager messageManager) {
		this.plugin = plugin;
		this.configManager = configManager;
		this.messageManager = messageManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (args[0]) {
			case SUBTRACT_ARG:
				return subtract(plugin.getServer().getPlayer(args[1]), Integer.parseInt(args[2]));
			case TRANSFER_ARG:
				return transfer(plugin.getServer().getPlayer(args[1]), plugin.getServer().getPlayer(args[2]), Integer.parseInt(args[3]));
			default:
				sender.sendMessage(messageManager.getMessage("dbank-usage"));
				return true;
		}
	}

	private boolean subtract(Player target, int amount) {
		double balance = configManager.getConfig().getDouble("player-info." + target.getUniqueId(), 0);

		configManager.getConfig().set("player-info." + target.getUniqueId(), balance - amount);

		return true;
	}
	private boolean transfer(Player source, Player target, int amount) {
		double sourceBalance = configManager.getConfig().getDouble("player-info." + source.getUniqueId(), 0);
		double targetBalance = configManager.getConfig().getDouble("player-info." + target.getUniqueId(), 0);

		configManager.getConfig().set("player-info." + source.getUniqueId(), sourceBalance - amount);
		configManager.getConfig().set("player-info." + target.getUniqueId(), targetBalance + amount);

		return true;
	}
}
