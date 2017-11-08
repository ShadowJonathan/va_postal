package com.vodhanel.minecraft.va_postal.mail;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.Util;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;


public class MailGen {
    VA_postal plugin;

    public MailGen(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized String stack2serial(ItemStack stack) {
        String name = stack.getType().name();
        String qty = Util.int2str(stack.getAmount());
        String id = Util.int2str(stack.getTypeId());
        String durability = Util.int2str(stack.getDurability());
        return name + "," + qty + "," + id + "," + durability;
    }

    public static synchronized void replace_slot_by_index_cen(int index, final ItemStack book_item) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {

            public void run() {
                VA_postal.central_po_inventory.setItem(index, book_item);
            }
        }, 6L);


    }

    public static synchronized String proper(String string) {
        try {
            if (string.length() > 0) {
                return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase().trim();
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public static synchronized String fixed_len(String input, int len) {
        try {
            input = input.trim();

            if (input.length() >= len) {
                return input.substring(0, len);
            }

            while (input.length() < len) {
                input = input + " ";
            }
            return input;
        } catch (Exception e) {
            String blank = "";
            for (int i = 0; i < len; i++) {
                blank = blank + " ";
            }
            return blank;
        }
    }

    public static synchronized String ifixed_len(int number, int len) {
        try {
            String input = Integer.toString(number);

            if (input.length() >= len) {
                return input.substring(0, len);
            }

            while (input.length() < len) {
                input = "0" + input;
            }
            return input;
        } catch (Exception e) {
            String blank = "";
            for (int i = 0; i < len; i++) {
                blank = blank + " ";
            }
            return blank;
        }
    }
}
