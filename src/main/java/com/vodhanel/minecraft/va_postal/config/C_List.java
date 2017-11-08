package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Set;

public class C_List {
    VA_postal plugin;

    public C_List(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized void list_children(Player player, String path) {
        path = GetConfig.path_format(path);
        ConfigurationSection cs = VA_postal.configsettings.getConfigurationSection(path);
        if (cs != null) {
            Set<String> child_keys = cs.getKeys(false);
            if (child_keys.size() > 0) {
                Object[] a_child_keys = child_keys.toArray();
                Arrays.sort(a_child_keys);
                for (Object a_child_key : a_child_keys) {
                    Util.pinform(player, "     &r" + Util.df(a_child_key.toString()));
                }
            }
        }
    }

    public static synchronized void list_local_po(Player player, String path) {
        path = GetConfig.path_format(path);

        String town;
        String slocation;
        ConfigurationSection cs = VA_postal.configsettings.getConfigurationSection(path);
        if (cs != null) {
            Set<String> child_keys = cs.getKeys(false);
            if (child_keys.size() > 0) {
                Object[] a_child_keys = child_keys.toArray();
                Arrays.sort(a_child_keys);
                for (Object a_child_key : a_child_keys) {
                    town = a_child_key.toString().trim();
                    slocation = C_Postoffice.get_local_po_location_by_name(town);
                    Util.pinform(player, "     &r" + Util.df(town) + ",     &bLocation:  " + slocation);
                }
            }
        }
    }

    public static synchronized void list_children_con(String path) {
        path = GetConfig.path_format(path);
        ConfigurationSection cs = VA_postal.configsettings.getConfigurationSection(path);
        if (cs != null) {
            Set<String> child_keys = cs.getKeys(false);
            if (child_keys.size() > 0) {
                Object[] a_child_keys = child_keys.toArray();
                Arrays.sort(a_child_keys);
                for (Object a_child_key : a_child_keys) {
                    Util.cinform(a_child_key.toString());
                }
            }
        }
    }

    public static synchronized void list_addresses_con(String spoffice) {
        String saddress;
        String sowner;
        Player owner;
        String sinterval;
        String display;
        String path = GetConfig.path_format("address." + spoffice);
        ConfigurationSection cs = VA_postal.configsettings.getConfigurationSection(path);
        if (cs != null) {
            Set<String> child_keys = cs.getKeys(false);
            if (child_keys.size() > 0) {
                Object[] a_child_keys = child_keys.toArray();
                Arrays.sort(a_child_keys);
                int total = 0;
                try {
                    for (Object a_child_key : a_child_keys) {
                        saddress = a_child_key.toString().trim();
                        owner = C_Owner.get_owner_address(spoffice, saddress);
                        sinterval = C_Address.get_addr_interval(spoffice, saddress);
                        total += Integer.parseInt(sinterval);
                        sinterval = "Seconds: " + sinterval;
                        saddress = fixed_len(Util.df(saddress), 20, " ");
                        if (owner == null || owner == VA_postal.SERVER) {
                            sowner = "Server";
                        } else {
                            sowner = owner.getDisplayName();
                        }
                        sowner = fixed_len(sowner, 20, " ");
                        display = saddress + sowner + sinterval;
                        Util.cinform("    " + display);
                    }
                } catch (NumberFormatException ignored) {
                }
                Util.cinform("Total of all routes in seconds: " + total);
            }
        }
    }

    public static synchronized void list_towns(Player player) {
        String stown;
        String sowner;
        Player owner;
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
                    owner = C_Owner.get_owner_local_po(stown);
                    if (owner == null || owner == VA_postal.SERVER) {
                        sowner = "&7&oServer";
                    } else {
                        sowner = "&f&r" + owner.getDisplayName();
                    }
                    sowner = fixed_len(sowner + "&7&o", 28, "-");
                    sworld = "&7&o" + get_world(C_Postoffice.get_local_po_location_by_name(stown));
                    stown = fixed_len("&f&r" + Util.df(stown) + "&7&o", 28, "-");
                    display = stown + sowner + sworld;
                    Util.pinform(player, "  " + display);
                    hits++;
                }
            }
            Util.pinform(player, "&7&oHits: &f&r" + hits);
        }
    }

    public static synchronized void list_towns_con() {
        String stown;
        String sowner;
        Player owner;
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
                    stown = fixed_len(Util.df(a_child_key.toString()), 20, " ");
                    owner = C_Owner.get_owner_local_po(stown);
                    if (owner == null || owner == VA_postal.SERVER) {
                        sowner = "&7&oServer";
                    } else {
                        sowner = "&f&r" + owner.getDisplayName();
                    }
                    sowner = fixed_len(sowner, 20, " ");
                    sworld = get_world(C_Postoffice.get_local_po_location_by_name(stown));
                    display = stown + sowner + sworld;
                    Util.cinform("  " + display);
                    hits++;
                }
            }
            Util.cinform("Hits: " + hits);
        }
    }

    public static synchronized String fixed_len(String input, int len, String filler) {
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

    public static synchronized String get_world(String slocation) {
        try {
            String[] parts;
            parts = slocation.split(",");
            return GetConfig.proper(parts[0]).trim();
        } catch (Exception e) {
        }
        return "null";
    }
}
