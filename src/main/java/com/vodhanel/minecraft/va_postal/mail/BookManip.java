package com.vodhanel.minecraft.va_postal.mail;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.config.C_Owner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ListIterator;


public class BookManip {
    VA_postal plugin;

    public BookManip(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized boolean is_there_a_postal_log(Inventory inventory) {
        if (inventory == null) {
            return false;
        }
        ItemStack ind_item = inventory.getItem(0);

        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(ind_item);
            if (!book.is_valid()) {
                book = null;
                return false;
            }
            String mail_title = book.getTitle().toLowerCase().trim();
            if (mail_title.contains("postal log")) {
                book = null;
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean is_this_a_postoffice(Inventory inventory) {
        if (inventory == null) {
            return false;
        }
        ItemStack ind_item = inventory.getItem(0);

        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(ind_item);
            if (!book.is_valid()) {
                book = null;
                return false;
            }
            String mail_title = book.getTitle().toLowerCase().trim();
            if (mail_title.contains("postal log")) {
                String page1 = book.getPage(1);
                if (page1.contains("Postal_Local")) {
                    book = null;
                    return true;
                }
            }
        }
        return false;
    }

    public static synchronized String[] get_postal_log_pair(Inventory inventory) {
        if (inventory == null) {
            return null;
        }


        String[] result = new String[2];
        result[0] = "null";
        result[1] = "null";
        ItemStack ind_item = inventory.getItem(0);
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(ind_item);
            if (!book.is_valid()) {
                book = null;
                return result;
            }
            String mail_title = book.getTitle().toLowerCase().trim();
            if (mail_title.contains("postal log")) {
                result[0] = book.getAuthor().toLowerCase().trim();
                if ((result[0] == null) || (result[0].isEmpty())) {
                    result[0] = "null";
                    return result;
                }
                String page1 = book.getPage(1);
                if ((page1 != null) && (!page1.isEmpty())) {
                    String[] parts = page1.split("\n");
                    if ((parts != null) && (parts.length > 2)) {
                        result[1] = parts[2].toLowerCase().trim();
                        if ((result[1] == null) || (result[1].isEmpty())) {
                            result[1] = "null";
                        }
                    }
                }
            }
        }
        return result;
    }

    public static synchronized ItemStack stamp_parcel_statement(Player player, ItemStack ind_item, boolean accept) {
        String[] spage = null;
        String author = "";
        String splayer = player.getName();
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = null;
            try {
                book = new Book(ind_item);
            } catch (Exception e) {
                book = null;
                return null;
            }
            if (!book.is_valid()) {
                book = null;
                return null;
            }
            author = book.getAuthor().toLowerCase().trim();
            String title = book.getTitle().toLowerCase().trim();
            spage = book.getPages();
            boolean fixed_accept = false;
            boolean fixed_refuse = false;

            spage[0] = spage[0].replace("[shipping label]", "[statement]");

            for (int i = 1; i < spage.length; i++) {
                if (spage[i].contains("§2/accept")) {
                    fixed_accept = true;
                    if (accept) {
                        spage[i] = spage[i].replace("§2/accept", "§cShipment Accepted");
                    } else {
                        spage[i] = spage[i].replace("§2/accept", "§cShipment Refused");
                    }
                }
                if (spage[i].contains("§c/refuse")) {
                    fixed_refuse = true;


                    Format formatter = new SimpleDateFormat("MM/dd/yy HH:mm");
                    Date date = new Date();
                    String fdate = "§c" + formatter.format(date) + "\n" + splayer;
                    spage[i] = spage[i].replace("§c/refuse", fdate);
                }
                if ((fixed_accept) && (fixed_refuse)) {
                    break;
                }
            }
        }

        Book book = new Book("[statement]", author, spage);

        return book.generateItemStack();
    }

