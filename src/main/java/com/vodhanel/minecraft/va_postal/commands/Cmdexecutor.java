package com.vodhanel.minecraft.va_postal.commands;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.*;
import com.vodhanel.minecraft.va_postal.config.*;
import com.vodhanel.minecraft.va_postal.listeners.RouteEditor;
import com.vodhanel.minecraft.va_postal.mail.BookManip;
import com.vodhanel.minecraft.va_postal.mail.ChestManip;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cmdexecutor implements org.bukkit.command.CommandExecutor {
    public static Player player;
    public static VA_postal plugin;
    public static String[][] command_confirm = new String[50][3];
    public static boolean confirm_queue_dirty = false;
    boolean result = false;

    public Cmdexecutor(VA_postal plugin) {
        this.plugin = plugin;
    }

    public static boolean hasPermission(Player player, String node) {
        String splayer = player.getName().toLowerCase().trim();

        if (VA_postal.perms != null) {
            if (VA_postal.perms.has(player, "postal.admin")) {
                Util.perm_inform("Vault: postal.admin, " + splayer);
                return true;
            }
            if (VA_postal.perms.has(player, node)) {
                Util.perm_inform("Vault: " + node + ", " + splayer);
                return true;
            }
        } else {
            if (player.hasPermission("postal.admin")) {
                Util.perm_inform("Bukkit: postal.admin");
                return true;
            }
            if (player.hasPermission(node)) {
                Util.perm_inform("Bukkit: " + node + ", " + splayer);
                return true;
            }
        }

        if ((VA_postal.using_towny()) &&
                (P_Towny.is_towny_admin_by_loc(player))) {
            Util.perm_inform("Towny admin, " + splayer);
            return true;
        }


        return false;
    }

    public static boolean hasPermission_ext(Player player, String node, String stown, String saddress) {
        String splayer = player.getName().toLowerCase().trim();

        if (VA_postal.perms != null) {
            if (VA_postal.perms.has(player, "postal.admin")) {
                Util.perm_inform("Vault: postal.admin, " + splayer);
                return true;
            }
            if (VA_postal.perms.has(player, node)) {
                Util.perm_inform("Vault: " + node + ", " + splayer);
                return true;
            }
        } else {
            if (player.hasPermission("postal.admin")) {
                Util.perm_inform("Bukkit: postal.admin, " + splayer);
                return true;
            }
            if (player.hasPermission(node)) {
                Util.perm_inform("Bukkit: " + node + ", " + splayer);
                return true;
            }
        }


        if ((VA_postal.using_towny()) &&
                (P_Towny.is_towny_admin_by_loc(player))) {
            Util.perm_inform("Towny admin, " + splayer);
            return true;
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
                (VA_postal.using_towny()) &&
                (P_Towny.is_towny_admin_by_db(splayer, stown))) {
            Util.perm_inform("Towny ranked assistant, " + splayer + ", " + stown);
            return true;
        }


        if ((stown != null) && (!"null".equals(stown)) &&
                (saddress != null) && (!"null".equals(saddress)) &&
                (VA_postal.using_towny()) &&
                (P_Towny.is_towny_plot_owner_by_db(splayer, stown, saddress))) {
            Util.perm_inform("Towny plot owner, " + splayer + stown + saddress);
            return true;
        }


        return false;
    }

    public static boolean postal(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length == 0) {
            if (hasPermission(player, "postal.admin")) {
                Cmd_static.menu_admin(player);
                return true;
            }

            if (C_Owner.does_player_own_anything(player)) {
                Cmd_static.menu_owner(player);
                return true;
            }

            Cmd_static.menu_player(player);
            return true;
        }
        if ("speed".equals(args[0].toLowerCase().trim())) {
            if (!hasPermission(player, "postal.admin")) {
                Util.pinform(player, "Required permission not present.");
                return true;
            }
            if (args.length < 2) {
                Util.pinform(player, "&7&oUsage: &f&r/postal speed <0.5 - 2.0>    &7&oNPC walking speed factor.");
                Util.pinform(player, "&7&cCurrent speed factor:  " + VA_postal.wtr_speed);
                return true;
            }
            Float result = 1.0F;
            try {
                result = new Float(args[1]);
            } catch (NumberFormatException numberFormatException) {
                Util.pinform(player, "&7&cProblem with the number you used.");
                return true;
            }
            if ((result.floatValue() < 0.5F) || (result.floatValue() > 2.0F)) {
                Util.pinform(player, "&7&cSpeed must be 0.5 - 2.0");
                return true;
            }
            VA_postal.wtr_speed = result.floatValue();
            Util.pinform(player, "&7&cSpeed factor set to:  " + VA_postal.wtr_speed);
            return true;
        }
        if ("admin".equals(args[0].toLowerCase().trim())) {
            if (!hasPermission(player, "postal.admin")) {
                Util.pinform(player, "Required permission not present.");
                return true;
            }
            VA_postal.admin_overide = true;
            VA_postal.admin_overide_stamp = Util.time_stamp();
            Util.pinform(player, "Postal security for all users is off for 60 seconds.");
            return true;
        }
        if ("bypass".equals(args[0].toLowerCase().trim())) {
            if (!hasPermission(player, "postal.admin")) {
                Util.pinform(player, "Required permission not present.");
                return true;
            }
            VA_postal.admin_bypass = true;
            VA_postal.admin_bypass_stamp = Util.time_stamp();
            Util.pinform(player, "Postal admin privileges will be on for five minutes.");
            return true;
        }
        if ("stop".equals(args[0].toLowerCase().trim())) {
            if (!hasPermission(player, "postal.admin")) {
                Util.pinform(player, "Required permission not present.");
                return true;
            }
            if (!VA_Dispatcher.dispatcher_running) {
                Util.pinform(player, "&7&oDispatcher is not running.");
                return true;
            }
            VA_Dispatcher.cancel_dispatcher(false);
            Util.pinform(player, "VA_postal Stopped");
            return true;
        }
        if ("start".equals(args[0].toLowerCase().trim())) {
            if (!hasPermission(player, "postal.admin")) {
                Util.pinform(player, "Required permission not present.");
                return true;
            }
            VA_Dispatcher.start_up(false);
            if (!VA_postal.needs_configuration) {
                Util.pinform(player, "VA_postal Started");
            }
            return true;
        }
        if ("restart".equals(args[0].toLowerCase().trim())) {
            if (!hasPermission(player, "postal.admin")) {
                Util.pinform(player, "Required permission not present.");
                return true;
            }
            VA_Dispatcher.restart(false);
            Util.pinform(player, "VA_postal Re-Started");
            return true;
        }
        if ("chests".equals(args[0].toLowerCase().trim())) {
            if (!hasPermission(player, "postal.admin")) {
                Util.pinform(player, "Required permission not present.");
                return true;
            }
            Util.pinform(player, "Post office chests:     ");
            String town = fixed_len("Central", 20, " ");
            String slocation = VA_postal.central_schest_location;
            Util.pinform(player, "   " + town + slocation);
            for (int i = 0; i < VA_postal.wtr_count; i++) {
                town = fixed_len(Util.df(VA_postal.wtr_poffice[i]), 20, " ");
                slocation = VA_postal.wtr_schest_location_postoffice[i];
                Util.pinform(player, "   " + town + slocation);
            }
            return true;
        }
        if ("test".equals(args[0].toLowerCase().trim())) {
            return true;
        }

        return true;
    }

    public static boolean postal_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length == 0) {
            Util.con_type("Usage: postal  <start/stop/restart/admin/conc/expedite>");
            Util.con_type(".............  <quiet/talk/debug/rtalk/ctalk/cstalk/chunks>");
            Util.con_type(".............  <mtalk/qtalk/wtalk/chests/speed/showroute>");
            return true;
        }

        if ("conc".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.wtr_concurrent) {
                VA_postal.wtr_concurrent = false;
                Util.con_type("Post offices will run one at a time");
            } else {
                VA_postal.wtr_concurrent = true;
                Util.con_type("Post offices will run concurrently");
            }
            return true;
        }
        if ("admin".equals(args[0].toLowerCase().trim())) {
            VA_postal.admin_overide = true;
            VA_postal.admin_overide_stamp = Util.time_stamp();
            Util.con_type("Postal security for all users is off for 60 seconds.");
            return true;
        }
        if ("talk".equals(args[0].toLowerCase().trim())) {
            VA_postal.quiet = false;
            VA_postal.debug = false;
            VA_postal.routetalk = true;
            VA_postal.mailtalk = 1;
            VA_postal.queuetalk = false;
            VA_postal.centraltalk = true;
            return true;
        }
        if ("alltalk".equals(args[0].toLowerCase().trim())) {
            VA_postal.quiet = false;
            VA_postal.debug = true;
            VA_postal.routetalk = true;
            VA_postal.mailtalk = 1;
            VA_postal.queuetalk = true;
            VA_postal.centraltalk = true;
            VA_postal.cstalk = true;
            return true;
        }
        if ("speed".equals(args[0].toLowerCase().trim())) {
            if (args.length < 2) {
                Util.con_type("Usage: postal speed <0.5 - 2.0>    NPC walking speed factor.");
                Util.con_type("Current speed factor:  " + VA_postal.wtr_speed);
                return true;
            }
            Float result = Float.valueOf(1.0F);
            try {
                result = new Float(args[1]);
            } catch (NumberFormatException numberFormatException) {
                Util.con_type("Must be a floating point number.");
                return true;
            }
            if ((result.floatValue() < 0.5F) || (result.floatValue() > 2.0F)) {
                Util.con_type("Speed factor must be 0.5 - 2.0");
                return true;
            }
            VA_postal.wtr_speed = result.floatValue();
            Util.con_type("Speed factor set to:  " + VA_postal.wtr_speed);
            return true;
        }
        if ("chunks".equals(args[0].toLowerCase().trim())) {
            if (args.length < 1) {
                Util.con_type("Usage: 'postal chunks'    route chunks loaded.");
                return true;
            }
            Util.con_type("Requested numer of chunks loaded:  " + VA_postal.chunks_requested);
            Util.con_type("Actual numer of chunks loaded:     " + VA_postal.chunks_loaded);
            return true;
        }
        if ("quiet".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.quiet) {
                VA_postal.quiet = false;
                Util.con_type("Quiet toggled OFF");
            } else {
                VA_postal.quiet = true;
                Util.con_type("Quiet toggled ON");
            }
            return true;
        }
        if ("debug".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.debug) {
                VA_postal.debug = false;
                Util.con_type("Debug toggled OFF");
            } else {
                VA_postal.debug = true;
                Util.con_type("Debug toggled ON");
            }
            return true;
        }
        if ("ptalk".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.permtalk) {
                VA_postal.permtalk = false;
                Util.con_type("PermissionTalk toggled OFF");
            } else {
                VA_postal.permtalk = true;
                Util.con_type("PermissionTalk toggled ON");
            }
            return true;
        }
        if ("rtalk".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.routetalk) {
                VA_postal.routetalk = false;
                Util.con_type("RouteTalk toggled OFF");
            } else {
                VA_postal.routetalk = true;
                Util.con_type("RouteTalk toggled ON");
            }
            return true;
        }
        if ("ctalk".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.centraltalk) {
                VA_postal.centraltalk = false;
                Util.con_type("CentralTalk toggled OFF");
            } else {
                VA_postal.centraltalk = true;
                Util.con_type("CentralTalk toggled ON");
            }
            return true;
        }
        if ("cstalk".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.cstalk) {
                VA_postal.cstalk = false;
                Util.con_type("Chest Search Talk toggled OFF");
            } else {
                VA_postal.cstalk = true;
                Util.con_type("Chest Search Talk toggled ON");
            }
            return true;
        }
        if ("qtalk".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.queuetalk) {
                VA_postal.queuetalk = false;
                Util.con_type("QueueTalk toggled OFF");
            } else {
                VA_postal.queuetalk = true;
                Util.con_type("QueueTalk toggled ON");
            }
            return true;
        }
        if ("wtalk".equals(args[0].toLowerCase().trim())) {
            if (VA_postal.wdtalk) {
                VA_postal.wdtalk = false;
                Util.con_type("WatchDogTalk toggled OFF");
            } else {
                VA_postal.wdtalk = true;
                Util.con_type("WatchDogTalk toggled ON");
            }
            return true;
        }
        if ("stop".equals(args[0].toLowerCase().trim())) {
            if (!VA_Dispatcher.dispatcher_running) {
                Util.con_type("Dispatcher is not running.");
                return true;
            }
            VA_Dispatcher.cancel_dispatcher(false);

            return true;
        }
        if ("start".equals(args[0].toLowerCase().trim())) {
            if (VA_Dispatcher.dispatcher_running) {
                Util.con_type("Dispatcher is already running.");
                return true;
            }
            VA_Dispatcher.start_up(false);
            return true;
        }
        if ("restart".equals(args[0].toLowerCase().trim())) {
            VA_Dispatcher.restart(false);
            return true;
        }
        if ("chests".equals(args[0].toLowerCase().trim())) {
            Util.con_type("Post office chests:     ");
            String town = fixed_len("Central", 20, " ");
            String slocation = VA_postal.central_schest_location;
            Util.con_type("   " + town + slocation);
            for (int i = 0; i < VA_postal.wtr_count; i++) {
                town = fixed_len(Util.df(VA_postal.wtr_poffice[i]), 20, " ");
                slocation = VA_postal.wtr_schest_location_postoffice[i];
                Util.con_type("   " + town + slocation);
            }
            return true;
        }
        if ("mtalk".equals(args[0].toLowerCase().trim())) {
            if (args.length < 2) {
                Util.con_type("Usage: postal mtalk <0 - 2>");
                Util.con_type("  postal mtalk 0               No mail announcements.");
                Util.con_type("  postal mtalk 1               Recipients only (default).");
                Util.con_type("  postal mtalk 2               Broadcast all.");
                Util.con_type("Current mtalk value:  " + VA_postal.mailtalk);
                return true;
            }
            int result = -1;
            try {
                result = new Integer(args[1]).intValue();
            } catch (NumberFormatException numberFormatException) {
                Util.con_type("Must be 0, 1 or 2");
                return true;
            }
            if ((result < 0) || (result > 2)) {
                Util.con_type("Must be 0, 1 or 2");
                return true;
            }
            VA_postal.mailtalk = result;
            Util.con_type("mtalk changed to:  " + VA_postal.mailtalk);
            return true;
        }
        if ("test".equals(args[0].toLowerCase().trim())) {
            return true;
        }

        return true;
    }

    public static boolean go(boolean console, CommandSender sender, String cmd, String[] args) {
        player = (Player) sender;
        String help_msg = "&7&oUsage: &f&r/go [PostOffice] [Address] &7&oteleport to a postal location";
        if ((args.length > 0) && ((args[0].equalsIgnoreCase("help")) || (args[0].equals("?")))) {
            Util.pinform(player, help_msg);
            return true;
        }
        if (args.length == 0) {
            gotocentral(false, player, "gotocentral", args);
        } else if (args.length == 1) {
            gotolocal(false, player, "gotolocal", args);
        } else if (args.length == 2) {
            gotoaddress(false, player, "gotoaddr", args);
        } else {
            Util.pinform(player, help_msg);
        }
        return true;
    }

    public static boolean setowner(boolean console, CommandSender sender, String cmd, String[] args) {
        player = (Player) sender;
        String help_msg = "&7&oUsage: &f&r/setowner [PostOffice] [Address] <Player> &7&oasigm ownership";
        if ((args.length > 0) && ((args[0].equalsIgnoreCase("help")) || (args[0].equals("?")))) {
            Util.pinform(player, help_msg);
            return true;
        }
        if ((args.length == 0) || (args.length == 1)) {
            String subject;
            if (args.length == 0) {
                subject = "none";
            } else {
                subject = args[0];
            }
            int max_dist = VA_postal.allowed_geo_proximity;
            String[] geo_object = Util.proximity_object(player, max_dist);
            if (geo_object == null) {
                return true;
            }
            if (geo_object[1].equals("post_office")) {
                String[] args_new = new String[2];
                args_new[0] = geo_object[0];
                args_new[1] = subject;
                ownerlocal(false, player, "ownerlocal", args_new);
            } else {
                String[] args_new = new String[3];
                args_new[0] = geo_object[0];
                args_new[1] = geo_object[1];
                args_new[2] = subject;
                owneraddr(false, player, "owneraddr", args_new);
            }
        } else if (args.length == 2) {
            ownerlocal(false, player, "ownerlocal", args);
        } else if (args.length == 3) {
            owneraddr(false, player, "owneraddr", args);
        } else {
            Util.pinform(player, help_msg);
        }
        return true;
    }

    public static boolean expedite(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 2) {
            Util.pinform(player, "&7&oUsage: &f&r/expedite <PostOffice> <Address>    &7&oPush route schedule forward.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.pinform(player, "&7&oDispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for town.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.pinform(player, "&7&oNeed more letters for address.  See list:");
            return true;
        }
        if ("null".equals(com.vodhanel.minecraft.va_postal.config.C_Queue.get_queue_pair(stown, saddress))) {
            Util.pinform(player, "&7&oRoute is not active, restart Postal to activate.");
            return true;
        }
        if (!hasPermission_ext(player, "postal.expedite", stown, "null")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.promote_schedule(stown, saddress, 6000, true);
            Util.pinform(player, "&7&oPushing schedule forward: &r" + Util.df(stown) + ", " + Util.df(saddress));
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to expedite route: &9&o" + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "/expedite " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean expedite_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 2) {
            Util.con_type("Usage: /expedite <PostOffice> <Address>    Push route schedule forward.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.con_type("Dispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("Need more letters for town.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, true, stown, false);
            } else {
                Util.list_postal_tree(null, true, stown);
            }
            Util.con_type("Need more letters for address.  See list:");
            return true;
        }
        if ("null".equals(com.vodhanel.minecraft.va_postal.config.C_Queue.get_queue_pair(stown, saddress))) {
            Util.con_type("Route is not active, restart Postal to activate.");
            return true;
        }

        if (is_player_comfirmation_registered(null)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.promote_schedule(stown, saddress, 6000, true);
            Util.con_type("Pushing schedule forward: " + Util.df(stown) + ", " + Util.df(saddress));
            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to expedite route: " + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "expedite " + stown + " " + saddress;
            register_player_comfirmation(null, scommand);
        }
        return true;
    }

    public static boolean closelocal(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 1) {
            Util.pinform(player, "&7&oUsage: &f&r/closelocal <PostOffice>    &7&oClose local post office.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.pinform(player, "&7&oDispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        if (!hasPermission_ext(player, "postal.closelocal", stown, "null")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if (is_player_comfirmation_registered(player)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.open_poffice(stown, false);
            Util.pinform(player, "&7&oClosed post office: &9&o" + Util.df(stown));
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to close post office: &9&o" + Util.df(stown));

            String scommand = "/closelocal " + stown;
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean closelocal_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 1) {
            Util.con_type("Usage: closelocal <PostOffice>    Close a local post office.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.con_type("Dispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("Need more letters for post office.  See list:");
            return true;
        }
        if (is_player_comfirmation_registered(null)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.open_poffice(stown, false);
            Util.con_type("Closed post office: " + Util.df(stown));
            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to close post office: " + Util.df(stown));

            String scommand = "closelocal " + stown;
            register_player_comfirmation(null, scommand);
        }
        return true;
    }

    public static boolean closeaddr(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 2) {
            Util.pinform(player, "&7&oUsage: &f&r/closeaddr <PostOffice> <Address>    &7&oClose local address.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.pinform(player, "&7&oDispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.pinform(player, "&7&oNeed more letters for address.  See list:");
            return true;
        }
        if (!hasPermission_ext(player, "postal.closeaddr", stown, "null")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if (is_player_comfirmation_registered(player)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.open_address(stown, saddress, false);
            Util.pinform(player, "&7&oClosed address: &9&o" + Util.df(stown) + ", " + Util.df(saddress));
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to close address: &9&o" + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "/closeaddr " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean closeaddr_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 1) {
            Util.con_type("Usage: closeaddr <PostOffice> <Address>   Close a local address.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.con_type("Dispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("Need more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, true, stown, false);
            } else {
                Util.list_postal_tree(null, true, stown);
            }
            Util.con_type("Need more letters for address.  See list:");
            return true;
        }
        if (is_player_comfirmation_registered(null)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.open_address(stown, saddress, false);
            Util.con_type("Closed address: " + Util.df(stown) + ", " + Util.df(saddress));
            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to close address: " + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "closeaddr " + stown + " " + saddress;
            register_player_comfirmation(null, scommand);
        }
        return true;
    }

    public static boolean openaddr(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 2) {
            Util.pinform(player, "&7&oUsage: &f&r/openaddr <PostOffice> <Address>    &7&oOpen local address.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.pinform(player, "&7&oDispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.pinform(player, "&7&oNeed more letters for address.  See list:");
            return true;
        }
        if (!hasPermission_ext(player, "postal.openaddr", stown, "null")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if (is_player_comfirmation_registered(player)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.open_address(stown, saddress, true);
            Util.pinform(player, "&7&oOpened address: &9&o" + Util.df(stown) + ", " + Util.df(saddress));
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to open address: &9&o" + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "/openaddr " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean openaddr_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 1) {
            Util.con_type("Usage: openaddr <PostOffice> <Address>   Open a local address.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.con_type("Dispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("Need more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, true, stown, false);
            } else {
                Util.list_postal_tree(null, true, stown);
            }
            Util.con_type("Need more letters for address.  See list:");
            return true;
        }
        if (is_player_comfirmation_registered(null)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.open_address(stown, saddress, true);
            Util.con_type("Opened address: " + Util.df(stown) + ", " + Util.df(saddress));
            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to open address: " + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "openaddr " + stown + " " + saddress;
            register_player_comfirmation(null, scommand);
        }
        return true;
    }

    public static boolean openlocal(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 1) {
            Util.pinform(player, "&7&oUsage: &f&r/openlocal <PostOffice>    &7&oOpen local post office.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.pinform(player, "&7&oDispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        if (!hasPermission_ext(player, "postal.openlocal", stown, "null")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if (is_player_comfirmation_registered(player)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.open_poffice(stown, true);
            Util.pinform(player, "&7&oOpened post office: &9&o" + Util.df(stown));
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to open post office: &9&o" + Util.df(stown));

            String scommand = "/openlocal " + stown;
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean openlocal_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length < 1) {
            Util.con_type("Usage: openlocal <PostOffice>    Open a local post office.");
            return true;
        }
        if (!VA_Dispatcher.dispatcher_running) {
            Util.con_type("Dispatcher is not running.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("Need more letters for post office.  See list:");
            return true;
        }
        if (is_player_comfirmation_registered(null)) {
            com.vodhanel.minecraft.va_postal.config.C_Dispatcher.open_poffice(stown, true);
            Util.con_type("Opened post office: " + Util.df(stown));
            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to open post office: " + Util.df(stown));

            String scommand = "openlocal " + stown;
            register_player_comfirmation(null, scommand);
        }
        return true;
    }

    public static boolean setcentral(boolean console, CommandSender sender, String cmd, String[] args) {
        if (!hasPermission(player, "postal.setcentral")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length >= 1) || ((args.length >= 1) && ("?".equals(args[0])))) {
            Util.pinform(player, "&7&oUsage: &f&r/setcentral    &7&oSet Central the Post Office.");
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            String location = Util.get_str_sender_location(sender);
            VA_postal.plugin.getConfig().set("Postoffice.Central.Location", location);
            VA_postal.plugin.saveConfig();
            Util.pinform(player, "&7&oCentral Post Office set: &f&r" + location);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to locate the central post office.");

            String scommand = "/setcentral";
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean gotocentral(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.gotocentral")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length >= 1) || ((args.length >= 1) && ("?".equals(args[0])))) {
            Util.pinform(player, "&7&oUsage: &f&r/gotocentral    &7&oTeleport to the Central the Post Office.");
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            String slocation = C_Postoffice.get_central_po_location();
            Util.pinform(player, "&7&oTeleporting you to:  &f&r" + slocation);
            Util.safe_tps(player, slocation);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to teleport you to the central post office.");

            String scommand = "/gotocentral";
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean setlocal(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.setlocal")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_admin_by_loc(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length < 1) || ((args.length >= 1) && ("?".equals(args[0])))) {
            Util.pinform(player, "&7&oUsage: &f&r/setlocal <PostOffice>    &7&oSet a local Post Office.");
            Util.pinform(player, "&7&oNote:  at least one address must be defined for a post office.");
            Util.pinform(player, "&7&oSee &f&r/setaddr &7&oand &f&r/setroute &7&oto define a address.  Postal");
            Util.pinform(player, "&7&oneeds a minimum of one address per post office to start");
            return true;
        }


        if ((VA_postal.using_towny()) && (!P_Towny.set_local_ok(player))) {
            return true;
        }

        String stown = args[0].toLowerCase().trim();
        if (!C_Postoffice.is_local_po_name_defined(stown)) {
            stown = Util.name_validate(stown);
        } else {
            Util.pinform(player, "&7&oPost office name &f&r" + Util.df(stown) + " &7&oalready being used.");
            return true;
        }


        org.bukkit.Location new_location = player.getLocation();
        org.bukkit.Location tlocation = null;
        String stlocation = "";
        long distance = 0L;
        long max_dist = 0L;
        String max_addr = "";
        String display = "";
        String confirm = "&7&oReady to locate local post office: &9&o" + Util.df(stown);
        String[] addr_list = C_Arrays.addresses_list(stown);


        boolean new_po = false;
        org.bukkit.block.Block block = com.vodhanel.minecraft.va_postal.mail.ChestManip.getNearestGenericChest_to_player(player, 2);
        if ((addr_list != null) && (addr_list.length > 0)) {
            Util.pinform(player, "&7&oAddresses serviced by &f&r" + Util.df(stown));
            for (String anAddr_list : addr_list) {
                if ((C_Address.is_address_defined(stown, anAddr_list)) &&
                        (com.vodhanel.minecraft.va_postal.config.C_Route.is_waypoint_defined(stown, anAddr_list, 0))) {
                    stlocation = com.vodhanel.minecraft.va_postal.config.C_Route.get_waypoint_location(stown, anAddr_list, 0);
                    tlocation = Util.str2location(stlocation);
                    distance = (long) new_location.distance(tlocation);
                    if (distance > max_dist) {
                        max_dist = distance;
                        max_addr = Util.df(anAddr_list);
                    }
                    display = fixed_len(anAddr_list, 20, " ");
                    Util.pinform(player, "  &f&r" + display + distance);
                }
            }

            Util.pinform(player, "&7&oDistance from each beginning waypoint.");
            if (max_dist > 5L) {
                Util.pinform(player, "&c&oYou are too far from the start point of an address.");
                Util.pinform(player, "&c&oPlease refer to the list above..");
                return true;
            }
        } else {
            if (block == null) {
                Util.pinform(player, "&6Please place a chest first and stand in front of it.");
                return true;
            }
            if (!com.vodhanel.minecraft.va_postal.mail.ChestManip.ok_to_use_chest(block, true)) {
                Util.pinform(player, "&6This chest either has a sign on it, or is too close to a sign.");
                Util.pinform(player, "&6To use this chest, the sign must temporarily be removed.");
                return true;
            }
            new_po = true;
            confirm = "&7&oReady to locate new, local post office: &9&o" + Util.df(stown);
        }


        if (is_player_comfirmation_registered(player)) {
            String slocation = Util.location2str(new_location);
            slocation = Util.put_point_on_ground(slocation, false);
            C_Postoffice.save_local_postoffice(stown, slocation);
            if (new_po) {
                String schest_loc = Util.location2str(block.getLocation());
                BookManip.standard_addr_sign(schest_loc, 1, stown, "[Local]", null);
                Util.pinform(player, "&7&oPost office: " + Util.df(stown) + " has been created.");
                if (!C_Postoffice.init_local_postoffice(stown)) {
                    Util.pinform(player, "&c&oRan out of NPC slots for postmen.");
                    Util.pinform(player, "&c&oRestart the server to allocate more.");
                }
            } else {
                Util.pinform(player, "&7&oLocal post office set: &f&r" + Util.df(stown) + ", " + slocation);
            }

            if ((VA_postal.using_towny()) &&
                    (P_Towny.is_towny_admin_by_loc(player))) {
                String mayor = P_Towny.towny_mayor_by_loc(player.getLocation());
                if ((!mayor.equals("not_towny")) && (!mayor.equals("not_mayor"))) {
                    C_Owner.set_owner_local_po(stown, mayor);
                    Util.pinform(player, "&7&oTowny mayor: &f&r" + Util.df(mayor));
                }
            }

            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, confirm);

            String scommand = "/setlocal " + stown;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean gotolocal(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.gotolocal")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length < 1) || ((args.length >= 1) && ("?".equals(args[0])))) {
            Util.pinform(player, "&7&oUsage: &f&r/gotolocal <PostOffice>    &7&oTeleport to a local Post Office.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        if (is_player_comfirmation_registered(player)) {
            String slocation = C_Postoffice.get_local_po_location_by_name(stown);
            Util.pinform(player, "&7&oTeleporting you to: &f&r" + Util.df(stown) + ", " + slocation);
            Util.safe_tps(player, slocation);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to teleport you to: &f&r" + Util.df(stown));

            String scommand = "/gotolocal " + stown;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean setroute(boolean console, CommandSender sender, String cmd, String[] args) {
        if (VA_postal.plistener_player != null) {
            Util.pinform(player, "&7&oThe route editor is being used by: " + VA_postal.plistener_player.getName());
            // FIXME MAKE MULTIPLE ROUTE EDITING POSSIBLE
            return true;
        }

        if ((args.length == 1) && ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/setroute <PostOffice> <Player>");
            return true;
        }

        String stown = "";
        String saddress = "";
        if (args.length == 0) {

            String[] geo_addr = C_Arrays.geo_addr_list_sorted(player);
            if ((geo_addr != null) && (geo_addr.length > 0)) {
                String[] parts = geo_addr[0].split(",");
                if ((parts != null) && (parts.length == 4)) {
                    int dist = Util.str2int(parts[0]);
                    int max_dist = VA_postal.allowed_geo_proximity;
                    if (dist > max_dist) {
                        Util.pinform(player, "&7&oThere is no address within " + Util.int2str(max_dist) + " blocks.");
                        return true;
                    }
                    stown = parts[1];
                    saddress = parts[2];
                } else {
                    Util.pinform(player, "&7&oCould not find nearest address.");
                    return true;
                }
            } else {
                Util.pinform(player, "&7&oCould not obtain geo list.");
            }
        } else {
            stown = C_Postoffice.town_complete(args[0]);
            if ("null".equals(stown)) {
                if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                    P_Towny.list_towny_tree(player, false, null, false);
                } else {
                    Util.list_postal_tree(player, false, null);
                }
                Util.pinform(player, "&7&oPost office does not exist.  See list:");
                return true;
            }
            saddress = C_Address.addresses_complete(stown, args[1]);
            if ("null".equals(saddress)) {
                if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                    P_Towny.list_towny_tree(player, true, stown, false);
                } else {
                    Util.list_postal_tree(player, true, stown);
                }
                Util.pinform(player, "&7&oPostal address: " + Util.df(stown) + "/" + Util.df(args[1]) + " does not exist.");
                Util.pinform(player, "&7&oUse /setaddress to define it.");
                return true;
            }
        }

        if ((!hasPermission_ext(player, "postal.setroute", stown, saddress)) && (VA_postal.using_towny()) && (!P_Towny.is_towny_plot_owner_by_loc(player)) && (!P_Towny.is_towny_admin_by_loc(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            Cmd_static.setroute_worker(player, stown, saddress);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to enter route editor: &9&o" + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "/setroute " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean setaddress(boolean console, CommandSender sender, String cmd, String[] args) {
        if (args.length == 0) {
            Util.pinform(player, "&7&oUsage: &f&r/setaddr <PostOffice> <Address>   &7&oto set a postal address");
            Util.pinform(player, "&7&oThis command must be followed by the &f&r/setroute &7&ocommand to");
            Util.pinform(player, "&7&odefine the route to the post office.");
            return true;
        }
        Block block = ChestManip.getNearestGenericChest_to_player(player, 2);
        if (block == null) {
            Util.pinform(player, "&6Please place a chest first and stand in front of it.");
            return true;
        }
        if (!ChestManip.ok_to_use_chest(block, true)) {
            Util.pinform(player, "&6This chest either has a sign on it, or is too close to a sign.");
            Util.pinform(player, "&6To use this chest, the sign must temporarily be removed.");
            return true;
        }
        String input_addr;
        String stown;
        if (args.length == 1) {
            input_addr = args[0];
            String[] geo_addr = C_Arrays.geo_po_list_sorted(player);
            if ((geo_addr != null) && (geo_addr.length > 0)) {
                String[] parts = geo_addr[0].split(",");
                if ((parts != null) && (parts.length == 3)) {
                    int dist = Util.str2int(parts[0]);
                    if (dist > 500) {
                        Util.pinform(player, "&7&oThere is no post office within 500 blocks.");
                        return true;
                    }
                    stown = parts[1];
                } else {
                    Util.pinform(player, "&7&oCould not find nearest post office.");
                    return true;
                }
            } else {
                Util.pinform(player, "&7&oCould not obtain geo list.");
                return true;
            }
        } else {
            input_addr = args[1];
            stown = C_Postoffice.town_complete(args[0]);
            if ("null".equals(stown)) {
                if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                    P_Towny.list_towny_tree(player, false, null, false);
                } else {
                    Util.list_postal_tree(player, false, null);
                }
                Util.pinform(player, "&7&oPost office does not exist.  See list:");
                return true;
            }
        }

        if ((!hasPermission_ext(player, "postal.setaddr", stown, "null")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_admin_by_loc(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }


        if ((VA_postal.using_towny()) && (!P_Towny.set_addr_ok(player, stown))) {
            return true;
        }

        boolean new_addr = false;
        String saddress = C_Address.addresses_complete(stown, input_addr);
        if ("null".equals(saddress)) {
            saddress = input_addr.trim();

            saddress = Util.name_validate(saddress);
            new_addr = true;
        }


        if (saddress.equalsIgnoreCase(stown)) {
            Util.pinform(player, "&6Address name may not be the same as PO name.");
            return true;
        }


        if (C_Address.is_address_defined(stown, saddress)) {
            Util.pinform(player, "&7&oAddress has been defined for:  " + Util.df(stown) + ", " + Util.df(saddress));
            Util.pinform(player, "&7&oUse &f&r/setroute   &7&oto modify it");
            Util.pinform(player, "&7&oUse &f&r/deleteaddr &7&oto delete it");
            Util.pinform(player, "&7&oUse &f&r/gotoaddr &7&oto go to it");
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            C_Address.save_postal_address_by_player(player, stown, saddress);
            if (new_addr) {
                C_Address.init_address(stown, saddress);
                String slocation = Util.location2str(block.getLocation());
                BookManip.standard_addr_sign(slocation, 1, stown, saddress, null);
            }
            Cmd_static.validate_route(player, stown, saddress);
            deregister_player_comfirmation(player);
        } else {
            Util.dinform("ADDR " + stown + " -> " + saddress);
            Util.pinform(player, "&7&oReady to define new address: &9&o" + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "/setaddr " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean address(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.addr")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length < 2) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/addr <PostOffice> <Address> [player]  &7&oto address book in your hand");
            return true;
        }
        org.bukkit.inventory.ItemStack stack = player.getItemInHand();
        if ((stack == null) || (stack.getType() != org.bukkit.Material.WRITTEN_BOOK)) {
            Util.pinform(player, "&7&oYou must have a signed book in your hand.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        double price = 0.0D;
        if (VA_postal.economy_configured) {
            price = P_Economy.has_price_of_postage(player, stown);
            if (price < 0.0D) {
                Util.pinform(player, "&f&oYou don't have enough money to cover postage.");
                return true;
            }
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.pinform(player, "&7&oNeed more letters for address.  See list:");
            return true;
        }

        String attention = "[Resident]";
        if (args.length == 3) {
            String splayer = Util.player_complete(args[2]);
            if (!"null".equals(splayer)) {
                attention = splayer;
            }
        }


        if (is_player_comfirmation_registered(player)) {
            Cmd_static.addr_worker(player, null, stack, attention, stown, saddress);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to address to: &9&o" + Util.df(stown) + ", " + Util.df(saddress) + ", " + Util.df(attention));
            if (price > 0.0D) {
                Util.pinform(player, "&fYou will be charged " + ef(price) + " for postage.");
            }

            String scommand = "/addr " + stown + " " + saddress + " " + attention;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean distr(boolean console, CommandSender sender, String cmd, String[] args) {
        if (!hasPermission(player, "postal.distr")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length < 1) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/distr <all/owners> [Town] [Expiration_Days]");
            Util.pinform(player, "&7&oTo distrubute the book in your hand to Postal addresses");
            return true;
        }
        org.bukkit.inventory.ItemStack stack = player.getItemInHand();
        if ((stack == null) || (stack.getType() != org.bukkit.Material.WRITTEN_BOOK)) {
            Util.pinform(player, "&7&oYou must have a signed book in your hand.");
            return true;
        }


        String sdistribution = "owners";
        if (("all".equals(args[0].toLowerCase().trim())) || ("[all]".equals(args[0].toLowerCase().trim()))) {
            sdistribution = "[all]";
        }

        String stown = "[all]";
        int iexpiration = GetConfig.distr_exp_days();


        if (args.length > 1) {
            boolean done = false;

            if (!"[all]".equals(args[1])) {
                String result = C_Postoffice.town_complete(args[1]);
                if (!"null".equals(result)) {
                    stown = result;
                    if (args.length == 2) {
                        done = true;
                    }
                }
            }

            if (!done) {
                int iresult;
                try {
                    iresult = Integer.parseInt(args[(args.length - 1)]);
                } catch (NumberFormatException numberFormatException) {
                    iresult = -1;
                }

                if ((iresult > 0) && (iresult < 30)) {
                    iexpiration = iresult;
                }
            }
        }

        double price = 0.0D;
        if (VA_postal.economy_configured) {
            price = P_Economy.has_price_of_distr(player, sdistribution, stown);
            if (price < 0.0D) {
                Util.pinform(player, "&f&oYou don't have enough money to cover postage.");
                return true;
            }
        }

        String s_expiration = Util.int2str(iexpiration);


        if (is_player_comfirmation_registered(player)) {
            Cmd_static.distr_worker(player, stack, sdistribution, stown, iexpiration);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to distribute to: &9&o" + Util.df(sdistribution) + ", " + Util.df(stown) + ", " + Util.df(s_expiration));
            if (price > 0.0D) {
                Util.pinform(player, "&fYou will be charged " + ef(price) + " for postage.");
            }

            String scommand = "/distr " + sdistribution + " " + stown + " " + s_expiration;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean accept(boolean console, CommandSender sender, String cmd, String[] args) {
        String splayer = player.getName().trim();
        if ((!hasPermission(player, "postal.accept")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }

        ItemStack stack = player.getItemInHand();
        if (!BookManip.holding_valid_shipper(player, stack, true)) {
            Util.pinform(player, "&7&oYou must have a valid shipping label in your hand.");
            return true;
        }

        double price = Cmd_static.cod_amount(stack);
        if ((price > 0.0D) &&
                (!P_Economy.does_player_have_amount(splayer, price))) {
            Util.pinform(player, "&7&oYou don't have enough money to pay for this COD.");
            return true;
        }


        if ((VA_postal.using_towny()) &&
                (!P_Towny.ok_to_build_towny(player))) {
            Util.pinform(player, "&7&oYou must have build permission where you place this package.");
            return true;
        }

        if ((VA_postal.wg_configured) &&
                (!P_WG.ok_to_build_wg(player))) {
            Util.pinform(player, "&7&oYou must have build permission where you place this package.");
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            if (Cmd_static.accept_worker(player, stack, price)) {
                Util.pinform(player, "&7&oShipment successfully received.");
            }
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&f&rReady accept shipment.");
            Util.pinform(player, "&7&oShipment will be placed directly in front of you.");
            if (price > 0.0D) {
                Util.pinform(player, "&7&oThis is COD and you will be charged $" + ef(price));
            }
            String scommand = "/postal accept";
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean refuse(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.refuse")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }

        ItemStack stack = player.getItemOnCursor();
        if (!BookManip.holding_valid_shipper(player, stack, true)) {
            Util.pinform(player, "&7&oYou must have a valid parcel statement in your hand.");
            return true;
        }

        Block block = ChestManip.parcel_place_chest_refuse(stack);
        if (block == null) {
            Util.pinform(player, "&7&oUnable to place parcel at origin.");
            return true;
        }


        org.bukkit.inventory.Inventory inventory = BookManip.parcel_fill_chest(block, stack);
        if (inventory == null) {
            Util.pinform(player, "&7&oUnable to complete shipment return.");
            return true;
        }


        org.bukkit.inventory.ItemStack stamped = BookManip.stamp_parcel_statement(player, stack, false);
        player.setItemInHand(null);
        BookManip.parcel_stmnt_to_chest(inventory, stamped, block, 4);
        Util.pinform(player, "&7&oShipment has been returned to sender.");

        return true;
    }

    public static boolean parcel(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.package")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length < 2) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/package <PostOffice> <Address> [player]  &7&oto package a chest in front of you");
            return true;
        }
        org.bukkit.block.Block block = ChestManip.at_chest(player);
        if (block == null) {
            Util.pinform(player, "&7&oStand next to the chest you want to ship.");
            return true;
        }
        if (!ChestManip.ok_to_use_chest(block, false)) {
            Util.pinform(player, "&6This chest either has a sign on it, or is too close to a sign.");
            return true;
        }

        if ((VA_postal.using_towny()) &&
                (!P_Towny.ok_to_build_towny(player))) {
            Util.pinform(player, "&7&oYou must have build permission where you package this chest.");
            return true;
        }

        if ((VA_postal.wg_configured) &&
                (!P_WG.ok_to_build_wg(player))) {
            Util.pinform(player, "&7&oYou must have build permission where you package this chest.");
            return true;
        }

        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        double price = 0.0D;
        if (VA_postal.economy_configured) {
            price = P_Economy.has_price_of_shipping(player, stown);
            if (price < 0.0D) {
                Util.pinform(player, "&f&oYou don't have enough money to cover shipping.");
                return true;
            }
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.pinform(player, "&7&oNeed more letters for address.  See list:");
            return true;
        }

        String attention = "[Resident]";
        if (args.length == 3) {
            String splayer = Util.player_complete(args[2]);
            if (!"null".equals(splayer)) {
                attention = Util.df(splayer);
            }
        }

        if (is_player_comfirmation_registered(player)) {
            if (Cmd_static.parcel_worker(player, attention, stown, saddress)) {
                Util.pinform(player, "&f&rThe shipping label is now in your hand.");
                Util.pinform(player, "&7&oMail this shipping label when you are ready to ship.");
            }
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady create shipping label");
            Util.pinform(player, "&7&oAddressed to: &9&o" + Util.df(stown) + ", " + Util.df(saddress) + ", " + Util.df(attention));
            if (price > 0.0D) {
                Util.pinform(player, "&fYou will be charged " + ef(price) + " for shipping.");
            }

            String scommand = "/postal package " + stown + " " + saddress + " " + attention;
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean cod(boolean console, CommandSender sender, String cmd, String[] args) {
        if (!VA_postal.economy_configured) {
            Util.pinform(player, "&f&oEconomy is not enabled.");
            return true;
        }
        if ((!hasPermission(player, "postal.cod")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length == 0) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/cod <price> &7&oto make a shipping label COD");
            return true;
        }

        ItemStack stack = player.getItemInHand();
        if (!BookManip.holding_valid_shipper(player, stack, false)) {
            Util.pinform(player, "&7&oYou must have a valid shipping label in your hand.");
            return true;
        }
        double cod_charge = P_Economy.has_price_of_cod(player);
        if (cod_charge < 0.0D) {
            Util.pinform(player, "&f&oYou don't have enough money to cover the COD surcharge.");
            return true;
        }
        double cod_price = Util.str2double(args[0]);
        if (cod_price <= 0.0D) {
            Util.pinform(player, "&f&oProblem reading the price: " + args[0]);
            return true;
        }
        if (is_player_comfirmation_registered(player)) {
            Cmd_static.cod_worker(player, stack, cod_price);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady make this shipper into a C.O.D.");
            Util.pinform(player, "&fYou will be charged " + ef(cod_charge) + " as a COD surcharge.");
            Util.pinform(player, "&fThe recipient must pay you $" + ef(cod_price) + " to accept the package.");
            String scommand = "/cod " + args[0];
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean attention(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.att")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length >= 1) && ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/att [player]  &7&oto set 'Attention to:' using a book in your hand");
            Util.pinform(player, "&7&oNo parameter resets book to &f&r'[Resident]'");
            return true;
        }
        org.bukkit.inventory.ItemStack stack = player.getItemInHand();
        if ((stack == null) || (stack.getType() != org.bukkit.Material.WRITTEN_BOOK)) {
            Util.pinform(player, "&7&oYou must have the book you want to address in your hand.");
            return true;
        }

        String attention = "[Resident]";
        if (args.length >= 1) {
            String splayer = Util.player_complete(args[0]);
            if (!"null".equals(splayer)) {
                attention = Util.df(splayer);
            }
        }

        if (!Cmd_static.att_worker(false, player, stack, attention)) {
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            Cmd_static.att_worker(true, player, stack, attention);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to set attention to: &9&o" + Util.df(attention));

            String scommand = "/att " + attention;
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean gotoaddress(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 2) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/gotoaddr <PostOffice> <Address>   &7&oto teleport to a postal address");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.pinform(player, "&7&oNeed more letters for address.  See list:");
            return true;
        }
        if (!hasPermission_ext(player, "postal.gotoaddr", stown, saddress)) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            String slocation = C_Address.get_address_location(stown, saddress);
            Util.pinform(player, "&7&oTeleporting you to: &r" + Util.df(stown) + ", " + Util.df(saddress));
            Util.safe_tps(player, slocation);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to teleport you to: &r" + Util.df(stown) + ", " + Util.df(saddress));

            String scommand = "/gotoaddr " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean gps(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length == 1) && ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/gps <PostOffice> [Address]   &7&oto set your compass");
            return true;
        }
        if (args.length == 0) {
            String[] g_list = C_Arrays.geo_list_sorted(player);

            Util.pinform(player, "");
            Util.pinform(player, "&7&oUsage: &f&r/gps <PostOffice> [Address]   &7&oto set your compass");
            Util.pinform(player, "");
            Util.pinform(player, "&7Dist Cmp  Post-Office--  Address------- Qwner--------");
            for (int i = 0; i < g_list.length; i++) {
                String[] parts = g_list[i].split(",");
                if (parts.length == 4) {
                    String stown = parts[1].toLowerCase().trim();
                    String saddr = parts[2].toLowerCase().trim();
                    String sownr = "Server";
                    boolean is_po = false;
                    if (saddr.equalsIgnoreCase("post_office")) {
                        is_po = true;
                        if (C_Owner.is_local_po_owner_defined(stown)) {
                            sownr = Util.df(C_Owner.get_owner_local_po(stown));
                        }
                    } else if (C_Owner.is_address_owner_defined(stown, saddr)) {
                        sownr = Util.df(C_Owner.get_owner_address(stown, saddr));
                    }

                    sownr = fixed_len(Util.df(sownr), 14, "-");
                    String hding = parts[3];
                    String sdist = parts[0].trim();
                    stown = fixed_len(Util.df(stown), 14, "-");
                    saddr = fixed_len(Util.df(saddr), 14, "-");
                    int dist = Util.str2int(sdist);
                    sdist = Util.int2fstr_leading_zeros(dist, 3);
                    if (is_po) {
                        Util.pinform(player, "&f" + sdist + "  &e&l" + hding + "  &6" + stown + " " + saddr + " &o" + sownr);
                    } else {
                        Util.pinform(player, "&f" + sdist + "  &e&l" + hding + "  &a" + stown + " " + saddr + " &o" + sownr);
                    }
                    if (i == 8) {
                        break;
                    }
                }
            }
            return true;
        }

        String stown = "null";
        if ((args.length >= 1) && (!args[0].equals("null"))) {
            stown = C_Postoffice.town_complete(args[0]);
            if ("null".equals(stown)) {
                if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                    P_Towny.list_towny_tree(player, false, null, false);
                } else {
                    Util.list_postal_tree(player, false, null);
                }
                Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
                return true;
            }
        }
        String saddress = "null";
        if ((args.length > 1) && (!args[1].equals("null"))) {
            saddress = C_Address.addresses_complete(stown, args[1]);
            if ("null".equals(saddress)) {
                if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                    P_Towny.list_towny_tree(player, true, stown, false);
                } else {
                    Util.list_postal_tree(player, true, stown);
                }
                Util.pinform(player, "&7&oNeed more letters for address.  See list:");
                return true;
            }
        }
        if (!hasPermission_ext(player, "postal.gps", stown, saddress)) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if (is_player_comfirmation_registered(player)) {
            if (saddress.equals("null")) {
                String slocation = C_Postoffice.get_local_po_location_by_name(stown);
                org.bukkit.Location location = Util.str2location(slocation);
                if (player.getWorld() == location.getWorld()) {
                    player.setCompassTarget(location);
                    Util.pinform(player, "&7&oYour compass has been set to: &r" + Util.df(stown));
                } else {
                    Util.pinform(player, Util.df(stown) + " is in a different world");
                }
            } else {
                String slocation = C_Address.get_address_location(stown, saddress);
                org.bukkit.Location location = Util.str2location(slocation);
                if (player.getWorld() == location.getWorld()) {
                    player.setCompassTarget(location);
                    Util.pinform(player, "&7&oYour compass has been set to: &r" + Util.df(stown) + ", " + Util.df(saddress));
                } else {
                    Util.pinform(player, Util.df(stown) + " is in a different world");
                }
            }
            deregister_player_comfirmation(player);
        } else {
            if (saddress.equals("null")) {
                Util.pinform(player, "&7&oReady to set your compass to: &r" + Util.df(stown));
            } else {
                Util.pinform(player, "&7&oReady to set your compass to: &r" + Util.df(stown) + ", " + Util.df(saddress));
            }

            String scommand = "/gpsp " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean owneraddr(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 2) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/owneraddr <PostOffice> <Address> [player/none] &7&oto to set address owner");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.pinform(player, "&7&oNeed more letters for address.  See list:");
            return true;
        }
        String subject = "none";
        if ((args.length != 2) && (!"none".equals(args[2].toLowerCase().trim()))) {
            subject = Util.player_complete(args[2]);
            if ("null".equals(subject)) {
                Util.pinform(player, "Need more letters for player.  See list:");
                Util.player_list_match(player, args[2]);
                return true;
            }
        }


        if (((!VA_postal.economy_configured) || (C_Owner.is_address_owner_defined(stown, saddress))) &&
                (!hasPermission_ext(player, "postal.owneraddr", stown, saddress))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }

        double price = 0.0D;
        if (!subject.equalsIgnoreCase("none")) {
            if (VA_postal.economy_configured) {
                price = P_Economy.has_price_of_address(subject);
                if (price < 0.0D) {
                    Util.pinform(player, "&f&o" + Util.df(subject) + "&7&o does not have enough money to cover the purchase price.");
                    return true;
                }
            }
        } else if ((VA_postal.economy_configured) &&
                (!P_Economy.can_central_buy_addr())) {
            Util.pinform(player, "&7&oThe central post office can't purchase the address right now.");
            Util.pinform(player, "&7&oTry again later when it has a larger balance.");
            return true;
        }


        if (is_player_comfirmation_registered(player)) {
            Cmd_static.owneraddr_worker(player, stown, subject, saddress);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "Ready to make " + Util.df(subject) + " the address owner of " + Util.df(stown) + ", " + Util.df(saddress));
            if ((!subject.equalsIgnoreCase("none")) &&
                    (price > 0.0D)) {
                Util.pinform(player, "&f&o" + Util.df(subject) + "&7&o will be charged &f&o" + ef(price));
            }


            String scommand = "/owneraddr " + stown + " " + saddress + " " + subject;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean owneraddr_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 2) || ("?".equals(args[0]))) {
            Util.con_type("Usage: owneraddr <PostOffice> <Address> [player/none] to set address owner");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("Need more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.con_type("Need more letters for address.  See list:");
            return true;
        }
        String subject = "none";
        if ((args.length != 2) && (!"none".equals(args[2].toLowerCase().trim()))) {
            subject = Util.player_complete(args[2]);
            if ("null".equals(subject)) {
                Util.con_type("Need more letters for player.  See list:");
                Util.player_list_match_con(args[2]);
                return true;
            }
        }
        double price = 0.0D;
        if (!subject.equalsIgnoreCase("none")) {
            if (VA_postal.economy_configured) {
                price = P_Economy.has_price_of_address(subject);
                if (price < 0.0D) {
                    Util.con_type(Util.df(subject) + " does not have enough money to cover the purchase price.");
                    return true;
                }
            }
        } else if ((VA_postal.economy_configured) &&
                (!P_Economy.can_central_buy_addr())) {
            Util.pinform(player, "&7&oThe central post office can't purchase the address right now.");
            Util.pinform(player, "&7&oTry again later when it has a larger balance.");
            return true;
        }


        if (is_player_comfirmation_registered(null)) {
            Cmd_static.owneraddr_worker(null, stown, subject, saddress);
            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to make " + subject + " the address owner of " + Util.df(stown) + ", " + Util.df(saddress));
            if ((!subject.equalsIgnoreCase("none")) &&
                    (price > 0.0D)) {
                Util.con_type(Util.df(subject) + " will be charged " + ef(price));
            }


            String scommand = "owneraddr " + stown + " " + saddress + " " + subject;
            register_player_comfirmation(null, scommand);
        }

        return true;
    }

    public static boolean ownerlocal(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 1) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: ownerlocal <PostOffice> [player/none] set post office owner");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        String subject = "none";
        if ((args.length != 1) && (!"none".equals(args[1].toLowerCase().trim()))) {
            subject = Util.player_complete(args[1]);
            if ("null".equals(subject)) {
                Util.pinform(player, "Need more letters for player.  See list:");
                Util.player_list_match(player, args[1]);
                return true;
            }
        }


        if (((!VA_postal.economy_configured) || (C_Owner.is_local_po_owner_defined(stown))) &&
                (!hasPermission_ext(player, "postal.ownerlocal", stown, "null"))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }

        double price = 0.0D;
        if (!subject.equalsIgnoreCase("none")) {
            if (VA_postal.economy_configured) {
                price = P_Economy.has_price_of_postoffice(subject);
                if (price < 0.0D) {
                    Util.pinform(player, "&f&o" + Util.df(subject) + "&7&o does not have enough money to cover the purchase price.");
                    return true;
                }
            }
        } else if ((VA_postal.economy_configured) &&
                (!P_Economy.can_central_buy_po())) {
            Util.pinform(player, "&7&oThe central post office can't purchase the post office right now.");
            Util.pinform(player, "&7&oTry again later when it has a larger balance.");
            return true;
        }


        if (is_player_comfirmation_registered(player)) {
            Cmd_static.ownerlocal_worker(player, stown, subject);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "Ready to make " + Util.df(subject) + " the post office owner of " + Util.df(stown));
            if ((!subject.equalsIgnoreCase("none")) &&
                    (price > 0.0D)) {
                Util.pinform(player, "&f&o" + Util.df(subject) + "&7&o will be charged &f&o" + ef(price));
            }


            String scommand = "/ownerlocal " + stown + " " + subject;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean ownerlocal_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 1) || ("?".equals(args[0]))) {
            Util.con_type("Usage: ownerlocal <PostOffice> [player/none] set post office owner");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("Need more letters for post office.  See list:");
            return true;
        }
        String subject = "none";
        if ((args.length != 1) && (!"none".equals(args[1].toLowerCase().trim()))) {
            subject = Util.player_complete(args[1]);
            if ("null".equals(subject)) {
                Util.con_type("Need more letters for player.  See list:");
                Util.player_list_match_con(args[1]);
                return true;
            }
        }
        double price = 0.0D;
        if (!subject.equalsIgnoreCase("none")) {
            if (VA_postal.economy_configured) {
                price = P_Economy.has_price_of_postoffice(subject);
                if (price < 0.0D) {
                    Util.con_type(Util.df(subject) + " does not have enough money to cover the purchase price.");
                    return true;
                }
            }
        } else if ((VA_postal.economy_configured) &&
                (!P_Economy.can_central_buy_po())) {
            Util.pinform(player, "&7&oThe central post office can't purchase the post office right now.");
            Util.pinform(player, "&7&oTry again later when it has a larger balance.");
            return true;
        }


        if (is_player_comfirmation_registered(null)) {
            Cmd_static.ownerlocal_worker(null, stown, subject);
            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to make " + Util.df(subject) + " the post office owner of " + Util.df(stown));
            if ((!subject.equalsIgnoreCase("none")) &&
                    (price > 0.0D)) {
                Util.con_type(Util.df(subject) + " will be charged " + ef(price));
            }


            String scommand = "ownerlocal " + stown + " " + subject;
            register_player_comfirmation(null, scommand);
        }

        return true;
    }

    public static boolean deletelocal(boolean console, CommandSender sender, String cmd, String[] args) {
        if (!hasPermission(player, "postal.deletelocal")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length < 1) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/deletelocal <PostOffice>   &7&oto delete an existing post office");
            Util.pinform(player, "&7&oAll addresses using this post office will be deleted.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }

        if (is_player_comfirmation_registered(player)) {
            C_Postoffice.delete_postoffice(player, stown);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "Ready to delete post office: " + Util.df(stown));

            String scommand = "/deletelocal " + stown + " ";
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean deletelocal_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 1) || ("?".equals(args[0]))) {
            Util.con_type("Usage: deletelocal <PostOffice>   to delete an existing post office");
            Util.con_type("All addresses using this post office will be deleted.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.cinform("Need more letters for post office.  See list:");
            return true;
        }

        if (is_player_comfirmation_registered(null)) {
            C_Postoffice.delete_postoffice(null, stown);
            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to delete post office: " + Util.df(stown));

            String scommand = "deletelocal " + stown + " ";
            register_player_comfirmation(null, scommand);
        }

        return true;
    }

    public static boolean deleteaddr(boolean console, CommandSender sender, String cmd, String[] args) {
        if (!hasPermission(player, "postal.deleteaddr")) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length < 2) || ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/deleteaddr <PostOffice> <Address>   &7&oto delete an existing address");
            return true;
        }

        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, true, stown, false);
            } else {
                Util.list_postal_tree(player, true, stown);
            }
            Util.pinform(player, "&7&oNeed more letters for address.  See list:");
            return true;
        }


        if (is_player_comfirmation_registered(player)) {
            C_Address.delete_address(player, stown, saddress);

            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "&7&oReady to delete address: " + Util.df(saddress) + " post office: " + Util.df(stown));

            String scommand = "/deleteaddr " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }

        return true;
    }

    public static boolean deleteaddr_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 2) || ("?".equals(args[0]))) {
            Util.con_type("Usage: &f&r/deleteaddr <PostOffice> <Address>   &7&oto delete an existing address");
            return true;
        }

        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.cinform("Need more letters for post office.  See list:");
            return true;
        }

        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, true, stown, false);
            } else {
                Util.list_postal_tree(null, true, stown);
            }
            Util.con_type("Need more letters for address.  See list:");
            return true;
        }


        if (is_player_comfirmation_registered(null)) {
            C_Address.delete_address(null, stown, saddress);

            deregister_player_comfirmation(null);
        } else {
            Util.con_type("Ready to delete address: " + Util.df(saddress) + " post office: " + Util.df(stown));

            String scommand = "deleteaddr " + stown + " " + saddress;
            register_player_comfirmation(null, scommand);
        }

        return true;
    }

    public static boolean tlist(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.tlist")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length >= 1) && ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/tlist [PostOffice/Town] &7&oto list Post Office addresses");
            return true;
        }
        if (args.length == 0) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "");
            Util.pinform(player, "&7&oUse a search string for town details.");
            return true;
        }
        String srch_param = args[0].toLowerCase().trim();
        if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
            P_Towny.list_towny_tree(player, true, srch_param, true);
            Util.pinform(player, "");
            Util.pinform(player, "&7&oTowns matching search:  &f&r" + Util.df(srch_param));
        } else {
            Util.list_postal_tree(player, true, srch_param);
            Util.pinform(player, "");
            Util.pinform(player, "&7&oPost offices matching search:  &f&r" + Util.df(srch_param));
        }

        return true;
    }

    public static boolean tlist_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length >= 1) && ("?".equals(args[0]))) {
            Util.con_type("Usage: /tlist [PostOffice/Town] to list Post Office addresses");
            return true;
        }
        if (args.length == 0) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("");
            Util.con_type("Use a search string for town details.");
            return true;
        }
        String srch_param = args[0].toLowerCase().trim();
        Util.list_postal_tree(null, true, srch_param);
        Util.con_type("");
        Util.con_type("Post offices matching search:  " + Util.df(srch_param));

        return true;
    }

    public static boolean alist(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.alist")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length == 1) && ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/alist <PostOffice>   &7&oto list post office addresses.");
            return true;
        }
        String stown = "";
        String heading = "";
        if (args.length == 1) {
            stown = C_Postoffice.town_complete(args[0]);
        } else {
            String[] list = C_Arrays.geo_po_list_sorted(player);
            if (list == null) {
                stown = "null";
            } else {
                String[] parts = list[0].split(",");
                if ((parts != null) && (parts.length == 3)) {
                    stown = parts[1].toLowerCase().trim();
                    String blocks = Util.int2str(Util.str2int(parts[0]));
                    heading = "&f&r" + Util.df(stown) + "&7&o is &f&r" + blocks + "&7&o blocks away, heading: &f&r" + parts[2];
                } else {
                    stown = "null";
                }
            }
        }
        if ("null".equals(stown)) {
            if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                P_Towny.list_towny_tree(player, false, null, false);
            } else {
                Util.list_postal_tree(player, false, null);
            }
            Util.pinform(player, "&7&oNeed more letters for post office.  See list:");
            return true;
        }
        Util.pinform(player, "");
        Util.pinform(player, "&r&l" + Util.df(stown));
        C_Address.list_addresses(player, stown);
        if (!heading.isEmpty()) {
            Util.pinform(player, "");
            Util.pinform(player, heading);
        }

        return true;
    }

    public static boolean alist_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 1) || ("?".equals(args[0]))) {
            Util.con_type("Usage: alist <PostOffice>   to list post office addresses.");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type(" Need more letters for post office.  See list:");
            return true;
        }
        Util.con_type("Addresses for " + Util.df(stown) + ":");
        com.vodhanel.minecraft.va_postal.config.C_List.list_addresses_con(stown);
        return true;
    }

    public static boolean plist(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((!hasPermission(player, "postal.plist")) && (VA_postal.using_towny()) && (!P_Towny.is_towny_resident_by_tvrs(player))) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }
        if ((args.length == 1) && (("help".equalsIgnoreCase(args[0])) || ("?".equals(args[0])))) {
            Util.pinform(player, "&7&oUsage: &f&r/plist <string match>   &7&oto list player ownerships.");
            return true;
        }

        if (args.length == 0) {
            String splayer = player.getName().toLowerCase().trim();
            String[] g_list = C_Arrays.geo_player_list_sorted(player);

            if ((g_list != null) && (g_list.length > 1)) {
                Util.pinform(player, "");
                Util.pinform(player, "&7Dist  Cmp  Player--------  Post Office--- Address-----");
                for (int i = 0; i < g_list.length; i++) {
                    String[] parts = g_list[i].split(",");
                    if (parts.length == 5) {
                        String subject = parts[1];
                        String stown;
                        String saddr = parts[4];
                        if (!splayer.equalsIgnoreCase(subject)) {
                            boolean is_po = false;
                            if (saddr.equalsIgnoreCase("post_office")) {
                                is_po = true;
                            }
                            stown = fixed_len(Util.df(parts[3]), 14, "-");
                            saddr = fixed_len(Util.df(parts[4]), 14, "-");
                            String hding = parts[2];
                            String sdist = parts[0].trim();
                            subject = fixed_len(Util.df(subject), 14, "-");
                            int dist = Util.str2int(sdist);
                            sdist = Util.int2fstr_leading_zeros(dist, 3);
                            if (is_po) {
                                Util.pinform(player, "&f" + sdist + "  &e&l" + hding + "  &f" + subject + " &6" + stown + " " + saddr);
                            } else {
                                Util.pinform(player, "&f" + sdist + "  &e&l" + hding + "  &f" + subject + " &a" + stown + " " + saddr);
                            }
                            if (i == 8) {
                                break;
                            }
                        }
                    }
                }
            } else {
                Util.pinform(player, "&7&oNo players to locate.");
            }
            return true;
        }

        String splayer = "null";
        if (args.length > 0) {
            splayer = Util.player_complete(args[0]);
        }
        if ("null".equals(splayer)) {
            String search_string = args[0].toLowerCase().trim();
            Util.player_list_match(player, search_string);
            Util.pinform(player, "&7&oUse: '/plist <player>' to find player addreses");
            return true;
        }


        int a_hits = 0;
        int p_hits = 0;
        String srch_param = splayer.toLowerCase().trim();
        Util.pinform(player, "");
        Util.pinform(player, "&f&l" + Util.df(splayer));
        String[] town_list = C_Arrays.town_list();
        if (town_list == null) {
            return true;
        }
        for (String stown : town_list) {
            String sworld = com.vodhanel.minecraft.va_postal.config.C_List.get_world(C_Postoffice.get_local_po_location_by_name(stown));
            String dstown = fixed_len(Util.df(stown), 15, "-");
            sworld = Util.df(sworld);
            String po_owner = "Server";
            String saddress = fixed_len("Post-Office", 15, "-");
            if (C_Owner.is_local_po_owner_defined(stown)) {
                po_owner = C_Owner.get_owner_local_po(stown).toLowerCase().trim();
                if (po_owner.equals(srch_param)) {
                    Util.pinform(player, "    &6&o" + dstown + "  &6&o" + saddress + "  &f&o" + sworld);
                    p_hits++;
                }
            }
            String[] addr_list = C_Arrays.addresses_list(stown);
            if (addr_list != null) {

                for (String anAddr_list : addr_list) {
                    saddress = anAddr_list;
                    if (C_Owner.is_address_owner_defined(stown, saddress)) {
                        String addr_owner = C_Owner.get_owner_address(stown, saddress).toLowerCase().trim();
                        if (addr_owner.equals(srch_param)) {
                            saddress = fixed_len(Util.df(saddress), 15, "-");
                            Util.pinform(player, "    &a&o" + dstown + "  &a&o" + saddress + "  &f&o" + sworld);
                            a_hits++;
                        }
                    }
                }
            }
        }

        return true;
    }

    public static boolean plist_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length == 0) || ((args.length == 1) && ("help".equalsIgnoreCase(args[0]))) || ("?".equals(args[0]))) {
            Util.con_type("Usage: /plist <string match>   to list player ownerships.");
            return true;
        }
        String splayer = "null";
        if (args.length > 0) {
            splayer = Util.player_complete(args[0]);
        }
        if ("null".equals(splayer)) {
            String search_string = args[0].toLowerCase().trim();
            Util.player_list_match_con(search_string);
            Util.con_type("Use: '/plist <player>' to find player addreses");
            return true;
        }


        int a_hits = 0;
        int p_hits = 0;
        String srch_param = splayer.toLowerCase().trim();
        Util.con_type("");
        Util.con_type(Util.df(splayer));
        String[] town_list = C_Arrays.town_list();
        if (town_list == null) {
            return true;
        }
        for (String stown : town_list) {
            String sworld = com.vodhanel.minecraft.va_postal.config.C_List.get_world(C_Postoffice.get_local_po_location_by_name(stown));
            String dstown = fixed_len(Util.df(stown), 15, "-");
            sworld = Util.df(sworld);
            String po_owner = "Server";
            String saddress = fixed_len("Post-Office", 15, "-");
            if (C_Owner.is_local_po_owner_defined(stown)) {
                po_owner = C_Owner.get_owner_local_po(stown).toLowerCase().trim();
                if (po_owner.equals(srch_param)) {
                    Util.con_type("\033[0;33m    " + dstown + "  " + saddress + "  " + sworld);
                    p_hits++;
                }
            }
            String[] addr_list = C_Arrays.addresses_list(stown);
            if (addr_list != null) {

                for (String anAddr_list : addr_list) {
                    saddress = anAddr_list;
                    if (C_Owner.is_address_owner_defined(stown, saddress)) {
                        String addr_owner = C_Owner.get_owner_address(stown, saddress).toLowerCase().trim();
                        if (addr_owner.equals(srch_param)) {
                            saddress = fixed_len(Util.df(saddress), 15, "-");
                            Util.con_type("\033[0;32m    " + dstown + "  " + saddress + "  " + sworld);
                            a_hits++;
                        }
                    }
                }
            }
        }

        return true;
    }

    public static boolean showroute(boolean console, CommandSender sender, String cmd, String[] args) {
        if (VA_postal.plistener_player != null) {
            Util.pinform(player, "&7&oThis command may not be used while the route editor is in use.");
            Util.pinform(player, "&7&oThe route editor is being used by: " + VA_postal.plistener_player.getName());
            return true;
        }
        if ((args.length == 1) && ("?".equals(args[0]))) {
            Util.pinform(player, "&7&oUsage: &f&r/showroute <PostOffice> <Address>   &7&oto highlite waypoints on a route");
            return true;
        }
        String stown = "";
        String saddress = "";
        if (args.length == 0) {

            String[] geo_addr = C_Arrays.geo_addr_list_sorted(player);
            if ((geo_addr != null) && (geo_addr.length > 0)) {
                String[] parts = geo_addr[0].split(",");
                if ((parts != null) && (parts.length == 4)) {
                    int dist = Util.str2int(parts[0]);
                    int max_dist = VA_postal.allowed_geo_proximity;
                    if (dist > max_dist) {
                        Util.pinform(player, "&7&oThere is no address within " + Util.int2str(max_dist) + " blocks.");
                        return true;
                    }
                    stown = parts[1];
                    saddress = parts[2];
                } else {
                    Util.pinform(player, "&7&oCould not find nearest address.");
                    return true;
                }
            } else {
                Util.pinform(player, "&7&oCould not obtain geo list.");
            }
        } else {
            stown = C_Postoffice.town_complete(args[0]);
            if ("null".equals(stown)) {
                if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                    P_Towny.list_towny_tree(player, false, null, false);
                } else {
                    Util.list_postal_tree(player, false, null);
                }
                Util.pinform(player, "&7&oPost office does not exist.  See list:");
                return true;
            }
            saddress = C_Address.addresses_complete(stown, args[1]);
            if ("null".equals(saddress)) {
                if ((VA_postal.using_towny()) && (P_Towny.is_this_a_town_by_loc(player))) {
                    P_Towny.list_towny_tree(player, true, stown, false);
                } else {
                    Util.list_postal_tree(player, true, stown);
                }
                Util.pinform(player, "&7&oPostal address: " + Util.df(stown) + "/" + Util.df(args[1]) + " does not exist.");
                Util.pinform(player, "&7&oUse /setaddress to define it.");
                return true;
            }
        }

        if (!hasPermission_ext(player, "postal.showroute", stown, saddress)) {
            Util.pinform(player, "&7&oRequired permission not present.");
            return true;
        }


        if (is_player_comfirmation_registered(player)) {
            com.vodhanel.minecraft.va_postal.listeners.RouteEditor.place_route_markers(stown, saddress);
            Util.pinform(player, "Waypoints have been highlighted for:  " + Util.df(stown) + ", " + Util.df(saddress));

            com.vodhanel.minecraft.va_postal.common.VA_Timers.hideroute(stown, saddress);
            deregister_player_comfirmation(player);
        } else {
            Util.pinform(player, "Ready to highlighted waypoints for:  " + Util.df(stown) + ", " + Util.df(saddress));
            String scommand = "/showroute " + stown + " " + saddress;
            register_player_comfirmation(player, scommand);
        }
        return true;
    }

    public static boolean showroute_con(boolean console, CommandSender sender, String cmd, String[] args) {
        if ((args.length < 2) || ("?".equals(args[0]))) {
            Util.con_type("Usage: showroute <PostOffice> <Address> to highlight waypoints on a route");
            return true;
        }
        String stown = C_Postoffice.town_complete(args[0]);
        if ("null".equals(stown)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, false, null, false);
            } else {
                Util.list_postal_tree(null, false, null);
            }
            Util.con_type("Need more letters for post office.  See list:");
            return true;
        }
        String saddress = C_Address.addresses_complete(stown, args[1]);
        if ("null".equals(saddress)) {
            if (VA_postal.using_towny()) {
                P_Towny.list_towny_tree(null, true, stown, false);
            } else {
                Util.list_postal_tree(null, true, stown);
            }
            Util.con_type("Need more letters for address.  See list:");
            return true;
        }


        if (is_player_comfirmation_registered(null)) {
            RouteEditor.place_route_markers(stown, saddress);
            Util.con_type("Waypoints have been highlighted for:  " + Util.df(stown) + ", " + Util.df(saddress));

            VA_Timers.hideroute(stown, saddress);
            deregister_player_comfirmation(null);
        } else {
            Util.cinform("Ready to highlight waypoints for:  " + Util.df(stown) + ", " + Util.df(saddress));
            String scommand = "showroute " + stown + " " + saddress;
            register_player_comfirmation(null, scommand);
        }

        return true;
    }

    public static boolean is_player_comfirmation_registered(Player player) {
        String splayer = null;
        if (player != null) {
            splayer = player.getName();
        } else {
            splayer = "console";
        }
        for (String[] aCommand_confirm : command_confirm) {
            if (splayer.equals(aCommand_confirm[0])) {


                return true;
            }
        }

        return false;
    }

    public static String get_registered_comfirmation_cmd(Player player) {
        String splayer = null;
        if (player != null) {
            splayer = player.getName();
        } else {
            splayer = "console";
        }
        for (String[] aCommand_confirm : command_confirm) {
            if (splayer.equals(aCommand_confirm[0])) {
                return aCommand_confirm[1];
            }
        }

        return "null";
    }

    public static void deregister_player_comfirmation(Player player) {
        String splayer = null;
        if (player != null) {
            splayer = player.getName();
        } else {
            splayer = "console";
        }
        for (int i = 0; i < command_confirm.length; i++) {
            if (splayer.equals(command_confirm[i][0])) {
                clear_row(i);
                break;
            }
        }
    }

    public static void register_player_comfirmation(Player player, String scommand) {
        String splayer = null;
        if (player != null) {
            splayer = player.getName();
        } else {
            splayer = "console";
        }
        for (int i = 0; i < command_confirm.length; i++) {
            if (command_confirm[i][0].equals("null")) {
                command_confirm[i][0] = splayer;
                command_confirm[i][1] = scommand;
                command_confirm[i][2] = stime_stamp();
                if (!"console".equals(splayer)) {
                    Util.pinform(player, "&e&oEnter &f&r'/' &e&oto confirm. ");
                    break;
                }
                Util.cinform("\033[0;33mEnter \033[0;37m'/'\033[0;33m to confirm. ");

                break;
            }
        }
        confirm_queue_dirty = true;
    }

    public static void age_confirm_queue() {
        int elapsed = -1;
        String splayer = "";
        String scommand = "";
        if (confirm_queue_dirty) {
            for (int i = 0; i < command_confirm.length; i++) {
                splayer = command_confirm[i][0];
                if (!"null".equals(splayer)) {
                    elapsed = elapse_seconds(command_confirm[i][2]);
                    if (elapsed > 30) {
                        scommand = command_confirm[i][1];
                        clear_row(i);
                        if (!"console".equals(splayer)) {
                            Util.spinform(splayer, "&e&oConfirm timeout: &r&o" + scommand);
                        } else {
                            Util.cinform("\033[0;33mConfirm timeout: \033[0;37m" + scommand);
                        }
                    }
                }
            }
        }
    }

    public static void init_confirm_queue() {
        for (int i = 0; i < command_confirm.length; i++) {
            clear_row(i);
        }
        confirm_queue_dirty = false;
    }

    public static void clear_row(int i) {
        command_confirm[i][0] = "null";
        command_confirm[i][1] = "null";
        command_confirm[i][2] = "0";
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
        long current_time = -1L;
        long saved_time = -1L;
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
            if (string.length() > 0) {
                return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase().trim();
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public static String ef(double value) {
        if (VA_postal.economy_configured) {
            return VA_postal.econ.format(value);
        }
        return "-1";
    }

    public static String fixed_len(String input, int len, String filler) {
        try {
            input = input.trim();

            if (input.length() >= len) {
                return input.substring(0, len);
            }

            while (input.length() < len) {
                input = input + filler;
            }
            return input;
        } catch (Exception e) {
            String blank = "";
            for (int i = 0; i < len; i++) {
                blank = blank + filler;
            }
            return blank;
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if ("CONSOLE".equals(sender.getName())) {
            if (cmd.getName().equalsIgnoreCase("postal")) {
                result = postal_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("alist")) {
                result = alist_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("tlist")) {
                result = tlist_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("plist")) {
                result = plist_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("showroute")) {
                result = showroute_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("expedite")) {
                result = expedite_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("owneraddr")) {
                result = owneraddr_con(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("ownerlocal")) {
                result = ownerlocal_con(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("deletelocal")) {
                result = deletelocal_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("deleteaddr")) {
                result = deleteaddr_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("openlocal")) {
                result = openlocal_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("openaddr")) {
                result = openaddr_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("closelocal")) {
                result = closelocal_con(true, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("closeaddr")) {
                result = closeaddr_con(true, sender, cmd.getName(), args);
            }

        } else {

            player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("postal")) {
                result = postal(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("go")) {
                result = go(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("setowner")) {
                result = setowner(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("addr")) {
                result = address(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("distr")) {
                result = distr(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("att")) {
                result = attention(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("tlist")) {
                result = tlist(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("alist")) {
                result = alist(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("plist")) {
                result = plist(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("gps")) {
                result = gps(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("gpsp")) {
                result = gps(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("expedite")) {
                result = expedite(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("setcentral")) {
                result = setcentral(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("gotocentral")) {
                result = gotocentral(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("setlocal")) {
                result = setlocal(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("gotolocal")) {
                result = gotolocal(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("setaddr")) {
                result = setaddress(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("gotoaddr")) {
                result = gotoaddress(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("owneraddr")) {
                result = owneraddr(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("ownerlocal")) {
                result = ownerlocal(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("setroute")) {
                result = setroute(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("showroute")) {
                result = showroute(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("deletelocal")) {
                result = deletelocal(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("deleteaddr")) {
                result = deleteaddr(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("openlocal")) {
                result = openlocal(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("openaddr")) {
                result = openaddr(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("closelocal")) {
                result = closelocal(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("closeaddr")) {
                result = closeaddr(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("package")) {
                result = parcel(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("cod")) {
                result = cod(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("accept")) {
                result = accept(false, sender, cmd.getName(), args);
            }

            if (cmd.getName().equalsIgnoreCase("refuse")) {
                result = refuse(false, sender, cmd.getName(), args);
            }
        }

        return result;
    }
}
