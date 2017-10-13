package com.vodhanel.minecraft.va_postal.common;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.config.C_Arrays;
import com.vodhanel.minecraft.va_postal.config.C_Owner;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.awt.*;

public class Util {
    VA_postal plugin;

    public Util(VA_postal instance) {
        plugin = instance;
    }

    public static String location2str(Location loc) {
        if (loc != null) {
            try {
                String str_loc = loc.getWorld().getName() + ",";
                double x = (int) Math.floor(loc.getX());
                str_loc = str_loc + Double.toString(x) + ",";
                double y = (int) Math.floor(loc.getY());
                str_loc = str_loc + Double.toString(y) + ",";
                double z = (int) Math.floor(loc.getZ());
                return str_loc + Double.toString(z);
            } catch (Exception e) {
            }
        }

        return "null";
    }

    public static Location str2location(String str) {
        if ((str == null) || ("null".equals(str)) || (str.trim().isEmpty())) {
            Util.dinform(AnsiColor.RED + "str2location: NULL");
            return null;
        }
        Location location;
        try {
            String[] arg = str.trim().split(",");
            double[] parsed = new double[3];
            for (int a = 0; a < 3; a++) {
                parsed[a] = ((int) Double.parseDouble(arg[(a + 1)].trim()));
            }
            location = new Location(Bukkit.getWorld(arg[0]), parsed[0], parsed[1], parsed[2], 0.0F, 0.0F);
            // Util.dinform("str2location: LOC IS "+location);
        } catch (NumberFormatException numberFormatException) {
            Util.dinform(AnsiColor.RED + "STR2LOCATION FAIL: " + str + " " + numberFormatException);
            return null;
        }
        return location;
    }

