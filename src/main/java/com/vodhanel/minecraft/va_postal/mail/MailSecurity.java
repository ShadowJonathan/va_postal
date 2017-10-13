package com.vodhanel.minecraft.va_postal.mail;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.commands.Cmdexecutor;
import com.vodhanel.minecraft.va_postal.common.P_Towny;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.config.C_Address;
import com.vodhanel.minecraft.va_postal.config.C_Dispatcher;
import com.vodhanel.minecraft.va_postal.config.C_Owner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ListIterator;


public class MailSecurity {
    private static String last_splayer_inform = "";
    VA_postal plugin;

    public MailSecurity(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized boolean hasPermission(Player player, String node) {
        String splayer = player.getName().toLowerCase().trim();
        boolean valid_admin_attempt = false;

        if (VA_postal.perms != null) {
            if ((VA_postal.perms.has(player, "postal.admin")) && (VA_postal.admin_bypass)) {
                Util.perm_inform("Vault (validated): postal.admin, " + splayer);
                return true;
            }
            if (VA_postal.perms.has(player, "postal.admin")) {
                valid_admin_attempt = true;
            }
            if ((!player.isOp()) && (VA_postal.perms.has(player, node))) {
                Util.perm_inform("Vault: " + node + ", " + splayer);
                return true;
            }
        } else {
            if ((player.hasPermission("postal.admin")) && (VA_postal.admin_bypass)) {
                Util.perm_inform("Bukkit (validated): postal.admin, " + splayer);
                return true;
            }
            if (player.hasPermission("postal.admin")) {
                valid_admin_attempt = true;
            }
            if ((!player.isOp()) && (player.hasPermission(node))) {
                Util.perm_inform("Bukkit: " + node + ", " + splayer);
                return true;
            }
        }


        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            if ((P_Towny.is_towny_admin_by_loc(player)) && (VA_postal.admin_bypass)) {
                Util.perm_inform("Towny (validated): towny admin, " + splayer);
                return true;
            }
            if (P_Towny.is_towny_admin_by_loc(player)) {
                valid_admin_attempt = true;
            }
        }

        if (valid_admin_attempt) {
            Util.pinform(player, "&7&oYou have permission, but enter &f&r'/postal bypass' &7&ofirst.");
        }


        return false;
    }

    public static synchronized boolean hasPermission_ext(Player player, String node, String stown, String saddress) {
        String splayer = player.getName().toLowerCase().trim();
        boolean valid_admin_attempt = false;

        if (VA_postal.perms != null) {
            if ((VA_postal.perms.has(player, "postal.admin")) && (VA_postal.admin_bypass)) {
                Util.perm_inform("Vault (validated): postal.admin, " + splayer);
                return true;
            }
            if (VA_postal.perms.has(player, "postal.admin")) {
                valid_admin_attempt = true;
            }
            if ((!player.isOp()) && (VA_postal.perms.has(player, node))) {
                Util.perm_inform("Vault: " + node + ", " + splayer);
                return true;
            }
        } else {
            if ((player.hasPermission("postal.admin")) && (VA_postal.admin_bypass)) {
                Util.perm_inform("Bukkit (validated): postal.admin, " + splayer);
                return true;
            }
            if (player.hasPermission("postal.admin")) {
                valid_admin_attempt = true;
            }
            if ((!player.isOp()) && (player.hasPermission(node))) {
                Util.perm_inform("Bukkit: " + node + ", " + splayer);
                return true;
            }
        }


        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            if ((P_Towny.is_towny_admin_by_loc(player)) && (VA_postal.admin_bypass)) {
                Util.perm_inform("Towny (validated): towny admin, " + splayer);
                return true;
            }
            if (P_Towny.is_towny_admin_by_loc(player)) {
                valid_admin_attempt = true;
            }
        }


        if (splayer.length() > 15) {
            splayer = splayer.substring(0, 15);
        }
        if ((stown != null) && (!"null".equals(stown))) {
            if (C_Owner.is_local_po_owner_defined(stown)) {
                String test = C_Owner.get_owner_local_po(stown);
                if (test.equalsIgnoreCase(splayer)) {
                    Util.perm_inform("PO owner, " + splayer + ", " + stown);
                    return true;
                }
            }
        }
        if ((stown != null) && (!"null".equals(stown)) &&
                (saddress != null) && (!"null".equals(saddress))) {
            if (C_Owner.is_address_owner_defined(stown, saddress)) {
                String test = C_Owner.get_owner_address(stown, saddress);
                if (test.equalsIgnoreCase(splayer)) {
                    Util.perm_inform("address owner, " + splayer + ", " + stown + ", " + saddress);
                    return true;
                }
            }
        }


