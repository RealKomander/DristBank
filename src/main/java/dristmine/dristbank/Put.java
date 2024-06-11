package dristmine.dristbank;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Put implements CommandExecutor {
    private final DristBank plugin;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final StorageManager storageManager;

    public Put(DristBank plugin, ConfigManager configManager, MessageManager messageManager, StorageManager storageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.storageManager = storageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageManager.getMessage("only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("dristbank.use")) {
            player.sendMessage(messageManager.getMessage("no-permission"));
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(messageManager.getMessage("put-usage"));
            return true;
        }

        int amount = 0;
        if (args.length == 0) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            amount = calculateAmount(itemInHand, player);

            if (amount == -1) {
                player.sendMessage(messageManager.getMessage("put-invalid-item"));
                return true;
            }

            // Remove ancient debris from player's hand
            itemInHand.setAmount(0);
            player.getInventory().setItemInMainHand(itemInHand);
        } else if (args[0].equalsIgnoreCase(Utils.MAX_AMOUNT_ARG)) {
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null) {
                    continue;
                }

                int tempAmount = calculateAmount(itemStack, player);
                if (tempAmount != -1) {
                    amount += tempAmount;

                    itemStack.setAmount(0);
                }
            }
        } else {
            player.sendMessage(messageManager.getMessage("put-usage"));
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(messageManager.getMessage("positive-amount"));
            return true;
        }

        // Add amount to player's bank account
        double balance = storageManager.getBalance(player.getUniqueId().toString());
        storageManager.updateBalance(player.getUniqueId().toString(), balance + amount);

        // Update total physical debris stored in the system
        int totalDebris = (int) storageManager.getTotalDebris();
        storageManager.updateTotalDebris(totalDebris + amount);

        player.sendMessage(messageManager.getMessage("put-success", amount, balance + amount));

        return true;
    }

    private int calculateAmount(ItemStack itemStack, Player player) {
        int result;

        if (itemStack.getType() == Material.ANCIENT_DEBRIS) {
            result = itemStack.getAmount();
        } else if (itemStack.getType() == Material.NETHERITE_SCRAP) {
            result = itemStack.getAmount();
        } else if (itemStack.getType() == Material.NETHERITE_INGOT) {
            result = itemStack.getAmount() * 4;
            Utils.itemGive(player, result, Material.GOLD_INGOT);
        } else if (itemStack.getType() == Material.NETHERITE_BLOCK) {
            result = itemStack.getAmount() * 36;
            Utils.itemGive(player, result, Material.GOLD_INGOT);
        } else {
            return -1;
        }

        return result;
    }
}
