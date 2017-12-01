package com.vodhanel.minecraft.va_postal.commands;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.P_Economy;
import com.vodhanel.minecraft.va_postal.common.Util;
import com.vodhanel.minecraft.va_postal.common.VA_Timers;
import com.vodhanel.minecraft.va_postal.config.*;
import com.vodhanel.minecraft.va_postal.listeners.RouteEditor;
import com.vodhanel.minecraft.va_postal.mail.Book;
import com.vodhanel.minecraft.va_postal.mail.BookManip;
import com.vodhanel.minecraft.va_postal.mail.ChestManip;
import com.vodhanel.minecraft.va_postal.mail.SignManip;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ListIterator;

public class Cmd_static {
    private static String cod_prepend = "§c  [COD] $";
    VA_postal plugin;

    public Cmd_static(VA_postal instance) {
        plugin = instance;
    }

    public static void menu_admin(Player player) {
        Util.pinform(player, " &7&oUsage: &f&r/postal <start/stop/restart/admin/chests/speed>");
        Util.pinform(player, " &7&oTeleport to a Postal location");
        Util.pinform(player, " &f&r/go [PostOffice] [address] &7&o(no parms = Central)");
        Util.pinform(player, " &7&oAddress a book in your hand");
        Util.pinform(player, " &f&r/addr <PostOffice> <address> [player]");
        Util.pinform(player, " &7&oSet Attention (to) of addressed book in your hand");
        Util.pinform(player, " &f&r/att <player>");
        Util.pinform(player, " &7&oPrepare a package for shipment");
        Util.pinform(player, " &f&r/package <PostOffice> <address> [player] &7&o(alias: /pk)");
        Util.pinform(player, " &7&oMake a shipping label C.O.D.");
        Util.pinform(player, " &f&r/cod <price>");
        Util.pinform(player, " &7&oAccept shipment - shipping label in your hand");
        Util.pinform(player, " &f&r/accept");
        Util.pinform(player, " &7&oRefuse shipment - shipping label in your hand");
        Util.pinform(player, " &f&r/refuse");
        Util.pinform(player, " &7&oDistribute written book to Postal addresses");
        Util.pinform(player, " &f&r/distr <all/owners> [PostOffice] [expiration_days]");
        Util.pinform(player, " &7&oList towns with post offices");
        Util.pinform(player, " &f&r/tlist &7&o(alias: /tl)");
        Util.pinform(player, " &7&oList addresses in a town");
        Util.pinform(player, " &f&r/alist <PostOffice> &7&o(alias: /al)");
        Util.pinform(player, " &7&oList players that have been on server");
        Util.pinform(player, " &f&r/plist <string match> &7&o(alias: /pl)");
        Util.pinform(player, " &7&oSet your compass to a post office or address");
        Util.pinform(player, " &f&r/gps <PostOffice> [Address] &7&o(alias: /gpsp)");
        Util.pinform(player, " &7&oPush a route schedule forward");
        Util.pinform(player, " &f&r/expedite <PostOffice> <address> &7&o(alias: /ex)");
        Util.pinform(player, " &7&oSet the central post office");
        Util.pinform(player, " &f&r/setcentral");
        Util.pinform(player, " &7&oSet a local post office");
        Util.pinform(player, " &f&r/setlocal <PostOffice>");
        Util.pinform(player, " &7&oSet a town address");
        Util.pinform(player, " &f&r/setaddr <PostOffice> <address>");
        Util.pinform(player, " &7&oDefine route from local post office to address");
        Util.pinform(player, " &f&r/setroute <PostOffice> <address>");
        Util.pinform(player, " &7&oAsign ownership of post office, or address");
        Util.pinform(player, " &f&r/setowner [PostOffice] [address] <player>");
        Util.pinform(player, " &7&oHighlight route waypoints");
        Util.pinform(player, " &f&r/showroute <PostOffice> <address> &7&o(alias: /sr)");
        Util.pinform(player, " &7&oOpen local post office");
        Util.pinform(player, " &f&r/openlocal <PostOffice>");
        Util.pinform(player, " &7&oClose local post office");
        Util.pinform(player, " &f&r/closelocal <PostOffice>");
        Util.pinform(player, " &7&oOpen local address");
        Util.pinform(player, " &f&r/openaddr <PostOffice> <address>");
        Util.pinform(player, " &7&oClose local address");
        Util.pinform(player, " &f&r/closeaddr <PostOffice> <address>");
        Util.pinform(player, " &7&oDelete local post office");
        Util.pinform(player, " &f&r/deletelocal <PostOffice>");
        Util.pinform(player, " &7&oDelete address");
        Util.pinform(player, " &f&r/deleteaddr <PostOffice> <address>");
        Util.pinform(player, " &7&o(Press '/' and 'PgUp' to review this list)");
    }