        if ((stown != null) && (!"null".equals(stown)) &&
                (VA_postal.towny_configured) && (VA_postal.towny_opt_in) &&
                (P_Towny.is_towny_admin_by_db(splayer, stown))) {
            Util.perm_inform("Towny ranked assistant, " + splayer + ", " + stown);
            return true;
        }


        if ((stown != null) && (!"null".equals(stown)) &&
                (saddress != null) && (!"null".equals(saddress)) &&
                (VA_postal.towny_configured) && (VA_postal.towny_opt_in) &&
                (P_Towny.is_towny_plot_owner_by_db(splayer, stown, saddress))) {
            Util.perm_inform("Towny plot owner, " + splayer + stown + saddress);
            return true;
        }


        if ((valid_admin_attempt) &&
                (!splayer.equals(last_splayer_inform))) {
            Util.pinform(player, "&7&oYou have permission, but enter &f&r'/postal bypass' &7&ofirst.");
            last_splayer_inform = splayer;
        }


        return false;
    }

    public static synchronized boolean qualified_mailbox_open(Player player, Block block) {
        if ((player == null) || (block == null)) {
            return false;
        }
        String splayer = player.getName();
        if (VA_postal.private_mailboxes) {
            if (Cmdexecutor.hasPermission(player, "postal.inspector")) {
                Util.perm_inform("Open Mailbox: admin access, " + splayer);
                open_chest(player, block);
                return true;
            }

            Chest chest = (Chest) block.getState();
            if (chest == null) {
                return false;
            }
            Inventory inventory = chest.getInventory();
            if (inventory == null) {
                return false;
            }

            if (player_mail_in_chest(player, inventory)) {
                Util.perm_inform("Open Mailbox: player mail inside, " + splayer);
                open_chest(player, block);
                return true;
            }
            if (!BookManip.is_there_a_postal_log(inventory)) {
                return false;
            }

            if (BookManip.is_this_a_postoffice(inventory)) {
                Util.perm_inform("Open Mailbox: post office chest, " + splayer);
                open_chest(player, block);
                return true;
            }
            String stown = "";
            String saddress = "";
            String[] pair = BookManip.get_postal_log_pair(inventory);
            if ((pair == null) || (pair.length < 2)) {
                return false;
            }
            stown = pair[0].toLowerCase().trim();
            saddress = pair[1].toLowerCase().trim();
            if ((stown == null) || (saddress == null)) {
                return false;
            }

            if (Cmdexecutor.hasPermission_ext(player, "postal.inspector", stown, saddress)) {
                Util.perm_inform("Open Mailbox: player is owner, " + splayer + ", " + stown + ", " + saddress);
                open_chest(player, block);
                return true;
            }


            Util.pinform(player, "&7&oSorry, there is no mail for you in this chest");
        } else {
            Util.perm_inform("Open Mailbox: mailbox privacy is not turned on, " + splayer);
            open_chest(player, block);
            return true;
        }
        return false;
    }

    public static synchronized void open_chest(Player player, Block block) {
        Chest chest = (Chest) block.getState();
        if ((player != null) && (chest != null)) {
            player.openInventory(chest.getInventory());
        }
    }

    public static synchronized boolean player_mail_in_chest(Player player, Inventory inventory) {
        if ((inventory == null) || (player == null)) {
            return false;
        }
        String splayer = player.getName().toLowerCase().trim();
        ListIterator<ItemStack> item_itr = inventory.iterator();
        if (item_itr != null) {
            while (item_itr.hasNext()) {
                ItemStack ind_item = (ItemStack) item_itr.next();
                if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                    Book book = new Book(ind_item);
                    if (book.is_valid()) {
                        try {
                            String page = book.getPage(1).toLowerCase().trim();
                            if ((book != null) && (page.contains(splayer))) {
                                book = null;
                                return true;
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return false;
    }

    public static synchronized boolean is_authorized_to_break_chest_event(Block pblock, Player player) {
        String splayer = player.getName().toLowerCase();
        Chest chest = (Chest) pblock.getState();
        Inventory inventory = chest.getInventory();
        if ((inventory == null) || (chest == null)) {
            return false;
        }
        if (BookManip.is_there_a_postal_log(inventory)) {
            String[] pair = BookManip.get_postal_log_pair(inventory);
            if (pair[0].equals("null")) {
                return false;
            }
            if (hasPermission_ext(player, "postal.bypass", pair[0], pair[1])) {
                inventory.clear(0);
                Util.perm_inform("Destroy Mailbox: owner or admin permission, " + splayer);
                return true;
            }


            return false;
        }

        Util.perm_inform("Destroy Mailbox: not a Postal chest, " + splayer);
        return true;
    }

    public static synchronized void event_check_chest_for_new_mail(Inventory chest_inv) {
        String chest_town = "";
        String chest_address = "";
        String mail_town = "";
        String mail_address = "";
        Book book = null;


        if (BookManip.is_there_a_postal_log(chest_inv)) {
            ItemStack ind_item = chest_inv.getItem(0);
            book = new Book(ind_item);
            String page1 = book.getPage(1);
            String[] parts = page1.split("\n");
            chest_town = book.getAuthor().toLowerCase().trim();
            chest_address = parts[2].toLowerCase().trim();
            if ((chest_address.contains("postal_local")) || (chest_address.contains("postal_central"))) {
                book = null;
                parts = null;
                return;
            }
        } else {
            return;
        }


        book = null;
        ListIterator<ItemStack> item_itr = chest_inv.iterator();
        if (item_itr != null)
            while (item_itr.hasNext()) {
                ItemStack ind_item = (ItemStack) item_itr.next();
                if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                    book = new Book(ind_item);
                    if (!book.is_valid()) {
                        book = null;
                    } else {
                        mail_town = book.getTitle().toLowerCase().trim();
                        mail_address = book.getAuthor().toLowerCase().trim();
                        if ((!mail_town.equalsIgnoreCase(chest_town)) || (!mail_address.equalsIgnoreCase(chest_address))) {
                            String[] spage = book.getPages();
                            if (spage[0].contains("[not-processed]")) {
                                C_Dispatcher.promote_schedule(chest_town, chest_address, 5000, false);

                                return;
                            }
                        }
                        book = null;
                    }
                }
            }
    }

    public static synchronized boolean is_authorized_to_break_sign_event(Block block, Player player) {
        if (block == null) {
            return false;
        }
        int type = SignManip.get_sign_type(block);
        if (type != 1) {
            return true;
        }
        String splayer = player.getName().toLowerCase().trim();

        if (splayer.length() > 15) {
            splayer = splayer.substring(0, 15);
        }
        String[] sign_set = SignManip.get_sign_set(block);
        String stown = sign_set[0].toLowerCase().trim();
        String saddress = sign_set[1].toLowerCase().trim();
        String sign_owner = sign_set[2].toLowerCase().trim();
        if (splayer.equals(sign_owner)) {
            return true;
        }
        if (hasPermission_ext(player, "postal.bypass", stown, saddress)) {
            return true;
        }

        return false;
    }

    public static synchronized boolean may_player_access_this_mail(Inventory inventory, ItemStack stack, int slot, Player player) {
        if ((inventory == null) || (stack == null)) {
            return false;
        }
        String splayer = player.getName().toLowerCase().trim();
        if (slot == 0) {
            if (hasPermission(player, "postal.inspector")) {
                ItemStack holding = player.getItemInHand();
                if (holding.getTypeId() != 0) {
                    player.getWorld().dropItemNaturally(player.getLocation(), player.getItemInHand());
                    Util.pinform(player, "&9The item you were holding has been dropped.");
                }
                ItemStack clone = stack.clone();
                player.setItemInHand(clone);
                Util.pinform(player, "&9A copy of the postal log has been placed in your hand.");
                return false;
            }
        } else {
            if (hasPermission(player, "postal.inspector")) {
                Util.perm_inform("Mail access: player has admin access, " + splayer);
                return true;
            }

            String stown = null;
            String saddress = null;
            try {
                Book book = new Book(inventory.getItem(0));
                String page1 = book.getPage(1);
                String[] parts = page1.split("\n");
                stown = book.getAuthor().trim();
                saddress = parts[2].trim();
                book = null;
            } catch (Exception e) {
                stown = null;
                saddress = null;
            }
            String test = "";
            if ((stown != null) &&
                    (C_Owner.is_local_po_owner_defined(stown))) {
                test = C_Owner.get_owner_local_po(stown).trim();
                if (test.equalsIgnoreCase(splayer)) {
                    Util.perm_inform("Mail access: player owns the PO, " + splayer + ", " + stown);
                    return true;
                }
            }


            if (VA_postal.private_mailboxes) {
                if (BookManip.is_this_a_postoffice(inventory)) {
                    return false;
                }
            }


            boolean address_owned = false;
            if ((saddress != null) &&
                    (C_Owner.is_address_owner_defined(stown, saddress))) {
                address_owned = true;
                test = C_Owner.get_owner_address(stown, saddress).trim();
                if (test.equalsIgnoreCase(splayer)) {
                    Util.perm_inform("Mail access: player owns the address, " + splayer + ", " + stown + ", " + saddress);
                    return true;
                }
            }


            if (splayer.length() > 15) {
                splayer = splayer.substring(0, 15);
            }
            String stitle = null;
            String sender = null;
            String srecipient = null;
            try {
                Book book = new Book(stack);
                stitle = book.getTitle().toLowerCase().trim();
                String page1 = book.getPage(1);
                String[] parts = page1.split("\n");
                sender = parts[9].substring(4).toLowerCase().trim();
                if (sender.length() > 15) {
                    sender = sender.substring(0, 15);
                }
                srecipient = parts[4].substring(4).toLowerCase().trim();
                if (srecipient.length() > 15) {
                    srecipient = srecipient.substring(0, 15);
                }
                book = null;
            } catch (Exception e) {
                sender = null;
                srecipient = null;
            }

            if ((stitle != null) &&
                    (stitle.contains("distribution"))) {
                Util.perm_inform("Mail access: general distribution, " + splayer);
                return true;
            }


            if ((srecipient != null) &&
                    (splayer.equals(srecipient))) {
                Util.perm_inform("Mail access: player is recipient, " + splayer);
                return true;
            }


            if ((sender != null) &&
                    (splayer.equals(sender))) {
                Util.perm_inform("Mail access: player is sender, " + splayer);
                return true;
            }


            if ((srecipient != null) &&
                    (!address_owned) && (srecipient.equals("[resident]"))) {
                Util.perm_inform("Mail access: mail addressed to [resident], " + splayer);
                return true;
            }
        }


        return false;
    }

    public static synchronized boolean allowed_to_break_shipment(Block sign_block, Player player) {
        String splayer = player.getName().toLowerCase();
        ItemStack stack = player.getItemInHand();
        if ((sign_block == null) || (stack == null) || (stack.getType() != Material.WRITTEN_BOOK)) {
            return false;
        }
        Book book = new Book(stack);
        if (!book.is_valid()) {
            return false;
        }
        String page1 = book.getPage(1);

        String[] parts = page1.split("\n");
        String sauthor = parts[9].substring(4);
        if ((sauthor != null) && (!sauthor.isEmpty()) &&
                (SignManip.text_exists_sign(sign_block, sauthor, 4))) {
            Util.perm_inform("Shipment break: player has shipper in hand, " + splayer);
            return true;
        }


        return false;
    }

    public static synchronized void remove_new_mail_marker_event(Player player, Block block) {
        if (block == null) {
            return;
        }
        if (!(block.getState() instanceof Chest)) {
            return;
        }

        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getInventory();
        String[] pair = BookManip.get_postal_log_pair(inventory);
        String owner = "Server";
        if ((!pair[0].equals("null")) &&
                (C_Owner.is_address_owner_defined(pair[0], pair[1]))) {
            owner = C_Owner.get_owner_address(pair[0], pair[1]);
        }

        String splayer = player.getName().trim();

        if ((owner.equals("Server")) || (owner.equalsIgnoreCase(splayer))) {
            Location location = block.getLocation();
            World w = location.getWorld();
            String stitle = "§a[Postal_Mail]";
            if (owner.equals("Server")) {
                SignManip.edit_sign_id_chest(block, stitle, null, null, null);
                Util.perm_inform("New-Mail clear flag: Server owned mailbox, " + splayer);
            } else if (owner.equalsIgnoreCase(splayer)) {
                SignManip.edit_sign_id_chest(block, stitle, null, null, null);

                C_Address.set_address_newmail(pair[0], pair[1], false);
                Util.perm_inform("New-Mail clear flag: owner accessed mailbox, " + splayer);
            }
        }
    }
}
