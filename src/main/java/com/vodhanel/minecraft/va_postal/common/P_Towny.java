package com.vodhanel.minecraft.va_postal.common;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.commands.Cmdexecutor;
import com.vodhanel.minecraft.va_postal.config.*;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class P_Towny {
    VA_postal plugin;

    public P_Towny(VA_postal instance) {
        plugin = instance;
    }

    public static void check_po_ownership(int id, String stown) {
        if (!"null".equals(VA_postal.wtr_schest_location_postoffice[id])) {
            String sloc = VA_postal.wtr_schest_location_postoffice[id];
            Location loc = Util.str2location(sloc);
            update_town_by_loc(stown, loc);
            Map.Entry<String, Player> mayor = towny_mayor_by_loc(loc);
            if (!mayor.getKey().equals("not_towny")) {
                if (mayor.getKey().equals("not_mayor")) {
                    if (C_Owner.is_local_po_owner_defined(stown)) {
                        C_Owner.del_owner_local_po(stown);
                        Util.cinform("Towny override: mayer removed from: " + Util.df(stown));
                    }

                } else if (C_Owner.is_local_po_owner_defined(stown)) {
                    Player local_po_owner = C_Owner.get_owner_local_po(stown);
                    if (local_po_owner != mayor.getValue()) {
                        C_Owner.set_owner_local_po(stown, mayor.getValue());
                        Util.cinform("Towny override: New mayor: " + Util.df(stown));
                    }
                } else {
                    C_Owner.set_owner_local_po(stown, mayor.getValue());
                    Util.cinform("Towny override: New mayor: " + Util.df(stown));
                }
            }
        }
    }


    public static void update_town_by_loc(String postal_town, Location location) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            WorldCoord wc = WorldCoord.parseWorldCoord(location);
            Coord coord = wc.getCoord();
            TownyWorld tworld;
            try {
                tworld = wc.getTownyWorld();
            } catch (NotRegisteredException e) {
                if (C_Towny.is_towny_town_defined(postal_town)) {
                    C_Towny.del_towny_town(postal_town);
                }
                return;
            }
            if (tworld.hasTownBlock(coord)) {
                TownBlock tblock;
                Town town;
                try {
                    tblock = wc.getTownBlock();
                } catch (NotRegisteredException e) {
                    if (C_Towny.is_towny_town_defined(postal_town)) {
                        C_Towny.del_towny_town(postal_town);
                    }
                    return;
                }
                if (tblock.hasTown()) {
                    try {
                        town = tblock.getTown();
                    } catch (NotRegisteredException e) {
                        if (C_Towny.is_towny_town_defined(postal_town)) {
                            C_Towny.del_towny_town(postal_town);
                        }
                        return;
                    }

                    String tname = town.getName();
                    if (C_Towny.is_towny_town_defined(postal_town)) {
                        String pname = C_Towny.get_towny_town(postal_town);
                        if (!tname.equalsIgnoreCase(pname)) {
                            C_Towny.set_towny_town(postal_town, tname);
                        }
                    } else {
                        C_Towny.set_towny_town(postal_town, tname);
                    }

                } else if (C_Towny.is_towny_town_defined(postal_town)) {
                    C_Towny.del_towny_town(postal_town);
                }
            }
        }
    }

    public static boolean set_local_ok(Player player) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in) &&
                (is_this_a_town_by_loc(player))) {
            String t_name = get_town_name_by_loc(player);
            if (!t_name.equals("null")) {
                double blocks = get_town_blocks_by_tvrs(t_name);
                if (blocks > 0.0D) {
                    double postal_count = C_Towny.towny_po_count(t_name);
                    if (postal_count > 0.0D) {
                        double blocks_per_po = C_Towny.blocks_per_po();
                        double result = blocks / (blocks_per_po * postal_count);
                        Util.pinform(player, t_name + " has " + (int) blocks + " blocks and " + (int) postal_count + " post offices");
                        Util.pinform(player, "&e&oYou are allowed one post office for every " + (int) blocks_per_po + " blocks");
                        if (result < 1.0D) {
                            Util.pinform(player, "&e&oYou do not have enough town blocks.");
                            if (!player.isOp()) {
                                return false;
                            }
                            Util.pinform(player, "&6&oOP override, you may continue.");
                        }
                    }
                }
            }
        }


        return true;
    }

    public static boolean set_addr_ok(Player player, String postal_town) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            String slocation = C_Postoffice.get_local_po_location_by_name(postal_town);
            Location location = Util.str2location(slocation);
            if (is_this_a_town_by_loc(location)) {
                if (!is_this_a_town_by_loc(player)) {
                    Util.pinform(player, "&e&oYou are outside the town limits.");
                    if (!player.isOp()) {
                        return false;
                    }
                    Util.pinform(player, "&6&oOP override, you may continue.");
                }

                String t_name = get_town_name_by_loc(player);
                if (!t_name.equals("null")) {
                    double blocks = get_town_blocks_by_tvrs(t_name);
                    if (blocks > 0.0D) {
                        double address_count = C_Towny.towny_addr_count(t_name);
                        if (address_count > 0.0D) {
                            double blocks_per_addr = C_Towny.blocks_per_addr();
                            double result = blocks / (blocks_per_addr * address_count);
                            Util.pinform(player, t_name + " has " + (int) blocks + " blocks and " + (int) address_count + " addresses");
                            Util.pinform(player, "&e&oYou are allowed one address for every " + (int) blocks_per_addr + " blocks");
                            if (result < 1.0D) {
                                Util.pinform(player, "&e&oYou do not have enough town blocks.");
                                if (!player.isOp()) {
                                    return false;
                                }
                                Util.pinform(player, "&6&oOP override, you may continue.");
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public static boolean is_waypoint_limit(Player player, String postal_town, String saddress) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in) &&
                (is_this_a_town_by_loc(player))) {
            int last_waypoint = C_Route.get_last_waypoint_position(postal_town, saddress);
            int allowed_points = C_Towny.wypnts_per_addr();
            if (last_waypoint >= allowed_points) {
                Util.pinform(player, "&e&oYou have reached your waypoint count limit");
                Util.pinform(player, "&e&oTry '/new' and space them out more.");
                if (!player.isOp()) {
                    return true;
                }
                Util.pinform(player, "&6&oOP override, you may continue.");
            }
        }


        return false;
    }

    public static boolean does_route_comply(Player player, String postal_town, String saddress) {
        if ((player == null) || (!player.isOnline())) {
            return false;
        }

        if (!is_this_a_town_by_loc(player)) {
            return true;
        }

        if (C_Route.is_waypoint_defined(postal_town, saddress, 0)) {
            String slocation = C_Route.get_waypoint_location(postal_town, saddress, 0);
            Location way_loc = Util.str2location(slocation);
            String wp_town = get_town_name_by_loc(way_loc);
            slocation = C_Postoffice.get_local_po_location_by_name(postal_town);
            Location po_loc = Util.str2location(slocation);
            String po_town = get_town_name_by_loc(po_loc);
            int dist_allowed = VA_postal.search_distance;
            if ((way_loc.distance(po_loc) <= dist_allowed) && (wp_town.equalsIgnoreCase(po_town))) {
                slocation = C_Route.get_last_waypoint_location(postal_town, saddress);
                way_loc = Util.str2location(slocation);
                wp_town = get_town_name_by_loc(way_loc);
                if (is_towny_admin_by_db(player, postal_town)) {
                    if (wp_town.equalsIgnoreCase(po_town)) {
                        return true;
                    }
                } else {
                    Player plot_owner = towny_addr_owner_by_loc(way_loc).getValue();
                    if (plot_owner == player) {
                        return true;
                    }
                }
                Util.pinform(player, "&e&oFinal waypoint not on owner's plot or correct town.");
            } else {
                Util.pinform(player, "&e&oStarting waypoint is not close enough to PO chest.");
            }
        }
        if (player.isOp()) {
            Util.pinform(player, "&6&oOP override, you may continue.");
            return true;
        }
        C_Dispatcher.open_address(postal_town, saddress, false);
        return false;
    }


    public static void list_towny_locals(Player player) {
        String stown;
        Player owner;
        String sowner;
        String sworld;
        String display;
        String path = GetConfig.path_format("postoffice.local");
        ConfigurationSection cs = VA_postal.configsettings.getConfigurationSection(path);
        if (cs != null) {
            int hits = 0;
            Set<String> child_keys = cs.getKeys(false);
            if (child_keys.size() > 0) {
                Object[] a_child_keys = child_keys.toArray();
                Arrays.sort(a_child_keys);
                for (Object a_child_key : a_child_keys) {
                    stown = a_child_key.toString().trim();
                    if (C_Towny.is_towny_town_defined(stown)) {
                        if (hits == 0) {
                            Util.pinform(player, "&7&oTowny Post Offices: ");
                        }
                        owner = C_Owner.get_owner_local_po(stown);
                        if (owner != null) {
                            sowner = "&7&oServer";
                        } else {
                            sowner = "&f&r" + owner.getDisplayName();
                        }
                        sowner = fixed_len(sowner + "&7&o", 28, "-");
                        sworld = "&7&o" + get_world(C_Postoffice.get_local_po_location_by_name(stown));
                        stown = fixed_len("&f&r" + Util.df(stown) + "&7&o", 28, "-");
                        display = stown + sowner + sworld;
                        Util.pinform(player, " " + display);
                        hits++;
                    }
                }
            }
            if (hits > 0) {
                Util.pinform(player, "&7&oGeneral Postal post offices:");
            }
        }
    }

    public static void list_towny_addr(Player player, String spoffice) {
        String saddress;
        String sowner;
        Player owner;
        String sinterval;
        String sworld = "";
        String display;
        String path = GetConfig.path_format("address." + spoffice);
        ConfigurationSection cs = VA_postal.configsettings.getConfigurationSection(path);
        if (cs != null) {
            if (C_Towny.is_towny_town_defined(spoffice)) {
                String towny_town = C_Towny.get_towny_town(spoffice);
                Util.pinform(player, "&7&oPost Office: " + Util.df(spoffice) + " in Towny town: " + Util.df(towny_town));
            } else {
                Util.pinform(player, "&7&oPost Office for Postal town: " + Util.df(spoffice));
            }

            Set<String> child_keys = cs.getKeys(false);
            if (child_keys.size() > 0) {
                Object[] a_child_keys = child_keys.toArray();
                Arrays.sort(a_child_keys);
                int total = 0;
                try {
                    for (Object a_child_key : a_child_keys) {
                        saddress = a_child_key.toString().trim();
                        sinterval = C_Address.get_addr_interval(spoffice, saddress);
                        total += Integer.parseInt(sinterval);
                        sinterval = "&7&oSeconds: &f&r" + sinterval;
                        saddress = fixed_len(Util.df(saddress) + "&7&o", 24, "-");
                        owner = C_Owner.get_owner_address(spoffice, saddress);
                        if (owner != null) {
                            sowner = "&7&oServer";
                        } else {
                            sowner = "&f&r" + owner.getDisplayName();
                        }
                        sowner = fixed_len(sowner + "&7&o", 28, "-");
                        display = saddress + sowner + sinterval;
                        Util.pinform(player, " " + display);
                    }
                } catch (Exception e) {
                    return;
                }
                Util.pinform(player, "&7&oTotal of all routes in seconds: &f&r" + total);
            }
        }
    }

    public static void list_towny_tree(Player player, boolean detail, String p_poffice, boolean srch_by_tny) {
        if (p_poffice != null) {
            p_poffice = p_poffice.toLowerCase().trim();
        }
        String[] list = C_Arrays.towny_list_sorted();
        if (list == null) {
            return;
        }

        String fmt_po = "&6&l";
        String fmt_addr = "&f&l";
        String fmt_ownr = "&a&o";
        String fmt_wrld = "&f&o";
        String fmt_tny = "&f&l";
        String fmt_myr = "&a&l";
        String fmt_fill = "-";
        if (player == null) {
            fmt_tny = AnsiColor.WHITE;
            fmt_po = AnsiColor.CYAN + "";
            fmt_addr = AnsiColor.WHITE;
            fmt_ownr = "\033[0;33m";
            fmt_myr = "\033[0;33m";
            fmt_wrld = "\033[0;32m";
            fmt_fill = ".";
        }

        String town;
        String poffice;
        String address;
        String l_town = "";
        String l_poffice = "";
        String sowner;
        Player owner;
        String smayor;
        Player mayor;
        Map.Entry<String, Player> m_entry;
        String slocation;
        Location location;
        String disp;
        String sworld;
        String distance;
        String heading;

        if (player != null) {
            Util.pinform(player, "");
            Util.pinform(player, "&7&oGeneral list:");
        }

        for (int i = 0; i < list.length; i++) {
            String[] parts = list[i].split(",");
            if (parts.length == 3) {
                town = parts[0];
                poffice = parts[1];
                address = parts[2];


                if (srch_by_tny ?
                        (p_poffice != null) && (!town.contains(p_poffice)) :
                        (p_poffice == null) || (poffice.contains(p_poffice))) {


                    boolean first_towny = false;


                    if (!town.equals(l_town)) {
                        first_towny = true;
                        String dtown;
                        if (town.equals("postal")) {
                            dtown = "Postal Post Offices";
                        } else {
                            dtown = town;
                        }
                        slocation = C_Postoffice.get_local_po_location_by_name(poffice);
                        location = Util.str2location(slocation);
                        m_entry = towny_mayor_by_loc(location);
                        if (!Objects.equals(m_entry.getKey(), "OK")) {
                            smayor = "";
                        } else {
                            smayor = m_entry.getValue().getDisplayName();
                        }
                        disp = fmt_tny + fixed_len(dtown.toUpperCase(), 19, fmt_fill);
                        disp = disp + " " + fmt_myr + smayor.toUpperCase();
                        if (player == null) {
                            if (i != 0) {
                                Util.cinform("");
                            }
                            Util.cinform(disp);
                        } else {
                            if (i != 0) {
                                Util.pinform(player, "");
                            }
                            Util.pinform(player, disp);
                        }
                    }


                    if (!poffice.equals(l_poffice)) {
                        owner = VA_postal.SERVER;
                        if (C_Owner.is_local_po_owner_defined(poffice)) {
                            owner = C_Owner.get_owner_local_po(poffice);
                        }
                        sworld = C_List.get_world(C_Postoffice.get_local_po_location_by_name(poffice));
                        disp = fmt_po + fixed_len(poffice.toUpperCase(), 17, fmt_fill);
                        disp = disp + " " + fmt_ownr + owner + " " + fmt_wrld + Util.df(sworld);
                        if (player == null) {
                            if ((!first_towny) && (detail) && (i != 0)) {
                                Util.cinform("");
                            }
                            Util.cinform("  " + disp);
                        } else {
                            if ((!first_towny) && (detail) && (i != 0)) {
                                Util.pinform(player, "");
                            }
                            Util.pinform(player, "  " + disp);
                        }
                    }


                    if (detail) {
                        sowner = " server";
                        if (C_Owner.is_address_owner_defined(poffice, address)) {
                            sowner = " " + C_Owner.get_owner_address(poffice, address).getDisplayName().toLowerCase();
                        }
                        disp = fmt_addr + fixed_len(Util.df(address), 16, fmt_fill);
                        disp = disp + fmt_ownr + sowner;
                        if (player == null) {
                            Util.cinform("    " + disp);
                        } else {
                            Util.pinform(player, "    " + disp);
                        }
                    }

                    l_town = town;
                    l_poffice = poffice;
                }
            }
        }


        if (player != null) {
            Util.pinform(player, "");
            Util.pinform(player, "&7&oClose to your position:");
            list = C_Arrays.geo_po_list_sorted(player);
            if (list == null) {
                return;
            }
            for (int i = 0; i < list.length; i++) {
                String[] parts = list[i].split(",");
                if (parts.length == 3) {
                    distance = Util.int2str(Util.str2int(parts[0]));
                    poffice = fixed_len(parts[1].toUpperCase(), 16, fmt_fill);
                    heading = parts[2];


                    if (i >= 3) break;
                    disp = fmt_po + poffice + "&f&l " + heading + "&a&o " + distance + "&a&o blocks away";
                    Util.pinform(player, disp);
                }
            }
        }
    }


    public static String get_town_name_by_loc(Player player) {
        return get_town_name_by_loc(player.getLocation());
    }

    public static String get_town_name_by_loc(Location location) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            WorldCoord wc = WorldCoord.parseWorldCoord(location);
            Coord coord = wc.getCoord();
            TownyWorld tworld;
            try {
                tworld = wc.getTownyWorld();
            } catch (NotRegisteredException e) {
                return "null";
            }
            if (tworld.hasTownBlock(coord)) {
                TownBlock tblock;
                try {
                    tblock = wc.getTownBlock();
                } catch (NotRegisteredException e) {
                    return "null";
                }
                if (tblock.hasTown()) {
                    try {
                        return tblock.getTown().getName();
                    } catch (NotRegisteredException e) {
                        return "null";
                    }
                }
            }
        }
        return "null";
    }

    public static String get_town_uid_by_loc(Location location) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            WorldCoord wc = WorldCoord.parseWorldCoord(location);
            Coord coord = wc.getCoord();
            TownyWorld tworld;
            try {
                tworld = wc.getTownyWorld();
            } catch (NotRegisteredException e) {
                return null;
            }
            if (tworld.hasTownBlock(coord)) {
                TownBlock tblock;
                try {
                    tblock = wc.getTownBlock();
                } catch (NotRegisteredException e) {
                    return null;
                }
                if (tblock.hasTown()) {
                    try {
                        int uid = tblock.getTown().getUID();
                        return Integer.toHexString(uid);
                    } catch (NotRegisteredException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public static Map.Entry<String, Player> towny_addr_owner_by_loc(Location location) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            WorldCoord wc = WorldCoord.parseWorldCoord(location);
            Coord coord = wc.getCoord();
            TownyWorld tworld;
            try {
                tworld = wc.getTownyWorld();
            } catch (NotRegisteredException e) {
                return entry("not_towny", null);
            }
            if (tworld.hasTownBlock(coord)) {
                TownBlock tblock;
                Resident resident;
                try {
                    tblock = wc.getTownBlock();
                } catch (NotRegisteredException e) {
                    return entry("not_towny", null);
                }
                try {
                    resident = tblock.getResident();
                } catch (NotRegisteredException e) {
                    return entry("un_owned_plot", null);
                }
                TownBlockOwner towner = resident;
                if (tblock.isOwner(towner)) {
                    return entry("OK", (Player) resident);
                }
                return entry("un_owned_plot", null);
            }
        }

        return entry("not_towny", null);
    }

    public static boolean is_towny_plot_owner_by_loc(Player player) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            Player nplayer = towny_addr_owner_by_loc(player.getLocation()).getValue();
            if (nplayer != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean is_towny_admin_by_loc(Player player) {
        String splayer;
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            splayer = player.getName();
            Location location = player.getLocation();
            WorldCoord wc = WorldCoord.parseWorldCoord(location);
            Coord coord = wc.getCoord();
            TownyWorld tworld;
            try {
                tworld = wc.getTownyWorld();
            } catch (NotRegisteredException e) {
                return false;
            }
            if (tworld.hasTownBlock(coord)) {
                TownBlock tblock;
                Town town;
                try {
                    tblock = wc.getTownBlock();
                } catch (NotRegisteredException e) {
                    return false;
                }
                try {
                    town = tblock.getTown();
                } catch (NotRegisteredException e) {
                    return false;
                }
                String mayor = town.getMayor().getName();
                if (mayor.equalsIgnoreCase(splayer)) {
                    return true;
                }

                for (Resident resident_i : town.getAssistants()) {
                    if ((resident_i.getName().equalsIgnoreCase(splayer)) &&
                            (resident_i.hasTownRank("postal"))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean is_this_a_town_by_loc(Player player) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            String splayer = player.getName();
            Location location = player.getLocation();
            return is_this_a_town_by_loc(location);
        }
        return false;
    }

    public static boolean is_this_a_town_by_loc(Location location) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            WorldCoord wc = WorldCoord.parseWorldCoord(location);
            Coord coord = wc.getCoord();
            TownyWorld tworld;
            try {
                tworld = wc.getTownyWorld();
            } catch (NotRegisteredException e) {
                return false;
            }
            if (tworld.hasTownBlock(coord)) {
                TownBlock tblock;
                Town town = null;
                try {
                    tblock = wc.getTownBlock();
                } catch (NotRegisteredException e) {
                    return false;
                }
                if (tblock.hasTown()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Map.Entry<String, Player> towny_mayor_by_loc(Location location) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            WorldCoord wc = WorldCoord.parseWorldCoord(location);
            Coord coord = wc.getCoord();
            TownyWorld tworld;
            try {
                tworld = wc.getTownyWorld();
            } catch (NotRegisteredException e) {
                return new AbstractMap.SimpleEntry<>("not_towny", null);
            }
            if (tworld.hasTownBlock(coord)) {
                TownBlock tblock;
                Town town;
                Resident resident;
                try {
                    tblock = wc.getTownBlock();
                } catch (NotRegisteredException e) {
                    return new AbstractMap.SimpleEntry<>("not_towny", null);
                }
                try {
                    town = tblock.getTown();
                } catch (NotRegisteredException e) {
                    return new AbstractMap.SimpleEntry<>("not_mayor", null);
                }
                try {
                    resident = town.getMayor();
                } catch (Exception e) {
                    return new AbstractMap.SimpleEntry<>("not_mayor", null);
                }
                if (resident != null) {
                    return new AbstractMap.SimpleEntry<>("OK", (Player) resident);
                }
                return new AbstractMap.SimpleEntry<>("not_mayor", null);
            }
        }

        return new AbstractMap.SimpleEntry<>("not_towny", null);
    }


    public static int get_town_blocks_by_db(String postal_town) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            if (C_Postoffice.is_local_po_name_defined(postal_town)) {
                String slocation = C_Postoffice.get_local_po_location_by_name(postal_town);
                Location location = Util.str2location(slocation);
                WorldCoord wc = WorldCoord.parseWorldCoord(location);
                Coord coord = wc.getCoord();
                TownyWorld tworld;
                try {
                    tworld = wc.getTownyWorld();
                } catch (NotRegisteredException e) {
                    return -1;
                }
                if (tworld.hasTownBlock(coord)) {
                    TownBlock tblock;
                    Town town;
                    try {
                        tblock = wc.getTownBlock();
                    } catch (NotRegisteredException e) {
                        return -1;
                    }
                    try {
                        town = tblock.getTown();
                    } catch (NotRegisteredException e) {
                        return -1;
                    }
                    return town.getTotalBlocks();
                }
            } else {
                return -1;
            }
        }
        return -1;
    }

    public static String get_town_name_by_db(String postal_town) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            if (C_Postoffice.is_local_po_name_defined(postal_town)) {
                String slocation = C_Postoffice.get_local_po_location_by_name(postal_town);
                Location location = Util.str2location(slocation);
                WorldCoord wc = WorldCoord.parseWorldCoord(location);
                Coord coord = wc.getCoord();
                TownyWorld tworld;
                try {
                    tworld = wc.getTownyWorld();
                } catch (NotRegisteredException e) {
                    return "null";
                }
                if (tworld.hasTownBlock(coord)) {
                    TownBlock tblock;
                    Town town;
                    try {
                        tblock = wc.getTownBlock();
                    } catch (NotRegisteredException e) {
                        return "null";
                    }
                    try {
                        town = tblock.getTown();
                    } catch (NotRegisteredException e) {
                        return "null";
                    }
                    return town.getName();
                }
            } else {
                return "null";
            }
        }
        return "null";
    }

    public static boolean is_towny_by_db(String postal_town) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            if (C_Postoffice.is_local_po_name_defined(postal_town)) {
                String slocation = C_Postoffice.get_local_po_location_by_name(postal_town);
                Location location = Util.str2location(slocation);
                WorldCoord wc = WorldCoord.parseWorldCoord(location);
                Coord coord = wc.getCoord();
                TownyWorld tworld;
                try {
                    tworld = wc.getTownyWorld();
                } catch (NotRegisteredException e) {
                    return false;
                }
                if (tworld.hasTownBlock(coord)) {
                    TownBlock tblock;
                    try {
                        tblock = wc.getTownBlock();
                    } catch (NotRegisteredException e) {
                        return false;
                    }
                    try {
                        tblock.getTown();
                    } catch (NotRegisteredException e) {
                        return false;
                    }
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean is_towny_plot_owner_by_db(Player player, String postal_town, String saddress) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            if (C_Address.is_address_defined(postal_town, saddress)) {
                String slocation = C_Address.get_address_location(postal_town, saddress);
                Location location = Util.str2location(slocation);
                Player plot_owner = towny_addr_owner_by_loc(location).getValue();
                if (player == plot_owner) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean is_towny_admin_by_db(Player player, String postal_town) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            if (C_Postoffice.is_local_po_name_defined(postal_town)) {
                String slocation = C_Postoffice.get_local_po_location_by_name(postal_town);
                Location location = Util.str2location(slocation);
                WorldCoord wc = WorldCoord.parseWorldCoord(location);
                Coord coord = wc.getCoord();
                TownyWorld tworld;
                try {
                    tworld = wc.getTownyWorld();
                } catch (NotRegisteredException e) {
                    return false;
                }
                if (tworld.hasTownBlock(coord)) {
                    TownBlock tblock;
                    Town town;
                    try {
                        tblock = wc.getTownBlock();
                    } catch (NotRegisteredException e) {
                        return false;
                    }
                    try {
                        town = tblock.getTown();
                    } catch (NotRegisteredException e) {
                        return false;
                    }
                    Player mayor = (Player) town.getMayor();
                    if (mayor == player) {
                        return true;
                    }

                    for (Resident resident_i : town.getAssistants()) {
                        if (resident_i == player) {
                            if (resident_i.hasTownRank("postal")) {
                                return true;
                            }

                            if (resident_i.hasTownRank(postal_town)) {
                                return true;
                            }
                        }
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }


    public static boolean is_towny_resident_by_tvrs(Player player) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            String splayer = player.getName();
            TownyUniverse tverse = VA_postal.towny.getTownyUniverse();
            Hashtable<String, Resident> table = tverse.getResidentMap();
            Enumeration<Resident> residents = table.elements();
            while (residents.hasMoreElements()) {
                Resident resident = residents.nextElement();
                String sresident = resident.getName();
                if ((resident.hasTown()) && (sresident.equalsIgnoreCase(splayer))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int get_num_town_residents_by_tvrs(String towny_town) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            TownyUniverse tverse = VA_postal.towny.getTownyUniverse();
            Hashtable<String, Resident> table = tverse.getResidentMap();
            Enumeration<Resident> residents = table.elements();
            int hits = 0;
            while (residents.hasMoreElements()) {
                Resident resident = residents.nextElement();
                if (resident.hasTown()) {
                    Town town;
                    try {
                        town = resident.getTown();
                    } catch (NotRegisteredException e) {
                        continue;
                    }

                    if (town.getName().equalsIgnoreCase(towny_town)) {
                        hits++;
                    }
                }
            }
            return hits;
        }
        return -1;
    }

    public static int get_num_total_residents_by_tvrs() {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            TownyUniverse tverse = VA_postal.towny.getTownyUniverse();
            Hashtable<String, Resident> table = tverse.getResidentMap();
            Enumeration<Resident> residents = table.elements();
            int hits = 0;
            while (residents.hasMoreElements()) {
                Resident resident = residents.nextElement();
                if (resident.hasTown()) {
                    hits++;
                }
            }
            return hits;
        }
        return -1;
    }

    public static String[] get_town_residents_by_tvrs(String towny_town) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            TownyUniverse tverse = VA_postal.towny.getTownyUniverse();
            Hashtable<String, Resident> table = tverse.getResidentMap();
            Enumeration<Resident> residents = table.elements();
            List<String> list = new ArrayList<>();
            while (residents.hasMoreElements()) {
                Resident resident = residents.nextElement();
                if (resident.hasTown()) {
                    Town town;
                    try {
                        town = resident.getTown();
                    } catch (NotRegisteredException e) {
                        continue;
                    }

                    if (town.getName().equalsIgnoreCase(towny_town)) {
                        list.add(resident.getName());
                    }
                }
            }
            String[] result = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = list.get(i);
            }
            return result;
        }
        return null;
    }

    public static String[] get_all_residents_by_tvrs() {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            TownyUniverse tverse = VA_postal.towny.getTownyUniverse();
            Hashtable<String, Resident> table = tverse.getResidentMap();
            Enumeration<Resident> residents = table.elements();
            List<String> list = new ArrayList<>();
            while (residents.hasMoreElements()) {
                Resident resident = residents.nextElement();
                if (resident.hasTown()) {
                    list.add(resident.getName());
                }
            }
            String[] result = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = list.get(i);
            }
            return result;
        }
        return null;
    }

    public static String[] get_towny_towns_by_tvrs() {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            TownyUniverse tverse = VA_postal.towny.getTownyUniverse();
            Hashtable<String, Town> table = tverse.getTownsMap();
            Enumeration<Town> towns = table.elements();
            List<String> list = new ArrayList<>();
            while (towns.hasMoreElements()) {
                Town town = towns.nextElement();
                list.add(town.getName());
            }
            String[] result = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = list.get(i);
            }
            return result;
        }
        return null;
    }

    public static int get_town_blocks_by_tvrs(String towny_town) {
        if ((VA_postal.towny_configured) && (VA_postal.towny_opt_in)) {
            TownyUniverse tverse = VA_postal.towny.getTownyUniverse();
            Hashtable<String, Town> table = tverse.getTownsMap();
            Enumeration<Town> towns = table.elements();
            while (towns.hasMoreElements()) {
                Town town = towns.nextElement();
                if (town.getName().equalsIgnoreCase(towny_town)) {
                    return town.getTotalBlocks();
                }
            }
        }
        return -1;
    }

    public static boolean ok_to_build_towny(Player player) {
        if (VA_postal.towny_configured) {
            if (Cmdexecutor.hasPermission(player, "postal.accept.bypass")) {
                return true;
            }
            Location location = player.getLocation();

            return PlayerCacheUtil.getCachePermission(player, location, player.getWorld().getBlockAt(location).getType(), TownyPermission.ActionType.BUILD);
        }

        return true;
    }

    private static Map.Entry<String, Player> entry(String s, Player p) {
        return new AbstractMap.SimpleEntry<>(s, p);
    }

    public static synchronized String get_world(String slocation) {
        try {
            String[] parts;
            parts = slocation.split(",");
            return proper(parts[0]).trim();
        } catch (Exception ignored) {
        }
        return "null";
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

    public static synchronized String fixed_len(String input, int len, String filler) {
        try {
            input = input.trim();

            if (input.length() >= len) {
                return input.substring(0, len);
            }

            StringBuilder inputBuilder = new StringBuilder(input);
            while (inputBuilder.length() < len) {
                inputBuilder.append(filler);
            }
            input = inputBuilder.toString();
            return input;
        } catch (Exception e) {
            StringBuilder blank = new StringBuilder();
            for (int i = 0; i < len; i++) {
                blank.append(filler);
            }
            return blank.toString();
        }
    }
}
