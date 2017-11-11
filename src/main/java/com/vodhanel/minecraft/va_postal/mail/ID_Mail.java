package com.vodhanel.minecraft.va_postal.mail;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.config.C_Address;
import com.vodhanel.minecraft.va_postal.config.C_Dispatcher;
import com.vodhanel.minecraft.va_postal.config.C_Owner;
import com.vodhanel.minecraft.va_postal.config.C_Postoffice;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class ID_Mail {
    VA_postal plugin;

    public ID_Mail(VA_postal instance) {
        plugin = instance;
    }


    public static synchronized void place_new_mail_marker(int id) {
        Location location = Util.str2location(VA_postal.wtr_schest_location[id]);
        World w = location.getWorld();
        Block block = w.getBlockAt(location);
        if (block == null) {
            return;
        }
        if (!SignManip.exists_sign_id_chest(block)) {
            create_addr_sign(id);
        }
        String stitle = "§c[Postal_Mail]";

        SignManip.edit_sign_id_chest(block, stitle, null, null, null);
    }

    public static synchronized void route_housekeeping(int id) {
        final String stown = VA_postal.wtr_poffice[id];
        final String saddr = VA_postal.wtr_address[id];
        if (!C_Dispatcher.is_address_on_firstpass(stown, saddr)) {
            return;
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {
            public void run() {
                ListIterator<ItemStack> item_itr = VA_postal.wtr_inventory_address[id].iterator(1);
                if (item_itr != null) {
                    int index = 0;
                    while (item_itr.hasNext()) {
                        ItemStack ind_item = item_itr.next();

                        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                            Book book = new Book(ind_item);

                            if (!book.is_valid()) {
                                item_itr.set(null);
                            }

                            String mail_title = book.getTitle().toLowerCase().trim();
                            if (mail_title.contains("postal log")) {
                                item_itr.set(null);
                            }
                            if (mail_title.contains("postal archive")) {
                                item_itr.set(null);
                            }
                            if (mail_title.contains("current resident")) {
                                item_itr.set(null);
                            }
                        } else {
                            item_itr.set(null);
                        }

                        index++;
                    }

                    C_Dispatcher.set_firstpass_done(stown, saddr);
                }
            }
        }, 50L);


    }

    public static synchronized boolean add_to_residence_chest(int id, ItemStack book_item) {
        if (book_item == null) {
            return false;
        }
        String spostoffice = VA_postal.wtr_poffice[id];
        String saddress = VA_postal.wtr_address[id];

        if (VA_postal.wtr_inventory_address[id].firstEmpty() != -1) {
            VA_postal.wtr_inventory_address[id].addItem(book_item);
            return true;
        }

        ListIterator<ItemStack> item_itr = VA_postal.wtr_inventory_address[id].iterator(0);
        if (item_itr != null) {
            int index = 0;
            while (item_itr.hasNext()) {
                ItemStack ind_item = item_itr.next();
                if ((ind_item == null) || (ind_item.getType() == Material.AIR)) {
                    VA_postal.wtr_inventory_address[id].setItem(index, book_item);
                    return true;
                }
                index++;
            }
        }

        item_itr = VA_postal.wtr_inventory_address[id].iterator(0);
        if (item_itr != null) {
            int index = 0;
            while (item_itr.hasNext()) {
                ItemStack ind_item = item_itr.next();
                if ((ind_item != null) && (ind_item.getType() != Material.WRITTEN_BOOK)) {
                    VA_postal.wtr_inventory_address[id].setItem(index, book_item);
                    return true;
                }
                index++;
            }
        }

        item_itr = VA_postal.wtr_inventory_address[id].iterator(0);
        long age_save = 0L;
        int index_save = -1;
        if (item_itr != null) {
            int index = 0;
            while (item_itr.hasNext()) {
                ItemStack ind_item = item_itr.next();
                if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                    Book book = new Book(ind_item);
                    if (book.is_valid()) {
                        String mail_title = book.getTitle().toLowerCase().trim();
                        if (mail_title.contains("[distribution]")) {
                            long dist_age = Util.str2long(book.getAuthor().trim());
                            if (age_save == 0L) {
                                age_save = dist_age;
                                index_save = index;
                            } else if (dist_age < age_save) {
                                age_save = dist_age;
                                index_save = index;
                            }
                        }

                    }
                }
                index++;
            }
        }
        if (index_save >= 0) {
            VA_postal.wtr_inventory_address[id].setItem(index_save, book_item);
            return true;
        }

        Util.dinform(AnsiColor.RED + "[" + spostoffice + ", " + saddress + "] Full chest at: " + VA_postal.wtr_schest_location[id]);
        return false;
    }

    public static synchronized boolean add_to_postoffice_chest(int id, final ItemStack book_item) {
        if (book_item == null) {
            return false;
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {

            public void run() {
                VA_postal.wtr_inventory_postoffice[id].addItem(book_item);
            }
        }, 10L);


        return true;
    }

    public static synchronized boolean replace_slot_by_index_addr(int id, final int index, final ItemStack book_item) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {

            public void run() {
                VA_postal.wtr_inventory_address[id].setItem(index, book_item);
            }
        }, 2L);


        return true;
    }

    public static synchronized boolean replace_slot_by_index_po(int id, final int index, final ItemStack book_item) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {

            public void run() {
                VA_postal.wtr_inventory_postoffice[id].setItem(index, book_item);
            }
        }, 4L);


        return true;
    }

    public static synchronized boolean set_postoffice_chest_inv(int id) {
        String spostoffice = VA_postal.wtr_poffice[id];
        VA_postal.wtr_slocation_local_po[id] = Util.put_point_on_ground(C_Postoffice.get_local_po_location_by_name(spostoffice), false);
        if (VA_postal.cstalk) {
            Util.cinform(AnsiColor.CYAN + "Searching for Local post office chest: " + spostoffice + "....");
            Util.cinform(AnsiColor.CYAN + "  Starting at: " + VA_postal.wtr_slocation_local_po[id]);
        }
        SetPostoffice_Chest_nTP_point(id);
        if ("null".equals(VA_postal.wtr_schest_location_postoffice[id])) {
            VA_postal.wtr_inventory_postoffice[id] = null;
            Util.cinform(AnsiColor.RED + "Unable to locate post office chest: " + spostoffice);
            return false;
        }
        Util.dinform("\033[0;32m[" + spostoffice + "] chest location: " + VA_postal.wtr_schest_location_postoffice[id]);
        Location mailbox_loc = Util.str2location(VA_postal.wtr_schest_location_postoffice[id]);
        Chest chest = (Chest) mailbox_loc.getBlock().getState();
        if ((chest == null) || (!ChestManip.is_chest(chest.getType()))) {
            VA_postal.wtr_inventory_postoffice[id] = null;
            Util.cinform(AnsiColor.RED + "[set_postoffice_chest_inv] unable to set inventory");
            return false;
        }
        VA_postal.wtr_inventory_postoffice[id] = chest.getInventory();
        return true;
    }

    public static synchronized boolean set_address_chest_inv(int id) {
        String spostoffice = VA_postal.wtr_poffice[id];
        String saddress = VA_postal.wtr_address[id];
        VA_postal.wtr_slocation_address[id] = Util.put_point_on_ground(C_Address.get_address_location(spostoffice, saddress), false);
        if (VA_postal.cstalk) {
            Util.cinform(AnsiColor.CYAN + "Searching for mailbox: " + spostoffice + ", " + saddress + "......");
            Util.cinform(AnsiColor.CYAN + "  Starting at:  " + VA_postal.wtr_slocation_address[id]);
        }

        SetAddress_Chest_nTP_point(id);

        if ("null".equals(VA_postal.wtr_schest_location[id])) {
            VA_postal.wtr_inventory_address[id] = null;
            Util.cinform(AnsiColor.RED + "Unable to locate residence chest: " + spostoffice + ", " + saddress);
            return false;
        }
        Util.dinform("\033[0;32m[" + spostoffice + ", " + saddress + "] chest location: " + VA_postal.wtr_schest_location[id]);
        Location mailbox_loc = Util.str2location(VA_postal.wtr_schest_location[id]);
        Chest chest = (Chest) mailbox_loc.getBlock().getState();
        if ((chest == null) || (!ChestManip.is_chest(chest.getType()))) {
            VA_postal.wtr_inventory_address[id] = null;
            Util.cinform(AnsiColor.RED + "[set_address_chest_inv] unable to set inventory");
            return false;
        }
        VA_postal.wtr_inventory_address[id] = chest.getInventory();
        return true;
    }

    public static synchronized boolean chest_contains_postal_log(int id) {
        if (VA_postal.wtr_inventory_address[id] == null) {
            Util.dinform(AnsiColor.RED + "[chest_contains_postal_log] indicates bad residence chest");
            return false;
        }
        ItemStack ind_item = VA_postal.wtr_inventory_address[id].getItem(0);
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(ind_item);
            if (!book.is_valid()) {
                return false;
            }
            String mail_title = book.getTitle().toLowerCase().trim();
            if (mail_title.contains("postal log")) {
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean npc_create_and_install_postal_log(int id) {
        if (VA_postal.wtr_inventory_address[id] == null) {
            Util.dinform(AnsiColor.RED + "[npc_create_and_install_postal_log] indicates bad residence chest");
            return false;
        }

        ItemStack log_book = create_log(id);

        replace_slot_by_index_addr(id, 0, log_book);
        return true;
    }

    public static synchronized boolean po_chest_contains_postal_log(int id) {
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            Util.dinform(AnsiColor.RED + "[po_chest_contains_postal_log] indicates bad po chest");
            return false;
        }


        String stown = VA_postal.wtr_poffice[id].toLowerCase().trim();
        String slocation = VA_postal.wtr_schest_location_postoffice[id];
        ItemStack ind_item = VA_postal.wtr_inventory_postoffice[id].getItem(0);
        Player owner = VA_postal.SERVER;
        if (C_Owner.is_local_po_owner_defined(stown)) {
            owner = C_Owner.get_owner_local_po(stown);
        }
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(ind_item);
            if (book.is_valid()) {
                String mail_title = book.getTitle().toLowerCase().trim();
                String mail_author = book.getAuthor().toLowerCase().trim();
                if ((mail_title.contains("postal log")) &&
                        (mail_author.contains(stown))) {
                    String page1 = book.getPage(1);
                    String[] parts = page1.split("\n");
                    String slocation_comp = parts[0].trim() + "," + parts[1].trim();
                    if (slocation.equalsIgnoreCase(slocation_comp)) {
                        Player owner_comp = book.extractEmbeddedOwner();
                        if (owner == owner_comp) {
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

    public static synchronized boolean po_create_and_install_postal_log(int id) {
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            Util.dinform(AnsiColor.RED + "[po_create_and_install_postal_log] indicates bad po chest");
            return false;
        }

        ItemStack log_book = po_create_log(id);

        replace_slot_by_index_po(id, 0, log_book);
        return true;
    }

    public static synchronized ItemStack po_create_log(int id) {
        String title = "§c[Postal Log]";
        String author = Util.df(VA_postal.wtr_poffice[id]);

        String slocation = VA_postal.wtr_schest_location_postoffice[id];
        String[] parts = slocation.split(",");
        String sworld = parts[0];
        String slocation_mod = parts[1] + "," + parts[2] + "," + parts[3];
        String stown = VA_postal.wtr_poffice[id];
        String sowner = "Server";
        Player owner = VA_postal.SERVER;
        if (C_Owner.is_local_po_owner_defined(stown)) {
            owner = C_Owner.get_owner_local_po(stown);
            sowner = owner.getDisplayName();
        }
        String[] pages = new String[]{Book.makeFirstLogPage(sworld, slocation_mod, "Postal_Local", sowner, owner)};

        Book book = new Book(title, author, pages);

        return book.generateItemStack();
    }

    public static synchronized boolean npc_update_postal_log(int id) {
        if (VA_postal.wtr_inventory_address[id] == null) {
            Util.dinform(AnsiColor.RED + "[chest_contains_postal_log] indicates bad residence chest");
            return false;
        }


        ItemStack ind_item = VA_postal.wtr_inventory_address[id].getItem(0);
        Book log_book = null;
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            log_book = new Book(ind_item);
            if (!log_book.is_valid()) {
                return false;
            }
            String mail_title = log_book.getTitle().toLowerCase().trim();
            if (!mail_title.contains("postal log")) {
                return false;
            }
        }
        if (log_book == null) {
            return false;
        }

        String stown = VA_postal.wtr_poffice[id];
        String saddr = VA_postal.wtr_address[id];
        String slocation = VA_postal.wtr_schest_location[id];
        String sworld;
        String slocation_mod;
        try {
            String[] parts = slocation.split(",");
            sworld = parts[0];
            slocation_mod = parts[1] + "," + parts[2] + "," + parts[3];
        } catch (Exception e) {
            return false;
        }
        String sowner = "Server";
        Player owner = VA_postal.SERVER;
        if (C_Owner.is_address_owner_defined(stown, saddr)) {
            owner = C_Owner.get_owner_address(stown, saddr);
            sowner = owner.getDisplayName();
        }

        ArrayList<String> input_lines = new ArrayList<>(Arrays.asList(log_book.getPage(1).split("\n")));
        String[] final_lines = Book.makeFirstLogPage(sworld, slocation_mod, saddr, sowner, owner).split("\n");

        for (int i = 5; i < 14; i++) {
            if ((i - 1 < final_lines.length) && (input_lines.get(i - 1) != null)) {
                final_lines[i] = input_lines.get(i - 1).trim();
            } else {
                final_lines[i] = "";
            }
        }

        String title = "§c[Postal Log]";
        String author = Util.df(stown);
        Book new_book = new Book(title, author, new String[]{String.join("\n", final_lines)});
        ItemStack new_stack = new_book.generateItemStack();
        replace_slot_by_index_addr(id, 0, new_stack);
        return true;
    }

    public static synchronized ItemStack create_log(int id) {
        String title = "§c[Postal Log]";
        String author = Util.df(VA_postal.wtr_poffice[id]);

        String saddress = Util.df(VA_postal.wtr_address[id]);
        String slocation = VA_postal.wtr_schest_location_postoffice[id];
        String[] parts = slocation.split(",");
        String sworld = parts[0];
        String slocation_mod = parts[1] + "," + parts[2] + "," + parts[3];
        String stown = VA_postal.wtr_poffice[id];
        String sowner = "Server";
        Player owner = VA_postal.SERVER;
        if (C_Owner.is_local_po_owner_defined(stown)) {
            owner = C_Owner.get_owner_local_po(stown);
            sowner = owner.getDisplayName();
        }
        String[] pages = new String[1];
        pages[0] = Book.makeFirstLogPage(sworld, slocation_mod, saddress, sowner, owner);

        Book book = new Book(title, author, pages);

        return book.generateItemStack();
    }

    public static synchronized boolean chest_contains_outgoing_mail(int id) {
        Location mailbox_loc = Util.str2location(VA_postal.wtr_schest_location[id]);
        Chest chest;
        try {
            chest = (Chest) mailbox_loc.getBlock().getState();
        } catch (Exception e) {
            return false;
        }
        if ((chest == null) || (!ChestManip.is_chest(chest.getType()))) {
            Util.dinform(AnsiColor.RED + "[npc_pickup_mail] indicates bad chest data");
            return false;
        }
        Inventory chest_inv = chest.getInventory();
        for (ItemStack ind_item : chest_inv) {
            if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                Book book;
                try {
                    book = new Book(ind_item);
                } catch (Exception e) {
                    return false;
                }
                if (book.is_valid()) {
                    try {
                        String mail_address = book.getAuthor().toLowerCase().trim();
                        String this_address = VA_postal.wtr_address[id].toLowerCase().trim();
                        String[] spage = book.getPages();
                        if ((spage[0].contains("[not-processed]")) &&
                                (!mail_address.equals(this_address))) {
                            Util.dinform("Found outgoing mail");
                            return true;
                        }
                    } catch (Exception ignored) {
                    }

                }
            }
        }
        Util.dinform("No outgoing mail");
        return false;
    }

    public static synchronized void npc_pickup_mail(int id) {
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            Util.dinform(AnsiColor.RED + "[npc_pickup_mail] indicates bad post office chest");
            return;
        }
        if (VA_postal.wtr_inventory_address[id] == null) {
            Util.dinform(AnsiColor.RED + "[npc_pickup_mail] indicates bad residence chest");
            return;
        }

        boolean mail_found = false;
        boolean shipper_found = false;
        ListIterator<ItemStack> item_itr = VA_postal.wtr_inventory_address[id].iterator();
        while (item_itr.hasNext()) {
            ItemStack ind_item = item_itr.next();
            if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                Book book = new Book(ind_item);
                if (book.is_valid()) {
                    try {
                        String mail_address = book.getAuthor().toLowerCase().trim();
                        String mail_title = book.getTitle().toLowerCase().trim();

                        if (mail_title.contains("[distribution]")) {
                            long dist_age = Util.str2long(book.getAuthor().trim());
                            long now = System.currentTimeMillis() / 1000L;
                            if (dist_age < now) {
                                item_itr.set(null);
                                continue;
                            }
                        }

                        String this_address = VA_postal.wtr_address[id].toLowerCase().trim();
                        String page1 = book.getPages()[0].toLowerCase();
                        if (page1.contains("[not-processed]")) {
                            mail_found = true;
                            if (!mail_address.equals(this_address)) {
                                ItemStack stamped_mail = stamp_pickup(id, ind_item);
                                add_to_postoffice_chest(id, stamped_mail);

                                if (page1.contains("[shipping label]")) {
                                    shipper_found = true;

                                    ChestManip.parcel_remove_origen_chest(ind_item);
                                }
                                item_itr.set(null);
                            }
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        if ((mail_found) && (VA_postal.mailtalk == 2)) {
            Util.binform("&9&o" + Util.df(VA_postal.wtr_poffice[id]) + " &7&oPostMan picked up mail from &9&o" + Util.df(VA_postal.wtr_address[id]));
        }

        if (shipper_found) {
            ItemStack in_hand = new ItemStack(Material.CHEST);
            VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
            VA_postal.wtr_npc_player[id].setItemOnCursor(in_hand);
        } else if (mail_found) {
            ItemStack in_hand = new ItemStack(Material.BOOK);
            VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
            VA_postal.wtr_npc_player[id].setItemOnCursor(in_hand);
        } else {
            VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
            VA_postal.wtr_npc_player[id].setItemOnCursor(null);
        }
    }

    public static synchronized ItemStack stamp_pickup(int id, ItemStack ind_item) {
        Book book = new Book(ind_item);
        if (!book.is_valid()) {
            return null;
        }
        String title = book.getTitle();
        String author = book.getAuthor();
        String[] pages = book.getPages();
        if ((pages == null) || (pages.length < 1)) {
            return null;
        }

        String[] parts = pages[0].split("\n");
        if (parts.length < 8) {
            return null;
        }
        parts[6] = ("§5  " + Util.df(VA_postal.wtr_poffice[id]));
        parts[7] = ("§5  " + Util.df(VA_postal.wtr_address[id]));

        pages[0] = "";
        for (String part : parts) {
            pages[0] = (pages[0] + part + "\n");
        }

        Book stamped_book = new Book(title, author, pages);

        return stamped_book.generateItemStack();
    }

    public static synchronized void npc_start_route(int id) {
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            return;
        }
        boolean mail_found = false;
        boolean shipper_found = false;
        ListIterator<ItemStack> item_itr = VA_postal.wtr_inventory_postoffice[id].iterator();
        int index = 0;
        while (item_itr.hasNext()) {
            ItemStack ind_item = item_itr.next();
            if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                Book book = new Book(ind_item);
                if (!book.is_valid()) {
                    item_itr.set(null);
                } else {
                    String mail_address = book.getAuthor().toLowerCase().trim();
                    String this_address = VA_postal.wtr_address[id].toLowerCase().trim();
                    if (mail_address.equals(this_address)) {
                        mail_found = true;
                        String[] spage = book.getPages();
                        String page1 = spage[0].toLowerCase();
                        if (page1.contains("[not-processed]")) {
                            ItemStack stamped_mail = stamp_po_pickup(id, ind_item);
                            replace_slot_by_index_po(id, index, stamped_mail);

                            if (page1.contains("[shipping label]")) {
                                ChestManip.parcel_remove_origen_chest(ind_item);
                            } else {
                                mail_found = true;
                            }
                        }

                        if (page1.contains("[shipping label]")) {
                            shipper_found = true;
                        }
                    }
                }
            } else {
                index++;
            }
        }
        if ((mail_found) &&
                (VA_postal.mailtalk == 2)) {
            Util.cinform("&9&o" + Util.df(VA_postal.wtr_poffice[id]) + " &7&oPostMan picked up mail at the post office.");
        }


        if (shipper_found) {
            ItemStack in_hand = new ItemStack(Material.CHEST);
            VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
            VA_postal.wtr_npc_player[id].setItemOnCursor(in_hand);
        } else if (mail_found) {
            ItemStack in_hand = new ItemStack(Material.BOOK);
            VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
            VA_postal.wtr_npc_player[id].setItemOnCursor(in_hand);
        } else {
            VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
            VA_postal.wtr_npc_player[id].setItemOnCursor(null);
        }
    }

    public static synchronized ItemStack stamp_po_pickup(int id, ItemStack ind_item) {
        Book book = new Book(ind_item);
        if (!book.is_valid()) {
            return null;
        }
        String title = book.getTitle();
        String author = book.getAuthor();
        String[] pages = book.getPages();
        if ((pages == null) || (pages.length < 1)) {
            return null;
        }

        String[] parts = pages[0].split("\n");
        if (parts.length < 8) {
            return null;
        }
        parts[6] = ("§5  " + Util.df(VA_postal.wtr_poffice[id]));
        parts[7] = "§5  Post Office";

        pages[0] = "";
        for (String part : parts) {
            pages[0] = (pages[0] + part + "\n");
        }

        Book stamped_book = new Book(title, author, pages);

        return stamped_book.generateItemStack();
    }

    public static synchronized void npc_deliver_mail(int id) {
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            Util.dinform(AnsiColor.RED + "[npc_deliver_mail] indicates bad post office chest");
            return;
        }
        if (VA_postal.wtr_inventory_address[id] == null) {
            Util.dinform(AnsiColor.RED + "[npc_deliver_mail] indicates bad residence chest");
            return;
        }
        boolean mail_found = false;
        boolean mail_delivered = false;
        String stown = VA_postal.wtr_poffice[id];
        ListIterator<ItemStack> item_itr = VA_postal.wtr_inventory_postoffice[id].iterator();
        while (item_itr.hasNext()) {
            ItemStack ind_item = item_itr.next();
            if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                Book book = new Book(ind_item);
                if (!book.is_valid()) {
                    item_itr.set(null);
                } else {
                    String mail_address = book.getAuthor().toLowerCase().trim();
                    String this_address = VA_postal.wtr_address[id].toLowerCase().trim();
                    if (mail_address.equals(this_address)) {
                        ItemStack stamped_mail = stamp_deliver(id, ind_item);
                        if (add_to_residence_chest(id, stamped_mail)) {
                            item_itr.set(null);

                            VA_postal.wtr_npc_player[id] = ((Player) VA_postal.wtr_npc[id].getEntity());
                            VA_postal.wtr_npc_player[id].setItemOnCursor(null);
                            mail_delivered = true;
                            C_Address.set_address_newmail(stown, this_address, true);
                        }
                        mail_found = true;
                    }
                }
            }
        }

        if (mail_delivered) place_new_mail_marker(id);

        if (VA_postal.mailtalk == 2)
            if ((mail_found) && (mail_delivered)) {
                Util.binform("&9&o" + Util.df(VA_postal.wtr_poffice[id]) + " &7&oPostMan delivered mail to &9&o" + Util.df(VA_postal.wtr_address[id]));
            } else if (mail_found) {
                Util.binform("&9&o" + Util.df(VA_postal.wtr_poffice[id]) + " &7&oUndeliverable (full chest) &9&o" + Util.df(VA_postal.wtr_address[id]));
                Util.binform("&7&oMail is stored at the &9&o" + Util.df(VA_postal.wtr_poffice[id] + " post office."));
            }
    }

    public static synchronized ItemStack stamp_deliver(int id, ItemStack ind_item) {
        Book book = new Book(ind_item);
        if (!book.is_valid()) {
            return null;
        }
        String[] pages = book.getPages();
        if ((pages == null) || (pages.length < 1)) {
            return null;
        }
        String[] parts = pages[0].split("\n");
        if (parts.length < 10) {
            return null;
        }
        String title = "§6" + parts[4].substring(4).trim();
        String author = "§a" + parts[9].substring(4).trim();
        Player recipient = book.extractEmbeddedReceipient();

        if (VA_postal.mailtalk == 1) {
            if (recipient != null) {
                Util.spinform_if_online(recipient, "&9&o" + Util.df(VA_postal.wtr_poffice[id]) + " &7&oDelivered your mail to &9&o" + Util.df(VA_postal.wtr_address[id]));
            } else if (C_Owner.is_address_owner_defined(VA_postal.wtr_poffice[id], VA_postal.wtr_address[id])) {
                recipient = C_Owner.get_owner_address(VA_postal.wtr_poffice[id], VA_postal.wtr_address[id]);
                Util.spinform_if_online(recipient, "&9&o" + Util.df(VA_postal.wtr_poffice[id]) + " &7&oDelivered your mail to &9&o" + Util.df(VA_postal.wtr_address[id]));
            }
        }
        Book stamped_book = new Book(title, author, pages);
        return stamped_book.generateItemStack();
    }

    public static synchronized void postmaster_service_postoffice(int id, String spostoffice) {
        boolean result = false;
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            Util.dinform(AnsiColor.RED + "[postmaster_service_postoffice] indicates bad post office chest");
            return;
        }
        if (VA_postal.central_po_inventory == null) {
            Util.dinform(AnsiColor.RED + "[postmaster_service_postoffice] indicates bad central chest");
            return;
        }


        VA_postal.central_route_player = (Player) VA_postal.central_route_npc.getEntity();
        VA_postal.central_route_player.setItemOnCursor(null);

        ListIterator<ItemStack> local_item_itr = VA_postal.wtr_inventory_postoffice[id].iterator();
        boolean out_of_town_received = false;
        while (local_item_itr.hasNext()) {
            ItemStack ind_item = local_item_itr.next();
            if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                Book book = new Book(ind_item);
                if (!book.is_valid()) {
                    local_item_itr.set(null);
                } else {
                    String mail_to_town = book.getTitle().toLowerCase().trim();
                    String this_town = spostoffice.toLowerCase().trim();
                    if ((!mail_to_town.equals(this_town)) && (!mail_to_town.contains("postal log"))) {
                        C_Dispatcher.promote_central(mail_to_town, 5000);
                        Util.dinform("Schedule promotion - CENTRAL " + mail_to_town);
                        String[] spage = book.getPages();
                        if (spage[0].contains("[not-processed]")) {
                            ItemStack stamped_mail = stamp_central_pickup(id, ind_item);
                            ChestManip.add_to_central_chest(stamped_mail);

                            if (spage[0].contains("[shipping label]")) {
                                ChestManip.parcel_remove_origen_chest(ind_item);
                                ItemStack chest_in_hand = new ItemStack(Material.CHEST);

                                VA_postal.central_route_player = (Player) VA_postal.central_route_npc.getEntity();
                                VA_postal.central_route_player.setItemOnCursor(chest_in_hand);
                            } else {
                                VA_postal.central_route_player = (Player) VA_postal.central_route_npc.getEntity();
                                VA_postal.central_route_player.setItemOnCursor(stamped_mail);
                            }
                        } else {
                            ChestManip.add_to_central_chest(ind_item);
                        }

                        local_item_itr.set(null);
                        out_of_town_received = true;
                    }
                }
            }
        }
        if ((out_of_town_received) &&
                (VA_postal.mailtalk == 2)) {
            Util.binform("&7&oThe PostMaster picked up out of town mail from &9&o" + Util.df(spostoffice));
        }


        ListIterator<ItemStack> central_item_itr = VA_postal.central_po_inventory.iterator();
        boolean out_of_town_delivered = false;
        while (central_item_itr.hasNext()) {
            ItemStack ind_item = central_item_itr.next();
            if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                Book book = new Book(ind_item);
                if (!book.is_valid()) {
                    central_item_itr.set(null);
                } else {
                    String mail_to_town = book.getTitle().toLowerCase().trim();
                    String mail_to_addr = book.getAuthor().toLowerCase().trim();
                    String this_town = spostoffice.toLowerCase().trim();
                    if (mail_to_town.equalsIgnoreCase(this_town)) {
                        add_to_postoffice_chest(id, ind_item);
                        central_item_itr.set(null);
                        out_of_town_delivered = true;
                        C_Dispatcher.promote_schedule(mail_to_town, mail_to_addr, 5000, false);
                        Util.dinform("Schedule promotion - LOCAL " + mail_to_addr);
                    }
                }
            }
        }
        if ((out_of_town_delivered) &&
                (VA_postal.mailtalk == 2)) {
            Util.binform("&7&oThe PostMaster delivered out of town mail to &9&o" + Util.df(spostoffice));
        }

    }

    public static synchronized void npc_post_office_return_from_route(int id) {
        boolean result = false;
        if (VA_postal.wtr_inventory_postoffice[id] == null) {
            return;
        }


        String spostoffice = VA_postal.wtr_poffice[id];
        ListIterator<ItemStack> local_item_itr;
        local_item_itr = VA_postal.wtr_inventory_postoffice[id].iterator();
        while (local_item_itr.hasNext()) {
            ItemStack ind_item = local_item_itr.next();
            if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                Book book = new Book(ind_item);
                if (!book.is_valid()) {
                    local_item_itr.set(null);
                } else {
                    String mail_to_town = book.getTitle().toLowerCase().trim();
                    String mail_to_address = book.getAuthor().toLowerCase().trim();
                    String this_town = spostoffice.toLowerCase().trim();
                    String[] spage = book.getPages();
                    if (spage[0].contains("Attention:")) {
                        if (mail_to_town.equalsIgnoreCase(this_town)) {
                            C_Dispatcher.promote_schedule(mail_to_town, mail_to_address, 5000, false);
                            Util.dinform("Schedule promotion - LOCAL " + mail_to_town);
                        } else {
                            C_Dispatcher.promote_central(this_town, 5000);
                            Util.dinform("Schedule promotion - CENTRAL " + this_town);
                        }
                    }
                }
            }
        }
    }

    public static synchronized ItemStack stamp_central_pickup(int id, ItemStack ind_item) {
        Book book = new Book(ind_item);
        if (!book.is_valid()) {
            return null;
        }
        String title = book.getTitle();
        String author = book.getAuthor();
        String[] pages = book.getPages();
        if ((pages == null) || (pages.length < 1)) {
            return null;
        }

        String[] parts = pages[0].split("\n");
        if (parts.length < 8) {
            return null;
        }
        parts[6] = "§5  Post Master";
        parts[7] = ("§5  " + Util.df(VA_postal.wtr_poffice[id]));

        pages[0] = "";
        for (String part : parts) {
            pages[0] = (pages[0] + part + "\n");
        }

        Book stamped_book = new Book(title, author, pages);

        return stamped_book.generateItemStack();
    }

    public static synchronized void SetPostoffice_Chest_nTP_point(int id) {
        String stown;
        String saddress;
        Player owner;
        String sauthor;
        String spage;
        String s_search_location;
        if (id == 1000) {
            s_search_location = VA_postal.central_po_slocation;
            sauthor = "Central";
            stown = "Server";
            saddress = "[Central]";
            spage = "Postal_Central";
            owner = null;
        } else {
            stown = VA_postal.wtr_poffice[id];
            s_search_location = VA_postal.wtr_slocation_local_po[id];
            sauthor = stown;
            saddress = "[Local]";
            spage = "Postal_Local";
            if (C_Owner.is_local_po_owner_defined(stown)) {
                owner = C_Owner.get_owner_local_po(stown);
            } else {
                owner = VA_postal.SERVER;
            }
        }
        if (s_search_location == null || s_search_location.isEmpty()) {
            return;
        }
        String result = "null";
        Block block;
        String first_hit = "null";
        boolean hit_recorded = false;
        boolean done = false;
        boolean sign_chest_found = false;
        int hits = 0;
        Location p_search_location = Util.str2location(s_search_location);
        assert p_search_location != null;
        p_search_location.subtract(0.0D, 1.0D, 0.0D);
        int maxradius = VA_postal.search_distance;
        Block b_sign_search = SignManip.LookForSignChest(p_search_location, maxradius, "[Postal_Mail]", stown, saddress, (owner != null && owner != VA_postal.SERVER ? owner.getName() : null));
        Location search_location;
        if (b_sign_search != null) {
            if (id == 1000) {
                VA_postal.central_schest_location = Util.location2str(b_sign_search.getLocation());
                VA_postal.central_po_slocation_spawn = ChestManip.chest_front(b_sign_search);
            } else {
                VA_postal.wtr_schest_location_postoffice[id] = Util.location2str(b_sign_search.getLocation());
                VA_postal.wtr_slocation_local_po_spawn[id] = ChestManip.chest_front(b_sign_search);
            }
            if (VA_postal.cstalk) {
                Util.cinform("\033[0;33m    Found Postal sign chest");
            }
            return;
        }
        search_location = p_search_location.clone();
        if (VA_postal.cstalk) {
            Util.cinform(AnsiColor.CYAN + "    Didn't find Postal sign chest");
        }


        if (id == 1000) {
            VA_postal.central_schest_location = "null";
        } else {
            VA_postal.wtr_schest_location_postoffice[id] = "null";
        }
        double y_limit = search_location.getY();
        Block b = search_location.getBlock();
        BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
        BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
        for (int r = 0; r <= maxradius; r++) {
            for (int s = 0; s < 6; s++) {
                BlockFace f = faces[(s % 3)];
                BlockFace[] o = orth[(s % 3)];
                if (s >= 3) {
                    f = f.getOppositeFace();
                }
                Block c = b.getRelative(f, r);
                for (int x = -r; x <= r; x++) {
                    for (int y = -r; y <= r; y++) {
                        block = c.getRelative(o[0], x).getRelative(o[1], y);
                        if ((ChestManip.is_chest(block.getType())) && (ChestManip.ok_to_use_chest(block, true))) {
                            if (block.getY() <= y_limit - 1.0D) {
                                continue;
                            }

                            if (block.getY() >= y_limit + 3.0D) {
                                continue;
                            }

                            hits++;
                            if (!hit_recorded) {
                                hit_recorded = true;
                                first_hit = Util.location2str(block.getLocation());
                            }

                            if (BookManip.valid_postal_log(block, sauthor, spage)) {
                                done = true;
                                result = Util.location2str(block.getLocation());
                                if (VA_postal.cstalk) {
                                    Util.cinform("\033[0;33m      Valid Postal Log found:  " + result);
                                }
                            }
                        }
                        if (done) {
                            break;
                        }
                    }
                    if (done) {
                        break;
                    }
                }
                if (done) {
                    break;
                }
            }
            if (done) {
                break;
            }
        }
        if ("null".equals(result)) {
            result = first_hit;
            if (VA_postal.cstalk) {
                Util.cinform("\033[0;33m      Reverting to First hit:  " + result);
            }
        }
        if (!"null".equals(result)) {
            if (id == 1000) {
                VA_postal.central_schest_location = result;
                if (VA_postal.cstalk) {
                    Util.cinform(AnsiColor.CYAN + "      Central chest recorded at:  " + VA_postal.central_schest_location);
                }
            } else {
                VA_postal.wtr_schest_location_postoffice[id] = result;
                if (VA_postal.cstalk) {
                    Util.cinform(AnsiColor.CYAN + "      Local chest recorded at:  " + VA_postal.wtr_schest_location_postoffice[id]);
                }
            }
            if (id == 1000) {
                VA_postal.central_po_slocation_spawn = ChestManip.chest_front(Util.str2block(result));
                if (VA_postal.cstalk) {
                    Util.cinform(AnsiColor.CYAN + "        Central spawn recorded at:  " + VA_postal.central_po_slocation_spawn);
                }
            } else {
                VA_postal.wtr_slocation_local_po_spawn[id] = ChestManip.chest_front(Util.str2block(result));
                if (VA_postal.cstalk) {
                    Util.cinform(AnsiColor.CYAN + "        Local spawn recorded at:  " + VA_postal.wtr_slocation_local_po_spawn[id]);
                }
            }
            BookManip.standard_addr_sign(result, 1, stown, saddress, null);
        }
    }

    public static synchronized void SetAddress_Chest_nTP_point(int id) {
        String s_search_location = VA_postal.wtr_slocation_address[id];
        if ("null".equals(s_search_location)) {
            return;
        }
        String result = "null";
        Block block;
        String first_hit = "null";
        boolean hit_recorded = false;
        boolean done = false;
        boolean sign_chest_found = false;
        int hits = 0;
        String stown = VA_postal.wtr_poffice[id].toLowerCase().trim();
        String saddress = VA_postal.wtr_address[id].toLowerCase().trim();
        Player owner;
        if (C_Owner.is_address_owner_defined(stown, saddress)) {
            owner = C_Owner.get_owner_address(stown, saddress);
        } else {
            owner = VA_postal.SERVER;
        }
        Location p_search_location = Util.str2location(s_search_location);
        assert p_search_location != null;
        p_search_location.subtract(0.0D, 1.0D, 0.0D);
        int maxradius = VA_postal.search_distance;
        Block b_sign_search = SignManip.LookForSignChest(p_search_location, maxradius, "[Postal_Mail]", stown, saddress, (owner != null && owner != VA_postal.SERVER ? owner.getName() : null));
        Location search_location;
        if (b_sign_search != null) {
            VA_postal.wtr_schest_location[id] = Util.location2str(b_sign_search.getLocation());
            VA_postal.wtr_slocation_address_spawn[id] = ChestManip.chest_front(b_sign_search);
            if (VA_postal.cstalk) {
                Util.cinform("\033[0;33m    Found Postal sign chest");
            }
            return;
        }
        search_location = p_search_location.clone();
        if (VA_postal.cstalk) {
            Util.cinform(AnsiColor.CYAN + "    Didn't find Postal sign chest");
        }

        VA_postal.wtr_schest_location[id] = "null";
        double y_limit = search_location.getY();
        Block b = search_location.getBlock();
        BlockFace[] faces = {BlockFace.UP, BlockFace.NORTH, BlockFace.EAST};
        BlockFace[][] orth = {{BlockFace.NORTH, BlockFace.EAST}, {BlockFace.UP, BlockFace.EAST}, {BlockFace.NORTH, BlockFace.UP}};
        for (int r = 0; r <= maxradius; r++) {
            for (int s = 0; s < 6; s++) {
                BlockFace f = faces[(s % 3)];
                BlockFace[] o = orth[(s % 3)];
                if (s >= 3) {
                    f = f.getOppositeFace();
                }
                Block c = b.getRelative(f, r);
                for (int x = -r; x <= r; x++) {
                    for (int y = -r; y <= r; y++) {
                        block = c.getRelative(o[0], x).getRelative(o[1], y);
                        if ((ChestManip.is_chest(block.getType())) && (ChestManip.ok_to_use_chest(block, true))) {
                            if (block.getY() <= y_limit - 1.0D) {
                                continue;
                            }

                            if (block.getY() >= y_limit + 3.0D) {
                                continue;
                            }

                            hits++;
                            if (!hit_recorded) {
                                hit_recorded = true;
                                first_hit = Util.location2str(block.getLocation());
                            }

                            if (BookManip.valid_postal_log(block, stown, saddress)) {
                                done = true;
                                result = Util.location2str(block.getLocation());
                                if (VA_postal.cstalk) {
                                    Util.cinform("\033[0;33m      Valid Postal Log found:  " + result);
                                }
                            }
                        }
                        if (done) break;
                    }
                    if (done) break;
                }
                if (done) break;
            }
            if (done) break;
        }

        if ("null".equals(result)) {
            result = first_hit;
            if (VA_postal.cstalk) {
                Util.cinform("\033[0;33m      Reverting to First hit:  " + result);
            }
        }

        if (!"null".equals(result)) {
            VA_postal.wtr_schest_location[id] = result;
            if (VA_postal.cstalk) {
                Util.cinform(AnsiColor.CYAN + "      Mailbox  recorded   at:  " + VA_postal.wtr_schest_location[id]);
            }
            VA_postal.wtr_slocation_address_spawn[id] = ChestManip.chest_front(Util.str2block(result));
            if (VA_postal.cstalk) {
                Util.cinform(AnsiColor.CYAN + "        Spawn  recorded   at:  " + VA_postal.wtr_slocation_address_spawn[id]);
            }
            BookManip.standard_addr_sign(result, 1, stown, saddress, null);
        }
    }

    public static synchronized void create_addr_sign(int id) {
        Location location = Util.str2location(VA_postal.wtr_schest_location[id]);
        assert location != null;
        World w = location.getWorld();
        Block block = w.getBlockAt(location);
        String stown = VA_postal.wtr_poffice[id];
        String saddress = VA_postal.wtr_address[id];
        String sowner = "§7[Server]";
        String stitle = "§a[Postal_Mail]";
        if (C_Owner.is_address_owner_defined(stown, saddress)) {
            sowner = C_Owner.get_owner_address(stown, saddress).getDisplayName();
            if (sowner.length() > 15) {
                sowner = sowner.substring(0, 15);
            }
        }
        SignManip.create_sign_id_chest(block, stitle, Util.df(stown), Util.df(saddress), sowner);
    }
}