    public static double get_2d_distance(Location loc_1, Location loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            Location base = simplified_copy(loc_1);
            base.setY(loc_2.getY());
            return base.distance(loc_2);
        }
        return -1.0D;
    }

    public static double get_2d_distance(String loc_1, String loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            Location base = simplified_copy(loc_1);
            Location target = simplified_copy(loc_2);
            return get_2d_distance(base, target);
        }
        return -1.0D;
    }

    public static double get_2d_distance(Location loc_1, String loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            Location base = simplified_copy(loc_1);
            Location target = simplified_copy(loc_2);
            return get_2d_distance(base, target);
        }
        return -1.0D;
    }

    public static double get_2d_distance(String loc_1, Location loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            Location base = simplified_copy(loc_1);
            Location target = simplified_copy(loc_2);
            return get_2d_distance(base, target);
        }
        return -1.0D;
    }

    public static double get_3d_distance(Location loc_1, Location loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            return loc_1.distance(loc_2);
        }
        return -1.0D;
    }

    public static double get_3d_distance(String loc_1, String loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            Location base = simplified_copy(loc_1);
            Location target = simplified_copy(loc_2);
            return get_3d_distance(base, target);
        }
        return -1.0D;
    }

    public static double get_3d_distance(Location loc_1, String loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            Location base = simplified_copy(loc_1);
            Location target = simplified_copy(loc_2);
            return get_3d_distance(base, target);
        }
        return -1.0D;
    }

    public static double get_3d_distance(String loc_1, Location loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            Location base = simplified_copy(loc_1);
            Location target = simplified_copy(loc_2);
            return get_3d_distance(base, target);
        }
        return -1.0D;
    }

    public static boolean are_wpts_equal_2d(Location loc_1, Location loc_2, int elev_range) {
        if ((loc_1 != null) && (loc_2 != null)) {
            double l1x = (int) Math.floor(loc_1.getX());
            double l1y = (int) Math.floor(loc_1.getY());
            double l1z = (int) Math.floor(loc_1.getZ());
            double l2x = (int) Math.floor(loc_2.getX());
            double l2y = (int) Math.floor(loc_2.getY());
            double l2z = (int) Math.floor(loc_2.getZ());
            double ydif = Math.abs(l1y - l2y);
            if ((l1x == l2x) && (l1z == l2z) && (ydif <= elev_range)) {
                return true;
            }
        }
        return false;
    }

    public static boolean are_wpts_equal_2d(String sloc_1, Location loc_2, int elev_range) {
        if ((sloc_1 != null) && (loc_2 != null)) {
            return are_wpts_equal_2d(str2location(sloc_1), loc_2, elev_range);
        }
        return false;
    }

    public static boolean are_wpts_equal_2d(Location loc_1, String sloc_2, int elev_range) {
        if ((loc_1 != null) && (sloc_2 != null)) {
            return are_wpts_equal_2d(loc_1, str2location(sloc_2), elev_range);
        }
        return false;
    }

    public static boolean are_wpts_equal_3d(Location loc_1, Location loc_2) {
        if ((loc_1 != null) && (loc_2 != null)) {
            double l1x = (int) Math.floor(loc_1.getX());
            double l1y = (int) Math.floor(loc_1.getY());
            double l1z = (int) Math.floor(loc_1.getZ());
            double l2x = (int) Math.floor(loc_2.getX());
            double l2y = (int) Math.floor(loc_2.getY());
            double l2z = (int) Math.floor(loc_2.getZ());
            if ((l1x == l2x) && (l1y == l2y) && (l1z == l2z)) {
                return true;
            }
        }
        return false;
    }

    public static boolean are_wpts_equal_3d(String sloc_1, Location loc_2) {
        if ((sloc_1 != null) && (loc_2 != null)) {
            return are_wpts_equal_3d(str2location(sloc_1), loc_2);
        }
        return false;
    }

    public static boolean are_wpts_equal_3d(Location loc_1, String sloc_2) {
        if ((loc_1 != null) && (sloc_2 != null)) {
            return are_wpts_equal_3d(loc_1, str2location(sloc_2));
        }
        return false;
    }


    public static Location offset_from_ref(Location ref_loc, int X, int Y, int Z) {
        if (ref_loc != null) {
            double x = (int) Math.floor(ref_loc.getX()) + X;
            double y = (int) Math.floor(ref_loc.getY()) + Y;
            double z = (int) Math.floor(ref_loc.getZ()) + Z;
            return new Location(ref_loc.getWorld(), x, y, z, 0.0F, 0.0F);
        }
        return null;
    }

    public static Location simplified_copy(Location ref_loc) {
        if (ref_loc != null) {
            double x = (int) Math.floor(ref_loc.getX());
            double y = (int) Math.floor(ref_loc.getY());
            double z = (int) Math.floor(ref_loc.getZ());
            return new Location(ref_loc.getWorld(), x, y, z, 0.0F, 0.0F);
        }
        Util.dinform(AnsiColor.RED + "simplified_copy RETURNS NULL");
        return null;
    }

    public static Location simplified_copy(String slocation) {
        //Util.dinform("simplified_copy: "+slocation);
        if (slocation != null) {
            return simplified_copy(str2location(slocation));
        }
        return null;
    }

    public static String int2str(int value) {
        String svalue = "0";
        try {
            svalue = Integer.toString(value).trim();
        } catch (Exception e) {
            svalue = "0";
        }
        if (svalue != null) {
            return svalue;
        }
        return "0";
    }

    public static int str2int(String svalue) {
        if (svalue == null) {
            return 0;
        }
        int value = 0;
        try {
            value = Integer.parseInt(svalue);
        } catch (NumberFormatException numberFormatException) {
            value = 0;
        }
        return value;
    }

    public static String long2str(int value) {
        String svalue = "0";
        try {
            svalue = Long.toString(value).trim();
        } catch (Exception e) {
            svalue = "0";
        }
        if (svalue != null) {
            return svalue;
        }
        return "0";
    }

    public static long str2long(String sinput) {
        long result;
        try {
            result = Long.parseLong(sinput);
        } catch (NumberFormatException numberFormatException) {
            result = -1L;
        }
        return result;
    }

    public static String double2str(double value) {
        String svalue = "0";
        try {
            svalue = Double.toString(value).trim();
        } catch (Exception e) {
            svalue = "0";
        }
        if (svalue != null) {
            return svalue;
        }
        return "0";
    }

    public static double str2double(String svalue) {
        if (svalue == null) {
            return 0.0D;
        }
        double value = 0.0D;
        try {
            value = Double.parseDouble(svalue);
        } catch (NumberFormatException numberFormatException) {
            value = 0.0D;
        }
        return value;
    }

    public static String float2str(float value) {
        String svalue = "0";
        try {
            svalue = Float.toString(value).trim();
        } catch (Exception e) {
            svalue = "0";
        }
        if (svalue != null) {
            return svalue;
        }
        return "0";
    }

    public static float str2float(String svalue) {
        if (svalue == null) {
            return 0.0F;
        }
        float value = 0.0F;
        try {
            value = Float.parseFloat(svalue);
        } catch (NumberFormatException numberFormatException) {
            value = 0.0F;
        }
        return value;
    }

    public static String int2fstr_leading_zeros(int value, int places) {
        String input = int2str(value);
        try {
            input = input.trim();

            if (input.length() > places) {
                String max = "";
                for (int i = 0; i < places; i++) {
                    max = max + "9";
                }
                return max;
            }

            while (input.length() < places) {
                input = "0" + input;
            }
            return input;
        } catch (Exception e) {
            String zero = "";
            for (int i = 0; i < places; i++) {
                zero = zero + "0";
            }
            return zero;
        }
    }

    public static void calibrate_compass(Player player) {
        if (player == null) {
            return;
        }
        Location loc_ref = player.getWorld().getBlockAt(0, 0, -12550820).getLocation();
        player.setCompassTarget(loc_ref);
    }

    public static double get_direction_to_target(Player player, Location loc_target) {
        if ((player == null) || (loc_target == null)) {
            return -1000.0D;
        }
        loc_target.setY(0.0D);
        Location loc_center = player.getLocation();
        loc_center.setY(0.0D);
        Location loc_ref = player.getWorld().getBlockAt(0, 0, -12550820).getLocation();
        Point center = new Point();
        center.setLocation(loc_center.getX(), loc_center.getZ());
        Point reference = new Point();
        reference.setLocation(loc_ref.getX(), loc_ref.getZ());
        Point target = new Point();
        target.setLocation(loc_target.getX(), loc_target.getZ());
        double result = -1000.0D;
        try {
            result = Math.toDegrees(Math.atan2(target.x - center.x, target.y - center.y) - Math.atan2(reference.x - center.x, reference.y - center.y));
        } catch (Exception e) {
            return -1000.0D;
        }

        if (result < 0.0D) {
            result += 360.0D;
        }
        result -= 180.0D;
        result *= -1.0D;
        return result;
    }

    public static String get_heading_from_direction(double dir) {
        if ((157.5D <= dir) || (dir < -157.5D))
            return " N ";
        if ((-157.5D <= dir) && (dir < -112.5D))
            return "NE";
        if ((-112.5D <= dir) && (dir < -67.5D))
            return " E ";
        if ((-67.5D <= dir) && (dir < -22.5D))
            return "SE";
        if (((-22.5D <= dir) && (dir < 0.0D)) || ((0.0D <= dir) && (dir < 22.5D)))
            return " S ";
        if ((22.5D <= dir) && (dir < 67.5D))
            return "SW";
        if ((67.5D <= dir) && (dir < 112.5D))
            return " W ";
        if ((112.5D <= dir) && (dir < 157.5D)) {
            return "NW";
        }
        return null;
    }

    public static String get_fmt_heading_to_target(Player player, Location target) {
        if ((player == null) || (target == null)) {
            return null;
        }
        double direction = get_direction_to_target(player, target);
        if ((direction >= -180.0D) && (direction <= 180.0D) &&
                (player.getWorld() == target.getWorld())) {
            return get_heading_from_direction(direction);
        }

        return null;
    }

    public static void list_postal_tree(Player player, boolean detail, String p_poffice) {
        if (p_poffice != null) {
            p_poffice = p_poffice.toLowerCase().trim();
        }
        String[] list = C_Arrays.postal_list_sorted();
        if (list == null) {
            return;
        }

        String fmt_po = "&6&l";
        String fmt_addr = "&f&l";
        String fmt_ownr = "&a&o";
        String fmt_wrld = "&f&o";
        String fmt_fill = "-";
        if (player == null) {
            fmt_po = AnsiColor.CYAN + "";
            fmt_addr = AnsiColor.WHITE;
            fmt_ownr = "\033[0;33m";
            fmt_wrld = "\033[0;32m";
            fmt_fill = ".";
        }

        String poffice = "";
        String address = "";
        String l_poffice = "";
        String owner = "";
        String disp = "";
        String sworld = "";
        String distance = "";
        String heading = "";

        if (player != null) {
            pinform(player, "");
            pinform(player, "&7&oGeneral list:");
        }

        for (int i = 0; i < list.length; i++) {
            String[] parts = list[i].split(",");
            if ((parts != null) && (parts.length == 2)) {
                poffice = parts[0];
                address = parts[1];


                if ((p_poffice == null) || (poffice.contains(p_poffice))) {


                    if (!poffice.equals(l_poffice)) {
                        owner = "Server";
                        disp = "";
                        if (C_Owner.is_local_po_owner_defined(poffice)) {
                            owner = Util.df(C_Owner.get_owner_local_po(poffice));
                        }
                        sworld = com.vodhanel.minecraft.va_postal.config.C_List.get_world(com.vodhanel.minecraft.va_postal.config.C_Postoffice.get_local_po_location_by_name(poffice));
                        disp = fmt_po + fixed_len(poffice.toUpperCase(), 16, fmt_fill);
                        disp = disp + " " + fmt_ownr + owner + " " + fmt_wrld + Util.df(sworld);
                        if ((player == null) && (i != 0)) {
                            if ((detail) && (i != 0)) {
                                cinform("");
                            }
                            cinform(disp);
                        } else {
                            if ((detail) && (i != 0)) {
                                pinform(player, "");
                            }
                            pinform(player, disp);
                        }
                    }


                    if (detail) {
                        owner = " server";
                        disp = "";
                        if (C_Owner.is_address_owner_defined(poffice, address)) {
                            owner = " " + C_Owner.get_owner_address(poffice, address).toLowerCase();
                        }
                        disp = fmt_addr + fixed_len(Util.df(address), 16, fmt_fill);
                        disp = disp + fmt_ownr + owner;
                        if (player == null) {
                            cinform("   " + disp);
                        } else {
                            pinform(player, "   " + disp);
                        }
                    }
                    l_poffice = poffice;
                }
            }
        }
        list = null;


        if (player != null) {
            pinform(player, "");
            pinform(player, "&7&oClose to your position:");
            list = C_Arrays.geo_po_list_sorted(player);
            if (list == null) {
                return;
            }
            for (int i = 0; i < list.length; i++) {
                String[] parts = list[i].split(",");
                if ((parts != null) && (parts.length == 3)) {
                    distance = int2str(str2int(parts[0]));
                    poffice = fixed_len(parts[1].toUpperCase(), 16, fmt_fill);
                    heading = parts[2];


                    if (i >= 3) break;
                    disp = fmt_po + poffice + "&f&l " + heading + "&a&o  " + distance + "&a&o  blocks away";
                    pinform(player, disp);
                }
            }
        }
    }


    public static String name_validate(String name_in) {
        String name_out = name_in.toLowerCase().trim();
        name_out = name_out.replace(".", "_");
        name_out = name_out.replace(",", "_");
        name_out = name_out.replace("'", "");
        if (name_out.length() > 15) {
            name_out = name_out.substring(0, 15);
        }
        return name_out;
    }

    public static int time_stamp() {
        try {
            long time = System.currentTimeMillis() / 1000L;
            return (int) time;
        } catch (Exception e) {
        }
        return -1;
    }

    public static String stime_stamp() {
        try {
            long time = System.currentTimeMillis() / 1000L;
            return Long.toString(time);
        } catch (Exception e) {
        }
        return "null";
    }

    public static String s_adj_time_stamp(int adj) {
        try {
            long time = System.currentTimeMillis() / 1000L + adj;
            return Long.toString(time);
        } catch (Exception e) {
        }
        return "null";
    }

    public static Block str2block(String str) {
        if ((str == null) || ("null".equals(str)) || (str.trim().isEmpty())) {
            return null;
        }
        Location location = null;
        try {
            String[] arg = str.trim().split(",");
            double[] parsed = new double[3];
            for (int a = 0; a < 3; a++) {
                parsed[a] = Double.parseDouble(arg[(a + 1)].trim());
            }
            location = new Location(Bukkit.getWorld(arg[0]), parsed[0], parsed[1], parsed[2]);
        } catch (NumberFormatException numberFormatException) {
            return null;
        }
        World w = location.getWorld();
        Block block = w.getBlockAt(location);
        return block;
    }

    public static World str2world(String sworld) {
        try {
            return Bukkit.getWorld(sworld);
        } catch (Exception e) {
        }
        return null;
    }

    public static String get_world(String slocation) {
        try {
            String[] parts = new String[4];
            parts = slocation.split(",");
            return proper(parts[0]).trim();
        } catch (Exception e) {
        }
        return "null";
    }

    public static Player str2player(String splayer) {
        try {
            return Bukkit.getPlayer(splayer);
        } catch (Exception e) {
        }
        return null;
    }

    public static String get_str_sender_location(org.bukkit.command.CommandSender sender) {
        Location loc = null;
        try {
            Player player = (Player) sender;
            loc = player.getLocation();
        } catch (Exception e) {
            return null;
        }
        return location2str(loc);
    }

    public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90.0F) % 360.0F;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }
        if ((0.0D <= rotation) && (rotation < 22.5D))
            return "N";
        if ((22.5D <= rotation) && (rotation < 67.5D))
            return "NE";
        if ((67.5D <= rotation) && (rotation < 112.5D))
            return "E";
        if ((112.5D <= rotation) && (rotation < 157.5D))
            return "SE";
        if ((157.5D <= rotation) && (rotation < 202.5D))
            return "S";
        if ((202.5D <= rotation) && (rotation < 247.5D))
            return "SW";
        if ((247.5D <= rotation) && (rotation < 292.5D))
            return "W";
        if ((292.5D <= rotation) && (rotation < 337.5D))
            return "NW";
        if ((337.5D <= rotation) && (rotation < 360.0D)) {
            return "N";
        }
        return null;
    }

    public static void pinform(Player player, String message) {
        if ((message == null) || (message.isEmpty())) {
            return;
        }
        if (player != null) {
            try {
                message = message.trim();

                player.sendMessage(com.vodhanel.minecraft.va_postal.common.MC_format.ColourUtils.format(message));
            } catch (Exception e) {
                player.sendMessage("Error Formatting Message.");
            }
        }
    }

    public static void spinform(String splayer, String message) {
        Player player = str2player(splayer);
        if (player != null) {
            try {
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
            } catch (Exception e) {
            }
        }
    }

    public static void spinform_if_online(String splayer, String message) {
        Player player = str2player(splayer);
        if (player != null) {
            try {
                player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
            } catch (Exception e) {
            }
        }
    }

    public static void binform(String message) {
        try {
            VA_postal.plugin.getServer().broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes("&".charAt(0), message));
        } catch (Exception e) {
        }
    }

    public static void cinform(String message) {
        if ((!VA_postal.quiet) &&
                (message != null) && (!message.isEmpty())) {
            try {
                System.out.println(message + AnsiColor.WHITE);
            } catch (Exception e) {
            }
        }
    }

    public static void perm_inform(String message) {
        if ((VA_postal.permtalk) &&
                (message != null) && (!message.isEmpty())) {
            message = "\033[1;32m[Postal] \033[1;37m" + message;
            try {
                System.out.println(message + AnsiColor.WHITE);
            } catch (Exception e) {
            }
        }
    }

    public static void con_type(String message) {
        if ((message != null) && (!message.isEmpty())) {
            try {
                System.out.println(message + AnsiColor.WHITE);
            } catch (Exception e) {
            }
        }
    }

    public static void dinform(String message) {
        if ((message != null) && (!message.isEmpty()) && (VA_postal.debug)) {
            try {
                System.out.println("\033[1;33m" + message + AnsiColor.WHITE);
            } catch (Exception e) {
            }
        }
    }

    public static boolean set_gamemode(Player player, String mode) {
        if (player == null) {
            return false;
        }
        try {
            if ("creative".equalsIgnoreCase(mode)) {
                player.setGameMode(org.bukkit.GameMode.CREATIVE);
                return true;
            }
            if ("survival".equalsIgnoreCase(mode)) {
                player.setGameMode(org.bukkit.GameMode.SURVIVAL);
                return true;
            }
            if ("adventure".equalsIgnoreCase(mode)) {
                player.setGameMode(org.bukkit.GameMode.ADVENTURE);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void safe_tps(Player player, String slocation) {
        String error = AnsiColor.RED + "Problem teleporting player";
        if ((player == null) || (slocation == null) || ("null".equals(slocation))) {
            cinform(error);
            return;
        }
        try {
            String sadjusted_loc = put_point_on_ground(slocation, false);
            Location adjusted_loc = str2location(sadjusted_loc);
            if (adjusted_loc == null) {
                cinform(error);
                return;
            }
            tp_player(player, adjusted_loc);
        } catch (Exception e) {
            cinform(error);
        }
    }


    public static void safe_tp(Player player, Location location) {
        String error = AnsiColor.RED + "Problem teleporting player";
        if ((player == null) || (location == null)) {
            cinform(error);
            return;
        }
        try {
            String sadjusted_loc = put_point_on_ground(location2str(location), false);
            Location adjusted_loc = str2location(sadjusted_loc);
            if (adjusted_loc == null) {
                cinform(error);
                return;
            }
            tp_player(player, adjusted_loc);
        } catch (Exception e) {
            cinform(error);
        }
    }

    public static void safe_tp_str(String splayer, String slocation) {
        String error = AnsiColor.RED + "Problem teleporting player";
        if ((splayer == null) || (slocation == null) || ("null".equals(splayer)) || ("null".equals(slocation))) {
            cinform(error);
            return;
        }
        try {
            Player player = str2player(splayer);
            String sadjusted_loc = put_point_on_ground(slocation, false);
            Location adjusted_loc = str2location(sadjusted_loc);
            if ((sadjusted_loc == null) || (player == null)) {
                cinform(sadjusted_loc);
                return;
            }
            tp_player(player, adjusted_loc);
        } catch (Exception e) {
            cinform(error);
        }
    }


    private static void tp_player(Player player, final Location location) {
        World world = location.getWorld();
        org.bukkit.Chunk chunk = world.getChunkAt(location);
        double target_y = location.getY();
        if (!world.isChunkLoaded(chunk)) {
            world.loadChunk(chunk);
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {

            public void run() {
                player.teleport(location);
            }
        }, 2L);


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {

            public void run() {
                player.teleport(location);
            }
        }, 4L);


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {

            public void run() {
                player.teleport(location);
            }
        }, 6L);
    }


    public static synchronized String put_point_on_ground(String slocation, boolean ground_block) {
        //Util.dinform("put_point_on_ground: "+slocation + " "+ground_block);
        Location base = simplified_copy(slocation);
        Location test_loc = simplified_copy(base);
        Block block = valid_waypnt_block(test_loc);
        if (ground_block) {
            if (block != null) {
                test_loc.add(0.0D, 1.0D, 0.0D);
                block = valid_waypnt_block(test_loc);
                if (block == null) {
                    return slocation;
                }

                test_loc = simplified_copy(base);
                block = valid_waypnt_block(test_loc);
                int count = 0;
                while ((block != null) && (count <= 5)) {
                    test_loc.add(0.0D, 1.0D, 0.0D);
                    block = valid_waypnt_block(test_loc);
                    count++;
                }
                if (block == null) {
                    test_loc.subtract(0.0D, 1.0D, 0.0D);
                    return location2str(test_loc);
                }


                return slocation;
            }


            test_loc = simplified_copy(base);
            block = valid_waypnt_block(test_loc);
            int count = 0;
            while ((block == null) && (count <= 5)) {
                test_loc.subtract(0.0D, 1.0D, 0.0D);
                block = valid_waypnt_block(test_loc);
                count++;
            }
            if (block != null) {
                return location2str(test_loc);
            }


            return slocation;
        }


        if (block == null) {
            test_loc.subtract(0.0D, 1.0D, 0.0D);
            block = valid_waypnt_block(test_loc);
            if (block != null) {
                return slocation;
            }

            test_loc = simplified_copy(base);
            block = valid_waypnt_block(test_loc);
            int count = 0;
            while ((block == null) && (count <= 5)) {
                test_loc.subtract(0.0D, 1.0D, 0.0D);
                block = valid_waypnt_block(test_loc);
                count++;
            }
            if (block != null) {
                test_loc.add(0.0D, 1.0D, 0.0D);
                return location2str(test_loc);
            }


            return slocation;
        }


        test_loc = simplified_copy(base);
        block = valid_waypnt_block(test_loc);
        int count = 0;
        while ((block != null) && (count <= 5)) {
            test_loc.add(0.0D, 1.0D, 0.0D);
            block = valid_waypnt_block(test_loc);
            count++;
        }
        if (block == null) {
            return location2str(test_loc);
        }


        return slocation;
    }


    public static synchronized Block valid_waypnt_block(Location location) {
        // TODO WTFFFFFFFFFFF
        if (location == null) {
            return null;
        }
        World w = location.getWorld();
        Block block = w.getBlockAt(location);
        //Util.dinform("valid_waypnt_block: " + AnsiColor.GREEN + "location " + AnsiColor.WHITE + "= [" + AnsiColor.YELLOW + location + AnsiColor.WHITE + "]");
        //Util.dinform("valid_waypnt_block: " + block + " " + block.getType() + " " + block.getType().isSolid() + " " + block.getType().isOccluding());

        if (block.getType().isSolid())
            return block;
        else
            return null;

        /*
        int type = block.getTypeId();
        if ((type >= 1) && (type <= 5)) {
            return block;
        }
        if (type == 7) {
            return block;
        }
        if ((type >= 12) && (type <= 22)) {
            return block;
        }
        if (type == 24) {
            return block;
        }
        if (type == 35) {
            return block;
        }
        if ((type >= 41) && (type <= 45)) {
            return block;
        }
        if ((type >= 48) && (type <= 49)) {
            return block;
        }
        if (type == 53) {
            return block;
        }
        if ((type >= 56) && (type <= 57)) {
            return block;
        }
        if (type == 60) {
            return block;
        }

        if (type == 67) {
            return block;
        }
        if ((type >= 73) && (type <= 74)) {
            return block;
        }
        if ((type >= 78) && (type <= 82)) {
            return block;
        }
        if ((type >= 86) && (type <= 89)) {
            return block;
        }
        if (type == 91) {
            return block;
        }
        if ((type >= 97) && (type <= 100)) {
            return block;
        }
        if ((type >= 108) && (type <= 110)) {
            return block;
        }
        if (type == 112) {
            return block;
        }
        if (type == 114) {
            return block;
        }
        if (type == 121) {
            return block;
        }
        if ((type >= 123) && (type <= 126)) {
            return block;
        }
        if ((type >= 128) && (type <= 129)) {
            return block;
        }
        if ((type >= 133) && (type <= 136)) {
            return block;
        }
        if ((type >= 152) && (type <= 153)) {
            return block;
        }
        if ((type >= 155) && (type <= 156)) {
            return block;
        }
        if ((type >= 170) && (type <= 173)) {
            return block;
        }
        return null;*/
    }

    public static void list_newmail(Player player) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(VA_postal.plugin, new Runnable() {
            public void run() {
                String splayer = player.getName().toLowerCase().trim();
                String[] town_list = C_Arrays.town_list();
                if (town_list == null) {
                    return;
                }
                boolean firstpass = true;
                for (String stown : town_list) {
                    String sworld = "&7&o" + com.vodhanel.minecraft.va_postal.config.C_List.get_world(com.vodhanel.minecraft.va_postal.config.C_Postoffice.get_local_po_location_by_name(stown));
                    String[] addr_list = C_Arrays.addresses_list(stown);
                    if (addr_list != null) {

                        for (String anAddr_list : addr_list) {
                            String saddress = anAddr_list;
                            if (C_Owner.is_address_owner_defined(stown, saddress)) {
                                String addr_owner = C_Owner.get_owner_address(stown, saddress).toLowerCase().trim();
                                if ((addr_owner.equals(splayer)) &&
                                        (com.vodhanel.minecraft.va_postal.config.C_Address.is_address_newmail(stown, saddress))) {
                                    if (firstpass) {
                                        firstpass = false;
                                        Util.pinform(player, "&r&a[Postal] &6&oYou have new mail at:");
                                        Util.new_mail_effect(player);
                                    }

                                    String dstown = com.vodhanel.minecraft.va_postal.config.C_List.fixed_len("&a&o" + Util.df(stown) + " &7&o", 24, "-");
                                    saddress = com.vodhanel.minecraft.va_postal.config.C_List.fixed_len("&a&o" + Util.df(saddress) + " &7&o", 24, "-");
                                    Util.pinform(player, "&7&o.    " + dstown + "  " + saddress + "  " + sworld);
                                }
                            }
                        }
                    }
                }
            }
        }, 20L);
    }


    public static void new_mail_effect(Player player) {
        player.getWorld().playEffect(player.getLocation(), org.bukkit.Effect.POTION_BREAK, 20, 0);
    }

    public static String player_complete(String splayer) {
        String result = "null";
        String test = "null";

        String[] player_list = null;
        try {
            player_list = all_players();
            if (player_list == null) {
                return "null";
            }
        } catch (Exception e) {
            return "null";
        }

        if (player_list.length > 0) {
            try {
                for (int i = 0; i < player_list.length; i++) {
                    if (player_list[i] == null) {
                        player_list[i] = "";
                    }
                }
            } catch (Exception e) {
                return "null";
            }
            String splyr = splayer.toLowerCase().trim();
            String kplyr = "";
            int hit = 0;

            try {
                for (int i = 0; i < player_list.length; i++) {
                    try {
                        kplyr = player_list[i].toString().toLowerCase().trim();
                    } catch (Exception e) {
                        continue;
                    }
                    if (kplyr.indexOf(splyr) >= 0) {
                        result = player_list[i].toString().trim();
                        hit++;
                    }
                }
                if (hit == 1) {
                    if (result.length() > 15) {
                    }

                    return result.substring(0, 15);
                }
            } catch (Exception e) {
                return "null";
            }


            String[] collection = new String[5];
            hit = 0;
            try {
                for (int i = 0; i < player_list.length; i++) {
                    try {
                        kplyr = player_list[i].toString().toLowerCase().trim();
                    } catch (Exception e) {
                        continue;
                    }
                    if (kplyr.length() >= splyr.length()) {
                        kplyr = kplyr.substring(0, splyr.length());
                        if (kplyr.indexOf(splyr) >= 0) {
                            test = player_list[i].toString().toLowerCase().trim();
                            result = player_list[i].toString().trim();

                            if (test.equals(splayer)) {
                                if (result.length() > 15) {
                                }

                                return result.substring(0, 15);
                            }


                            hit++;
                            collection[hit] = player_list[i].trim();
                            if (hit > 4) {
                                return "null";
                            }
                        }
                    }
                }
                if (hit == 1) {
                    if (result.length() > 15) {
                    }

                    return result.substring(0, 15);
                }
            } catch (Exception e) {
                return "null";
            }


            int no_match = 0;
            String last_match = "";
            String case_save = null;
            try {
                if (hit > 1) {
                    for (int i = 0; i < hit; i++) {

                        test = collection[i].toLowerCase().trim();
                        if (!test.equals(collection[i].trim())) {
                            case_save = collection[i].trim();
                        }
                        if (!test.equals(last_match)) {
                            no_match++;
                        }
                        last_match = test;
                    }
                }
                if (no_match == 1) {
                    if (case_save != null) {
                        if (case_save.length() > 15) {
                        }

                        return case_save.substring(0, 15);
                    }


                    if (test.length() > 15) {
                    }

                    return test.substring(0, 15);
                }
            } catch (Exception e) {
            }
        }


        return "null";
    }

    public static void player_list_match(Player player, String smatch) {
        String result = "null";
        String test = "null";
        boolean all = false;
        if ("*".equals(smatch)) {
            all = true;
        }
        String[] player_list = all_players();
        if (player_list == null) {
            return;
        }
        pinform(player, " &7&oPlayers matching search string:  " + smatch);
        String splyr = smatch.toLowerCase().trim();
        String kplyr = "";
        int hit = 0;

        for (int i = 0; i < player_list.length; i++) {
            kplyr = player_list[i].toString().toLowerCase().trim();
            if ((kplyr.indexOf(splyr) >= 0) || (all)) {
                result = player_list[i].toString().trim();

                if (!result.isEmpty()) {
                    pinform(player, "   &f&r" + result);
                }
                hit++;
            }
        }
        pinform(player, "&7&oHits:  &f&r" + hit);
    }

    public static void player_list_match_con(String smatch) {
        String result = "null";
        String test = "null";
        boolean all = false;
        if ("*".equals(smatch)) {
            all = true;
        }
        String[] player_list = all_players();
        if (player_list == null) {
            return;
        }
        con_type("Players matching search string:  " + smatch);
        String splyr = smatch.toLowerCase().trim();
        String kplyr = "";
        int hit = 0;

        for (int i = 0; i < player_list.length; i++) {
            kplyr = player_list[i].toString().toLowerCase().trim();
            if ((kplyr.indexOf(splyr) >= 0) || (all)) {
                result = player_list[i].toString().trim();

                if (!result.isEmpty()) {
                    con_type("  " + result);
                }
                hit++;
            }
        }
        con_type("Hits:  " + hit);
    }

    public static String[] all_players() {
        int list_len = 0;
        Player[] onlineplayers = null;
        org.bukkit.OfflinePlayer[] offlineplayers = null;
        try {
            onlineplayers = Bukkit.getOnlinePlayers().toArray(new Player[0]); // FIXME NOT SURE
            offlineplayers = Bukkit.getOfflinePlayers();
            list_len = onlineplayers.length + offlineplayers.length;
        } catch (Exception e) {
            return null;
        }

        String[] player_list = new String[list_len];
        try {
            int pos = 0;
            for (int i = 0; i < onlineplayers.length; i++) {
                player_list[pos] = onlineplayers[i].getName().toString();
                pos++;
            }
            for (int i = 0; i < offlineplayers.length; i++) {
                player_list[pos] = offlineplayers[i].getName().toString();
                pos++;
            }
        } catch (Exception e) {
            return null;
        }

        if ((player_list != null) && (player_list.length > 0)) {
            try {
                java.util.Arrays.sort(player_list);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }


        int count = 0;
        String last_name = "";
        try {
            for (int i = 0; i < player_list.length; i++) {
                if (!player_list[i].equals(last_name)) {
                    count++;
                }
                last_name = player_list[i];
            }
        } catch (Exception e) {
            return null;
        }

        int pos = 0;
        last_name = "";
        String[] player_list_unique = new String[count];
        try {
            for (int i = 0; i < player_list.length; i++) {
                if (!player_list[i].equals(last_name)) {
                    player_list_unique[pos] = player_list[i];
                    pos++;
                }
                last_name = player_list[i];
            }
        } catch (Exception e) {
            return null;
        }

        return player_list_unique;
    }

    public static String[] proximity_object(Player player, int allowed_dist) {
        String msg = "&7&oThere is no PO or address within " + allowed_dist + " blocks.";
        String[] result = new String[2];
        String[] geo_object = C_Arrays.geo_list_sorted(player);
        if ((geo_object != null) && (geo_object.length > 0)) {
            String[] parts = geo_object[0].split(",");
            if ((parts != null) && (parts.length == 4)) {
                int dist = str2int(parts[0]);
                if (dist > allowed_dist) {
                    pinform(player, msg);
                    return null;
                }
                result[0] = parts[1].toLowerCase().trim();
                result[1] = parts[2].toLowerCase().trim();
                return result;
            }
        }
        pinform(player, msg);
        return null;
    }

    public static String proximity_postoffice(Player player, int allowed_dist) {
        String msg = "&7&oThere is no POst office within " + allowed_dist + " blocks.";
        String[] geo_object = C_Arrays.geo_po_list_sorted(player);
        if ((geo_object != null) && (geo_object.length > 0)) {
            String[] parts = geo_object[0].split(",");
            if ((parts != null) && (parts.length == 4)) {
                int dist = str2int(parts[0]);
                if (dist > allowed_dist) {
                    pinform(player, msg);
                    return null;
                }
                return parts[1].toLowerCase().trim();
            }
        }
        pinform(player, msg);
        return null;
    }

    public static String[] proximity_address(Player player, int allowed_dist) {
        String msg = "&7&oThere is no PO or address within " + allowed_dist + " blocks.";
        String[] result = new String[2];
        String[] geo_object = C_Arrays.geo_addr_list_sorted(player);
        if ((geo_object != null) && (geo_object.length > 0)) {
            String[] parts = geo_object[0].split(",");
            if ((parts != null) && (parts.length == 4)) {
                int dist = str2int(parts[0]);
                if (dist > allowed_dist) {
                    pinform(player, msg);
                    return null;
                }
                result[0] = parts[1].toLowerCase().trim();
                result[1] = parts[2].toLowerCase().trim();
                return result;
            }
        }
        pinform(player, msg);
        return null;
    }

    public static String proper(String string) {
        try {
            if (string.length() > 0) {
                return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase().trim();
            }
        } catch (Exception e) {
        }

        return "";
    }

    public static String df(String string) {
        try {
            String[] parts = string.split("_");
            String name = proper(parts[0]);
            if (parts.length > 1) {
                name = name + "_" + Util.proper(parts[1]);
            }
            if (parts.length > 2) {
                name = name + "_" + Util.proper(parts[2]);
            }
            if (parts.length > 3) {
            }
            return name + "_" + Util.proper(parts[3]);
        } catch (Exception e) {
            //Util.dinform("ERROR IN DF: " + e);
            return string;
        }
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

    public static String min_len(String input, int len, String filler) {
        try {
            input = input.trim();

            if (input.length() >= len) {
                return input;
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

    public static int blocks_between_it_and_ground(Block block) {
        int result = 0;

        Location location = block.getLocation();
        World w = location.getWorld();
        location.subtract(0.0D, 1.0D, 0.0D);
        Block btype = w.getBlockAt(location);

        while (btype.getType() == org.bukkit.Material.AIR) {
            location.subtract(0.0D, 1.0D, 0.0D);
            btype = w.getBlockAt(location);
            result++;
        }
        return result;
    }
}