    public static void menu_owner(Player player) {
        Util.pinform(player, " &7&oPostal commands; chests with Postal Logs are mail boxes.");
        Util.pinform(player, " &7&oUse book and quill to write letters");
        Util.pinform(player, " &7&oTeleport to a Postal location");
        Util.pinform(player, " &f&r/go [PostOffice] [address] &7&o(no parms = Central)");
        Util.pinform(player, " &7&oAddress a book in your hand");
        Util.pinform(player, " &f&r/addr <PostOffice> <address> [player]");
        Util.pinform(player, " &7&oSet Attention: (to) of addressed book in your hand");
        Util.pinform(player, " &f&r/att <player>");
        Util.pinform(player, " &7&oPrepare a package for shipment");
        Util.pinform(player, " &f&r/package <PostOffice> <address> [player] &7&o(alias: /pk)");
        Util.pinform(player, " &7&oMake a shipping label C.O.D.");
        Util.pinform(player, " &f&r/cod <price>");
        Util.pinform(player, " &7&oAccept shipment - shipping label in your hand");
        Util.pinform(player, " &f&r/accept");
        Util.pinform(player, " &7&oRefuse shipment - shipping label in your hand");
        Util.pinform(player, " &f&r/refuse");
        Util.pinform(player, " &7&oList towns with post offices");
        Util.pinform(player, " &f&r/tlist &7&o(alias: /tl)");
        Util.pinform(player, " &7&oList addresses in a town");
        Util.pinform(player, " &f&r/alist <PostOffice> &7&o(alias: /al)");
        Util.pinform(player, " &7&oList players that have been on server");
        Util.pinform(player, " &f&r/plist <string match> &7&o(alias: /pl)");
        Util.pinform(player, " &7&oSet your compass to a post office or address");
        Util.pinform(player, " &f&r/gps <PostOffice> [Address] &7&o(alias: /gpsp)");
        Util.pinform(player, " &7&oPush a route schedule forward");
        Util.pinform(player, " &f&r/expedite <PostOffice> <address> &7&o(alias: /ex)");
        Util.pinform(player, " &7&oSet a local post office");
        Util.pinform(player, " &f&r/setlocal <PostOffice>");
        Util.pinform(player, " &7&oDefine route from local post office to address");
        Util.pinform(player, " &f&r/setroute <PostOffice> <address>");
        Util.pinform(player, " &7&oHighlight route waypoints");
        Util.pinform(player, " &f&r/showroute <PostOffice> <address> &7&o(alias: /sr)");
        Util.pinform(player, " &7&oOpen local post office");
        Util.pinform(player, " &f&r/openlocal <PostOffice>");
        Util.pinform(player, " &7&oClose local post office");
        Util.pinform(player, " &f&r/closelocal <PostOffice>");
        Util.pinform(player, " &7&oOpen local address");
        Util.pinform(player, " &f&r/openaddr <PostOffice> <address>");
        Util.pinform(player, " &7&oClose local address");
        Util.pinform(player, " &f&r/closeaddr <PostOffice> <address>");
        Util.pinform(player, " &7&o(Press '/' and 'PgUp' to review this list)");
    }

