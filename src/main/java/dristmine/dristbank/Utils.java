package dristmine.dristbank;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {

    public static void itemGive(Player player, int amount, Material material) {
        // Drop items in groups of 64 until the remaining amount is less than 64
        while (amount > 0) {
            int stackSize = Math.min(amount, 64); // Calculate the size of this stack
            ItemStack itemStack = new ItemStack(material, stackSize); // Create ItemStack
            Item itemEntity = player.getWorld().dropItem(player.getLocation(), itemStack); // Drop item entity
            itemEntity.setPickupDelay(0); // Set pickup delay
            amount -= stackSize; // Subtract dropped items from the total amount
        }
    }
}
