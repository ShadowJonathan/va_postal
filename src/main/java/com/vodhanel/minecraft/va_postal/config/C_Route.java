package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.AnsiColor;
import com.vodhanel.minecraft.va_postal.common.Util;
import org.bukkit.Location;

import java.util.ArrayList;

public class C_Route {
    VA_postal plugin;

    public C_Route(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized boolean delete_last_waypoint(String po_name, String splayer) {
        if (is_route_defined(po_name, splayer)) {
            int pos = 0;
            try {
                pos = get_last_waypoint_position(po_name, splayer);
            } catch (Exception e) {
                return false;
            }
            if (is_waypoint_defined(po_name, splayer, pos)) {
                String spos = null;
                try {
                    spos = Integer.toString(pos).trim();
                } catch (Exception e) {
                    return false;
                }
                try {
                    String spath = GetConfig.path_format("address." + po_name + "." + splayer + ".route." + spos);
                    VA_postal.plugin.getConfig().set(spath, null);
                    VA_postal.plugin.saveConfig();
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static synchronized int route_waypoint_count(String po_name, String address) {
        try {
            String spath = GetConfig.path_format("address." + po_name + "." + address + ".route");
            return GetConfig.get_number_of_children(spath);
        } catch (Exception e) {
        }
        return 0;
    }

    public static synchronized boolean is_waypoint_defined(String po_name, String address, int pos) {
        String spos = "null";
        try {
            spos = Integer.toString(pos).trim();
        } catch (Exception e) {
            return false;
        }
        try {
            String spath = GetConfig.path_format("address." + po_name + "." + address + ".route." + spos);
            return GetConfig.is_parent_defined(spath);
        } catch (Exception e) {
        }
        return false;
    }

    public static synchronized String get_last_waypoint_location(String stown, String address) {
        String result = null;
        String spos = null;
        try {
            int pos = get_last_waypoint_position(stown, address);
            spos = Integer.toString(pos).trim();
        } catch (Exception e) {
            return "null";
        }
        try {
            String spath = GetConfig.path_format("address." + stown + "." + address + ".route." + spos + ".location");
            result = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            return "null";
        }
        if (result == null) {
            return "null";
        }
        return result;
    }

    public static synchronized String get_waypoint_location(String po_name, String address, int pos) {
        String result = null;
        String spos = null;
        //Util.binform(po_name + " " + splayer + " " + pos);
        try {
            spos = Integer.toString(pos).trim();
        } catch (Exception e) {
            Util.dinform(AnsiColor.RED + "EXCEPTION WHEN GETTING WAYPOINT LOCATION: " + AnsiColor.L_YELLOW + po_name + " " + address + " " + pos
                    + AnsiColor.RED + " " + e);
            return "null";
        }
        try {
            String spath = GetConfig.path_format("address." + po_name + "." + address + ".route." + spos + ".location");
            result = VA_postal.plugin.getConfig().getString(spath);
        } catch (Exception e) {
            Util.dinform(AnsiColor.RED + "EXCEPTION WHEN GETTING WAYPOINT LOCATION: " + AnsiColor.L_YELLOW + po_name + " " + address + " " + pos + AnsiColor.RED + " " + e);
            return "null";
        }
        if (result == null) {
            Util.dinform(AnsiColor.RED + "RESULT WAS NULL FOR " + AnsiColor.L_YELLOW + po_name + " " + address + " " + pos);
            return "null";
        }
        return result;
    }

    public static synchronized ArrayList<Location> get_waypoint_locations(String po_name, String address) {
        ArrayList<Location> list = new ArrayList<>();
        for (int i = 0; i < route_waypoint_count(po_name, address); i++) {
            list.add(Util.str2location(get_waypoint_location(po_name, address, i)));
        }
        return list;
    }

    public static synchronized int get_last_waypoint_position(String po_name, String splayer) {
        int result = -1;
        int cn = -1;
        try {
            String spath = GetConfig.path_format("address." + po_name + "." + splayer + ".route");
            cn = GetConfig.get_number_of_children(spath);
        } catch (Exception e) {
            return -1;
        }
        result = cn - 1;
        return result;
    }

    public static synchronized int append_route_waypoint(String stown, String saddress, String slocation) {
        int pos = -1;
        try {
            pos = route_waypoint_count(stown, saddress);
        } catch (Exception e) {
            pos = -1;
        }
        try {
            if (set_route_waypoint(stown, saddress, pos, slocation)) {
                return pos;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public static synchronized boolean set_route_waypoint(String stown, String saddress, int pos, String slocation) {
        String spos = null;
        try {
            spos = Integer.toString(pos).trim();
            String spath = GetConfig.path_format("address." + stown + "." + saddress + ".route." + spos + ".location");
            VA_postal.plugin.getConfig().set(spath, slocation);
            VA_postal.plugin.saveConfig();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static synchronized boolean set_route_waypoint_ns(String stown, String saddress, int pos, String slocation) {
        String spos = null;
        try {
            spos = Integer.toString(pos).trim();
            String spath = GetConfig.path_format("address." + stown + "." + saddress + ".route." + spos + ".location");
            VA_postal.plugin.getConfig().set(spath, slocation);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static synchronized void save_config() {
        VA_postal.plugin.saveConfig();
    }

    public static synchronized void delete_route(String po_name, String splayer) {
        try {
            if (is_route_defined(po_name, splayer)) {
                String spath = GetConfig.path_format("address." + po_name + "." + splayer + ".route");
                VA_postal.plugin.getConfig().set(spath, null);
                VA_postal.plugin.saveConfig();
            }
        } catch (Exception e) {
            Util.cinform(AnsiColor.RED + "Problem deleting route");
        }
    }

    public static synchronized boolean is_route_defined(String po_name, String splayer) {
        try {
            String spath = GetConfig.path_format("address." + po_name + "." + splayer + ".route");
            return GetConfig.is_parent_defined(spath);
        } catch (Exception e) {
        }
        return false;
    }
}