    public static void menu_player(Player player) {
        Util.pinform(player, " &7&oPostal commands; chests with Postal Logs are mail boxes.");
        Util.pinform(player, " &7&oUse book and quill to write letters");
        Util.pinform(player, " &7&oTeleport to a Postal location");
        Util.pinform(player, " &f&r/go [PostOffice] [address] &7&o(no parms = Central)");
        Util.pinform(player, " &7&oAddress a book in your hand:");
        Util.pinform(player, " &f&r/addr <PostOffice> <address> [player]");
        Util.pinform(player, " &7&oSet Attention: (to) of addressed book in your hand");
        Util.pinform(player, " &f&r/att <player>");
        Util.pinform(player, " &7&oPrepare a package for shipment");
        Util.pinform(player, " &f&r/package <PostOffice> <address> [player] &7&o(alias: /pk)");
        Util.pinform(player, " &7&oMake a shipping label C.O.D.");
        Util.pinform(player, " &f&r/cod <price>");
        Util.pinform(player, " &7&oAccept shipment - shipping label in your hand");
        Util.pinform(player, " &f&r/accept");
        Util.pinform(player, " &7&oRefuse shipment - shipping label in your hand");
        Util.pinform(player, " &f&r/refuse");
        Util.pinform(player, " &7&oList towns with post offices");
        Util.pinform(player, " &f&r/tlist &7&o(alias: /tl)");
        Util.pinform(player, " &7&oList addresses in a town");
        Util.pinform(player, " &f&r/alist <PostOffice> &7&o(alias: /al)");
        Util.pinform(player, " &7&oList players that have been on server");
        Util.pinform(player, " &f&r/plist <string match> &7&o(alias: /pl)");
        Util.pinform(player, " &7&oSet your compass to a post office or address");
        Util.pinform(player, " &f&r/gps <PostOffice> [Address] &7&o(alias: /gpse)");
        Util.pinform(player, " &7&o(Press '/' and 'PgUp' to review this list)");
    }

    public static void setroute_worker(Player player, String stown, String saddress) {
        VA_postal.plistener_local_po = stown;
        VA_postal.plistener_address = saddress;
        RouteEditor.place_route_markers(stown, saddress);
        if (C_Route.is_waypoint_defined(stown, saddress, 1)) {
            VA_postal.plistener_newroute = false;
            Location player_loc = Util.simplified_copy(player.getLocation());
            int wypnt_count = C_Route.route_waypoint_count(stown, saddress);
            String test_loc;
            double test_dist;
            double saved_dist = 1000.0D;
            int saved_pos = -1;
            String saved_loc = "";
            for (int i = 0; i < wypnt_count; i++) {
                test_loc = C_Route.get_waypoint_location(stown, saddress, i);
                test_dist = Util.get_3d_distance(player_loc, test_loc);
                if (test_dist < saved_dist) {
                    saved_dist = test_dist;
                    saved_loc = test_loc;
                    saved_pos = i;
                }
            }
            if (saved_dist > 20.0D) {
                Util.safe_tps(player, saved_loc);
                Util.pinform(player, "&6You have been teleported to the closest waypoint.");
                Util.pinform(player, "&7&oExisting route. You are on waypoint: &r" + Util.int2str(saved_pos));
            }
        } else {
            VA_postal.plistener_newroute = true;
            Util.pinform(player, "&7&oNew route. Teleporting you to: &r" + Util.df(stown));
            String slocation = C_Postoffice.get_local_po_location_by_name(stown);
            Util.safe_tps(player, slocation);
        }
        C_Dispatcher.open_address(stown, saddress, false);
        try {
            VA_Timers.routeRditor_start(true, player);
        } catch (IllegalPluginAccessException ignored) {
        }
        Util.pinform(player, "&7&oAddress: &r" + Util.df(stown) + "/" + Util.df(saddress));
        Util.pinform(player, "&7&oSelect waypoints to the residence with left mouse clicks.");
        Util.pinform(player, "&7&oRight mouse click to back up, or undo a point");
        Util.pinform(player, "&7&oDouble click on final point or '/exit' to finish");

        String saddr_location = C_Address.get_address_location(stown, saddress);
        player.setCompassTarget(Util.str2location(saddr_location));
        Util.pinform(player, "&7&oYour compass has been set to the address.");

        if (VA_postal.plistener_quickbar == null) VA_postal.plistener_quickbar = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            VA_postal.plistener_quickbar[i] = player.getInventory().getItem(i);
            player.getInventory().setItem(i, null);
        }

