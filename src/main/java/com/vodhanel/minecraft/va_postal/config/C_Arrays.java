package com.vodhanel.minecraft.va_postal.config;

import com.vodhanel.minecraft.va_postal.VA_postal;
import com.vodhanel.minecraft.va_postal.common.Util;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class C_Arrays {
    VA_postal plugin;

    public C_Arrays(VA_postal instance) {
        plugin = instance;
    }

    public static synchronized String[] town_list() {
        String[] result = null;
        ConfigurationSection cs;
        try {
            String path = GetConfig.path_format("address");
            cs = VA_postal.configsettings.getConfigurationSection(path);
        } catch (Exception e) {
            return null;
        }
        if (cs != null) {
            Set<String> child_keys = cs.getKeys(false);
            try {
                if ((child_keys != null) && (child_keys.size() > 0)) {
                    Object[] a_child_keys = child_keys.toArray();
                    result = new String[a_child_keys.length];
                    for (int i = 0; i < a_child_keys.length; i++) {
                        result[i] = a_child_keys[i].toString().toLowerCase().trim();
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }


        if ((result != null) && (result.length > 0)) {
            try {
                Arrays.sort(result);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }

        return result;
    }

    public static synchronized String[] towny_list_sorted() {
        List<String> list = new ArrayList();

        String[] town_list = town_list();
        if (town_list == null) {
            return null;
        }
        for (String aTown_list : town_list) {
            String postal = aTown_list.trim();
            String towny;
            if (C_Towny.is_towny_town_defined(postal)) {
                towny = C_Towny.get_towny_town(postal).trim();
            } else {
                towny = "aaaaaa";
            }
            String[] addr_list = addresses_list(postal);
            if (addr_list != null) {

                for (String anAddr_list : addr_list) {
                    String saddress = anAddr_list.trim();
                    list.add(towny + "," + postal + "," + saddress);
                }
                addr_list = null;
            }
        }
        town_list = null;


        if (list.size() <= 0) {
            return null;
        }


        String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = ((String) list.get(i)).toLowerCase().trim();
        }
        list = null;


        if (result.length > 0) {
            try {
                Arrays.sort(result);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }


        if (result.length > 0) {
            for (int i = 0; i < result.length; i++) {
                result[i] = result[i].replace("aaaaaa", "postal");
            }
        } else {
            return null;
        }

        return result;
    }

    public static synchronized String[] postal_list_sorted() {
        List<String> list = new ArrayList();

        String[] town_list = town_list();
        if (town_list == null) {
            return null;
        }
        for (String aTown_list : town_list) {
            String postal = aTown_list.trim();
            String[] addr_list = addresses_list(postal);
            if (addr_list != null) {

                for (String anAddr_list : addr_list) {
                    String saddress = anAddr_list.trim();
                    list.add(postal + "," + saddress);
                }
                addr_list = null;
            }
        }
        town_list = null;


        String[] result;
        if (list.size() > 0) {
            result = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = ((String) list.get(i)).toLowerCase().trim();
            }
            list = null;
        } else {
            return null;
        }
        if (result.length > 0) {
            try {
                Arrays.sort(result);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }

        return result;
    }

    public static synchronized String[] geo_po_list_sorted(Player player) {
        if (player == null) {
            return null;
        }
        Util.calibrate_compass(player);
        List<String> list = new ArrayList();
        Location location = player.getLocation();
        if (location == null) {
            return null;
        }

        String[] town_list = town_list();
        if (town_list == null) {
            return null;
        }
        for (String aTown_list : town_list) {
            String postal = aTown_list.toLowerCase().trim();
            Location t_location = Util.str2location(C_Postoffice.get_local_po_location_by_name(postal));
            if ((t_location != null) &&
                    (t_location.getWorld() == player.getWorld())) {
                int distance = (int) location.distance(t_location);
                String formatted_dist = Util.int2fstr_leading_zeros(distance, 5);
                String heading = Util.get_fmt_heading_to_target(player, t_location);
                if (heading != null) {
                    list.add(formatted_dist + "," + postal + "," + heading);
                }
            }
        }

        town_list = null;
        if (list.size() > 0) {
            String[] result = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = ((String) list.get(i)).trim();
            }
            list = null;


            if (result.length > 0) {
                try {
                    Arrays.sort(result);
                } catch (Exception e) {
                    result = null;
                }
            } else {
                result = null;
            }

            return result;
        }
        return null;
    }

    public static synchronized String[] geo_addr_list_sorted(Player player) {
        if (player == null) {
            return null;
        }
        Util.calibrate_compass(player);
        List<String> list = new ArrayList();
        Location location = player.getLocation();
        if (location == null) {
            return null;
        }

        String[] town_list = town_list();
        if (town_list == null) {
            return null;
        }
        for (String aTown_list : town_list) {
            String stown = aTown_list.toLowerCase().trim();
            String[] addr_list = addresses_list(stown);
            if (addr_list != null) {

                for (String anAddr_list : addr_list) {
                    String saddress = anAddr_list.toLowerCase().trim();
                    Location t_location = Util.str2location(C_Address.get_address_location(stown, saddress));
                    if ((t_location != null) &&
                            (t_location.getWorld() == player.getWorld())) {
                        int distance = (int) location.distance(t_location);
                        String formatted_dist = Util.int2fstr_leading_zeros(distance, 5);
                        String heading = Util.get_fmt_heading_to_target(player, t_location);
                        if (heading != null) {
                            list.add(formatted_dist + "," + stown + "," + saddress + "," + heading);
                        }
                    }
                }

                addr_list = null;
            }
        }
        town_list = null;
        if (list.size() > 0) {
            String[] result = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = ((String) list.get(i)).trim();
            }
            list = null;


            if (result.length > 0) {
                try {
                    Arrays.sort(result);
                } catch (Exception e) {
                    result = null;
                }
            } else {
                result = null;
            }
            return result;
        }
        return null;
    }

    public static synchronized String[] geo_list_sorted(Player player) {
        if (player == null) {
            return null;
        }
        int master_i = 0;

        String[] po_list = geo_po_list_sorted(player);
        if ((po_list != null) && (po_list.length > 0)) {
            master_i = po_list.length;
        }

        String[] addr_list = geo_addr_list_sorted(player);
        if ((addr_list != null) && (addr_list.length > 0)) {
            master_i += addr_list.length;
        }

        String[] consolidated = new String[master_i];

        master_i = 0;
        if ((po_list != null) && (po_list.length > 0)) {
            for (String aPo_list : po_list) {
                String[] parts = aPo_list.split(",");
                if ((parts != null) && (parts.length == 3)) {
                    String line = parts[0] + "," + parts[1] + ",Post_Office," + parts[2];
                    consolidated[master_i] = line;
                } else {
                    consolidated[master_i] = "99999";
                }
                master_i++;
            }
            po_list = null;
        }
        if ((addr_list != null) && (addr_list.length > 0)) {
            for (String anAddr_list : addr_list) {
                if (anAddr_list != null) {
                    consolidated[master_i] = anAddr_list;
                } else {
                    consolidated[master_i] = "99999";
                }
                master_i++;
            }
            addr_list = null;
        }

        if (consolidated.length > 0) {
            try {
                Arrays.sort(consolidated);
            } catch (Exception e) {
                consolidated = null;
            }
            return consolidated;
        }
        return null;
    }

    public static synchronized String[] geo_player_list_sorted(Player player) {
        if (player == null) {
            return null;
        }
        Util.calibrate_compass(player);
        ArrayList<String> list = new ArrayList<>();
        Location location = player.getLocation();
        if (location == null) {
            return null;
        }
        for (Player others : VA_postal.plugin.getServer().getOnlinePlayers()) {
            Location t_location = others.getLocation();
            if ((t_location != null) &&
                    (location.getWorld() == t_location.getWorld())) {
                String[] g_list = geo_list_sorted(others);
                if ((g_list != null) && (g_list.length > 0)) {
                    String[] parts = g_list[0].split(",");
                    if (parts.length == 4) {
                        int distance = (int) location.distance(t_location);
                        String formatted_dist = Util.int2fstr_leading_zeros(distance, 5);
                        String t_player = others.getName();
                        String heading = Util.get_fmt_heading_to_target(player, t_location);
                        list.add(formatted_dist + "," + t_player + "," + heading + "," + parts[1] + "," + parts[2]);
                    }
                }
            }
        }


        if (list.size() > 0) {
            String[] result = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = list.get(i).trim();
            }

            if (result.length > 0) {
                try {
                    Arrays.sort(result);
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
            return result;
        }
        return null;
    }

    public static synchronized String[] addresses_list(String spoffice) {
        if (spoffice == null) {
            return null;
        }
        String[] result = null;
        ConfigurationSection cs = null;
        try {
            String path = GetConfig.path_format("address." + spoffice);
            cs = VA_postal.configsettings.getConfigurationSection(path);
        } catch (Exception e) {
            return null;
        }
        if (cs != null) {
            Set<String> child_keys = cs.getKeys(false);
            if (child_keys == null) {
                return null;
            }
            try {
                if (child_keys.size() > 0) {
                    Object[] a_child_keys = child_keys.toArray();
                    result = new String[a_child_keys.length];
                    for (int i = 0; i < a_child_keys.length; i++) {
                        result[i] = a_child_keys[i].toString().toLowerCase().trim();
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }


        if ((result != null) && (result.length > 0)) {
            try {
                Arrays.sort(result);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }

        return result;
    }
}