    public static synchronized boolean holding_valid_shipper(Player player, ItemStack ind_item, boolean processed) {
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = null;
            try {
                book = new Book(ind_item);
            } catch (Exception e) {
                book = null;
                return false;
            }
            if (!book.is_valid()) {
                book = null;
                return false;
            }
            String[] spage = book.getPages();
            if (processed) {
                if (!spage[0].contains("[not-processed]")) {
                    if (spage[0].toLowerCase().contains("[shipping label]")) {
                        for (String aSpage : spage) {
                            if (aSpage.contains("/accept")) {
                                book = null;
                                return true;
                            }
                        }

                        Util.pinform(player, "&c&oThis order has already been filled.");
                    } else {
                        Util.pinform(player, "&c&oThis is not a shipping label.");
                    }
                } else {
                    Util.pinform(player, "&c&oThis has not been processed by the post office.");
                }
            } else if (spage[0].contains("[not-processed]")) {
                if (spage[0].toLowerCase().contains("[shipping label]")) {
                    return true;
                }

                Util.pinform(player, "&c&oThis is not a shipping label.");
            } else {
                Util.pinform(player, "&c&oThis has already been processed by the post office.");
            }
        }

        return false;
    }

    public static synchronized String[] parcel_list(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        String[] lines_and_items = new String[100];
        ListIterator<ItemStack> item_itr = inventory.iterator(0);
        int index = 0;
        int slot = 0;
        if (item_itr != null)
            while (item_itr.hasNext()) {
                ItemStack ind_item = item_itr.next();
                if ((ind_item == null) || (ind_item.getTypeId() == 0)) {
                    slot++;
                } else {
                    String sqty = MailGen.ifixed_len(ind_item.getAmount(), 2);
                    String sid = MailGen.ifixed_len(ind_item.getTypeId(), 3);
                    String name = Util.df(ind_item.getType().name().toLowerCase());
                    lines_and_items[(slot + 50)] = MailGen.stack2serial(ind_item);

                    if (name.length() >= 12) {
                        name = name.substring(0, 12);
                    }
                    lines_and_items[index] = ("§2" + sqty + " §7" + sid + " §9" + name);
                    index++;
                    slot++;
                }
            }
        if (index > 0) {
            return lines_and_items;
        }
        return null;
    }

    public static synchronized Inventory parcel_fill_chest(Block block, ItemStack ind_item) {
        Book book = new Book(ind_item);
        String[] pages = book.getPages();

        String name = "";

        Chest chest = (Chest) block.getState();
        if (chest == null) {
            book = null;
            return null;
        }


        Inventory inventory = chest.getInventory();


        String[] parts = null;
        ItemStack items = null;
        for (int i = 10; i < 37; i++) {
            if ((pages[i] != null) && (!pages[i].isEmpty())) {
                short dur;
                int item;
                int qty;
                try {
                    parts = pages[i].split(",");
                    if (parts.length == 4) {
                        name = parts[0].trim();
                        qty = Util.str2int(parts[1]);
                        item = Util.str2int(parts[2]);
                        dur = (short) Util.str2int(parts[3]);
                    } else {
                        continue;
                    }
                } catch (NumberFormatException numberFormatException) {
                    continue;
                }
                try {
                    items = new ItemStack(item);
                    items.setDurability(dur);
                    items.setAmount(qty);
                    inventory.setItem(i - 10, items);
                } catch (Exception e) {
                    Util.dinform("Did not include " + name + " itrm: " + item);
                }
            }
        }
        book = null;
        return inventory;
    }

    public static synchronized String[] parcel_pages(Player player, Inventory inventory) {
        ItemStack stack_in_hand = player.getItemInHand();
        if ((stack_in_hand != null) &&
                (stack_in_hand.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(stack_in_hand);
            if (!book.is_valid()) {
                book = null;
            } else {
                return book.getPages();
            }
        }


        if (inventory == null) {
            return null;
        }
        String[] pass_1 = new String[27];
        ListIterator<ItemStack> item_itr = inventory.iterator(0);
        int count = 0;
        if (item_itr != null) {
            int index = 0;
            while (item_itr.hasNext()) {
                ItemStack ind_item = item_itr.next();
                if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                    Book book = new Book(ind_item);
                    if (!book.is_valid()) {
                        book = null;
                    } else {
                        String mail_title = book.getTitle().toLowerCase().trim();
                        if (mail_title.contains("statement"))
                            return book.getPages();
                    }
                }
            }
        }
        return null;
    }

    public static synchronized void parcel_stmnt_to_chest(Inventory inventory, ItemStack stack, Block block, int type) {
        if ((inventory == null) || (stack == null)) {
            return;
        }
        String[] pass_1 = new String[27];
        ListIterator<ItemStack> item_itr = inventory.iterator(0);
        boolean done = false;
        int count = 0;
        if (item_itr != null) {
            int index = 0;
            while (item_itr.hasNext()) {
                ItemStack ind_item = item_itr.next();
                if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                    Book book = new Book(ind_item);
                    if (!book.is_valid()) {
                        book = null;
                    } else {
                        String mail_title = book.getTitle().toLowerCase().trim();
                        if (mail_title.contains("statement")) {
                            item_itr.set(stack.clone());
                            book = null;
                            done = true;
                            break;
                        }
                    }
                }
            }
            if (!done) {
                item_itr = inventory.iterator(0);
                while (item_itr.hasNext()) {
                    ItemStack ind_item = item_itr.next();
                    if ((ind_item == null) || (ind_item.getTypeId() == 0)) {
                        item_itr.set(stack.clone());
                        done = true;
                        break;
                    }
                }
            }
            if (!done) {
                inventory.setItem(0, stack.clone());
                done = true;
            }
        }
        if ((done) && (block != null)) {
            Book book = new Book(stack);
            if (!book.is_valid()) {
                book = null;
                return;
            }

            String stown = "";
            String saddress = "";
            String ssender = "";
            String srecipient = "";
            String[] pages = book.getPages();
            try {
                String[] parts = pages[0].split("\n");
                stown = parts[1].substring(4).toLowerCase().trim();
                saddress = parts[2].substring(4).toLowerCase().trim();
                ssender = parts[9].substring(4).toLowerCase().trim();
                srecipient = parts[4].substring(4).toLowerCase().trim();
            } catch (Exception e) {
                book = null;
                return;
            }
            String slocation = Util.location2str(block.getLocation());
            standard_addr_sign(slocation, type, stown, saddress, ssender);
        }
    }

    public static synchronized boolean central_chest_contains_postal_log() {
        if (VA_postal.central_po_inventory == null) {
            Util.dinform(AnsiColor.RED + "[central_chest_contains_postal_log] indicates bad po chest");
            return false;
        }


        String slocation = VA_postal.central_schest_location;
        ItemStack ind_item = VA_postal.central_po_inventory.getItem(0);
        String sowner = "server";
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(ind_item);
            if (book.is_valid()) {
                String mail_title = book.getTitle().toLowerCase().trim();
                String mail_author = book.getAuthor().toLowerCase().trim();
                if (mail_title.contains("postal log")) {
                    String page1 = book.getPage(1);
                    String[] parts = page1.split("\n");
                    String slocation_comp = parts[0].trim() + "," + parts[1].trim();
                    if (slocation.equalsIgnoreCase(slocation_comp)) {
                        return true;
                    }
                    parts = null;
                }
                book = null;
            }
        }
        return false;
    }

    public static synchronized boolean central_create_and_install_postal_log() {
        if (VA_postal.central_po_inventory == null) {
            Util.dinform(AnsiColor.RED + "[central_create_and_install_postal_log] indicates bad po chest");
            return false;
        }

        ItemStack log_book = ChestManip.central_create_log();

        MailGen.replace_slot_by_index_cen(0, log_book);
        return true;
    }

    public static synchronized void standard_addr_sign(String slocation, int header, String stown, String saddr, String sowner) {
        Location location = Util.str2location(slocation);
        World w = null;
        if (location != null) {
            w = location.getWorld();
        } else {
            return;
        }
        Block block = w.getBlockAt(location);
        if (!(block.getState() instanceof Chest)) {
            return;
        }
        byte dir = block.getData();
        Location chest_front = block.getLocation();

        if (dir == 2) {
            chest_front.subtract(0.0D, 0.0D, 1.0D);
        } else if (dir == 3) {
            chest_front.add(0.0D, 0.0D, 1.0D);
        } else if (dir == 4) {
            chest_front.subtract(1.0D, 0.0D, 0.0D);
        } else if (dir == 5) {
            chest_front.add(1.0D, 0.0D, 0.0D);
        }
        Block block_sign = w.getBlockAt(chest_front);
        try {
            block_sign.setTypeId(68);
            block_sign.setData(dir);
        } catch (Exception e) {
            return;
        }

        String line_1 = "";
        String line_2 = "";
        String line_3 = "";
        String line_4 = "";
        switch (header) {
            case 1:
                line_1 = "§a[Postal_Mail]";
                break;
            case 2:
                line_1 = "[Postal_Ship]";
                line_4 = Util.df(sowner);
                break;
            case 3:
                line_1 = "[Postal_Accept]";
                line_4 = Util.df(sowner);
                break;
            case 4:
                line_1 = "[Postal_Refuse]";
                line_4 = Util.df(sowner);
                break;
            default:
                line_1 = "§a[Postal_Mail]";
        }
        line_2 = Util.df(stown);
        line_3 = Util.df(saddr);
        if (sowner == null) {
            if (saddr.contains("[Local]")) {
                if (C_Owner.is_local_po_owner_defined(stown)) {
                    line_4 = Util.df(C_Owner.get_owner_local_po(stown).getDisplayName());
                }
            } else if (C_Owner.is_address_owner_defined(stown, saddr)) {
                line_4 = Util.df(C_Owner.get_owner_address(stown, saddr).getDisplayName());
            }
        }
        //Util.dinform("sowner for "+stown+" "+saddr+" was "+sowner);

        if (line_4.isEmpty()) {
            line_4 = "§a[Server]";
        }
        if (line_4.length() > 15) {
            line_4 = line_4.substring(0, 15);
        }
        if ((block_sign.getState() instanceof Sign)) {
            Sign sign = (Sign) block_sign.getState();
            sign.setLine(0, line_1);
            sign.setLine(1, line_2);
            sign.setLine(2, line_3);
            sign.setLine(3, line_4);
            sign.update();
        }
    }

    public static synchronized boolean valid_postal_log(Block block, String s_author, String s_page1) {
        if (block == null) {
            return false;
        }
        if (!(block.getState() instanceof Chest)) {
            return false;
        }
        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getInventory();
        ItemStack ind_item = inventory.getItem(0);
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(ind_item);
            if (!book.is_valid()) {
                book = null;
                return false;
            }
            String mail_title = book.getTitle().toLowerCase().trim();
            if (mail_title.contains("postal log")) {
                if (s_author != null) {
                    String mail_author = book.getAuthor().toLowerCase().trim();
                    s_author = s_author.toLowerCase().trim();
                    if (!mail_author.contains(s_author)) {
                        book = null;
                        return false;
                    }
                }
                if (s_page1 != null) {
                    String mail_page1 = book.getPage(1).toLowerCase().trim();
                    s_page1 = s_page1.toLowerCase().trim();
                    if (!mail_page1.contains(s_author)) {
                        book = null;
                        return false;
                    }
                }
                book = null;
                return true;
            }
        }
        return false;
    }

    public static synchronized Inventory postal_inventory(Block block) {
        if (block == null) {
            return null;
        }
        if (!(block.getState() instanceof Chest)) {
            return null;
        }
        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getInventory();
        ItemStack ind_item = inventory.getItem(0);
        if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
            Book book = new Book(ind_item);
            if (!book.is_valid()) {
                book = null;
                return null;
            }
            String mail_title = book.getTitle().toLowerCase().trim();
            if (mail_title.contains("postal log")) {
                book = null;
                return inventory;
            }
        }
        return null;
    }
}
