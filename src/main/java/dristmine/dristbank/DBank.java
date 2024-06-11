package dristmine.dristbank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.helpers.Util;

public class DBank implements CommandExecutor {
	private final DristBank plugin;
	private final MessageManager messageManager;
	private final StorageManager storageManager;

	public DBank(DristBank plugin, StorageManager storageManager, MessageManager messageManager) {
		this.plugin = plugin;
		this.messageManager = messageManager;
		this.storageManager = storageManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final String SUBTRACT_ARG = "subtract";
		final String TRANSFER_ARG = "transfer";

		if (!sender.hasPermission("dristbank.admin")) {
			sender.sendMessage(messageManager.getMessage("no-permission"));
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(messageManager.getMessage("dbank-usage"));
			return true;
		}

		Player target;
		Player source;

		double amount;

		switch (args[0]) {
		case SUBTRACT_ARG:
			if (args.length != 3) {
				sender.sendMessage(messageManager.getMessage("dbank-subtract-usage"));
				return true;
			}

			target = plugin.getServer().getPlayer(args[1]);

			if (target == null || !target.isOnline()) {
				sender.sendMessage(messageManager.getMessage("player-not-found"));
				return true;
			}

			try {
				amount = Double.parseDouble(args[2]);
			} catch (NumberFormatException e) {
				if (!args[2].equalsIgnoreCase(Utils.MAX_AMOUNT_ARG)) {
					sender.sendMessage(messageManager.getMessage("invalid-amount"));
					return true;
				}

				amount = storageManager.getBalance(target.getUniqueId().toString());
			}

			if (amount <= 0) {
				sender.sendMessage(messageManager.getMessage("positive-amount"));
				return true;
			}

			return subtract(target, amount);

		case TRANSFER_ARG:
			if (args.length != 4) {
				sender.sendMessage(messageManager.getMessage("dbank-transfer-usage"));
				return true;
			}

			source = plugin.getServer().getPlayer(args[1]);
			target = plugin.getServer().getPlayer(args[2]);

			if (source == null || target == null || !source.isOnline() || !target.isOnline()) {
				sender.sendMessage(messageManager.getMessage("player-not-found"));
				return true;
			}

			try {
				amount = Double.parseDouble(args[3]);
			} catch (NumberFormatException e) {
				if (!args[3].equalsIgnoreCase(Utils.MAX_AMOUNT_ARG)) {
					sender.sendMessage(messageManager.getMessage("invalid-amount"));
					return true;
				}

				amount = storageManager.getBalance(source.getUniqueId().toString());
			}

			if (amount <= 0) {
				sender.sendMessage(messageManager.getMessage("positive-amount"));
				return true;
			}

			return transfer(source, target, amount);

		default:
			sender.sendMessage(messageManager.getMessage("dbank-usage"));
			return true;
		}
	}

	private boolean subtract(Player target, double amount) {
		double balance = storageManager.getBalance(target.getUniqueId().toString());

		storageManager.updateBalance(target.getUniqueId().toString(), balance - amount);

		return true;
	}

	private boolean transfer(Player source, Player target, double amount) {
		double sourceBalance = storageManager.getBalance(source.getUniqueId().toString());
		double targetBalance = storageManager.getBalance(target.getUniqueId().toString());

		storageManager.updateBalance(source.getUniqueId().toString(), sourceBalance - amount);
		storageManager.updateBalance(target.getUniqueId().toString(), targetBalance + amount);

		return true;
	}
}