        player.getInventory().setItem(4, new ItemStack(345));

        RouteEditor.create_hud(stown, saddress);

        RouteEditor.startHighlighter();
    }

    public static void validate_route(Player player, String stown, String saddress) {
        boolean waypoint_exist = C_Route.is_waypoint_defined(stown, saddress, 0);
        if (!waypoint_exist) {
            C_Dispatcher.open_address(stown, saddress, false);
            Util.cinform("There is no route " + stown + ", " + saddress + ", closing route.");
        }
    }

    public static boolean accept_worker(Player player, ItemStack stack, double price) {
        String splayer = player.getName().trim();
        Block block = ChestManip.parcel_place_chest_accept(player);
        if (block == null) {
            Util.pinform(player, "&7&oUnable to place parcel here.");
            return false;
        }

        if (price > 0.0D) {
            Player sender = cod_sender(stack);
            if (sender != null) {
                P_Economy.charge_player(player, price);
                P_Economy.pay_player(sender, price);
            } else Util.cinform("[Postal] Unable charge for COD.");
        }

        Inventory inventory = BookManip.parcel_fill_chest(block, stack);
        if (inventory == null) {
            Util.pinform(player, "&7&oUnable to complete shipment.");
            return false;
        }

        ItemStack stamped = BookManip.stamp_parcel_statement(player, stack, true);
        player.setItemOnCursor(stamped);
        return true;
    }

    public static void cod_worker(Player player, ItemStack stack, double cod_price) {
        boolean no_charge = false;
        if (cod_amount(stack) > 0.0D) {
            no_charge = true;
            Util.pinform(player, "&7&oThere is no charge to change COD amount..");
        }
        ItemStack stamped = stamp_cod(stack, cod_price);
        if (stamped == null) return;
        stack = null;
        player.setItemInHand(stamped);
        if (!no_charge) P_Economy.charge_cod_surcharge(player);
    }

    public static ItemStack stamp_cod(ItemStack ind_item, double price) {
        Book book = new Book(ind_item);
        if (!book.is_valid()) return null;
        String title = book.getTitle();
        String author = book.getAuthor();
        String[] pages = book.getPages();
        if ((pages == null) || (pages.length < 1)) return null;
        String[] parts = pages[0].split("\n");
        if ((parts == null) || (parts.length < 12)) return null;
        parts[11] = (cod_prepend + ef(price));
        pages[0] = "";
        for (String part : parts) pages[0] = (pages[0] + part + "\n");
        Book stamped_book = new Book(title, author, pages);
        return stamped_book.generateItemStack();
    }

    public static double cod_amount(ItemStack ind_item) {
        Book book = new Book(ind_item);
        if (!book.is_valid()) return 0.0D;
        String title = book.getTitle();
        String author = book.getAuthor();
        String[] pages = book.getPages();
        book = null;
        if ((pages == null) || (pages.length < 1)) return 0.0D;
        String[] parts = pages[0].split("\n");
        if ((parts == null) || (parts.length < 12)) return 0.0D;
        String sraw = parts[11].trim();
        if (sraw.contains("§7.")) return 0.0D;
        if (sraw.contains(cod_prepend)) {
            String sprice = sraw.replace(cod_prepend, "");
            sprice = sprice.replace(",", "").trim();
            return Util.str2double(sprice);
        }
        return 0.0D;
    }

    public static Player cod_sender(ItemStack ind_item) {
        Book book = new Book(ind_item);
        if (!book.is_valid()) return null;
        String title = book.getTitle();
        String author = book.getAuthor();
        String[] pages = book.getPages();
        if ((pages == null) || (pages.length < 1)) return null;
        return book.extractEmbeddedAuthor();
    }

    public static boolean parcel_worker(Player player, String attention, Player Attention, String stown, String saddress) {
        Block block = ChestManip.get_parcel_chest(player);
        String slocation = Util.location2str(block.getLocation());

        String[] parts = slocation.split(",");
        String sworld = parts[0].trim();
        String scoords = parts[1].trim() + "," + parts[2].trim() + "," + parts[3].trim();
        String chest_dir = "";
        Chest chest = (Chest) block.getState();
        if (chest == null) return false;
        byte c_data = block.getData();
        Inventory inventory = chest.getInventory();

        if (inventory.getSize() > 27) {
            Util.pinform(player, "&7&oDouble chests are not allowed.");
            return false;
        }

        String[] item_list = BookManip.parcel_list(inventory);
        if (item_list == null) {
            Util.pinform(player, "&7&oNothing in the chest to ship.");
            return false;
        }
        String[] pages = new String[49];
        pages[0] = ("§7" + sworld + "\n");
        pages[0] = (pages[0] + scoords + "," + c_data + "\n\n");
        int line = 3;
        int page = 0;

        for (String anItem_list : item_list) {
            if (anItem_list == null) break;
            pages[page] = (pages[page] + anItem_list + "\n");
            if (line > 8) {
                page++;
                pages[page] = "";
                line = 0;
            } else line++;
        }

        if (line > 6) {
            page++;
            pages[page] = "";
        }
        pages[page] = (pages[page] + "\n§2/accept\n");
        pages[page] = (pages[page] + "§c/refuse\n");
        page++;

        String[] message_pages = BookManip.parcel_pages(player, inventory);
        if (message_pages != null) for (String message_page : message_pages) {
            pages[page] = message_page;
            page++;
            if (page >= 9) break;
        }

        while (page < pages.length) {
            pages[page] = "";
            page++;
        }

        System.arraycopy(item_list, 50, pages, 9, 27);


        String splayer = player.getName().trim();
        if (splayer.length() > 15) splayer = splayer.substring(0, 15);
        Book new_book = new Book("[shipping label]", splayer, pages);
        ItemStack new_stack = new_book.generateItemStack();
        addr_worker(player, inventory, new_stack, attention, Attention, stown, saddress);
        BookManip.standard_addr_sign(slocation, 2, stown, saddress, splayer);
        return true;
    }

    public static void addr_worker(Player player, Inventory inventory, ItemStack stack, String attention, Player OriginalPlayerAttention, String stown, String saddress) {
        Book book = new Book(stack);
        String title;
        String author;
        String town;
        String address;
        String[] existing_pages;

        String page1 = book.getPage(1);
        boolean re_address = false;
        if (page1.contains("[not-processed]")) {
            re_address = true;
            town = Util.df(stown);
            address = Util.df(saddress);
            String[] parts = page1.split("\n");
            title = Util.df(parts[10].substring(2).trim());
            author = parts[9].substring(2).trim();
            existing_pages = book.getPages();
        } else {
            title = proper(book.getTitle());
            author = book.getAuthor();
            town = Util.df(stown);
            address = Util.df(saddress);
            existing_pages = book.getPages_with_blank_first_page();
        }

        Player pauthor = Util.str2player(author);

        Format formatter = new SimpleDateFormat("MM/dd/yy HH:mm");
        Date date = new Date();
        String fdate = formatter.format(date);

        existing_pages[0] = Book.makeFirstMailPage(town, address, attention, null, null, author, title, fdate, pauthor, OriginalPlayerAttention);

        Book new_book = new Book(town, address, existing_pages);
        ItemStack new_stack = new_book.generateItemStack();
        if ((inventory != null) &&
                (title.equals(Util.df("[shipping label]")))) {

            ItemStack holding = player.getItemOnCursor();
            if (holding.getType() != Material.AIR) {
                player.getWorld().dropItemNaturally(player.getLocation(), player.getItemOnCursor());
                Util.pinform(player, "&9The item you were holding has been dropped.");
            }
        }

        player.setItemOnCursor(new_stack);
        if (VA_postal.economy_configured)
            if (inventory == null) if (!re_address) P_Economy.charge_postage(player, stown);
            else Util.pinform(player, "&6There is no charge for re-addressing.");
            else P_Economy.charge_shipping(player, stown);
        Util.pinform(player, "&7&oTitle &9&o" + title + " &7&oAuthor &9&o" + author);
        Util.pinform(player, "&7&oAddressed to &9&o" + Util.df(saddress) + " &7&otown of &9&o" + Util.df(stown));
        Util.pinform(player, "&7&oAttention: &9&o" + attention);
    }

    public static void distr_worker(Player player, ItemStack stack, String sdistribution, String srch_stown, int iexpiration) {
        if ("[all]".equals(sdistribution)) sdistribution = "all_addresses";
        if ("[all]".equals(srch_stown)) srch_stown = "all_towns";

        Book book = new Book(stack);
        String title = proper(book.getTitle());
        String author = book.getAuthor();
        String[] existing_pages = book.getPages_with_blank_first_page();


        Format formatter = new SimpleDateFormat("MM/dd/yy HH:mm");
        Date date = new Date();
        String fdate = formatter.format(date);

        Player pauthor = Util.str2player(author);

        existing_pages[0] = Book.makeFirstMailPage(Util.df(sdistribution), Util.df(srch_stown), "Resident", "[Central]", "[PostMaster]", author, title, fdate, pauthor, null);

        int seconds_per_day = 86400;
        int exp_factor = seconds_per_day * iexpiration;
        long expiration = System.currentTimeMillis() / 1000L + exp_factor;
        String s_expiration;
        try {
            s_expiration = Long.toString(expiration).trim();
        } catch (Exception e) {
            s_expiration = "0000000000";
        }

        Book new_book = new Book("[Distribution]", s_expiration, existing_pages);
        ItemStack dist_stack = new_book.generateItemStack();
        player.setItemOnCursor(null);


        String[] town_list = C_Arrays.town_list();
        if (town_list == null) {
            Util.cinform("Problem getting town array.");
            return;
        }
        Util.pinform(player, "Distribution: " + s_expiration);
        for (String stown : town_list) {
            String[] addr_list = C_Arrays.addresses_list(stown);
            if (addr_list == null) Util.cinform("Problem getting address array.");
            else
                for (String saddress : addr_list) {
                    Player owner = VA_postal.SERVER;
                    if (("all_addresses".equalsIgnoreCase(sdistribution)) ||
                            (C_Owner.is_address_owner_defined(stown, saddress)))
                        if (("all_towns".equalsIgnoreCase(srch_stown)) ||
                                (stown.equalsIgnoreCase(srch_stown))) {


                            if (C_Owner.is_address_owner_defined(stown, saddress))
                                owner = C_Owner.get_owner_address(stown, saddress);


                            String search_location = Util.put_point_on_ground(C_Address.get_address_location(stown, saddress), false);
                            if ("null".equals(search_location)) Util.cinform("Problem getting search location.");
                            else {
                                Location p_search_location = Util.str2location(search_location);
                                p_search_location.subtract(0.0D, 1.0D, 0.0D);
                                Block b_sign_search = SignManip.LookForSignChest(p_search_location, 10, "[Postal_Mail]", stown, saddress, null);
                                String details = "[" + Util.df(stown) + ", " + Util.df(saddress) + ", " + owner + "]&7";
                                details = " " + fixed_len(details, 45, "-");
                                if (b_sign_search != null) {
                                    Chest chest = (Chest) b_sign_search.getState();
                                    if ((chest == null) || (!ChestManip.is_chest(chest.getType())))
                                        Util.cinform("Problem with chest definition");
                                    else if (add_to_mailbox(chest, dist_stack))
                                        Util.pinform(player, details + " Delivered");
                                    else Util.pinform(player, details + " No Room");
                                } else Util.pinform(player, details + " No Mailbox");
                            }
                        }
                }
        }
        if (VA_postal.economy_configured) P_Economy.charge_distr(player, sdistribution, srch_stown);
    }

    public static void place_new_mail_marker(Location location) {
        World w = location.getWorld();
        Block block = w.getBlockAt(location);
        if (block == null) return;
        String stitle = "§c[Postal_Mail]";

        SignManip.edit_sign_id_chest(block, stitle, null, null, null);
    }

    public static boolean add_to_mailbox(Chest chest, ItemStack book_item) {
        if ((book_item == null) || (chest == null)) return false;
        Inventory inventory = chest.getInventory();

        if (inventory.firstEmpty() != -1) {
            inventory.addItem(book_item);
            return true;
        }

        ListIterator item_itr = inventory.iterator(0);
        if (item_itr != null) {
            int index = 0;
            while (item_itr.hasNext()) {
                ItemStack ind_item = (ItemStack) item_itr.next();
                if ((ind_item == null) || (ind_item.getTypeId() == 0)) {
                    inventory.setItem(index, book_item);
                    return true;
                }
                index++;
            }
        }

        item_itr = null;
        item_itr = inventory.iterator(0);
        if (item_itr != null) {
            int index = 0;
            while (item_itr.hasNext()) {
                ItemStack ind_item = (ItemStack) item_itr.next();
                if ((ind_item != null) && (ind_item.getType() != Material.WRITTEN_BOOK)) {
                    inventory.setItem(index, book_item);
                    return true;
                }
                index++;
            }
        }

        item_itr = null;
        item_itr = inventory.iterator(0);
        long age_save = 0L;
        int index_save = -1;
        if (item_itr != null) {
            int index = 0;
            while (item_itr.hasNext()) {
                ItemStack ind_item = (ItemStack) item_itr.next();
                if ((ind_item != null) && (ind_item.getType() == Material.WRITTEN_BOOK)) {
                    Book book = new Book(ind_item);
                    if (book.is_valid()) {
                        String mail_title = book.getTitle().toLowerCase().trim();
                        if (mail_title.contains("[distribution]")) {
                            long dist_age = val(book.getAuthor().trim());
                            if (age_save == 0L) {
                                age_save = dist_age;
                                index_save = index;
                            } else if (dist_age < age_save) {
                                age_save = dist_age;
                                index_save = index;
                            }
                        }

                        book = null;
                    }
                }
                index++;
            }
        }
        if (index_save >= 0) {
            inventory.setItem(index_save, book_item);
            return true;
        }

        return false;
    }

    public static long val(String sinput) {
        long result;
        try {
            result = Long.parseLong(sinput);
        } catch (NumberFormatException numberFormatException) {
            result = -1L;
        }
        return result;
    }

    public static boolean att_worker(boolean create, Player player, ItemStack stack, String attention, Player original) {
        Book book = new Book(stack);
        String title;
        String author;
        Player pauthor;
        String town;
        String address;
        String[] existing_pages;

        String page1 = book.getPage(1);
        boolean re_address = false;
        if (page1.contains("[not-processed]")) {
            String[] parts = page1.split("\n");
            town = Util.df(parts[1].substring(2).trim());
            address = Util.df(parts[2].substring(2).trim());
            title = proper(parts[10].substring(2).trim());
            author = parts[9].substring(2).trim();
            pauthor = book.extractEmbeddedAuthor();
            existing_pages = book.getPages();
        } else {
            Util.pinform(player, "&7&oThis command is meant to easily re-set 'Attention to:'");
            Util.pinform(player, "&7&oYou must have an addressed book in your hand.");
            Util.pinform(player, "&7&oUse &f&raddr <Town> <Address> [player]");
            return false;
        }

        if (create) {
            Format formatter = new SimpleDateFormat("MM/dd/yy HH:mm");
            Date date = new Date();
            String fdate = formatter.format(date);
            String spage = "";
            spage = spage + "§7§oTo:\n";
            spage = spage + "§c " + town + "\n";
            spage = spage + "§9 " + address + "\n";
            spage = spage + "§7§oAttention:\n";
            spage = spage + "§2 " + attention + "\n";
            spage = spage + "§7§oMailed from:\n";
            spage = spage + "§7 [not-processed]\n";
            spage = spage + "§7 [not-processed]\n";
            spage = spage + "§7§oWritten by:\n";
            spage = spage + "§8 " + author + "\n";
            spage = spage + "§8 " + title + "\n";
            spage = spage + "§7 \n";
            spage = spage + "§7" + fdate + "\n";
            existing_pages[0] = spage;
            Book new_book = new Book(town, address, existing_pages);
            ItemStack new_stack = new_book.generateItemStack();
            player.setItemInHand(new_stack);
            Util.pinform(player, "&7&oTitle &9&o" + title + " &7&oAuthor &9&o" + author);
            Util.pinform(player, "&7&oAddressed to &9&o" + Util.df(address) + " &7&otown of &9&o" + Util.df(town));
            Util.pinform(player, "&7&oAttention: &9&o" + attention);
        }
        return true;
    }

    public static void owneraddr_worker(Player player, String stown, Player subject, String saddress) {
        if (VA_postal.economy_configured) {
            double cost;
            cost = P_Economy.charge_addr_purchase(player, subject, stown, saddress);
            if (cost > 0.0D) if (player == null) Util.con_type(subject + " charged " + ef(cost));
            else Util.pinform(player, subject + " charged " + ef(cost));
        }
        if (subject != null) {
            C_Owner.set_owner_address(stown, saddress, subject);
            if (player == null) Util.con_type(Util.df(stown) + ", " + Util.df(saddress) + " now owned by " + subject);
            else Util.pinform(player, Util.df(stown) + ", " + Util.df(saddress) + " now owned by " + subject);
        } else {
            C_Owner.del_owner_address(stown, saddress);
            if (player == null) Util.con_type("Owner removed from: " + Util.df(stown) + ", " + Util.df(saddress));
            else Util.pinform(player, "Owner removed from: " + Util.df(stown) + ", " + Util.df(saddress));
        }
    }

    public static void ownerlocal_worker(Player player, String stown, Player subject) {
        if (VA_postal.economy_configured) {
            double cost;

            cost = P_Economy.charge_po_purchase(player, subject, stown);
            if (cost > 0.0D) if (player == null) Util.con_type(subject + " charged " + ef(cost));
            else Util.pinform(player, subject.getDisplayName() + " charged " + ef(cost));
        } else if (subject != null) {
            C_Owner.set_owner_local_po(stown, subject);
            if (player == null) Util.con_type(Util.df(stown) + " is now owned by " + subject.getDisplayName());
            else Util.pinform(player, Util.df(stown) + " is now owned by " + subject.getDisplayName());
        } else {
            if (C_Owner.is_local_po_owner_defined(stown)) C_Owner.del_owner_local_po(stown);
            if (player == null) Util.con_type("Owner removed from: " + Util.df(stown));
            else Util.pinform(player, "Owner removed from: " + Util.df(stown));
        }
    }

    public static String ef(double value) {
        if (VA_postal.economy_configured) return VA_postal.econ.format(value);
        return "-1";
    }

    public static String stime_stamp() {
        try {
            long time = System.currentTimeMillis() / 1000L;
            return Long.toString(time);
        } catch (Exception e) {
        }
        return "null";
    }

    public static int elapse_seconds(String stime) {
        long current_time;
        long saved_time;
        try {
            saved_time = Long.parseLong(stime);
        } catch (NumberFormatException numberFormatException) {
            return -1;
        }
        try {
            current_time = System.currentTimeMillis() / 1000L;
        } catch (Exception e) {
            return -1;
        }
        return (int) (current_time - saved_time);
    }

    public static String proper(String string) {
        try {
            if (string.length() > 0)
                return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase().trim();
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public static synchronized String fixed_len(String input, int len, String filler) {
        try {
            input = input.trim();

            if (input.length() >= len) return input.substring(0, len);

            while (input.length() < len) input = input + filler;
            return input;
        } catch (Exception e) {
            String blank = "";
            for (int i = 0; i < len; i++) blank = blank + filler;
            return blank;
        }
    }
}
